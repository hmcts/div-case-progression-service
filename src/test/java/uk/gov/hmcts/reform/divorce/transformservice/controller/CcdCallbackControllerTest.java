package uk.gov.hmcts.reform.divorce.transformservice.controller;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import uk.gov.hmcts.reform.divorce.CaseProgressionApplication;
import uk.gov.hmcts.reform.divorce.notifications.service.EmailService;
import uk.gov.hmcts.reform.divorce.testutils.ObjectMapperTestUtil;
import uk.gov.hmcts.reform.divorce.transformservice.client.pdf.PdfGeneratorException;
import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.CreateEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CaseDetails;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CoreCaseData;
import uk.gov.hmcts.reform.divorce.transformservice.domain.transformservice.CCDCallbackResponse;
import uk.gov.hmcts.reform.divorce.transformservice.service.UpdateService;
import uk.gov.hmcts.reform.divorce.validationservice.domain.ValidationResponse;
import uk.gov.hmcts.reform.divorce.validationservice.service.ValidationService;

import java.io.File;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(CcdCallBackController.class)
@ContextConfiguration(classes = CaseProgressionApplication.class)
public class CcdCallbackControllerTest {

    private static final String ADD_PDF_URL = "/caseprogression/petition-issued";
    private static final String PETITION_SUBMITTED_URL = "/caseprogression/petition-submitted";
    private static final String AUTH_TOKEN = "test";
    private static final String AUTH_HEADER = "Authorization";

    @Autowired
    private WebApplicationContext applicationContext;

    @MockBean
    public EmailService emailService;
    @MockBean
    private UpdateService updateService;

    @MockBean
    private ValidationService validationService;

    private MockMvc mvc;

    private String requestContent;

    @Before
    public void setUp() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(applicationContext).build();

