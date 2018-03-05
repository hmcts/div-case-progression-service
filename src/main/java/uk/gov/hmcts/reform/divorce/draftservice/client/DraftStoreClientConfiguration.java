package uk.gov.hmcts.reform.divorce.draftservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DraftStoreClientConfiguration {

    private static final String ALL_DRAFTS_URL_TEMPLATE = "%s/drafts";
    private static final String ALL_DRAFTS_WITH_AFTER_URL_TEMPLATE = "%s/drafts/?after=%s";
    private static final String SINGLE_DRAFT_URL_TEMPLATE = "%s/drafts/%s";

    @Value("${draft.store.api.baseUrl}")
    private String draftStoreBaseUrl;

    public String getAllDraftsUrl() {
        return String.format(ALL_DRAFTS_URL_TEMPLATE, draftStoreBaseUrl);
    }

    public String getAllDraftsUrl(String after) {
        if (after == null) {
            return getAllDraftsUrl();
        }
        return String.format(ALL_DRAFTS_WITH_AFTER_URL_TEMPLATE, draftStoreBaseUrl, after);
    }

    public String getSingleDraftUrl(String draftId) {
        return String.format(SINGLE_DRAFT_URL_TEMPLATE, draftStoreBaseUrl, draftId);
    }
}
