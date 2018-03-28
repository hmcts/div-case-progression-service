package uk.gov.hmcts.reform.divorce.health;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.reform.divorce.transformservice.client.TransformationHttpEntityFactory;

@Component
public class DraftStoreApi extends WebServiceHealthCheck {
    public DraftStoreApi(
        TransformationHttpEntityFactory httpEntityFactory,
        RestTemplate restTemplate,
        @Value("${draft.store.api.health.uri}") String uri) {
        super(httpEntityFactory, restTemplate, uri);
    }
}
