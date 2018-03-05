package uk.gov.hmcts.reform.divorce.notifications.domain;

import lombok.Value;

import java.util.Map;

@Value
public final class EmailToSend {
    String emailAddress;
    String templateId;
    Map<String, String> templateFields;
    String referenceId;
}
