package uk.gov.hmcts.reform.divorce.transformservice.docker;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.BasicHttpEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.hmcts.reform.divorce.transformservice.docker.exception.RoleCreationException;
import uk.gov.hmcts.reform.divorce.transformservice.docker.exception.ServiceTokenGenerationException;
import uk.gov.hmcts.reform.divorce.transformservice.docker.exception.UserTokenGenerationException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DataLoaderTest {
    private static final String CCD_SERVICE_ID = "ccd_gw";
    private static final String CCD_IMPORT_ROLE = "ccd-import";
    private static final int CCD_USER_ID = 1;

    private static final String SERVICE_TOKEN_ENDPOINT_URL = "http://localhost:4502/testing-support/lease";
    private static final String USER_TOKEN_ENDPOINT_URL = "http://localhost:4501/testing-support/lease";
    private static final String ROLE_ENDPOINT_URL = "http://localhost:4451/api/user-role";
    private static final String UPLOAD_SPREADSHEET_URL = "http://localhost:4451/import";

    @Mock
    private HttpRequestFactory httpRequestFactory;

    @Mock
    private HttpClient httpClient;

    @InjectMocks
    private DataLoader dataLoader;

    @Test
    public void shouldReturnJwtFromServiceTokenCall() throws Exception {
        String jwt = "abcde12345";
        Expectations expectations = createServiceToken(jwt);
        String result = dataLoader.getServiceToken(CCD_SERVICE_ID);
        assertEquals(jwt, result);
        expectations.verify();
    }

    @Test
    public void shouldThrowServiceTokenGenerationException() throws Exception {
        HttpPost request = mock(HttpPost.class);
        Map<String, String> params = createServiceTokenRequestParams();
        IOException exception = mock(IOException.class);
        when(httpRequestFactory.createPostRequest(eq(SERVICE_TOKEN_ENDPOINT_URL), eq(params))).thenReturn(request);
        doThrow(exception).when(httpClient).execute(request);

        try {
            dataLoader.getServiceToken(CCD_SERVICE_ID);
            fail("Expected ServiceTokenGenerationException");
        } catch (ServiceTokenGenerationException e) {
            assertEquals(exception, e.getCause());
        }

        verify(httpRequestFactory).createPostRequest(eq(SERVICE_TOKEN_ENDPOINT_URL), eq(params));
    }

    @Test
    public void shouldReturnJwtFromUserTokenCall() throws Exception {
        String jwt = "abcde12345";
        Expectations expectations = createUserToken(jwt);

        String result = dataLoader.getUserToken(CCD_IMPORT_ROLE, CCD_USER_ID);

        assertEquals(jwt, result);
        expectations.verify();
    }

    @Test
    public void shouldThrowUserTokenGenerationException() throws Exception {
        HttpPost request = mock(HttpPost.class);
        Map<String, String> params = createUserTokenRequestParams();
        IOException exception = mock(IOException.class);
        when(httpRequestFactory.createPostRequest(eq(USER_TOKEN_ENDPOINT_URL), eq(params))).thenReturn(request);
        doThrow(exception).when(httpClient).execute(request);

        try {
            dataLoader.getUserToken(CCD_IMPORT_ROLE, CCD_USER_ID);
            fail("Expected ServiceTokenGenerationException");
        } catch (UserTokenGenerationException e) {
            assertEquals(exception, e.getCause());
        }

        verify(httpRequestFactory).createPostRequest(eq(USER_TOKEN_ENDPOINT_URL), eq(params));
    }


    @Test
    public void shouldCreateRole() throws Exception {
        String role = "test-role";
        String classification = "test-classification";
        String serviceToken = "abcde12345";
        String userToken = "12345abcde";

        Expectations serviceTokenExpectations = createServiceToken(serviceToken);
        Expectations userTokenExpectations = createUserToken(userToken);
        Expectations roleExpectations = createRole(serviceToken, userToken, role, classification);

        dataLoader.createRole(role, classification);

        roleExpectations.verify();
        serviceTokenExpectations.verify();
        userTokenExpectations.verify();
    }

    @Test
    public void shouldThrowRoleCreationException() throws Exception {
        String role = "test-role";
        String classification = "test-classification";
        String serviceToken = "abcde12345";
        String userToken = "12345abcde";

        HttpPut createRoleRequest = mock(HttpPut.class);

        Expectations serviceTokenExpectations = createServiceToken(serviceToken);
        Expectations userTokenExpectations = createUserToken(userToken);

        Map<String, String> roleRequestHeaders = createRoleRequestHeaders(serviceToken, userToken);
        Map<String, String> roleRequestParams = createRoleRequestParams(role, classification);

        when(httpRequestFactory.createJsonPutRequest(eq(ROLE_ENDPOINT_URL), eq(roleRequestParams), eq(roleRequestHeaders)))
                .thenReturn(createRoleRequest);

        IOException exception = mock(IOException.class);

        doThrow(exception).when(httpClient).execute(createRoleRequest);

        try {
            dataLoader.createRole(role, classification);
            fail("Expected RoleCreationException");
        } catch (RoleCreationException e) {
            assertEquals(exception, e.getCause());
        }

        verify(httpClient).execute(createRoleRequest);

        serviceTokenExpectations.verify();
        userTokenExpectations.verify();
    }

    @Test
    public void shouldUploadSpreadsheetToCCD() throws Exception {
        String filepath = "/path/to/file";
        String fileField = "file";
        String serviceToken = "abcde12345";
        String userToken = "12345abcde";

        Expectations serviceTokenExpectations = createServiceToken(serviceToken);
        Expectations userTokenExpectations = createUserToken(userToken);

        Map<String, String> uploadSpreadsheetHeaders = new HashMap<>();
        uploadSpreadsheetHeaders.put("Authorization", "Bearer " + userToken);
        uploadSpreadsheetHeaders.put("ServiceAuthorization", "Bearer " + serviceToken);

        HttpPost uploadSpreadsheetRequest = mock(HttpPost.class);

        when(httpRequestFactory.createMultipartPostRequest(eq(UPLOAD_SPREADSHEET_URL), eq(uploadSpreadsheetHeaders), eq(fileField), eq(filepath)))
                .thenReturn(uploadSpreadsheetRequest);

        dataLoader.importMappings(filepath);

        verify(httpClient).execute(uploadSpreadsheetRequest);
        serviceTokenExpectations.verify();
        userTokenExpectations.verify();
    }

    private Map<String, String> createRoleRequestHeaders(String serviceToken, String userToken) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + userToken);
        headers.put("ServiceAuthorization", "Bearer " + serviceToken);
        headers.put("Content-type", "application/json");

        return headers;
    }

    private Map<String, String> createRoleRequestParams(String role, String classification) {
        Map<String, String> params = new HashMap<>();
        params.put("role", role);
        params.put("security_classification", classification);

        return params;
    }

    private Expectations createRole(String serviceToken, String userToken, String role, String classification) throws Exception{
        HttpPut createRoleRequest = mock(HttpPut.class);

        Map<String, String> roleRequestHeaders = createRoleRequestHeaders(serviceToken, userToken);
        Map<String, String> roleRequestParams = createRoleRequestParams(role, classification);

        when(httpRequestFactory.createJsonPutRequest(eq(ROLE_ENDPOINT_URL), eq(roleRequestParams), eq(roleRequestHeaders)))
                .thenReturn(createRoleRequest);

        return () -> {
            verify(httpClient).execute(createRoleRequest);
            verify(httpRequestFactory).createJsonPutRequest(eq(ROLE_ENDPOINT_URL), eq(roleRequestParams), eq(roleRequestHeaders));
        };
    }

    private Expectations createUserToken(String jwt) throws Exception {
        HttpPost request = mock(HttpPost.class);
        Map<String, String> params = createUserTokenRequestParams();

        HttpResponse response = mock(HttpResponse.class);
        BasicHttpEntity entity = createHttpEntity(jwt);

        setUserTokenExpectations(request, params, response, entity);

        return () -> verifyUserTokenExpectations(request, params, response);
    }

    private Expectations createServiceToken(String jwt) throws Exception {
        HttpPost request = mock(HttpPost.class);
        Map<String, String> params = createServiceTokenRequestParams();
        HttpResponse response = mock(HttpResponse.class);
        BasicHttpEntity entity = createHttpEntity(jwt);

        setServiceTokenExpectations(request, params, response, entity);

        return () -> verifyServiceTokenExpectations(request, params, response);
    }

    private Map<String, String> createUserTokenRequestParams() {
        Map<String, String> params = new HashMap<>();
        params.put("role", CCD_IMPORT_ROLE);
        params.put("id", String.valueOf(CCD_USER_ID));

        return params;
    }

    private Map<String, String> createServiceTokenRequestParams() {
        Map<String, String> params = new HashMap<>();
        params.put("microservice", CCD_SERVICE_ID);

        return params;
    }

    private BasicHttpEntity createHttpEntity(String jwt) {
        BasicHttpEntity entity = new BasicHttpEntity();
        entity.setContent(new ByteArrayInputStream(jwt.getBytes()));

        return entity;
    }

    private void setServiceTokenExpectations(HttpPost request, Map<String, String> params, HttpResponse response, HttpEntity entity) throws Exception {
        when(httpRequestFactory.createPostRequest(eq(SERVICE_TOKEN_ENDPOINT_URL), eq(params))).thenReturn(request);
        when(httpClient.execute(request)).thenReturn(response);
        when(response.getEntity()).thenReturn(entity);
    }

    private void verifyServiceTokenExpectations(HttpPost request, Map<String, String> params, HttpResponse response) throws Exception {
        verify(httpRequestFactory).createPostRequest(eq(SERVICE_TOKEN_ENDPOINT_URL), eq(params));
        verify(httpClient).execute(request);
        verify(response).getEntity();
    }

    private void setUserTokenExpectations(HttpPost request, Map<String, String> params, HttpResponse response, HttpEntity entity) throws Exception {
        when(httpRequestFactory.createPostRequest(eq(USER_TOKEN_ENDPOINT_URL), eq(params))).thenReturn(request);
        when(httpClient.execute(request)).thenReturn(response);
        when(response.getEntity()).thenReturn(entity);
    }

    private void verifyUserTokenExpectations(HttpPost request, Map<String, String> params, HttpResponse response) throws Exception {
        verify(httpRequestFactory).createPostRequest(eq(USER_TOKEN_ENDPOINT_URL), eq(params));
        verify(httpClient).execute(request);
        verify(response).getEntity();
    }
}