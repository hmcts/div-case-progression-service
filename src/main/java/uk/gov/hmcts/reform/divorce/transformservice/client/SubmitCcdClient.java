package uk.gov.hmcts.reform.divorce.transformservice.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.reform.divorce.common.JwtFactory;
import uk.gov.hmcts.reform.divorce.transformservice.domain.Jwt;
import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.CreateEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.SubmitEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CaseDataContent;

@Component
public class SubmitCcdClient implements CcdClient {

    private static final Logger log = LoggerFactory.getLogger(SubmitCcdClient.class);
    @Autowired
    private CcdClientConfiguration ccdClientConfiguration;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private TransformationHttpEntityFactory httpEntityFactory;
    @Autowired
    private JwtFactory jwtFactory;

    @Override
    public CreateEvent createCase(String encodedJwt) {

        HttpEntity<String> httpEntity = httpEntityFactory.createRequestEntityForCcdGet(encodedJwt);

        Jwt jwt = jwtFactory.create(encodedJwt);

        String url = ccdClientConfiguration.getCreateCaseUrl(jwt.getId());
        log.info("Formatted url create case {} ", url);
        return restTemplate.exchange(url, HttpMethod.GET, httpEntity, CreateEvent.class).getBody();
    }

    @Override
    public SubmitEvent submitCase(String userToken, CaseDataContent caseDataContent) {

        HttpEntity<CaseDataContent> httpEntity = httpEntityFactory.createRequestEntityForSubmitCase(userToken,
            caseDataContent);

        Jwt jwt = jwtFactory.create(userToken);

        String url = ccdClientConfiguration.getSubmitCaseUrl(jwt.getId());
        log.info("Formatted url submit case {} ", url);
        return restTemplate.exchange(url, HttpMethod.POST, httpEntity, SubmitEvent.class).getBody();
    }
}
