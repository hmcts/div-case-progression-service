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
import uk.gov.hmcts.reform.divorce.testutils.ObjectMapperTestUtil;
import uk.gov.hmcts.reform.divorce.transformservice.client.pdf.PdfGeneratorException;
import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.CreateEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CaseDetails;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CoreCaseData;
import uk.gov.hmcts.reform.divorce.transformservice.domain.transformservice.CCDCallbackResponse;
import uk.gov.hmcts.reform.divorce.transformservice.service.UpdateService;

import java.io.File;
import java.nio.charset.Charset;
import java.text.MessageFormat;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
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

    @Autowired
    private WebApplicationContext applicationContext;

    @MockBean
    private UpdateService updateService;

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

        when(updateService.addPdf(submittedCase)).thenReturn(coreCaseData);

        MvcResult result = mvc.perform(post(ADD_PDF_URL)
            .content(ObjectMapperTestUtil.convertObjectToJsonString(submittedCase))
            .contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk()).andReturn();

        CCDCallbackResponse response =
            ObjectMapperTestUtil.convertJsonToObject(
                result.getResponse().getContentAsByteArray(),
                CCDCallbackResponse.class);

        assertEquals(coreCaseData, response.getData());

        verify(updateService).addPdf(submittedCase);
        verifyNoMoreInteractions(updateService);
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

        doThrow(exception).when(updateService).addPdf(submittedCase);

        ResultActions perform = mvc.perform(post(ADD_PDF_URL)
            .content(requestContent)
            .header("requestId", "123")
            .contentType(MediaType.APPLICATION_JSON_UTF8));
        perform
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors[0]", is(exceptionMessage)));

        verify(updateService).addPdf(eq(submittedCase));
        verify(exception).getMessage();
        verifyNoMoreInteractions(updateService);
    }

}
