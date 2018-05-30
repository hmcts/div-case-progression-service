package uk.gov.hmcts.reform.divorce.transformservice.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.reform.divorce.idam.models.UserDetails;
import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.CreateEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.SubmitEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CaseDataContent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.DivorceSession;

@Component
@Slf4j
public class SubmitCcdClient {

    @Autowired
    private CcdClientConfiguration ccdClientConfiguration;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private TransformationHttpEntityFactory httpEntityFactory;


    public CreateEvent createCase(UserDetails userDetails, String encodedJwt, DivorceSession divorceSessionData) {

        HttpEntity<String> httpEntity = httpEntityFactory.createRequestEntityForCcdGet(encodedJwt);

        String url = ccdClientConfiguration.getCreateCaseUrl(
                userDetails.getId(), divorceSessionData.getHelpWithFeesNeedHelp());
        log.info("Formatted url create case {} ", url);
        return restTemplate.exchange(url, HttpMethod.GET, httpEntity, CreateEvent.class).getBody();
    }

    public SubmitEvent submitCase(UserDetails userDetails, String userToken, CaseDataContent caseDataContent) {

        HttpEntity<CaseDataContent> httpEntity = httpEntityFactory.createRequestEntityForSubmitCase(userToken,
            caseDataContent);

        String url = ccdClientConfiguration.getSubmitCaseUrl(userDetails.getId());
        log.info("Formatted url submit case {} ", url);
        return restTemplate.exchange(url, HttpMethod.POST, httpEntity, SubmitEvent.class).getBody();
    }
}
