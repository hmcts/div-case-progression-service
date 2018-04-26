package uk.gov.hmcts.reform.divorce.transformservice.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.reform.divorce.idam.models.UserDetails;
import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.CaseEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.CreateEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CaseDataContent;

@Component
@Slf4j
public class UpdateCcdEventClient implements CcdEventClient {

    @Autowired
    private CcdClientConfiguration ccdClientConfiguration;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private TransformationHttpEntityFactory httpEntityFactory;

    @Override
    public CreateEvent startEvent(UserDetails userDetails, String encodedJwt, Long caseId, String eventId) {

        HttpEntity<String> httpEntity = httpEntityFactory.createRequestEntityForCcdGet(encodedJwt);

        String url = ccdClientConfiguration.getStartEventUrl(userDetails, caseId, eventId);
        log.info("Formatted url start case event {} ", url);
        return restTemplate.exchange(url, HttpMethod.GET, httpEntity, CreateEvent.class).getBody();
    }

    @Override
    public CaseEvent createCaseEvent(UserDetails userDetails, String encodedJwt, Long caseId, CaseDataContent caseDataContent) {

        HttpEntity<CaseDataContent> httpEntity = httpEntityFactory.createRequestEntityForSubmitCase(encodedJwt,
            caseDataContent);

        String url = ccdClientConfiguration.getCreateCaseEventUrl(userDetails, caseId);
        log.info("Formatted url create case event {} ", url);
        return restTemplate.exchange(url, HttpMethod.POST, httpEntity, CaseEvent.class).getBody();
    }
}
