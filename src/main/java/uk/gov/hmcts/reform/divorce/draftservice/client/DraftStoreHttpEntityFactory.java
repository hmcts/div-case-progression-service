package uk.gov.hmcts.reform.divorce.draftservice.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.divorce.draftservice.domain.CreateDraft;
import uk.gov.hmcts.reform.divorce.draftservice.domain.UpdateDraft;

@Component
public class DraftStoreHttpEntityFactory {

    private static final String SERVICE_AUTHORIZATION_HEADER_NAME = "ServiceAuthorization";
    private static final String SECRET_HEADER_NAME = "Secret";

    @Autowired
    private AuthTokenGenerator serviceTokenGenerator;


    public HttpEntity<CreateDraft> createRequestEntityForDraft(String userToken, String secret,
                                                               CreateDraft createDraft) {
        HttpHeaders headers = getHeaders(userToken, secret);
        return new HttpEntity<>(createDraft, headers);
    }

    public HttpEntity<UpdateDraft> createRequestEntityForDraft(String userToken, String secret,
                                                               UpdateDraft updateDraft) {
        HttpHeaders headers = getHeaders(userToken, secret);
        return new HttpEntity<>(updateDraft, headers);
    }

    public HttpEntity<Void> createRequestEntityFroDraft(String userToken, String secret) {
        HttpHeaders headers = getHeaders(userToken, secret);
        return new HttpEntity<>(headers);
    }

    public HttpEntity<Void> createRequestEntityFroDraft(String userToken) {
        HttpHeaders headers = getHeaders(userToken);
        return new HttpEntity<>(headers);
    }

    private HttpHeaders getHeaders(String userToken, String secret) {
        HttpHeaders headers = getHeaders(userToken);
        headers.add(SECRET_HEADER_NAME, secret);

        return headers;
    }

    private HttpHeaders getHeaders(String userToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, userToken);
        headers.add(SERVICE_AUTHORIZATION_HEADER_NAME, serviceTokenGenerator.generate());
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        return headers;
    }
}
