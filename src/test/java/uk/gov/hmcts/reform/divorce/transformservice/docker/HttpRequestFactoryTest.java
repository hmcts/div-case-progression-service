package uk.gov.hmcts.reform.divorce.transformservice.docker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.hmcts.reform.divorce.transformservice.docker.exception.RequestCreationException;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class HttpRequestFactoryTest {

    @Mock
    private HttpEntityFactory httpEntityFactory;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private HttpRequestFactory httpRequestFactory;

    @Test
    public void shouldReturnHttpPostWithUrlAndEntity() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("role", "citizen");
        params.put("id", "15");

        String url = "http://localhost";
        String content = "role=citizen&id=15";

        HttpEntity httpEntity = mock(HttpEntity.class);

        when(httpEntityFactory.createEntity(content)).thenReturn(httpEntity);

        HttpPost request = httpRequestFactory.createPostRequest(url, params);

        assertEquals(url, request.getURI().toString());
        assertEquals(httpEntity, request.getEntity());
        assertEquals("application/x-www-form-urlencoded", request.getFirstHeader("Content-type").getValue());

        verify(httpEntityFactory).createEntity(content);
    }

    @Test
    public void shouldReturnHttpPutWithHeadersUrlAndEntity() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("role", "citizen");
        params.put("id", "15");

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-type", "application/json");
        headers.put("Authorization", "Bearer abcde12345");

        String url = "http://localhost";
        String content = "{\"role\":\"citizen\",\"id\":\"15\"}";

        when(objectMapper.writeValueAsString(params)).thenReturn(content);

        HttpEntity httpEntity = mock(HttpEntity.class);

        when(httpEntityFactory.createEntity(content)).thenReturn(httpEntity);

        HttpPut request = httpRequestFactory.createJsonPutRequest(url, params, headers);

        assertEquals(url, request.getURI().toString());
        assertEquals(httpEntity, request.getEntity());
        assertEquals("application/json", request.getFirstHeader("Content-type").getValue());
        assertEquals("Bearer abcde12345", request.getFirstHeader("Authorization").getValue());

        verify(objectMapper).writeValueAsString(params);
        verify(httpEntityFactory).createEntity(content);
    }

    @Test
    public void shouldThrowRequestCreationException() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("role", "citizen");
        params.put("id", "15");

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-type", "application/json");
        headers.put("Authorization", "Bearer abcde12345");

        String url = "http://localhost";

        JsonProcessingException exception = mock(JsonProcessingException.class);

        doThrow(exception).when(objectMapper).writeValueAsString(params);

        try {
            httpRequestFactory.createJsonPutRequest(url, params, headers);
            fail("Expected RequestCreationException");
        } catch (RequestCreationException e) {
            assertEquals(exception, e.getCause());
        }

        verify(objectMapper).writeValueAsString(params);
        verifyZeroInteractions(httpEntityFactory);
    }

    @Test
    public void shouldReturnMultipartFormRequest() throws Exception {
        String url = "http://localhost/";
        String filepath = "/path/to/file";
        String fileField = "file";

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-type", "multipart/form-data");
        headers.put("Authorization", "Bearer abcde12345");

        HttpEntity httpEntity = mock(HttpEntity.class);

        when(httpEntityFactory.createMultipartEntity(fileField, filepath)).thenReturn(httpEntity);

        HttpPost request = httpRequestFactory.createMultipartPostRequest(url, headers, fileField, filepath);

        assertEquals(url, request.getURI().toString());
        assertEquals(httpEntity, request.getEntity());
        assertEquals("multipart/form-data", request.getFirstHeader("Content-type").getValue());
        assertEquals("Bearer abcde12345", request.getFirstHeader("Authorization").getValue());

        verify(httpEntityFactory).createMultipartEntity(fileField, filepath);
    }
}