package uk.gov.hmcts.reform.divorce.notifications.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.divorce.draftservice.client.EmailClient;
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

    @Autowired
    private EmailClient emailClient;

    @Value("#{${uk.gov.notify.email.templates}}")
    private Map<String, String> emailTemplates;

    @Value("#{${uk.gov.notify.email.template.vars}}")
    private Map<String, Map<String, String>> emailTemplateVars;

    public void sendSaveDraftConfirmationEmail(String destinationAddress) {
        EmailToSend emailToSend = generateEmail(destinationAddress, EmailTemplateNames.SAVE_DRAFT.name());
        sendEmail(emailToSend, "draft saved confirmation");
    }

    public void sendRejectionNotificationEmail(String destinationAddress) {
        EmailToSend emailToSend = generateEmail(destinationAddress, EmailTemplateNames.REJECTION.name());
        sendEmail(emailToSend, "rejection notification");
    }

    private EmailToSend generateEmail(String destinationAddress,
                                      String templateName) {
        String              referenceId    = UUID.randomUUID().toString();
        String              templateId     = emailTemplates.get(templateName);
        Map<String, String> templateFields = emailTemplateVars.get(templateName);

        return new EmailToSend(destinationAddress, templateId, templateFields, referenceId);
    }

    private void sendEmail(EmailToSend emailToSend,
                           String      emailDescription) {
        try {
            log.debug("Attempting to send {} email. Reference ID: {}", emailDescription, emailToSend.getReferenceId());
            emailClient.sendEmail(
                emailToSend.getTemplateId(),
                emailToSend.getEmailAddress(),
                emailToSend.getTemplateFields(),
                emailToSend.getReferenceId()
            );
            log.info("Sending email success. Reference ID: {}", emailToSend.getReferenceId());
        } catch (NotificationClientException e) {
            log.warn("Failed to send email. Reference ID: {}. Reason:", emailToSend.getReferenceId(), e);
        }
    }
}
