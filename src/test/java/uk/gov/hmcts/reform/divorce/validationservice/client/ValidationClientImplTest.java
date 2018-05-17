package uk.gov.hmcts.reform.divorce.validationservice.client;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import uk.gov.hmcts.reform.divorce.validationservice.domain.ValidationRequest;
import uk.gov.hmcts.reform.divorce.validationservice.domain.ValidationResponse;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration
@TestPropertySource("classpath:application.properties")
public class ValidationClientImplTest {

    @Mock
    private DefaultValidationHttpEntityFactory httpEntityFactory;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    ValidationClientImpl validationClient;

    @Test
    @SuppressWarnings("unchecked")
    public void shouldReturnValidationResponseOnValidateCall() {
        // HttpEntity<ValidationRequest> httpEntity = mock(HttpEntity.class);
        // ResponseEntity<ValidationResponse> responseEntity = mock(ResponseEntity.class);

        // ValidationRequest validationRequest = new ValidationRequest();

        // ValidationResponse validationResponse = new ValidationResponse();
        // validationResponse.setValidationStatus("success");

        // String validateUrl = "http://localhost:4008/version/1/validate";
        
        // when(httpEntityFactory.createRequestEntityForValidation(eq(validationRequest)))
        //     .thenReturn(httpEntity);
        // when(restTemplate.exchange(eq(validateUrl), eq(HttpMethod.POST), eq(httpEntity), eq(ValidationResponse.class)))
        //     .thenReturn(responseEntity);

        // when(responseEntity.getBody()).thenReturn(validationResponse);

        // assertEquals(validationResponse, validationClient.validate(validationRequest));

        // verify(httpEntityFactory).createRequestEntityForValidation(eq(validationRequest));
        // verify(restTemplate).exchange(eq(validateUrl), eq(HttpMethod.POST), eq(httpEntity), eq(ValidationResponse.class));

        // verifyNoMoreInteractions(httpEntityFactory, restTemplate);
    }
}