package uk.gov.hmcts.reform.divorce.health;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.reform.divorce.transformservice.client.TransformationHttpEntityFactory;

import java.util.HashMap;

@Slf4j
public abstract class WebServiceHealthCheck implements HealthIndicator {
    protected final TransformationHttpEntityFactory httpEntityFactory;
    protected final RestTemplate restTemplate;
    protected final String uri;

    WebServiceHealthCheck(TransformationHttpEntityFactory httpEntityFactory, RestTemplate restTemplate,
                          String uri) {
        this.httpEntityFactory = httpEntityFactory;
        this.restTemplate = restTemplate;
        this.uri = uri;
    }

    public Health health() {
        HttpEntity<Object> httpEntity = httpEntityFactory.createRequestEntityForHealthCheck();
        ResponseEntity<Object> responseEntity;
        try {
            responseEntity = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, Object.class, new HashMap<>());
        } catch (HttpServerErrorException | ResourceAccessException serverException) {
            log.error("Exception occurred while doing health check", serverException);

            return Health.down().build();
        } catch (Exception exception) {
            log.info("Unable to access upstream service", exception);

            return Health.unknown().build();
        }

        return responseEntity.getStatusCode().equals(HttpStatus.OK) ? Health.up().build() : Health.unknown().build();
    }
}
