package uk.gov.hmcts.reform.divorce.common;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuthorizationHeaderServiceTest {

    @Mock
    private AuthTokenGenerator serviceTokenGenerator;

    @InjectMocks
    private AuthorizationHeaderService authorizationHeaderService;

    @Test
    public void setAuthorizationHeaders() throws Exception {
        String userToken = "Bearer user-token";
        String serviceToken = "service-token";

        when(serviceTokenGenerator.generate()).thenReturn(serviceToken);

        HttpHeaders headers = authorizationHeaderService.generateAuthorizationHeaders(userToken);

        assertEquals(1, headers.get("Authorization").size());
        assertEquals(userToken, headers.get("Authorization").get(0));

        assertEquals(1, headers.get("ServiceAuthorization").size());
        assertEquals(serviceToken, headers.get("ServiceAuthorization").get(0));

        verify(serviceTokenGenerator).generate();
        verifyNoMoreInteractions(serviceTokenGenerator);
    }

    @Test
    public void appendBearerToAuthorizationHeaderIfNotExists() throws Exception {
        String userToken = "user-token";
        String serviceToken = "service-token";

        when(serviceTokenGenerator.generate()).thenReturn(serviceToken);

        HttpHeaders headers = authorizationHeaderService.generateAuthorizationHeaders(userToken);

        assertEquals(1, headers.get("Authorization").size());
        assertEquals("Bearer " + userToken, headers.get("Authorization").get(0));
    }
}
