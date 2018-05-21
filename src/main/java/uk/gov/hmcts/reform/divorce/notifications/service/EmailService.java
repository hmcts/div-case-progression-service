package uk.gov.hmcts.reform.divorce.notifications.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.divorce.draftservice.client.EmailClient;
import uk.gov.hmcts.reform.divorce.notifications.domain.EmailTemplateNames;
import uk.gov.hmcts.reform.divorce.notifications.domain.EmailToSend;
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
        String referenceId = UUID.randomUUID().toString();
        EmailToSend emailToSend = new EmailToSend(destinationAddress,
            emailTemplates.get(EmailTemplateNames.SAVE_DRAFT.name()),
            emailTemplateVars.get(EmailTemplateNames.SAVE_DRAFT.name()),
            referenceId);
        try {
            log.debug("Attempting to send email. Reference ID: {}", referenceId);
            sendEmail(emailToSend);
            log.info("Sending email success. Reference ID: {}", referenceId);
        } catch (NotificationClientException e) {
            log.warn("Failed to send email. Reference ID: {}. Reason:", referenceId, e);
        }
    }

    public void sendSubmissionConfirmationEmail(String destinationEmailAddress) {
        String referenceId = UUID.randomUUID().toString();
        EmailToSend emailToSend = new EmailToSend(destinationEmailAddress,
            emailTemplates.get(EmailTemplateNames.APPLIC_SUBMISSION.name()),
            emailTemplateVars.get(EmailTemplateNames.APPLIC_SUBMISSION.name()),
            referenceId);
        try {
            log.debug("Attempting to send submission confirmation email. Reference ID: {}", referenceId);
            sendEmail(emailToSend);
            log.info("Sending email success. Reference ID: {}", referenceId);
        } catch (NotificationClientException e) {
            log.warn("Failed to send email. Reference ID: {}. Reason:", referenceId, e);
        }
    }

    private void sendEmail(EmailToSend emailToSend) throws NotificationClientException {
        emailClient.sendEmail(
            emailToSend.getTemplateId(),
            emailToSend.getEmailAddress(),
            emailToSend.getTemplateFields(),
            emailToSend.getReferenceId()
        );
    }
}
