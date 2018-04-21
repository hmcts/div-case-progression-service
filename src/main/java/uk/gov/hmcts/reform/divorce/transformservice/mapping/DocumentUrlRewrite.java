package uk.gov.hmcts.reform.divorce.transformservice.mapping;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DocumentUrlRewrite {

    private static final String URL_REGEX = ".*?(/documents/.*)";

    @Value("${document.management.store.url}")
    private String documentManagementStoreUrl;

    public String getDocumentUrl(String url) {
        if (!url.startsWith(documentManagementStoreUrl)) {
            return url.replaceAll(URL_REGEX, documentManagementStoreUrl + "$1");
        }

        return url;
    }
}
