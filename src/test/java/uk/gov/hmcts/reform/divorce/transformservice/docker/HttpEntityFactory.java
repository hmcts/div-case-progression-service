package uk.gov.hmcts.reform.divorce.transformservice.docker;

import org.apache.http.HttpEntity;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.io.ByteArrayInputStream;
import java.io.File;

public class HttpEntityFactory {

    HttpEntity createEntity(String content) {
        BasicHttpEntity entity = new BasicHttpEntity();
        entity.setContent(new ByteArrayInputStream(content.getBytes()));

        return entity;
    }

    public HttpEntity createMultipartEntity(String fileField, String filepath) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.STRICT);
        builder.addBinaryBody(fileField, new File(filepath), ContentType.DEFAULT_BINARY, filepath);

        return builder.build();
    }
}
