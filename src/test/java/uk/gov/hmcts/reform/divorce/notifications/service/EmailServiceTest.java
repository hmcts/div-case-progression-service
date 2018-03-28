package uk.gov.hmcts.reform.divorce.notifications.service;

import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.reform.divorce.notifications.domain.EmailTemplateNames;
import uk.gov.service.notify.NotificationClientApi;
import uk.gov.service.notify.NotificationClientException;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class EmailServiceTest {
    private NotificationClientApi client;
    private String emailAddress;
    private Map<String, String> templates;
    private EmailService emailService;

    @Before
    public void init() {
        client = mock(NotificationClientApi.class);
        emailAddress = "simulate-delivered@notifications.service.gov.uk";
        templates = new HashMap<>();
        templates.put(EmailTemplateNames.SAVE_DRAFT.name(), "testTemplate123");
        emailService = new EmailService(client, templates);
    }

    @Test
    public void successfullySendEmail() throws NotificationClientException {
        emailService.sendSaveDraftConfirmationEmail(emailAddress);

        verify(client).sendEmail(
            eq(templates.get(EmailTemplateNames.SAVE_DRAFT.name())),
            eq(emailAddress),
            eq(null),
            anyString());
    }

    @Test
    public void failToSendEmail() throws NotificationClientException {
        doThrow(new NotificationClientException(new Exception("Exception inception")))
            .when(client).sendEmail(anyString(), anyString(), eq(null), anyString());

        emailService.sendSaveDraftConfirmationEmail(emailAddress);

        verify(client).sendEmail(
            eq(templates.get(EmailTemplateNames.SAVE_DRAFT.name())),
            eq(emailAddress),
            eq(null),
            anyString());
    }
}