        requestContent = FileUtils.readFileToString(new File(getClass()
            .getResource("/fixtures/divorce/add-pdf-request-body.json").toURI()), Charset.defaultCharset());
    }

    @Test
    public void shouldReturnCaseIdWhenPdfIsGeneratedAndAddedToCase() throws Exception {
        final Long caseId = 1235678L;

        CoreCaseData coreCaseData = new CoreCaseData();

        CreateEvent submittedCase = new CreateEvent();
        submittedCase.setEventId("uploadDocument");
        CaseDetails caseDetails = new CaseDetails();
        caseDetails.setCaseData(coreCaseData);
        caseDetails.setCaseId(caseId + "");
        submittedCase.setCaseDetails(caseDetails);

        when(validationService.validateCoreCaseData(coreCaseData)).thenReturn(new ValidationResponse());
        when(updateService.addPdf(submittedCase, AUTH_TOKEN)).thenReturn(coreCaseData);

        MvcResult result = mvc.perform(post(ADD_PDF_URL)
            .content(ObjectMapperTestUtil.convertObjectToJsonString(submittedCase))
            .header(AUTH_HEADER, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk()).andReturn();

        CCDCallbackResponse response =
            ObjectMapperTestUtil.convertJsonToObject(
                result.getResponse().getContentAsByteArray(),
                CCDCallbackResponse.class);

        assertEquals(coreCaseData, response.getData());

        verify(validationService).validateCoreCaseData(coreCaseData);
        verify(updateService).addPdf(submittedCase, AUTH_TOKEN);
        verifyNoMoreInteractions(validationService, updateService);
    }

    @Test
    public void shouldReturnErrorInResponseBodyWhenGeneratorExceptionHappen() throws Exception {
        final String genericExceptionMessage = "Pdf Generator error Exception message : {0}";
        PdfGeneratorException exception = mock(PdfGeneratorException.class);
        testExceptionHandling(genericExceptionMessage, exception);
    }

    private void testExceptionHandling(String genericExceptionMessage, Exception exception) throws Exception {
        final String errorMessage = "error-message caught Exception";
        final String exceptionMessage = MessageFormat.format(genericExceptionMessage, errorMessage);

        final Long caseId = 1235678L;

        CreateEvent submittedCase = new CreateEvent();
        submittedCase.setEventId("uploadDocument");
        CaseDetails caseDetails = new CaseDetails();
        caseDetails.setCaseId(caseId + "");
        submittedCase.setCaseDetails(caseDetails);

        when(exception.getMessage()).thenReturn(errorMessage);

        when(validationService.validateCoreCaseData(null)).thenReturn(new ValidationResponse());
        doThrow(exception).when(updateService).addPdf(submittedCase, AUTH_TOKEN);

        ResultActions perform = mvc.perform(post(ADD_PDF_URL)
            .content(requestContent)
            .header("requestId", "123")
            .header(AUTH_HEADER, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON_UTF8));
        perform
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors[0]", is(exceptionMessage)));

        verify(validationService).validateCoreCaseData(null);
        verify(updateService).addPdf(eq(submittedCase), eq(AUTH_TOKEN));
        verify(exception).getMessage();
        verifyNoMoreInteractions(validationService, updateService);
    }


    @Test
    public void givenCallbackIsReceivedFromCCD_thenProcessACallback_ExpectJWTTokenInTheHeader() throws Exception {
        CreateEvent submittedCase = new CreateEvent();
        CaseDetails caseDetails = new CaseDetails();
        submittedCase.setCaseDetails(caseDetails);
        
        String authorizationKey = "ZZZZZZZZZZZZZZ";
        when(validationService.validateCoreCaseData(null)).thenReturn(new ValidationResponse());
        when(updateService.addPdf(anyObject(), anyString())).thenReturn(null);
        mvc.perform(post(ADD_PDF_URL)
            .content(ObjectMapperTestUtil.convertObjectToJsonString(submittedCase))
            .header(AUTH_HEADER, authorizationKey)
            .contentType(MediaType.APPLICATION_JSON_UTF8));

        verify(validationService).validateCoreCaseData(null);
        verify(updateService).addPdf(anyObject(), eq(authorizationKey));
    }

    @Test
    public void givenCallbackIsReceivedFromCCD_thenProcessACallbackWithNullAuthHeader_ExpectToPass() throws Exception {
        CreateEvent submittedCase = new CreateEvent();
        CaseDetails caseDetails = new CaseDetails();
        submittedCase.setCaseDetails(caseDetails);
        
        when(validationService.validateCoreCaseData(null)).thenReturn(new ValidationResponse());
        when(updateService.addPdf(anyObject(), anyString())).thenReturn(null);
        mvc.perform(post(ADD_PDF_URL)
            .content(ObjectMapperTestUtil.convertObjectToJsonString(submittedCase))
            .contentType(MediaType.APPLICATION_JSON_UTF8));
        verify(validationService).validateCoreCaseData(null);
        verify(updateService).addPdf(anyObject(), eq(null));
    }

    @Test
    public void givenNoPetitionerEmail_whenPetitionSubmittedEndpointIsCalled_thenEmailServiceIsNotCalled() throws Exception {
        CreateEvent submittedCase = new CreateEvent();
        CaseDetails caseDetails = new CaseDetails();
        CoreCaseData coreCaseData = new CoreCaseData();
        caseDetails.setCaseData(coreCaseData);
        submittedCase.setCaseDetails(caseDetails);

        mvc.perform(post(PETITION_SUBMITTED_URL)
            .content(ObjectMapperTestUtil.convertObjectToJsonString(submittedCase))
            .contentType(MediaType.APPLICATION_JSON_UTF8));

        verify(emailService, never()).sendSubmissionNotificationEmail(anyObject(), anyObject());
    }

    @Test
    public void givenPetitionerEmailAndEastMidlands_whenPetitionSubmittedEndpointIsCalled_thenEmailServiceIsCalled() throws Exception {
        CoreCaseData coreCaseData = new CoreCaseData();
        coreCaseData.setD8PetitionerEmail("example@email.com");
        coreCaseData.setD8DivorceUnit("eastMidlands");

        CaseDetails caseDetails = new CaseDetails();
        caseDetails.setCaseId("1000-1000-1000-1000");
        caseDetails.setCaseData(coreCaseData);

        CreateEvent submittedCase = new CreateEvent();
        submittedCase.setCaseDetails(caseDetails);

        Map<String, String> templateVars = getTemplateVars("East Midlands Regional Divorce Centre");

        doNothing().when(emailService).sendSubmissionNotificationEmail(anyObject(), eq(templateVars));

        mvc.perform(post(PETITION_SUBMITTED_URL)
            .content(ObjectMapperTestUtil.convertObjectToJsonString(submittedCase))
            .contentType(MediaType.APPLICATION_JSON_UTF8));

        verify(emailService).sendSubmissionNotificationEmail(anyObject(), eq(templateVars));
        verifyNoMoreInteractions(emailService);
    }

    @Test
    public void givenPetitionerEmailAndWestMidlands_whenPetitionSubmittedEndpointIsCalled_thenEmailServiceIsCalled() throws Exception {
        CoreCaseData coreCaseData = new CoreCaseData();
        coreCaseData.setD8PetitionerEmail("example@email.com");
        coreCaseData.setD8DivorceUnit("westMidlands");

        CaseDetails caseDetails = new CaseDetails();
        caseDetails.setCaseId("1000-1000-1000-1000");
        caseDetails.setCaseData(coreCaseData);

        CreateEvent submittedCase = new CreateEvent();
        submittedCase.setCaseDetails(caseDetails);

        Map<String, String> templateVars = getTemplateVars("West Midlands Regional Divorce Centre");

        doNothing().when(emailService).sendSubmissionNotificationEmail(anyObject(), eq(templateVars));

        mvc.perform(post(PETITION_SUBMITTED_URL)
            .content(ObjectMapperTestUtil.convertObjectToJsonString(submittedCase))
            .contentType(MediaType.APPLICATION_JSON_UTF8));

        verify(emailService).sendSubmissionNotificationEmail(anyObject(), eq(templateVars));
        verifyNoMoreInteractions(emailService);
    }

    @Test
    public void givenPetitionerEmailAndSouthWest_whenPetitionSubmittedEndpointIsCalled_thenEmailServiceIsCalled() throws Exception {
        CoreCaseData coreCaseData = new CoreCaseData();
        coreCaseData.setD8PetitionerEmail("example@email.com");
        coreCaseData.setD8DivorceUnit("southWest");

        CaseDetails caseDetails = new CaseDetails();
        caseDetails.setCaseId("1000-1000-1000-1000");
        caseDetails.setCaseData(coreCaseData);

        CreateEvent submittedCase = new CreateEvent();
        submittedCase.setCaseDetails(caseDetails);

        Map<String, String> templateVars = getTemplateVars("South West Regional Divorce Centre");

        doNothing().when(emailService).sendSubmissionNotificationEmail(anyObject(), eq(templateVars));

        mvc.perform(post(PETITION_SUBMITTED_URL)
            .content(ObjectMapperTestUtil.convertObjectToJsonString(submittedCase))
            .contentType(MediaType.APPLICATION_JSON_UTF8));

        verify(emailService).sendSubmissionNotificationEmail(anyObject(), eq(templateVars));
        verifyNoMoreInteractions(emailService);
    }

    @Test
    public void givenPetitionerEmailAndNorthWest_whenPetitionSubmittedEndpointIsCalled_thenEmailServiceIsCalled() throws Exception {
        CoreCaseData coreCaseData = new CoreCaseData();
        coreCaseData.setD8PetitionerEmail("example@email.com");
        coreCaseData.setD8DivorceUnit("northWest");

        CaseDetails caseDetails = new CaseDetails();
        caseDetails.setCaseId("1000-1000-1000-1000");
        caseDetails.setCaseData(coreCaseData);

        CreateEvent submittedCase = new CreateEvent();
        submittedCase.setCaseDetails(caseDetails);

        Map<String, String> templateVars = getTemplateVars("North West Regional Divorce Centre");

        doNothing().when(emailService).sendSubmissionNotificationEmail(anyObject(), eq(templateVars));

        mvc.perform(post(PETITION_SUBMITTED_URL)
            .content(ObjectMapperTestUtil.convertObjectToJsonString(submittedCase))
            .contentType(MediaType.APPLICATION_JSON_UTF8));

        verify(emailService).sendSubmissionNotificationEmail(anyObject(), eq(templateVars));
        verifyNoMoreInteractions(emailService);
    }

    public void givenErrorCallbackIsReceivedFromCCD_thenValidateTheRequest_ExpectToReturnErrors() throws Exception {
        CreateEvent submittedCase = new CreateEvent();
        CaseDetails caseDetails = new CaseDetails();
        submittedCase.setCaseDetails(caseDetails);
        
        List<String> errors = new ArrayList<>();
        errors.add("error");

        ValidationResponse validationResponse = new ValidationResponse();
        validationResponse.setValidationStatus("failed");
        validationResponse.setErrors(errors);

        when(validationService.validateCoreCaseData(null)).thenReturn(validationResponse);

        mvc.perform(post(ADD_PDF_URL)
            .content(ObjectMapperTestUtil.convertObjectToJsonString(submittedCase))
            .contentType(MediaType.APPLICATION_JSON_UTF8));

        verify(validationService).validateCoreCaseData(null);
        verify(updateService, never()).addPdf(anyObject(), eq(null));
    }

    @Test
    public void givenWarningsCallbackIsReceivedFromCCD_thenValidateTheRequest_ExpectToReturnErrors() throws Exception {
        CreateEvent submittedCase = new CreateEvent();
        CaseDetails caseDetails = new CaseDetails();
        submittedCase.setCaseDetails(caseDetails);
        
        List<String> warnings = new ArrayList<>();
        warnings.add("warning");

        ValidationResponse validationResponse = new ValidationResponse();
        validationResponse.setValidationStatus("failed");
        validationResponse.setErrors(warnings);

        when(validationService.validateCoreCaseData(null)).thenReturn(validationResponse);

        mvc.perform(post(ADD_PDF_URL)
            .content(ObjectMapperTestUtil.convertObjectToJsonString(submittedCase))
            .contentType(MediaType.APPLICATION_JSON_UTF8));

        verify(validationService).validateCoreCaseData(null);
        verify(updateService, never()).addPdf(anyObject(), eq(null));
    }

    private Map<String, String> getTemplateVars(String divorceUnit) {
        Map<String, String> templateVars = new HashMap<>();
        
        templateVars.put("email address", "example@email.com");
        templateVars.put("first name",    null);
        templateVars.put("last name",     null);
        templateVars.put("RDC name",      divorceUnit);
        templateVars.put("CCD reference", "1000-1000-1000-1000");

        return templateVars;
    }
}
