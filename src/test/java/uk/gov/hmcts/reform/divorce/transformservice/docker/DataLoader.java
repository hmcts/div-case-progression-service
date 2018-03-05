package uk.gov.hmcts.reform.divorce.transformservice.docker;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import uk.gov.hmcts.reform.divorce.transformservice.docker.exception.MappingImportException;
import uk.gov.hmcts.reform.divorce.transformservice.docker.exception.RoleCreationException;
import uk.gov.hmcts.reform.divorce.transformservice.docker.exception.ServiceTokenGenerationException;
import uk.gov.hmcts.reform.divorce.transformservice.docker.exception.UserTokenGenerationException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DataLoader {
    private static final String CCD_SERVICE_ID = "ccd_gw";
    private static final String CCD_IMPORT_ROLE = "ccd-import";
    private static final String SERVICE_TOKEN_ENDPOINT_URL = "http://localhost:4502/testing-support/lease";
    private static final String USER_TOKEN_ENDPOINT_URL = "http://localhost:4501/testing-support/lease";
    private static final String ROLE_ENDPOINT_URL = "http://localhost:4451/api/user-role";
    private static final String UPLOAD_SPREADSHEET_URL = "http://localhost:4451/import";


    private final HttpClient httpClient;
    private final HttpRequestFactory httpRequestFactory;

    public DataLoader(HttpClient httpClient, HttpRequestFactory httpRequestFactory) {
        this.httpClient = httpClient;
        this.httpRequestFactory = httpRequestFactory;
    }

    public DataLoader(HttpRequestFactory httpRequestFactory) {
        this.httpRequestFactory = httpRequestFactory;
        this.httpClient = HttpClients.createDefault();
    }

    public String getServiceToken(String service) {

        Map<String, String> params = new HashMap<>();
        params.put("microservice", service);

        HttpPost request = httpRequestFactory.createPostRequest(SERVICE_TOKEN_ENDPOINT_URL, params);

        try {
            HttpResponse response = httpClient.execute(request);
            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            throw new ServiceTokenGenerationException(e);
        }
    }

    public String getUserToken(String role, int id) {
        Map<String, String> params = new HashMap<>();
        params.put("role", role);
        params.put("id", String.valueOf(id));

        HttpPost request = httpRequestFactory.createPostRequest(USER_TOKEN_ENDPOINT_URL, params);

        try {
            HttpResponse response = httpClient.execute(request);
            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            throw new UserTokenGenerationException(e);
        }
    }

    public void createRole(String role, String classification) {
        String userToken = getUserToken(CCD_IMPORT_ROLE, 1);
        String serviceToken = getServiceToken(CCD_SERVICE_ID);

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + userToken);
        headers.put("ServiceAuthorization", "Bearer " + serviceToken);
        headers.put("Content-type", "application/json");

        Map<String, String> params = new HashMap<>();
        params.put("role", role);
        params.put("security_classification", classification);

        HttpPut request = httpRequestFactory.createJsonPutRequest(ROLE_ENDPOINT_URL, params, headers);

        try {
            httpClient.execute(request);
        } catch (IOException e) {
            throw new RoleCreationException(e);
        }
    }

    public void importMappings(String filepath) {
        String userToken = getUserToken(CCD_IMPORT_ROLE, 1);
        String serviceToken = getServiceToken(CCD_SERVICE_ID);

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + userToken);
        headers.put("ServiceAuthorization", "Bearer " + serviceToken);

        HttpPost request = httpRequestFactory.createMultipartPostRequest(UPLOAD_SPREADSHEET_URL, headers, "file", filepath);

        try {
            httpClient.execute(request);
        } catch (IOException e) {
            throw new MappingImportException(e);
        }
    }
}
