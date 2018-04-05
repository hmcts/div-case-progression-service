package uk.gov.hmcts.reform.divorce.emclient;

import io.restassured.response.Response;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

import static net.serenitybdd.rest.SerenityRest.given;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EvidenceManagementUtil {

    public static Response readDataFromEvidenceManagement(String uri, String caseWorkerToken) {
        Map<String, Object> headers = new HashMap<>();

        headers.put("Authorization", caseWorkerToken);
        headers.put("user-roles", "caseworker-divorce");

        return given()
                .contentType("application/json")
                .headers(headers)
                .when()
                .get(uri)
                .andReturn();
    }

    public static String getDocumentStoreURI(String uri, String documentManagementURL) {
        if (uri.contains("http://em-api-gateway-web:3404")) {
            return uri.replace("http://em-api-gateway-web:3404", documentManagementURL);
        }

        if (uri.contains("document-management-store:8080")) {
            return uri.replace("http://document-management-store:8080", documentManagementURL);
        }

        return uri;
    }
}
