package uk.gov.hmcts.reform.divorce.draftservice.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.reform.divorce.draftservice.domain.CreateDraft;
import uk.gov.hmcts.reform.divorce.draftservice.domain.DraftList;
import uk.gov.hmcts.reform.divorce.draftservice.domain.UpdateDraft;
import uk.gov.hmcts.reform.divorce.draftservice.exception.DraftStoreUnavailableException;

@Component
@Slf4j
public class DraftStoreClient {
    private static final String DRAFT_STORE_IS_UNAVAILABLE_ERROR_MESSAGE = "Draft store is unavailable";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DraftStoreClientConfiguration clientConfiguration;

    @Autowired
    private DraftStoreHttpEntityFactory httpEntityFactory;

    public DraftList getAll(String jwt, String secret) {
        return getAll(jwt, secret, null);
    }

    public DraftList getAll(String jwt, String secret, String after) {
        log.debug("Sending a request to the draft store to get a page with drafts");
        try {
            ResponseEntity<DraftList> draftListResponse = restTemplate.exchange(
                clientConfiguration.getAllDraftsUrl(after),
                HttpMethod.GET,
                httpEntityFactory.createRequestEntityForDraft(jwt, secret),
                DraftList.class);
            return draftListResponse.getBody();
        } catch (ResourceAccessException | HttpServerErrorException e) {
            log.warn(DRAFT_STORE_IS_UNAVAILABLE_ERROR_MESSAGE, e.getMessage());
            throw new DraftStoreUnavailableException(DRAFT_STORE_IS_UNAVAILABLE_ERROR_MESSAGE);
        }
    }

    public void createDraft(String jwt, String secret, CreateDraft createDraft) {
        log.debug("Sending a request to the draft store to create a new divorce draft");
        try {
            restTemplate.exchange(
                clientConfiguration.getAllDraftsUrl(),
                HttpMethod.POST,
                httpEntityFactory.createRequestEntityForDraft(jwt, secret, createDraft),
                Void.class);
        } catch (ResourceAccessException | HttpServerErrorException e) {
            log.warn(DRAFT_STORE_IS_UNAVAILABLE_ERROR_MESSAGE, e.getMessage());
            throw new DraftStoreUnavailableException(DRAFT_STORE_IS_UNAVAILABLE_ERROR_MESSAGE);
        }
    }

    public void updateDraft(String jwt, String id, String secret, UpdateDraft updateDraft) {
        log.debug("Sending a request to the draft store to update the existing divorce draft");
        try {
            restTemplate.exchange(
                clientConfiguration.getSingleDraftUrl(id),
                HttpMethod.PUT,
                httpEntityFactory.createRequestEntityForDraft(jwt, secret, updateDraft),
                Void.class);
        } catch (ResourceAccessException | HttpServerErrorException e) {
            log.warn(DRAFT_STORE_IS_UNAVAILABLE_ERROR_MESSAGE, e.getMessage());
            throw new DraftStoreUnavailableException(DRAFT_STORE_IS_UNAVAILABLE_ERROR_MESSAGE);
        }
    }

    public void deleteDraft(String jwt, String id) {
        log.debug("Sending a request to the draft store to delete the existing divorce draft");


        System.out.println("\n\n\n\n\n\n");
        System.out.println("jwt = " + jwt);
        System.out.println("id = " + id);

        try {
            restTemplate.exchange(
                clientConfiguration.getSingleDraftUrl(id),
                HttpMethod.DELETE,
                httpEntityFactory.createRequestEntityForDraft(jwt),
                Void.class);
        } catch (ResourceAccessException | HttpServerErrorException e) {
            log.warn(DRAFT_STORE_IS_UNAVAILABLE_ERROR_MESSAGE, e.getMessage());
            throw new DraftStoreUnavailableException(DRAFT_STORE_IS_UNAVAILABLE_ERROR_MESSAGE);
        }
    }
}
