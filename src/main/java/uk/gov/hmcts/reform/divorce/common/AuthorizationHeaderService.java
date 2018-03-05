package uk.gov.hmcts.reform.divorce.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

@Component
public class AuthorizationHeaderService {
    private static final String AUTHORIZATION_SCHEME = "Bearer ";

    private final AuthTokenGenerator serviceTokenGenerator;

    @Autowired
    public AuthorizationHeaderService(final AuthTokenGenerator serviceTokenGenerator) {
        this.serviceTokenGenerator = serviceTokenGenerator;
    }

    public HttpHeaders generateAuthorizationHeaders(String userToken) {
        final HttpHeaders headers = new HttpHeaders();

        String serviceToken = serviceTokenGenerator.generate();

        String authorizationToken = userToken.startsWith(AUTHORIZATION_SCHEME) ?
            userToken : 
            AUTHORIZATION_SCHEME.concat(userToken);

        headers.add("Authorization", authorizationToken);

        headers.add("ServiceAuthorization", serviceToken.replaceFirst(AUTHORIZATION_SCHEME, ""));

        return headers;
    }
}
