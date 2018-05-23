package uk.gov.hmcts.reform.divorce.transformservice.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.divorce.idam.models.UserDetails;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.YesNoAnswer;

@Component
public class BaseCcdClientConfiguration implements CcdClientConfiguration {

    //citizen urls
    private static final String CREATE_CASE_URL_FORMAT =
        "%s/citizens/%s/jurisdictions/%s/case-types/%s/event-triggers/%s/token?ignore-warning=true";
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

    private static final String CREATE_CASE_EVENT_KEY_WITHOUT_HELP_WITH_FEES = "create";
    private static final String CREATE_CASE_EVENT_KEY_WITH_HELP_WITH_FEES = "hwfCreate";

    private final String caseworkerRole;
    private final String jurisdictionId;
    private final String caseTypeId;
    private final String ccdBaseUrl;

    @Autowired
    public BaseCcdClientConfiguration(@Value("${ccd.caseworker.role}") String caseworkerRole,
                                      @Value("${ccd.jurisdictionid}") String jurisdictionId,
                                      @Value("${ccd.casetypeid}") String caseTypeId,
                                      @Value("${ccd.caseDataStore.baseUrl}")String ccdBaseUrl) {
        this.caseworkerRole = caseworkerRole;
        this.jurisdictionId = jurisdictionId;
        this.caseTypeId = caseTypeId;
        this.ccdBaseUrl = ccdBaseUrl;
    }

    @Override
    public String getCreateCaseUrl(String userId, YesNoAnswer helpWithFeesAnswer) {

        String createCaseCcdEventKey = helpWithFeesAnswer == YesNoAnswer.YES
                ? CREATE_CASE_EVENT_KEY_WITH_HELP_WITH_FEES : CREATE_CASE_EVENT_KEY_WITHOUT_HELP_WITH_FEES;

        return String.format(CREATE_CASE_URL_FORMAT, ccdBaseUrl, userId, jurisdictionId, caseTypeId,
                createCaseCcdEventKey);
    }

    @Override
    public String getSubmitCaseUrl(String userId) {
        return String.format(SUBMIT_CASE_URL_FORMAT, ccdBaseUrl, userId, jurisdictionId, caseTypeId);
    }

    @Override
    public String getStartEventUrl(UserDetails userDetails, Long caseId, String eventId) {
        String url = isCaseWorkerUser(userDetails)
            ? START_CASE_EVENT_URL_CASEWORKER_FORMAT : START_CASE_EVENT_URL_FORMAT;
        return String.format(url, ccdBaseUrl, userDetails.getId(), jurisdictionId, caseTypeId, caseId,
            eventId);
    }

    @Override
    public String getCreateCaseEventUrl(UserDetails userDetails, Long caseId) {
        String url = isCaseWorkerUser(userDetails)
            ? CREATE_CASE_EVENT_URL_CASEWORKER_FORMAT : CREATE_CASE_EVENT_URL_FORMAT;
        return String.format(url, ccdBaseUrl, userDetails.getId(), jurisdictionId, caseTypeId, caseId);
    }

    private boolean isCaseWorkerUser(UserDetails userDetails) {
        return userDetails.getRoles().stream().anyMatch(role -> role.equalsIgnoreCase(caseworkerRole));
    }

}
