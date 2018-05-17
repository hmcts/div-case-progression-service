package uk.gov.hmcts.reform.divorce.transformservice.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.divorce.common.JwtFactory;
import uk.gov.hmcts.reform.divorce.transformservice.domain.Jwt;

import java.util.Arrays;
import java.util.Optional;

@Component
public class BaseCcdClientConfiguration implements CcdClientConfiguration {

    //citizen urls
    private static final String CREATE_CASE_URL_FORMAT =
        "%s/citizens/%d/jurisdictions/%s/case-types/%s/event-triggers/create/token?ignore-warning=true";
    private static final String SUBMIT_CASE_URL_FORMAT =
        "%s/citizens/%d/jurisdictions/%s/case-types/%s/cases?ignore-warning=true";
    private static final String START_CASE_EVENT_URL_FORMAT =
        "%s/citizens/%d/jurisdictions/%s/case-types/%s/cases/%d/event-triggers/%s/"
            + "token?ignore-warning=true";
    private static final String CREATE_CASE_EVENT_URL_FORMAT =
        "%s/citizens/%d/jurisdictions/%s/case-types/%s/cases/%d/events?ignore-warning=true";
    //update caseworker urls
    private static final String START_CASE_EVENT_URL_CASEWORKER_FORMAT =
        "%s/caseworkers/%d/jurisdictions/%s/case-types/%s/cases/%d/event-triggers/%s/"
            + "token?ignore-warning=true";
    private static final String CREATE_CASE_EVENT_URL_CASEWORKER_FORMAT =
        "%s/caseworkers/%d/jurisdictions/%s/case-types/%s/cases/%d/events?ignore-warning=true";
    private static final String RETRIEVE_CASE_IDS_URL_FORMAT =
        "%s/citizens/%d/jurisdictions/%s/case-types/%s/cases?queryParameters=%s";

    @Value("${ccd.caseworker.role}")
    private String caseworkerRole = "caseworker-divorce";

    @Value("${ccd.jurisdictionid}")
    private String jurisdictionId;

    @Value("${ccd.casetypeid}")
    private String caseTypeId;

    @Value("${ccd.caseDataStore.baseUrl}")
    private String ccdBaseUrl;

    @Autowired
    private JwtFactory jwtFactory;

    @Override
    public String getCreateCaseUrl(Long jwtId) {
        return String.format(CREATE_CASE_URL_FORMAT, ccdBaseUrl, jwtId, jurisdictionId, caseTypeId);
    }

    @Override
    public String getSubmitCaseUrl(Long jwtId) {
        return String.format(SUBMIT_CASE_URL_FORMAT, ccdBaseUrl, jwtId, jurisdictionId, caseTypeId);
    }

    @Override
    public String getStartEventUrl(String encodedJwtToken, Long caseId, String eventId) {
        Jwt jwt = getJwt(encodedJwtToken);
        String url =
            isCaseWorkerUser(jwt.getData()) ? START_CASE_EVENT_URL_CASEWORKER_FORMAT : START_CASE_EVENT_URL_FORMAT;
        return String.format(url, ccdBaseUrl, jwt.getId(), jurisdictionId, caseTypeId, caseId,
            eventId);
    }

    @Override
    public String getCreateCaseEventUrl(String encodedJwtToken, Long caseId) {
        Jwt jwt = getJwt(encodedJwtToken);
        String url =
            isCaseWorkerUser(jwt.getData()) ? CREATE_CASE_EVENT_URL_CASEWORKER_FORMAT : CREATE_CASE_EVENT_URL_FORMAT;
        return String.format(url, ccdBaseUrl, jwt.getId(), jurisdictionId, caseTypeId, caseId);
    }

    @Override
    public String getCases(Long jwtId, String queryParams) {
        return String.format(RETRIEVE_CASE_IDS_URL_FORMAT, ccdBaseUrl, jwtId, jurisdictionId, caseTypeId, queryParams);
    }

    private boolean isCaseWorkerUser(String group) {
        return Optional.ofNullable(group).isPresent()
            && Arrays.stream(group.split(","))
            .anyMatch(role -> role.equalsIgnoreCase(caseworkerRole));
    }

    private Jwt getJwt(String encodedJwtToken) {
        return jwtFactory.create(encodedJwtToken);
    }
}
