package uk.gov.hmcts.reform.divorce.health;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.reform.divorce.transformservice.client.TransformationHttpEntityFactory;

@Component
public class ServiceAuthProviderApi extends WebServiceHealthCheck {
    @Autowired
    public ServiceAuthProviderApi(TransformationHttpEntityFactory httpEntityFactory, RestTemplate restTemplate,
                                  @Value("${auth.provider.health.uri}") String uri) {
        super(httpEntityFactory, restTemplate, uri);
    }
}
