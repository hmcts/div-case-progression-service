package uk.gov.hmcts.reform.divorce.health;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.reform.divorce.transformservice.client.TransformationHttpEntityFactory;

@Component
public class PaymentApi extends WebServiceHealthCheck {
    @Autowired
    public PaymentApi(TransformationHttpEntityFactory httpEntityFactory, RestTemplate restTemplate,
                      @Value("${payment.api.healthUrl}") String uri) {
        super(httpEntityFactory, restTemplate, uri);
    }
}

