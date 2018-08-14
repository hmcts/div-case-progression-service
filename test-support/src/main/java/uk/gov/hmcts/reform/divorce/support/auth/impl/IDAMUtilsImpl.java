package uk.gov.hmcts.reform.divorce.support.auth.impl;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.hmcts.reform.divorce.support.auth.IDAMUtils;

import java.util.Base64;

public class IDAMUtilsImpl implements IDAMUtils {

    @Value("${auth.idam.client.baseUrl}")
    private String idamUserBaseUrl;

    @Value("${auth.idam.client.redirectUri}")
    private String idamRedirectUri;

    @Value("${auth.idam.client.secret}")
    private String idamSecret;

    private String idamCreateUrl() {
        return idamUserBaseUrl + "/testing-support/accounts";
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

    @Override
    public void createUserInIdam(String username, String password) {
        String s = "{\"email\":\"" + username + "@test.com\", \"forename\":\"" + username +
            "\",\"surname\":\"User\",\"password\":\"" + password + "\"}";

        RestAssured.given()
                .header("Content-Type", "application/json")
                .relaxedHTTPSValidation()
                .body(s)
                .post(idamCreateUrl());
    }

    @Override
    public void createDivorceCaseworkerUserInIdam(String username, String password) {
        String body = "{\"email\":\"" + username + "@test.com" + "\", "
                + "\"forename\":" + "\"" + username + "\"," + "\"surname\":\"User\",\"password\":\"" + password + "\", "
                + "\"roles\":[\"caseworker-divorce\"], \"userGroup\":{\"code\":\"caseworker\"}}";
        
        RestAssured.given()
                .header("Content-Type", "application/json")
                .relaxedHTTPSValidation()
                .body(body)
                .post(idamCreateUrl());
    }

    @Override
    public String generateUserTokenWithNoRoles(String username, String password) {
        String userLoginDetails = String.join(":", username + "@test.com", password);
        final String authHeader = "Basic " + new String(Base64.getEncoder().encode((userLoginDetails).getBytes()));

        Response response = RestAssured.given()
                .header("Authorization", authHeader)
                .relaxedHTTPSValidation()
                .post(idamCodeUrl());

        if (response.getStatusCode() >= 300) {
            throw  new IllegalStateException("Token generation failed with code: " + response.getStatusCode() + " body: " + response.getBody().prettyPrint());
        }

        response = RestAssured.given()
                .relaxedHTTPSValidation()
                .post(idamTokenUrl(response.getBody().path("code")));

        String token = response.getBody().path("access_token");
        return "Bearer " + token;
    }

}
