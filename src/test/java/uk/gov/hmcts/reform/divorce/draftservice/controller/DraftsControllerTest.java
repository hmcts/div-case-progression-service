package uk.gov.hmcts.reform.divorce.draftservice.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.reform.divorce.CaseProgressionApplication;
import uk.gov.hmcts.reform.divorce.draftservice.exception.DraftStoreUnavailableException;
import uk.gov.hmcts.reform.divorce.draftservice.service.DraftsService;
import uk.gov.hmcts.reform.divorce.notifications.service.EmailService;

import java.io.File;

import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(DraftsController.class)
@ContextConfiguration(classes = CaseProgressionApplication.class)
public class DraftsControllerTest {

    private static final String DRAFTS_URL = "/draftsapi/version/1";
    private static final String JWT = "Bearer hgsdja87wegqeuf...";
    @MockBean
    private DraftsService draftsService;
    @MockBean
    private EmailService emailService;
    @Autowired
    private WebApplicationContext applicationContext;
    private MockMvc mvc;

    private JsonNode requestContent;

    @Before
    public void setUp() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(applicationContext).build();

        ObjectMapper objectMapper = new ObjectMapper();
        requestContent = objectMapper.readTree(new File(getClass()
            .getResource("/fixtures/divorce/submit-request-body.json").toURI()));
    }

    @Test
    public void shouldReturnNoContentWhenSavingADraftByExplicitCallWithValidEmailProvided() throws Exception {
        String notificationEmail = "simulate-delivered@notifications.service.gov.uk";
        mvc.perform(put(DRAFTS_URL + "?notificationEmail=" + notificationEmail)
            .content(requestContent.toString())
            .header("Authorization", JWT)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isNoContent());

        verify(emailService).sendSaveDraftConfirmationEmail(eq(notificationEmail));
    }

    @Test
    public void shouldReturnNoContentWhenSavingADraftByExplicitCallWithBlankEmail() throws Exception {
        String notificationEmail = "";
        mvc.perform(put(DRAFTS_URL + "?notificationEmail=" + notificationEmail)
            .content(requestContent.toString())
            .header("Authorization", JWT)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isNoContent());

        verify(emailService, never()).sendSaveDraftConfirmationEmail(eq(notificationEmail));
    }

    @Test
    public void shouldReturnBadRequestWhenSavingADraftByExplicitCallWithInvalidEmail() throws Exception {
        String notificationEmail = "InvalidEmailAddress";
        mvc.perform(put(DRAFTS_URL + "?notificationEmail=" + notificationEmail)
            .content(requestContent.toString())
            .header("Authorization", JWT)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isBadRequest());

        verify(emailService, never()).sendSaveDraftConfirmationEmail(eq(notificationEmail));
    }

    @Test
    public void shouldReturnNoContentWhenSavingADraftAutomatically() throws Exception {
        mvc.perform(put(DRAFTS_URL)
            .content(requestContent.toString())
            .header("Authorization", JWT)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isNoContent());

        verify(emailService, never()).sendSaveDraftConfirmationEmail(anyString());
    }

    @Test
    public void shouldReturnOKAndTheSavedSessionWhenRetrievingADraft() throws Exception {
        when(draftsService.getDraft(JWT)).thenReturn(requestContent);

        mvc.perform(get(DRAFTS_URL)
            .header("Authorization", JWT)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(content().json(requestContent.toString()));
    }

    @Test
    public void shouldReturnNotFoundWhenTheDraftDoesNotExist() throws Exception {
        mvc.perform(get(DRAFTS_URL)
            .header("Authorization", JWT)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isNotFound())
            .andExpect(content().string(""));
    }

    @Test
    public void shouldReturnNoContentWhenDeletingADraft() throws Exception {
        mvc.perform(delete(DRAFTS_URL)
            .header("Authorization", JWT)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isNoContent());
    }

    @Test
    public void shouldReturnErrorResponseWhenDraftBodyIsInvalidJson() throws Exception {
        HttpClientErrorException httpClientErrorException =
            new HttpClientErrorException(HttpStatus.NOT_FOUND, "Not found");

        doThrow(httpClientErrorException).when(draftsService).saveDraft(anyString(), any());

        mvc.perform(put(DRAFTS_URL)
            .content(requestContent.toString())
            .header("Authorization", JWT)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status", is(404)))
            .andExpect(jsonPath("$.error", is("Not Found")))
            .andExpect(jsonPath("path", is("/draftsapi/version/1")));

    }

    @Test
    public void shouldReturn503WhenServiceThrowsDraftStoreUnavailableException() throws Exception {
        doThrow(DraftStoreUnavailableException.class).when(draftsService).saveDraft(anyString(), any());

        mvc.perform(put(DRAFTS_URL)
            .content(requestContent.toString())
            .header("Authorization", JWT)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isServiceUnavailable());

    }

}
