package uk.gov.hmcts.reform.divorce;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.reform.divorce.transformservice.mapping.DocumentCollectionMapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CaseProgressionApplication.class)
public class CaseProgressionApplicationTest {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DocumentCollectionMapper documentCollectionMapper;

    @Test
    public void contextLoads() throws Exception {

        assertEquals(1, restTemplate.getMessageConverters().size());
        assertTrue(restTemplate.getMessageConverters().get(0) instanceof MappingJackson2HttpMessageConverter);

        assertNotNull(documentCollectionMapper);
    }
}
