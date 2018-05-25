/*
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
import uk.gov.hmcts.reform.divorce.idam.models.UserDetails;
import uk.gov.hmcts.reform.divorce.idam.services.UserService;

import java.util.Optional;

*/
/**
 * TODO: write about the class
 *
 * @author wjtlopez
 *//*

@Service
@Slf4j
public class DraftRetrievalService {

    private final UserService userService;
    private final EncryptionKeyFactory encryptionKeyFactory;
    private final DraftStoreClient draftStoreClient;
    private DraftModelFactory draftModelFactory;

    @Autowired
    public DraftRetrievalService(UserService userService, EncryptionKeyFactory encryptionKeyFactory, DraftStoreClient draftStoreClient) {
        this.userService = userService;
        this.encryptionKeyFactory = encryptionKeyFactory;
        this.draftStoreClient = draftStoreClient;
    }

    public JsonNode getDraft(String jwt) {

        UserDetails userDetails = userService.getUserDetails(jwt);

        String encryptionKey = encryptionKeyFactory.createEncryptionKey(userDetails.getId());

        DraftList draftList = draftStoreClient.getAll(jwt, encryptionKey);




        return findDivorceDraft(jwt, encryptionKey, draftList);

    }

    private Optional<Draft> findDivorceDraft(String jwt, String secret, DraftList draftList) {
        if (draftList != null && !draftList.getData().isEmpty()) {
            Optional<Draft> divorceDraft = draftList.getData().stream()
                    .filter(draft -> draftModelFactory.isDivorceDraft(draft))
                    .findFirst();
            if (!divorceDraft.isPresent()) {
                if (draftList.getPaging().getAfter() != null) {
                    log.debug("Divorce session draft could not be found on the current page with drafts. "
                            + "Going to next page");
                    draftList = draftStoreClient.getAll(jwt, secret, draftList.getPaging().getAfter());
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
*/
