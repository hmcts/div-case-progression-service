package uk.gov.hmcts.reform.divorce.transformservice.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.CreateEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CaseDataContent;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * TODO: write about the class
 *
 * @author wjtlopez
 */
@Component
public class RetrieveCcdClient {

    private final CcdClientConfiguration ccdClientConfiguration;
    private final RestTemplate restTemplate;
    private final TransformationHttpEntityFactory httpEntityFactory;

    @Autowired
    public RetrieveCcdClient(CcdClientConfiguration ccdClientConfiguration,
                             RestTemplate restTemplate,
                             TransformationHttpEntityFactory httpEntityFactory) {
        this.ccdClientConfiguration = ccdClientConfiguration;
        this.restTemplate = restTemplate;
        this.httpEntityFactory = httpEntityFactory;
    }

    public List<LinkedHashMap> getCase(String userId, String jwt) {
        String url = ccdClientConfiguration.getRetrieveCaseUrl(userId);
        HttpEntity<String> httpEntity = httpEntityFactory.createRequestEntityForCcdGet(jwt);

        return restTemplate.exchange(url, HttpMethod.GET, httpEntity, List.class).getBody();
    }
}
