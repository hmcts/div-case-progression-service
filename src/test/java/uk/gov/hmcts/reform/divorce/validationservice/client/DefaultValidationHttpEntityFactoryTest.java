package uk.gov.hmcts.reform.divorce.validationservice.client;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import uk.gov.hmcts.reform.divorce.validationservice.domain.ValidationRequest;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class DefaultValidationHttpEntityFactoryTest {

    @InjectMocks
    private DefaultValidationHttpEntityFactory httpEntityFactory;

    @Test
    public void newValidationRequestEntityReturnsHttpEntity() throws Exception {
        ValidationRequest request = new ValidationRequest();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        HttpEntity<ValidationRequest> entity = httpEntityFactory.createRequestEntityForValidation(request);

        assertEquals(headers, entity.getHeaders());
        assertEquals(MediaType.APPLICATION_JSON_UTF8, headers.getContentType());
        assertEquals(request, entity.getBody());
    }
}
