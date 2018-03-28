package uk.gov.hmcts.reform.divorce.transformservice.client;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import uk.gov.hmcts.reform.divorce.common.AuthorizationHeaderService;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CaseDataContent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultTransformationHttpEntityFactoryTest {

    @Mock
    private AuthorizationHeaderService authorizationHeaderService;

    @InjectMocks
    private DefaultTransformationHttpEntityFactory httpEntityFactory;

    @Test
    public void newCreateRequestEntityReturnsHttpEntity() throws Exception {
        String encodedJwt = "_jwt";
        HttpHeaders headers = new HttpHeaders();

        when(authorizationHeaderService.generateAuthorizationHeaders(encodedJwt)).thenReturn(headers);

        HttpEntity<String> entity = httpEntityFactory.createRequestEntityForCcdGet(encodedJwt);

        assertEquals(headers, entity.getHeaders());
        assertEquals(MediaType.APPLICATION_JSON_UTF8, headers.getContentType());

        verify(authorizationHeaderService).generateAuthorizationHeaders(encodedJwt);
        verifyNoMoreInteractions(authorizationHeaderService);
    }

    @Test
    public void newSubmitRequestEntityReturnsHttpEntity() throws Exception {
        String encodedJwt = "_jwt";
        HttpHeaders headers = new HttpHeaders();
        CaseDataContent coreCaseData = mock(CaseDataContent.class);

        when(authorizationHeaderService.generateAuthorizationHeaders(encodedJwt)).thenReturn(headers);

        HttpEntity<CaseDataContent> entity =
            httpEntityFactory.createRequestEntityForSubmitCase(encodedJwt, coreCaseData);

        assertThat(entity.getHeaders()).isEqualTo(headers);
        assertThat(entity.getBody()).isEqualTo(coreCaseData);
        assertThat(headers.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_UTF8);

        verify(authorizationHeaderService).generateAuthorizationHeaders(encodedJwt);
        verifyNoMoreInteractions(authorizationHeaderService);
    }

    @Test
    public void shouldReturnEntityWithJsonAcceptHeadersWhenCallingCreateEntityForHealthCheck() throws Exception {
        HttpEntity<Object> httpEntity = httpEntityFactory.createRequestEntityForHealthCheck();

        assertThat(httpEntity.getHeaders().size()).isEqualTo(1);
        assertThat(httpEntity.getHeaders().getAccept().get(0)).isEqualTo(MediaType.APPLICATION_JSON_UTF8);
        assertThat(httpEntity.getBody()).isNull();
    }
}
