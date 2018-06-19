package uk.gov.hmcts.reform.divorce.health;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import uk.gov.hmcts.reform.divorce.transformservice.client.TransformationHttpEntityFactory;

@Component
public class DivValidationService extends WebServiceHealthCheck {
    @Autowired
    public DivValidationService(TransformationHttpEntityFactory httpEntityFactory, RestTemplate restTemplate,
                                @Value("${div.validation.service.healthUrl}") String uri) {
        super(httpEntityFactory, restTemplate, uri);
    }
}
