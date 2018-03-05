package uk.gov.hmcts.reform.divorce.draftservice.client;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DraftStoreClientConfigurationTest {

    private static final String DRAFT_ID = "123";

    @Value("${draft.store.api.baseUrl}")
    private String draftsApiBaseUrl;

    @Autowired
    private DraftStoreClientConfiguration underTest;

    @Test
    public void getAllDraftsUrlShouldReturnTheCorrectUrl() {
        assertEquals(draftsApiBaseUrl + "/drafts", underTest.getAllDraftsUrl());
    }

    @Test
    public void getAllDraftsUrlShouldReturnTheCorrectUrlWithAfterRequestParam() {
        assertEquals(draftsApiBaseUrl + "/drafts/?after=10", underTest.getAllDraftsUrl("10"));
    }

    @Test
    public void getAllDraftsUrlShouldReturnTheCorrectUrlWhenAfterIsNull() {
        assertEquals(draftsApiBaseUrl + "/drafts", underTest.getAllDraftsUrl(null));
    }

    @Test
    public void getSingleDraftUrlShouldReturnTheCorrectUrl() {
        assertEquals(draftsApiBaseUrl + "/drafts/" + DRAFT_ID, underTest.getSingleDraftUrl(DRAFT_ID));
    }
}