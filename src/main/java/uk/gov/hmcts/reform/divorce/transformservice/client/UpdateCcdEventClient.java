package uk.gov.hmcts.reform.divorce.transformservice.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.CaseEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.CreateEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CaseDataContent;

@Component
public class UpdateCcdEventClient implements CcdEventClient {

    private static final Logger log = LoggerFactory.getLogger(UpdateCcdEventClient.class);
    @Autowired
    private CcdClientConfiguration ccdClientConfiguration;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private TransformationHttpEntityFactory httpEntityFactory;

    @Override
    public CreateEvent startEvent(String encodedJwt, Long caseId, String eventId) {

        HttpEntity<String> httpEntity = httpEntityFactory.createRequestEntityForCcdGet(encodedJwt);

        String url = ccdClientConfiguration.getStartEventUrl(encodedJwt, caseId, eventId);
        log.info("Formatted url start case event {} ", url);
        return restTemplate.exchange(url, HttpMethod.GET, httpEntity, CreateEvent.class).getBody();
    }

    @Override
    public CaseEvent createCaseEvent(String encodedJwt, Long caseId, CaseDataContent caseDataContent) {

        HttpEntity<CaseDataContent> httpEntity = httpEntityFactory.createRequestEntityForSubmitCase(encodedJwt,
            caseDataContent);

        String url = ccdClientConfiguration.getCreateCaseEventUrl(encodedJwt, caseId);
        log.info("Formatted url create case event {} ", url);
        return restTemplate.exchange(url, HttpMethod.POST, httpEntity, CaseEvent.class).getBody();
    }
}
