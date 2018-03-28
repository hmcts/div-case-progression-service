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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.reform.divorce.CaseProgressionApplication;
import uk.gov.hmcts.reform.divorce.errorhandler.JwtParsingException;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.DivorceEventSession;
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
public class CcdSubmissionControllerUpdateTest {

    private static final String UPDATE_URL = "/transformationapi/version/1/updateCase";

    @Autowired
    private WebApplicationContext applicationContext;

    @MockBean
    private UpdateService updateService;

    @MockBean
    private SubmissionService submissionService;

    private MockMvc mvc;

    private String requestContent;

    @Before
    public void setUp() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(applicationContext).build();

        requestContent = FileUtils.readFileToString(new File(getClass()
            .getResource("/fixtures/divorce/update-request-body.json").toURI()), Charset.defaultCharset());
    }

    @Test
    public void shouldReturnCaseIdWhenDivorceSessionDataIsUpdatedToCCD() throws Exception {
        final String jwt = "Bearer hgsdja87wegqeuf...";
        final Long caseId = 123567L;
        final DivorceSession divorceSession = new DivorceSession();
        final DivorceEventSession divorceEventSession = new DivorceEventSession();

        divorceSession.setPetitionerFirstName("Dan");

        divorceEventSession.setEventData(divorceSession);
        divorceEventSession.setEventId("paymentMade");

        when(updateService.update(eq(caseId), eq(divorceEventSession), eq(jwt))).thenReturn(caseId);

        mvc.perform(post(UPDATE_URL + "/" + caseId)
            .content(requestContent)
            .header("Authorization", jwt)
            .contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(status()
            .isOk())
            .andExpect(jsonPath("$.caseId", is(caseId.intValue())))
            .andExpect(jsonPath("$.status", is("success")));

        verify(updateService).update(eq(caseId), eq(divorceEventSession), eq(jwt));
        verifyNoMoreInteractions(updateService);
    }

    @Test
    public void shouldReturnErrorInResponseBodyWhenJwtParsingExceptionIsThrown() throws Exception {
        final String errorMessage = "error-message JwtParsingException";
        final String genericExceptionMessage = "Request Id : {0} and Exception message : {1}";
        final String exceptionMessage = MessageFormat.format(genericExceptionMessage, "123", errorMessage);
        final String jwt = "Bearer hgsdja87wegqeuf...";
        final Long caseId = 123567L;
        final DivorceSession divorceSession = new DivorceSession();
        final DivorceEventSession divorceEventSession = new DivorceEventSession();

        divorceSession.setPetitionerFirstName("Dan");

        divorceEventSession.setEventData(divorceSession);
        divorceEventSession.setEventId("paymentMade");

        JwtParsingException exception = mock(JwtParsingException.class);

        when(exception.getMessage()).thenReturn(errorMessage);

        doThrow(exception).when(updateService).update(eq(caseId), eq(divorceEventSession), eq(jwt));

        mvc.perform(post(UPDATE_URL + "/" + caseId)
            .content(requestContent)
            .header("requestId", "123")
            .header("Authorization", jwt)
            .contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(status()
            .isOk())
            .andExpect(jsonPath("$.error", is(exceptionMessage)))
            .andExpect(jsonPath("$.status", is("error")));

        verify(updateService).update(eq(caseId), eq(divorceEventSession), eq(jwt));
        verify(exception).getMessage();
        verifyNoMoreInteractions(updateService);
    }

    @Test
    public void shouldReturnErrorInResponseBodyWhenHttpServerErrorExceptionThrown() throws Exception {
        final String errorMessage = "error-message HttpServerErrorException";
        final String message = "Request Id : 123 and Exception message : error-message HttpServerErrorException, "
            + "Exception response body: exception body";
        final String exceptionMessage = MessageFormat.format(message, "123", errorMessage, "exception body");
        final String jwt = "Bearer hgsdja87wegqeuf...";
        final Long caseId = 123567L;
        final DivorceSession divorceSession = new DivorceSession();
        final DivorceEventSession divorceEventSession = new DivorceEventSession();

        divorceSession.setPetitionerFirstName("Dan");

        divorceEventSession.setEventData(divorceSession);
        divorceEventSession.setEventId("paymentMade");

        HttpServerErrorException exception = mock(HttpServerErrorException.class);

        when(exception.getMessage()).thenReturn(errorMessage);
        when(exception.getResponseBodyAsString()).thenReturn("exception body");

        doThrow(exception).when(updateService).update(eq(caseId), eq(divorceEventSession), eq(jwt));

        mvc.perform(post(UPDATE_URL + "/" + caseId)
            .content(requestContent)
            .header("requestId", "123")
            .header("Authorization", jwt)
            .contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(status()
            .isOk())
            .andExpect(jsonPath("$.error", is(exceptionMessage)))
            .andExpect(jsonPath("$.status", is("error")));

        verify(updateService).update(eq(caseId), eq(divorceEventSession), eq(jwt));
        verify(exception).getMessage();
        verify(exception).getResponseBodyAsString();
        verifyNoMoreInteractions(updateService);
    }

    @Test
    public void shouldReturnErrorInResponseBodyWhenResourceAccessExceptionIsThrown() throws Exception {
        final String errorMessage = "error-message caught ResourceAccessException";
        final String genericExceptionMessage = "Request Id : {0} and ResourceAccessException message : {1}";
        final String exceptionMessage = MessageFormat.format(genericExceptionMessage, "123", errorMessage);
        final String jwt = "Bearer hgsdja87wegqeuf...";

        final Long caseId = 123567L;
        final DivorceSession divorceSession = new DivorceSession();
        final DivorceEventSession divorceEventSession = new DivorceEventSession();

        divorceSession.setPetitionerFirstName("Dan");

        divorceEventSession.setEventData(divorceSession);
        divorceEventSession.setEventId("paymentMade");

        ResourceAccessException exception = mock(ResourceAccessException.class);

        when(exception.getMessage()).thenReturn(errorMessage);

        doThrow(exception).when(updateService).update(eq(caseId), eq(divorceEventSession), eq(jwt));

        mvc.perform(post(UPDATE_URL + "/" + caseId)
            .content(requestContent)
            .header("requestId", "123")
            .header("Authorization", jwt)
            .contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(status()
            .isOk())
            .andExpect(jsonPath("$.error", is(exceptionMessage)))
            .andExpect(jsonPath("$.status", is("error")));

        verify(updateService).update(eq(caseId), eq(divorceEventSession), eq(jwt));
        verify(exception).getMessage();
        verifyNoMoreInteractions(updateService);
    }

    @Test
    public void shouldReturnErrorInResponseBodyWhenDateTimeParseExceptionIsThrown() throws Exception {
        final String errorMessage = "error-message caught Exception";
        final String genericExceptionMessage = "Request Id : {0} and Exception message : {1}";
        final String exceptionMessage = MessageFormat.format(genericExceptionMessage, "123", errorMessage);
        final String jwt = "Bearer hgsdja87wegqeuf...";

        final Long caseId = 123567L;
        final DivorceSession divorceSession = new DivorceSession();
        final DivorceEventSession divorceEventSession = new DivorceEventSession();
        divorceSession.setPetitionerFirstName("Dan");

        divorceEventSession.setEventData(divorceSession);
        divorceEventSession.setEventId("paymentMade");

        DateTimeParseException exception = mock(DateTimeParseException.class);

        when(exception.getMessage()).thenReturn(errorMessage);

        doThrow(exception).when(updateService).update(eq(caseId), eq(divorceEventSession), eq(jwt));

        mvc.perform(post(UPDATE_URL + "/" + caseId)
            .content(requestContent)
            .header("requestId", "123")
            .header("Authorization", jwt)
            .contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(status()
            .isOk())
            .andExpect(jsonPath("$.error", is(exceptionMessage)))
            .andExpect(jsonPath("$.status", is("error")));

        verify(updateService).update(eq(caseId), eq(divorceEventSession), eq(jwt));
        verify(exception).getMessage();
        verifyNoMoreInteractions(updateService);
    }
}
