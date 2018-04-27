package uk.gov.hmcts.reform.divorce.transformservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.divorce.idam.models.UserDetails;

@Component
public class BaseCcdClientConfiguration implements CcdClientConfiguration {

    //citizen urls
    private static final String CREATE_CASE_URL_FORMAT =
        "%s/citizens/%s/jurisdictions/%s/case-types/%s/event-triggers/create/token?ignore-warning=true";
    private static final String SUBMIT_CASE_URL_FORMAT =
        "%s/citizens/%s/jurisdictions/%s/case-types/%s/cases?ignore-warning=true";
    private static final String START_CASE_EVENT_URL_FORMAT =
        "%s/citizens/%s/jurisdictions/%s/case-types/%s/cases/%d/event-triggers/%s/"
            + "token?ignore-warning=true";
    private static final String CREATE_CASE_EVENT_URL_FORMAT =
        "%s/citizens/%s/jurisdictions/%s/case-types/%s/cases/%d/events?ignore-warning=true";
    //update caseworker urls
    private static final String START_CASE_EVENT_URL_CASEWORKER_FORMAT =
        "%s/caseworkers/%s/jurisdictions/%s/case-types/%s/cases/%d/event-triggers/%s/"
            + "token?ignore-warning=true";
    private static final String CREATE_CASE_EVENT_URL_CASEWORKER_FORMAT =
        "%s/caseworkers/%s/jurisdictions/%s/case-types/%s/cases/%d/events?ignore-warning=true";

    @Value("${ccd.caseworker.role}")
    private String caseworkerRole = "caseworker-divorce";

    @Value("${ccd.jurisdictionid}")
    private String jurisdictionId;

    @Value("${ccd.casetypeid}")
    private String caseTypeId;

    @Value("${ccd.caseDataStore.baseUrl}")
    private String ccdBaseUrl;

    @Override
    public String getCreateCaseUrl(String userId) {
        return String.format(CREATE_CASE_URL_FORMAT, ccdBaseUrl, userId, jurisdictionId, caseTypeId);
    }

    @Override
    public String getSubmitCaseUrl(String userId) {
        return String.format(SUBMIT_CASE_URL_FORMAT, ccdBaseUrl, userId, jurisdictionId, caseTypeId);
    }

    @Override
    public String getStartEventUrl(UserDetails userDetails, Long caseId, String eventId) {
        String url = isCaseWorkerUser(userDetails) ? START_CASE_EVENT_URL_CASEWORKER_FORMAT : START_CASE_EVENT_URL_FORMAT;
        return String.format(url, ccdBaseUrl, userDetails.getId(), jurisdictionId, caseTypeId, caseId,
            eventId);
    }

    @Override
    public String getCreateCaseEventUrl(UserDetails userDetails, Long caseId) {
        String url = isCaseWorkerUser(userDetails) ? CREATE_CASE_EVENT_URL_CASEWORKER_FORMAT : CREATE_CASE_EVENT_URL_FORMAT;
        return String.format(url, ccdBaseUrl, userDetails.getId(), jurisdictionId, caseTypeId, caseId);
    }

    private boolean isCaseWorkerUser(UserDetails userDetails) {
        return userDetails.getRoles().stream().anyMatch(role -> role.equalsIgnoreCase(caseworkerRole));
    }

}
