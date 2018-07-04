package uk.gov.hmcts.reform.divorce.transformservice.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class RetrieveCcdClient {

    private final CcdClientConfiguration ccdClientConfiguration;
    private final RestTemplate restTemplate;
    private final TransformationHttpEntityFactory httpEntityFactory;
    private final Boolean checkCcdEnabled;

    @Autowired
    public RetrieveCcdClient(CcdClientConfiguration ccdClientConfiguration,
                             RestTemplate restTemplate,
                             TransformationHttpEntityFactory httpEntityFactory,
                             @Value("${draft.api.ccd.check.enabled}") String checkCcdEnabled) {
        this.ccdClientConfiguration = ccdClientConfiguration;
        this.restTemplate = restTemplate;
        this.httpEntityFactory = httpEntityFactory;
        this.checkCcdEnabled = Boolean.valueOf(checkCcdEnabled);
    }

    public List<Map<String, Object>> getCases(String userId, String jwt) {
        String url = ccdClientConfiguration.getRetrieveCaseUrl(userId);
        HttpEntity<String> httpEntity = httpEntityFactory.createRequestEntityForCcdGet(jwt);

        if (!checkCcdEnabled) {
            return Collections.emptyList();
        }

        List<Map<String, Object>> cases = restTemplate.exchange(url, HttpMethod.GET, httpEntity, List.class).getBody();

        if (cases.isEmpty()) {
            log.debug("No cases found");
            return Collections.emptyList();
        }

        log.info(String.format("Found %s cases", cases.size()));

        return cases;
    }
}
