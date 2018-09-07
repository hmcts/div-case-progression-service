package uk.gov.hmcts.reform.divorce.support.auth;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.divorce.support.auth.model.CreateUserRequest;
import uk.gov.hmcts.reform.divorce.support.auth.model.UserCode;
import uk.gov.hmcts.reform.divorce.support.util.ResourceLoader;

import java.util.Base64;
import java.util.Locale;
import java.util.UUID;

@Service
public class IdamUserSupport {

    @Value("${auth.idam.client.baseUrl}")
    private String idamUserBaseUrl;

    @Value("${auth.idam.client.redirectUri}")
    private String idamRedirectUri;

    @Value("${auth.idam.client.secret}")
    private String idamSecret;

    private String idamUsername;

    private String idamPassword;

    private String testUserJwtToken;

    private String testCaseworkerJwtToken;

    public String generateNewUserAndReturnToken() {
        String username = "simulate-delivered" + UUID.randomUUID() + "@notifications.service.gov.uk";
        String password = UUID.randomUUID().toString().toUpperCase(Locale.UK);
        createUserInIdam(username, password);
        return generateUserTokenWithNoRoles(username, password);
    }

    public synchronized String getIdamTestUser() {
        if (StringUtils.isBlank(testUserJwtToken)) {
            createUserAndToken();
        }
        return testUserJwtToken;
    }

    protected void createUserAndToken() {
        createUserInIdam();
        testUserJwtToken = generateUserTokenWithNoRoles(idamUsername, idamPassword);
    }

    public synchronized String getIdamTestCaseWorkerUser() {
        if (StringUtils.isBlank(testCaseworkerJwtToken)) {
            String username = "simulate-delivered" + UUID.randomUUID() + "@notifications.service.gov.uk";
            String password = UUID.randomUUID().toString().toUpperCase(Locale.UK);
            createCaseworkerUserInIdam(username, password);
            testCaseworkerJwtToken = generateUserTokenWithNoRoles(username, password);
        }

        return testCaseworkerJwtToken;
    }

    private void createUserInIdam(String username, String password) {
        CreateUserRequest userRequest = CreateUserRequest.builder()
            .email(username)
            .password(password)
            .forename("Test")
            .surname("User")
            .roles(new UserCode[] { UserCode.builder().code("citizen").build() })
            .userGroup(UserCode.builder().code("divorce-private-beta").build())
            .build();

        RestAssured.given()
            .header("Content-Type", "application/json")
            .body(ResourceLoader.objectToJson(userRequest))
            .post(idamCreateUrl());
    }

    private void createUserInIdam() {
        idamUsername = "simulate-delivered" + UUID.randomUUID() + "@notifications.service.gov.uk";
        idamPassword = UUID.randomUUID().toString();

        createUserInIdam(idamUsername, idamPassword);
    }

    private void createCaseworkerUserInIdam(String username, String password) {
        CreateUserRequest userRequest = CreateUserRequest.builder()
            .email(username)
            .password(password)
            .forename("Test")
            .surname("User")
            .roles(new UserCode[] {
                UserCode.builder().code("caseworker").build(),
                UserCode.builder().code("caseworker-divorce").build(),
                UserCode.builder().code("caseworker-divorce-courtadmin").build()
            })
            .userGroup(UserCode.builder().code("caseworker").build())
            .build();

        RestAssured.given()
            .header("Content-Type", "application/json")
            .body(ResourceLoader.objectToJson(userRequest))
            .post(idamCreateUrl());
    }

    private String idamCreateUrl() {
        return idamUserBaseUrl + "/testing-support/accounts";
    }

    private String generateUserTokenWithNoRoles(String username, String password) {
        String userLoginDetails = String.join(":", username, password);
        final String authHeader = "Basic " + new String(Base64.getEncoder().encode(userLoginDetails.getBytes()));

        Response response = RestAssured.given()
            .header("Authorization", authHeader)
            .relaxedHTTPSValidation()
            .post(idamCodeUrl());

        if (response.getStatusCode() >= 300) {
            throw new IllegalStateException("Token generation failed with code: " + response.getStatusCode()
                + " body: " + response.getBody().prettyPrint());
        }

        response = RestAssured.given()
            .relaxedHTTPSValidation()
            .post(idamTokenUrl(response.getBody().path("code")));

        String token = response.getBody().path("access_token");
        return "Bearer " + token;
    }

    private String idamCodeUrl() {
        return idamUserBaseUrl + "/oauth2/authorize"
            + "?response_type=code"
            + "&client_id=divorce"
            + "&redirect_uri=" + idamRedirectUri;
    }

    private String idamTokenUrl(String code) {
        return idamUserBaseUrl + "/oauth2/token"
            + "?code=" + code
            + "&client_id=divorce"
            + "&client_secret=" + idamSecret
            + "&redirect_uri=" + idamRedirectUri
            + "&grant_type=authorization_code";
    }
}
