package uk.gov.hmcts.reform.divorce.draftservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.divorce.draftservice.client.DraftStoreClient;
import uk.gov.hmcts.reform.divorce.draftservice.domain.Draft;
import uk.gov.hmcts.reform.divorce.draftservice.domain.DraftList;
import uk.gov.hmcts.reform.divorce.draftservice.domain.DraftsResponse;
import uk.gov.hmcts.reform.divorce.draftservice.factory.DraftModelFactory;
import uk.gov.hmcts.reform.divorce.draftservice.factory.DraftResponseFactory;
import uk.gov.hmcts.reform.divorce.draftservice.factory.EncryptionKeyFactory;
import uk.gov.hmcts.reform.divorce.idam.models.UserDetails;
import uk.gov.hmcts.reform.divorce.idam.services.UserService;
import uk.gov.hmcts.reform.divorce.transformservice.client.RetrieveCcdClient;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CaseDataContent;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
class DraftsRetrievalService {

    private final DraftModelFactory modelFactory;
    private final EncryptionKeyFactory keyFactory;
    private final DraftStoreClient client;
    private final UserService userService;
    private final RetrieveCcdClient retrieveCcdClient;
    private final DraftResponseFactory draftResponseFactory;
    private final boolean checkCcdEnabled;

    @Autowired
    public DraftsRetrievalService(DraftModelFactory modelFactory,
                                  EncryptionKeyFactory keyFactory,
                                  DraftStoreClient client,
                                  UserService userService,
                                  RetrieveCcdClient retrieveCcdClient,
                                  DraftResponseFactory draftResponseFactory,
                                  @Value("${draft.api.ccd.check.enabled}") boolean checkCcdEnabled) {
        this.modelFactory = modelFactory;
        this.keyFactory = keyFactory;
        this.client = client;
        this.userService = userService;
        this.retrieveCcdClient = retrieveCcdClient;
        this.draftResponseFactory = draftResponseFactory;
        this.checkCcdEnabled = checkCcdEnabled;
    }

    protected JsonNode getDraft(String jwt) {
        log.debug("Retrieving a divorce session draft");
        UserDetails userDetails = userService.getUserDetails(jwt);
        DraftsResponse draftsResponse = getDivorceDraft(jwt, keyFactory.createEncryptionKey(userDetails.getId()));

        JsonNode draftResponseData = draftsResponse.getData();
        if (draftResponseData == null) {
            log.debug("There is no saved divorce session draft or case in ccd");
            return null;
        } else {
            log.debug(String.format("Returning the %s", draftsResponse.isDraft() ? "saved draft data" :
                    "existing case details awaiting payment"));
            return draftResponseData;
        }
    }

    protected DraftsResponse getDivorceDraft(String jwt, String secret) {
        log.debug("Looking for a saved divorce session draft");
        DraftList draftList = client.getAll(jwt, secret);

        Optional<Draft> divorceDraft = findDivorceDraft(jwt, secret, draftList);

        if (divorceDraft.isPresent()) {
            return draftResponseFactory.buildDraftResponseFromDraft(divorceDraft.get());
        } else if (checkCcdEnabled) {
            List<LinkedHashMap> listOfCases = retrieveCcdClient.getCase(userService.getUserDetails(jwt).getId(), jwt);
            return draftResponseFactory.buildDraftResponseFromCaseData(listOfCases);
        }

        return null;
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
