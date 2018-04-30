package uk.gov.hmcts.reform.divorce.transformservice.controller;

import feign.FeignException;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.reform.divorce.CaseProgressionApplication;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.DivorceSession;
import uk.gov.hmcts.reform.divorce.transformservice.service.SubmissionService;
import uk.gov.hmcts.reform.divorce.transformservice.service.UpdateService;

import java.io.File;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.time.format.DateTimeParseException;

import static org.hamcrest.Matchers.is;
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
@WebMvcTest(CcdSubmissionController.class)
@ContextConfiguration(classes = CaseProgressionApplication.class)
public class CcdSubmissionControllerTest {

    private static final String SUBMIT_URL = "/transformationapi/version/1/submit";

    @Autowired
    private WebApplicationContext applicationContext;

    @MockBean
    private SubmissionService submissionService;

    @MockBean
    private UpdateService updateService;

    private MockMvc mvc;

    private String requestContent;

    @Before
    public void setUp() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(applicationContext).build();

        requestContent = FileUtils.readFileToString(new File(getClass()
            .getResource("/fixtures/divorce/submit-request-body.json").toURI()), Charset.defaultCharset());
    }

    @Test
    public void shouldReturnCaseIdWhenDivorceSessionDataIsSubmittedToCCD() throws Exception {
        final String jwt = "Bearer hgsdja87wegqeuf...";
        final String petitionerFirstName = "Danny";
        final Long caseId = 123567L;
        final DivorceSession divorceSession = new DivorceSession();
        divorceSession.setPetitionerFirstName(petitionerFirstName);

        when(submissionService.submit(eq(divorceSession), eq(jwt))).thenReturn(caseId);

        mvc.perform(post(SUBMIT_URL)
            .content(requestContent)
            .header("Authorization", jwt)
            .contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(status()
            .isOk())
            .andExpect(jsonPath("$.caseId", is(caseId.intValue())))
            .andExpect(jsonPath("$.status", is("success")));

        verify(submissionService).submit(eq(divorceSession), eq(jwt));
        verifyNoMoreInteractions(submissionService);
    }

    @Test
    public void shouldReturnErrorInResponseBodyWhenHttpClientErrorExceptionIsThrown() throws Exception {
        final String errorMessage = "error-message HttpClientErrorException";
        final String genericExceptionMessage = "Request Id : 123 and Exception message : "
            + "error-message HttpClientErrorException, Exception response body: exception body";
        HttpClientErrorException exception = mock(HttpClientErrorException.class);
        when(exception.getMessage()).thenReturn(errorMessage);
        when(exception.getResponseBodyAsString()).thenReturn("exception body");

        testExceptionIsHandledCorrectly(errorMessage, genericExceptionMessage, exception);
    }

    @Test
    public void shouldReturnErrorInResponseBodyWhenHttpServerErrorExceptionThrown() throws Exception {
        final String errorMessage = "error-message HttpServerErrorException";
        final String genericExceptionMessage = "Request Id : 123 and Exception message : "
            + "error-message HttpServerErrorException, Exception response body: exception body";
        HttpServerErrorException exception = mock(HttpServerErrorException.class);
        when(exception.getMessage()).thenReturn(errorMessage);
        when(exception.getResponseBodyAsString()).thenReturn("exception body");

        testExceptionIsHandledCorrectly(errorMessage, genericExceptionMessage, exception);
    }

    @Test
    public void shouldReturnErrorInResponseBodyWhenResourceAccessExceptionIsThrown() throws Exception {
        final String errorMessage = "error-message caught ResourceAccessException";
        final String genericExceptionMessage = "Request Id : {0} and ResourceAccessException message : {1}";
        RuntimeException exception = mock(ResourceAccessException.class);

        testExceptionIsHandledCorrectly(errorMessage, genericExceptionMessage, exception);
    }

    @Test
    public void shouldReturnErrorInResponseBodyWhenDateTimeParseExceptionIsThrown() throws Exception {
        final String errorMessage = "error-message caught Exception";
        final String genericExceptionMessage = "Request Id : {0} and Exception message : {1}";
        RuntimeException exception = mock(DateTimeParseException.class);

        testExceptionIsHandledCorrectly(errorMessage, genericExceptionMessage, exception);
    }

    @Test
    public void shouldReturnErrorInResponseBodyWhenFeignExceptionIsThrown() throws Exception {
        final String errorMessage = "error-message caught Exception";
        final String genericExceptionMessage = "Request Id : {0} and Exception message : {1}";
        RuntimeException exception = mock(FeignException.class);

        testExceptionIsHandledCorrectly(errorMessage, genericExceptionMessage, exception);
    }

    private void testExceptionIsHandledCorrectly(String errorMessage, String genericExceptionMessage,
                                                 RuntimeException exception) throws Exception {
        final String exceptionMessage = MessageFormat.format(genericExceptionMessage, "123", errorMessage);
        final String jwt = "Bearer hgsdja87wegqeuf...";
        final String petitionerFirstName = "Danny";

        DivorceSession divorceSession = new DivorceSession();
        divorceSession.setPetitionerFirstName(petitionerFirstName);

        when(exception.getMessage()).thenReturn(errorMessage);

        doThrow(exception).when(submissionService).submit(eq(divorceSession), eq(jwt));

        mvc.perform(post(SUBMIT_URL)
            .content(requestContent)
            .header("requestId", "123")
            .header("Authorization", jwt)
            .contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(status()
            .isOk())
            .andExpect(jsonPath("$.error", is(exceptionMessage)))
            .andExpect(jsonPath("$.status", is("error")));

        verify(submissionService).submit(eq(divorceSession), eq(jwt));
        verify(exception).getMessage();
        verifyNoMoreInteractions(submissionService);
    }

}
