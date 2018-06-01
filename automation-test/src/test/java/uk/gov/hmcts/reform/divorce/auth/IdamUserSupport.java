package uk.gov.hmcts.reform.divorce.auth;

import io.restassured.RestAssured;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.UUID;

@Service
public class IdamUserSupport {

    private static final String IDAM_CASEWORKER_USER = "CaseWorkerTest";
    private static final String IDAM_CASEWORKER_PASSWORD = "password";
    private static final String REDIRECT_URI = "https://www.preprod.ccd.reform.hmcts.net/oauth2redirect";

    @Value("${auth.idam.client.baseUrl}")
    private String idamUserBaseUrl;

    @Value("${auth.idam.secret}")
    private String idamSecret;

    private String idamUsername;

    private String idamPassword;

    private String testUserJwtToken;

    private String testCaseworkerJwtToken;

    public synchronized String getIdamTestUser() {
        if (StringUtils.isBlank(testUserJwtToken)) {
            createUserInIdam();
            testUserJwtToken = generateClientToken(idamUsername, idamPassword);
        }

        return testUserJwtToken;
    }

    public synchronized String getIdamTestCaseWorkerUser() {
        if (StringUtils.isBlank(testCaseworkerJwtToken)) {
            createCaseworkerUserInIdam();
            testCaseworkerJwtToken = generateClientToken(IDAM_CASEWORKER_USER, IDAM_CASEWORKER_PASSWORD);
        }

        return testCaseworkerJwtToken;
    }

    private void createUserInIdam() {
        idamUsername = "simulate-delivered" + UUID.randomUUID() + "@notifications.service.gov.uk";
        idamPassword = UUID.randomUUID().toString();

        RestAssured.given()
                .header("Content-Type", "application/json")
                .body("{\"email\":\"" + idamUsername + "\", \"forename\":\"Test\",\"surname\":\"User\",\"password\":\"" + idamPassword + "\"}")
                .post(idamCreateUrl());
    }

    private void createCaseworkerUserInIdam() {
        RestAssured.given()
                .header("Content-Type", "application/json")
                .body("{\"email\":\"" + IDAM_CASEWORKER_USER + "\", "
                        + "\"forename\":\"CaseWorkerTest\",\"surname\":\"User\",\"password\":\"" + IDAM_CASEWORKER_PASSWORD + "\", "
                        + "\"roles\":[\"caseworker-divorce\"], \"userGroup\":{\"code\":\"caseworker\"}}")
                .post(idamCreateUrl());
    }

    private String idamCreateUrl() {
        return idamUserBaseUrl + "/testing-support/accounts";
    }

    private String generateClientToken(String username, String password) {
        String code = generateClientCode(username, password);
        String token = "";

        token = RestAssured.given().post(idamUserBaseUrl + "/oauth2/token?code=" + code +
            "&client_secret=" + idamSecret +
            "&client_id=probate" +
            "&redirect_uri=" + REDIRECT_URI +
            "&grant_type=authorization_code")
            .body().path("access_token");

        return "Bearer " + token;
    }

    private String generateClientCode(String username, String password) {
        String code = "";
        createUserInIdam();
        System.out.println("created user in idam");
        final String encoded = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
        System.out.println("encoded auth is.." + encoded);
        code = RestAssured.given().baseUri(idamUserBaseUrl)
            .header("Authorization", "Basic " + encoded)
            .post("/oauth2/authorize?response_type=code&client_id=divorce&redirect_uri=" + REDIRECT_URI)
            .body().path("code");
        return code;

    }
}
