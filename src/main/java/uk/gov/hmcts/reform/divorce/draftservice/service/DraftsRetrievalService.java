package uk.gov.hmcts.reform.divorce.draftservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.divorce.draftservice.client.DraftStoreClient;
import uk.gov.hmcts.reform.divorce.draftservice.domain.Draft;
import uk.gov.hmcts.reform.divorce.draftservice.domain.DraftList;
import uk.gov.hmcts.reform.divorce.draftservice.domain.DraftsResponse;
import uk.gov.hmcts.reform.divorce.draftservice.factory.DraftModelFactory;
import uk.gov.hmcts.reform.divorce.draftservice.factory.DraftResponseFactory;

import java.util.Optional;

@Service
@Slf4j
class DraftsRetrievalService {

    private final DraftModelFactory modelFactory;
    private final DraftStoreClient draftStoreClient;
    private final AwaitingPaymentCaseRetriever awaitingPaymentCaseRetriever;

    @Autowired
    public DraftsRetrievalService(DraftModelFactory modelFactory,
                                  DraftStoreClient draftStoreClient,
                                  AwaitingPaymentCaseRetriever awaitingPaymentCaseRetriever) {
        this.modelFactory = modelFactory;
        this.draftStoreClient = draftStoreClient;
        this.awaitingPaymentCaseRetriever = awaitingPaymentCaseRetriever;
    }

    protected DraftsResponse getDraft(String jwt, String userId, String secret) {
        log.info("Retrieving a divorce session draft");
        DraftList draftList = draftStoreClient.getAll(jwt, secret);

        Optional<Draft> divorceDraft = findDivorceDraft(jwt, secret, draftList);

        if (divorceDraft.isPresent()) {
            log.info("Returning the saved draft data");
            return DraftResponseFactory.buildDraftResponseFromDraft(divorceDraft.get());
        } else {
            log.info("Checking CCD for an existing case as draft not found");
            return DraftResponseFactory.buildDraftResponseFromCaseData(
                awaitingPaymentCaseRetriever.getCases(userId, jwt));
        }
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
                    return findDivorceDraft(jwt, secret, draftStoreClient.getAll(jwt, secret, draftList.getPaging().getAfter()));
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
