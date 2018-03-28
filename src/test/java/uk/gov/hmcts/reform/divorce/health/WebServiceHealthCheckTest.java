package uk.gov.hmcts.reform.divorce.health;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.actuate.health.Health;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.reform.divorce.transformservice.client.TransformationHttpEntityFactory;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WebServiceHealthCheckTest {
    private String uri = "http://example.com";
    private RestTemplate restTemplate = mock(RestTemplate.class);
    private TransformationHttpEntityFactory httpEntityFactory = mock(TransformationHttpEntityFactory.class);

    private WebServiceHealthCheck healthCheck = new TestWebServiceHealthCheck(httpEntityFactory, restTemplate, uri);

    @Test
    public void shouldReturnUpWhenServiceReturnsOk() {
        HttpEntity<Object> httpEntity = new HttpEntity<>(null);
        ResponseEntity<Object> responseEntity = new ResponseEntity<>(HttpStatus.OK);

        when(httpEntityFactory.createRequestEntityForHealthCheck()).thenReturn(httpEntity);

        when(restTemplate.exchange(eq(uri), eq(HttpMethod.GET), eq(httpEntity), eq(Object.class), eq(new HashMap<>())))
            .thenReturn(responseEntity);

        assertThat(healthCheck.health()).isEqualTo(Health.up().build());

        verify(httpEntityFactory).createRequestEntityForHealthCheck();
        verify(restTemplate).exchange(eq(uri), eq(HttpMethod.GET), eq(httpEntity), eq(Object.class),
            eq(new HashMap<>()));

        verifyNoMoreInteractions(httpEntityFactory, restTemplate);
    }

    @Test
    public void shouldReturnDownWhenServiceReturnsServiceUnavailable() {
        HttpEntity<Object> httpEntity = new HttpEntity<>(null);

        when(httpEntityFactory.createRequestEntityForHealthCheck()).thenReturn(httpEntity);

        HttpServerErrorException exception = mock(HttpServerErrorException.class);
        when(exception.getStatusCode()).thenReturn(HttpStatus.SERVICE_UNAVAILABLE);

        doThrow(exception).when(restTemplate)
            .exchange(eq(uri), eq(HttpMethod.GET), eq(httpEntity), eq(Object.class), eq(new HashMap<>()));

        assertThat(healthCheck.health()).isEqualTo(Health.down().build());

        verify(httpEntityFactory).createRequestEntityForHealthCheck();
        verify(restTemplate).exchange(eq(uri), eq(HttpMethod.GET), eq(httpEntity), eq(Object.class),
            eq(new HashMap<>()));

        verifyNoMoreInteractions(httpEntityFactory, restTemplate);
    }

    @Test
    public void shouldReturnUnknownIfExceptionIsThrown() {
        HttpEntity<Object> httpEntity = new HttpEntity<>(null);

        when(httpEntityFactory.createRequestEntityForHealthCheck()).thenReturn(httpEntity);

        RuntimeException exception = mock(RuntimeException.class);

        doThrow(exception).when(restTemplate)
            .exchange(eq(uri), eq(HttpMethod.GET), eq(httpEntity), eq(Object.class), eq(new HashMap<>()));

        assertThat(healthCheck.health()).isEqualTo(Health.unknown().build());

        verify(httpEntityFactory).createRequestEntityForHealthCheck();
        verify(restTemplate).exchange(eq(uri), eq(HttpMethod.GET), eq(httpEntity), eq(Object.class),
            eq(new HashMap<>()));

        verifyNoMoreInteractions(httpEntityFactory, restTemplate);
    }

    @Test
    public void shouldReturnDownWhenResourceAccessExceptionIsThrown() {
        HttpEntity<Object> httpEntity = new HttpEntity<>(null);

        when(httpEntityFactory.createRequestEntityForHealthCheck()).thenReturn(httpEntity);

        ResourceAccessException exception = mock(ResourceAccessException.class);

        doThrow(exception).when(restTemplate)
            .exchange(eq(uri), eq(HttpMethod.GET), eq(httpEntity), eq(Object.class), eq(new HashMap<>()));

        assertThat(healthCheck.health()).isEqualTo(Health.down().build());

        verify(httpEntityFactory).createRequestEntityForHealthCheck();
        verify(restTemplate).exchange(eq(uri), eq(HttpMethod.GET), eq(httpEntity), eq(Object.class),
            eq(new HashMap<>()));

        verifyNoMoreInteractions(httpEntityFactory, restTemplate);
    }

    @Test
    public void shouldReturnUnknownStatusIfUpstreamStatusIsNot200or503() {
        HttpEntity<Object> httpEntity = new HttpEntity<>(null);
        ResponseEntity<Object> responseEntity = new ResponseEntity<>(HttpStatus.MOVED_PERMANENTLY);

        when(httpEntityFactory.createRequestEntityForHealthCheck()).thenReturn(httpEntity);

        when(restTemplate.exchange(eq(uri), eq(HttpMethod.GET), eq(httpEntity), eq(Object.class), eq(new HashMap<>())))
            .thenReturn(responseEntity);

        assertThat(healthCheck.health()).isEqualTo(Health.unknown().build());

        verify(httpEntityFactory).createRequestEntityForHealthCheck();
        verify(restTemplate).exchange(eq(uri), eq(HttpMethod.GET), eq(httpEntity), eq(Object.class),
            eq(new HashMap<>()));

        verifyNoMoreInteractions(httpEntityFactory, restTemplate);
    }

    private static class TestWebServiceHealthCheck extends WebServiceHealthCheck {
        TestWebServiceHealthCheck(TransformationHttpEntityFactory httpEntityFactory, RestTemplate restTemplate,
                                  String uri) {
            super(httpEntityFactory, restTemplate, uri);
        }
    }
}
