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

import java.util.List;

@Component
@Slf4j
public class SubmitCcdClient implements CcdClient {

    @Autowired
    private CcdClientConfiguration ccdClientConfiguration;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private TransformationHttpEntityFactory httpEntityFactory;

    @Override
    public CreateEvent createCase(UserDetails userDetails, String encodedJwt) {

        HttpEntity<String> httpEntity = httpEntityFactory.createRequestEntityForCcdGet(encodedJwt);

        String url = ccdClientConfiguration.getCreateCaseUrl(userDetails.getId());
        log.info("Formatted url create case {} ", url);
        return restTemplate.exchange(url, HttpMethod.GET, httpEntity, CreateEvent.class).getBody();
    }

    @Override
    public SubmitEvent submitCase(UserDetails userDetails, String userToken, CaseDataContent caseDataContent) {

        HttpEntity<CaseDataContent> httpEntity = httpEntityFactory.createRequestEntityForSubmitCase(userToken,
            caseDataContent);

        String url = ccdClientConfiguration.getSubmitCaseUrl(userDetails.getId());
        log.info("Formatted url submit case {} ", url);
        return restTemplate.exchange(url, HttpMethod.POST, httpEntity, SubmitEvent.class).getBody();
    }

    @Override
    public List<CaseDataContent> getCase(String userToken, String queryParams) {
        HttpEntity<String> httpEntity = httpEntityFactory.createRequestEntityForCcdGetCaseDataContent(userToken);
        String url = ccdClientConfiguration.getCases(userToken, queryParams);
        log.info("Formatted url submit case {} ", url);
        return restTemplate.exchange(url, HttpMethod.GET, httpEntity, List.class).getBody();
    }
}
