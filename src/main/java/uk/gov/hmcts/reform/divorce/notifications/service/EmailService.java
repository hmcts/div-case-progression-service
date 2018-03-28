package uk.gov.hmcts.reform.divorce.notifications.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.divorce.notifications.domain.EmailTemplateNames;
import uk.gov.hmcts.reform.divorce.notifications.domain.EmailToSend;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientApi;
import uk.gov.service.notify.NotificationClientException;

import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class EmailService {

    private NotificationClientApi notificationClient;
    private Map<String, String> emailTemplates;

    @Autowired
    public EmailService(@Value("${uk.gov.notify.api.key}")
                            String apiKey,
                        @Value("#{${uk.gov.notify.email.templates}}")
                            Map<String, String> emailTemplates) {
        this(new NotificationClient(apiKey), emailTemplates);
    }

    EmailService(NotificationClientApi notificationClient,
                 Map<String, String> emailTemplates) {
        this.notificationClient = notificationClient;
        this.emailTemplates = emailTemplates;
    }

    public void sendSaveDraftConfirmationEmail(String destinationAddress) {
        String referenceId = UUID.randomUUID().toString();
        EmailToSend emailToSend = new EmailToSend(destinationAddress,
            emailTemplates.get(EmailTemplateNames.SAVE_DRAFT.name()),
            null,
            referenceId);
        try {
            log.debug("Attempting to send email. Reference ID: {}", referenceId);
            sendEmail(emailToSend);
            log.info("Sending email success. Reference ID: {}", referenceId);
        } catch (NotificationClientException e) {
            log.warn("Failed to send email. Reference ID: {}. Reason:", referenceId, e);
        }
    }

    private void sendEmail(EmailToSend emailToSend) throws NotificationClientException {
        notificationClient.sendEmail(
            emailToSend.getTemplateId(),
            emailToSend.getEmailAddress(),
            emailToSend.getTemplateFields(),
            emailToSend.getReferenceId()
        );
    }
}
