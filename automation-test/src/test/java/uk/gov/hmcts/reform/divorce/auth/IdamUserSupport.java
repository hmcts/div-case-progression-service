package uk.gov.hmcts.reform.divorce.auth;

import io.restassured.RestAssured;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;

@Service
public class IdamUserSupport {

    @Value("${auth.idam.client.baseUrl}")
    private String idamUserBaseUrl;

    @Value("${auth.idam.secret}")
    private String idamSecret;

    @Value("${auth.idam.redirect.url}")
    private String idamRedirectUrl;

    public String getIdamTestUser(String username, String password) {
        createCitizen(username, password);
        return generateClientToken(username, password);
    }

    public String getIdamTestCaseWorkerUser(String username, String password) {
        createCaseworkerUserInIdam(username, password);
        return generateClientToken(username, password);
    }

    public void deleteUsers(List<String> usernames) {
        usernames.forEach(this::deleteUser);
    }

    private void createCitizen(String username, String password) {
        RestAssured.given()
                .header("Content-Type", "application/json")
                .body("{\"email\":\"" + username + "\", \"forename\":\"Test\",\"surname\":\"User\",\"password\":\"" + password + "\"}")
                .post(idamCreateUrl());

    }

    private void createCaseworkerUserInIdam(String username, String password) {
        RestAssured.given()
                .header("Content-Type", "application/json")
                .body("{\"email\":\"" + username + "\", "
                        + "\"forename\":\"CaseWorkerTest\",\"surname\":\"User\",\"password\":\"" + password + "\", "
                        + "\"roles\":[\"caseworker-divorce\"], \"userGroup\":{\"code\":\"caseworker\"}}")
                .post(idamCreateUrl());

    }

    private String idamCreateUrl() {
        return idamUserBaseUrl + "/testing-support/accounts";
    }

    private String generateClientToken(String username, String password) {
        String code = generateClientCode(username, password);

        String token = RestAssured.given().post(idamUserBaseUrl + "/oauth2/token?code=" + code +
            "&client_secret=" + idamSecret +
            "&client_id=divorce" +
            "&redirect_uri=" + idamRedirectUrl +
            "&grant_type=authorization_code")
            .body().path("access_token");

        return "Bearer " + token;
    }

    private String generateClientCode(String username, String password) {
        String encoded = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());

        return RestAssured.given().baseUri(idamUserBaseUrl)
            .header("Authorization", "Basic " + encoded)
            .post("/oauth2/authorize?response_type=code&client_id=divorce&redirect_uri=" + idamRedirectUrl)
            .body().path("code");
    }

    private void deleteUser(String username) {
        System.out.println("Deleting user " + username);
        RestAssured.given().baseUri(idamUserBaseUrl)
            .header("Accept", "application/json")
            .header("Content-Type", "application/json")
            .delete(String.format("testing-support/accounts/%s", username));
    }
}
