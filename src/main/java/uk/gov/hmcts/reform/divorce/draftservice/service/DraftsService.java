package uk.gov.hmcts.reform.divorce.draftservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.divorce.draftservice.client.DraftStoreClient;
import uk.gov.hmcts.reform.divorce.draftservice.domain.Draft;
import uk.gov.hmcts.reform.divorce.draftservice.domain.DraftsResponse;
import uk.gov.hmcts.reform.divorce.draftservice.factory.DraftModelFactory;
import uk.gov.hmcts.reform.divorce.draftservice.factory.EncryptionKeyFactory;
import uk.gov.hmcts.reform.divorce.idam.models.UserDetails;
import uk.gov.hmcts.reform.divorce.idam.services.UserService;

import java.util.Optional;


@Service
@Slf4j
public class DraftsService {

    private final DraftsRetrievalService draftsRetrievalService;
    private final UserService userService;
    private final EncryptionKeyFactory encryptionKeyFactory;
    private final DraftStoreClient draftStoreClient;
    private final DraftModelFactory draftModelFactory;

    @Autowired
    public DraftsService(DraftsRetrievalService draftsRetrievalService,
                         UserService userService,
                         EncryptionKeyFactory encryptionKeyFactory,
                         DraftStoreClient draftStoreClient,
                         DraftModelFactory draftModelFactory) {
        this.draftsRetrievalService = draftsRetrievalService;
        this.userService = userService;
        this.encryptionKeyFactory = encryptionKeyFactory;
        this.draftStoreClient = draftStoreClient;
        this.draftModelFactory = draftModelFactory;
    }

    public void saveDraft(String jwt, JsonNode data) {
        UserDetails userDetails = userService.getUserDetails(jwt);
        String secret = encryptionKeyFactory.createEncryptionKey(userDetails.getId());
        DraftsResponse draftsResponse = draftsRetrievalService.getDivorceDraft(jwt, secret);
        if (draftsResponse.isDraft()) {
            log.debug("Updating the existing divorce session draft");
            draftStoreClient.updateDraft(
                    jwt,
                    draftsResponse.getDraftId(),
                    secret,
                    draftModelFactory.updateDraft(data));
        } else {
            log.debug("Creating a new divorce session draft");
            draftStoreClient.createDraft(
                    jwt,
                    secret,
                    draftModelFactory.createDraft(data));
        }
    }

    public void deleteDraft(String jwt) {
        log.debug("Deleting the divorce session draft");
        UserDetails userDetails = userService.getUserDetails(jwt);
        DraftsResponse draftsResponse = draftsRetrievalService.getDivorceDraft(jwt,
                encryptionKeyFactory.createEncryptionKey(userDetails.getId()));
        if (draftsResponse.isDraft()) {
            draftStoreClient.deleteDraft(jwt, draftsResponse.getDraftId());
        }
    }

    public JsonNode getDraft(String jwt) {
        return draftsRetrievalService.getDraft(jwt);
    }
}
