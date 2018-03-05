package uk.gov.hmcts.reform.divorce.transformservice.docker;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import static org.junit.Assert.*;

public class HttpEntityFactoryTest {

    private HttpEntityFactory httpEntityFactory = new HttpEntityFactory();

    @Test
    public void shouldReturnContentAsEntity() throws Exception {
        String content = "test-content";

        HttpEntity entity = httpEntityFactory.createEntity(content);

        assertEquals(content, EntityUtils.toString(entity));
    }

    @Test
    public void shouldReturnFileAsEntity() throws Exception {
        String fileField = "file";
        String filepath = getClass().getResource("/fixtures/divorce/update-request-body.json").getFile();

        HttpEntity httpEntity = httpEntityFactory.createMultipartEntity(fileField, filepath);

        assertTrue(httpEntity.getContentType().getValue().startsWith("multipart/form-data"));
    }
}