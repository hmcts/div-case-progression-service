package uk.gov.hmcts.reform.divorce.transformservice.docker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import uk.gov.hmcts.reform.divorce.transformservice.docker.exception.RequestCreationException;

import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequestFactory {
    private final HttpEntityFactory httpEntityFactory;
    private final ObjectMapper objectMapper;

    HttpRequestFactory(HttpEntityFactory httpEntityFactory, ObjectMapper objectMapper) {
        this.httpEntityFactory = httpEntityFactory;
        this.objectMapper = objectMapper;
    }

    HttpPost createPostRequest(String url, Map<String, String> params) {

        String content = params.entrySet().stream()
            .map(e -> String.join("=", e.getKey(), e.getValue()))
            .collect(Collectors.toList())
            .stream()
            .collect(Collectors.joining("&"));

        HttpPost request = new HttpPost(url);

        request.setHeader("Content-type", "application/x-www-form-urlencoded");
        request.setEntity(httpEntityFactory.createEntity(content));

        return request;
    }

    HttpPut createJsonPutRequest(String url, Map<String, String> params, Map<String, String> headers) {

        HttpPut request = new HttpPut(url);

        try {
            String content = objectMapper.writeValueAsString(params);
            headers.forEach(request::addHeader);
            request.setEntity(httpEntityFactory.createEntity(content));
            return request;
        } catch (JsonProcessingException e) {
            throw new RequestCreationException(e);
        }
    }

    public HttpPost createMultipartPostRequest(String url, Map<String, String> headers, String fileField,
                                               String filepath) {
        HttpPost request = new HttpPost(url);

        headers.forEach(request::addHeader);
        request.setEntity(httpEntityFactory.createMultipartEntity(fileField, filepath));
        return request;
    }
}
