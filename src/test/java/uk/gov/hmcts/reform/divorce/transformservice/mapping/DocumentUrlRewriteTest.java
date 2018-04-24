package uk.gov.hmcts.reform.divorce.transformservice.mapping;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DocumentUrlRewriteTest {

    @Value("${document.management.store.url}")
    private String documentManagementStoreUrl;

    @Autowired
    private DocumentUrlRewrite urlRewrite;


    @Test
    public void getDocumentUrlShouldReplaceOldDocumentManagementUrl() {
        String oldUrl = "https://api-gateway.preprod.dm.reform.hmcts.net/documents/95f20d8e-7186-4180-98e9-f7400f7c6527";

        String newUrl = urlRewrite.getDocumentUrl(oldUrl);

        assertEquals(String.format("%s/documents/95f20d8e-7186-4180-98e9-f7400f7c6527", documentManagementStoreUrl),
            newUrl);
    }

    @Test
    public void getDocumentUrlShouldNotRepalceNewDocumentManagementUrl() {
        String url = String.format("%s/documents/95f20d8e-7186-4180-98e9-f7400f7c6527", documentManagementStoreUrl);

        String updatedUrl = urlRewrite.getDocumentUrl(url);

        assertEquals(url, updatedUrl);
    }

}
