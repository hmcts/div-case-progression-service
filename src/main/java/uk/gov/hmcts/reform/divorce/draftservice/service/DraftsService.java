package uk.gov.hmcts.reform.divorce.draftservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.divorce.draftservice.client.DraftStoreClient;
import uk.gov.hmcts.reform.divorce.draftservice.domain.Draft;
import uk.gov.hmcts.reform.divorce.draftservice.domain.DraftList;
import uk.gov.hmcts.reform.divorce.draftservice.factory.DraftModelFactory;
import uk.gov.hmcts.reform.divorce.draftservice.factory.EncryptionKeyFactory;
import uk.gov.hmcts.reform.divorce.idam.services.UserService;

import java.util.Optional;

@Service
@Slf4j
public class DraftsService {

    @Autowired
    private DraftModelFactory modelFactory;

    @Autowired
    private EncryptionKeyFactory keyFactory;

    @Autowired
    private DraftStoreClient client;

    public void saveDraft(String jwt, JsonNode data) {
        String secret = keyFactory.createEncryptionKey(jwt);
        Optional<Draft> divorceDraft = getDivorceDraft(jwt, secret);
        if (divorceDraft.isPresent()) {
            log.debug("Updating the existing divorce session draft");
            client.updateDraft(
                jwt,
                divorceDraft.get().getId(),
                secret,
                modelFactory.updateDraft(data));
        } else {
            log.debug("Creating a new divorce session draft");
            client.createDraft(
                jwt,
                secret,
                modelFactory.createDraft(data));
        }
    }

    public JsonNode getDraft(String jwt) {
        log.debug("Retrieving a divorce session draft");
        Optional<Draft> divorceDraft = getDivorceDraft(jwt, keyFactory.createEncryptionKey(jwt));
        if (divorceDraft.isPresent()) {
            log.debug("Returning the saved divorce session draft");
            return divorceDraft.get().getDocument();
        }
        log.debug("There is no saved divorce session draft");
        return null;
    }

    public void deleteDraft(String jwt) {
        log.debug("Deleting the divorce session draft");
        Optional<Draft> divorceDraft = getDivorceDraft(jwt, keyFactory.createEncryptionKey(jwt));
        divorceDraft.ifPresent(draft -> client.deleteDraft(jwt, draft.getId()));
    }

    private Optional<Draft> getDivorceDraft(String jwt, String secret) {
        log.debug("Looking for a saved divorce session draft");
        DraftList draftList = client.getAll(jwt, secret);

        return findDivorceDraft(jwt, secret, draftList);
    }

    private Optional<Draft> findDivorceDraft(String jwt, String secret, DraftList draftList) {
        if (draftList != null && !draftList.getData().isEmpty()) {
            Optional<Draft> divorceDraft = draftList.getData().stream()
                .filter(draft -> modelFactory.isDivorceDraft(draft))
                .findFirst();
            if (!divorceDraft.isPresent()) {
                if (draftList.getPaging().getAfter() != null) {
                    log.debug("Divorce session draft could not be found on the current page with drafts. "
                        + "Going to next page");
                    draftList = client.getAll(jwt, secret, draftList.getPaging().getAfter());
                    return findDivorceDraft(jwt, secret, draftList);
                }
            } else {
                log.debug("Divorce session draft found");
                return divorceDraft;
            }
        }
        log.debug("Divorce session draft could not be found");
        return Optional.empty();
    }
}
