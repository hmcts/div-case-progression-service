package uk.gov.hmcts.reform.divorce.caseprogression.draftsapi.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.JWTParser;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.serenitybdd.rest.SerenityRest;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.divorce.auth.ServiceAuthSupport;
import uk.gov.hmcts.reform.divorce.auth.model.ServiceAuthTokenFor;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.config;
import static io.restassured.config.EncoderConfig.encoderConfig;

@Component
public class DraftStoreClient {

    @Value("${draft.store.api.baseUrl}")
    private String draftStoreAPIBaseUrl;

    @Value("${draft.store.api.encryption.key}")
    private String draftStoreEncryptionKey;

    @Value("${draft.store.api.document.type}")
    private String documentType;

    @Value("${draft.store.api.max.age}")
    private int maxAge;

    @Value("${draft.store.api.encryption.key.template}")
    private String draftStoreSecretTemplate;

    @Autowired
    private ServiceAuthSupport serviceAuthSupport;

    public List<Draft> getDivorceDrafts(String jwt) {
        DraftList draftList = getAllDrafts(jwt, null);

        return findDivorceDraft(jwt, draftList, new ArrayList<>());
    }

    public void createDraft(String jwt, String draft) {
        ObjectMapper objectMapper = new ObjectMapper();
        CreateDraft createDraft;
        try {
            createDraft = new CreateDraft(objectMapper.readTree(draft), documentType, maxAge);
        } catch (IOException e) {
            throw new IllegalArgumentException("Draft is not in the correct format");
        }
        try {
            SerenityRest.given()
                    .config(config().encoderConfig(encoderConfig().encodeContentTypeAs("application", ContentType.JSON)))
                    .headers(getHeaders(jwt))
                    .body(objectMapper.writeValueAsString(createDraft))
                    .when()
                    .post(getDraftsUrl(null))
                    .andReturn();
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Draft cannot be serialzed as JSON");
        }
    }


    private DraftList getAllDrafts(String jwt, String after) {
        Response response = SerenityRest.given()
                .headers(getHeaders(jwt))
                .when()
                .get(getDraftsUrl(after))
                .andReturn();

        return response.getBody().as(DraftList.class);
    }

    private List<Draft> findDivorceDraft(String jwt, DraftList draftList, List<Draft> divorcesDrafts) {
        if (draftList != null && draftList.getData() !=null && !draftList.getData().isEmpty()) {
            divorcesDrafts.addAll(draftList.getData().stream()
                    .filter(draft -> draft.getType().equalsIgnoreCase(documentType))
                    .collect(Collectors.toList()));
            if (draftList.getPaging().getAfter() != null) {
                draftList = getAllDrafts(jwt, draftList.getPaging().getAfter());
                return findDivorceDraft(jwt, draftList, divorcesDrafts);
            }
        }
        return divorcesDrafts;
    }

    private String getDraftsUrl(String after) {
        return String.format("%s/drafts%s", draftStoreAPIBaseUrl, after != null ? String.format("?after=%s", after) : "");
    }

    private Map<String, String> getHeaders(String jwt) {
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.AUTHORIZATION, jwt);
        headers.put("ServiceAuthorization",
                serviceAuthSupport.getServiceAuthTokenFor(ServiceAuthTokenFor.CASE_PROGRESSION));
        headers.put("Secret", getSecret(jwt));
        headers.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8.getType());

        return headers;
    }

    private String getSecret(String jwt) {
        return Base64.encodeBase64String(
                String.format(
                        draftStoreSecretTemplate,
                        draftStoreEncryptionKey,
                        getUserId(jwt)
                ).getBytes());
    }

    private String getUserId(String jwt) {
        try {
            jwt = jwt.replaceAll("Bearer ", "");
            return (String) JWTParser.parse(jwt).getJWTClaimsSet().getClaims().get("id");
        } catch (ParseException e) {
            throw new IllegalArgumentException("JWT is not valid");
        }
    }
}
