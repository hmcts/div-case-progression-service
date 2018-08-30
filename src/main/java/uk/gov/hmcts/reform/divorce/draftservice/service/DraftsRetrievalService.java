package uk.gov.hmcts.reform.divorce.draftservice.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.divorce.draftservice.client.DraftStoreClient;
import uk.gov.hmcts.reform.divorce.draftservice.domain.Draft;
import uk.gov.hmcts.reform.divorce.draftservice.domain.DraftList;
import uk.gov.hmcts.reform.divorce.draftservice.domain.DraftsResponse;
import uk.gov.hmcts.reform.divorce.draftservice.factory.DraftModelFactory;
import uk.gov.hmcts.reform.divorce.draftservice.factory.DraftResponseFactory;
import uk.gov.hmcts.reform.divorce.transformservice.client.RetrieveCcdClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
class DraftsRetrievalService {

    private final DraftModelFactory modelFactory;
    private final DraftStoreClient draftStoreClient;
    private final RetrieveCcdClient retrieveCcdClient;

    @Autowired
    public DraftsRetrievalService(DraftModelFactory modelFactory,
                                  DraftStoreClient draftStoreClient,
                                  RetrieveCcdClient retrieveCcdClient) {
        this.modelFactory = modelFactory;
        this.draftStoreClient = draftStoreClient;
        this.retrieveCcdClient = retrieveCcdClient;
    }

    protected DraftsResponse getDraft(String jwt, String userId, String secret) {
        log.info("Retrieving a divorce session draft for userId {}", userId);

        List<Map<String, Object>> caseData = retrieveCcdClient.getCases(userId, jwt);

        if (CollectionUtils.isNotEmpty(caseData)) {
            log.info("CCD has a draft for userId {}", userId);
            return DraftResponseFactory.buildDraftResponseFromCaseData(caseData);
        } else {
            DraftList draftList = draftStoreClient.getAll(jwt, secret);
            Optional<Draft> divorceDraft = findDivorceDraft(jwt, secret, draftList);
            log.info("Returning the saved draft data for userId {}", userId);
            return DraftResponseFactory.buildDraftResponseFromDraft(divorceDraft.orElse(null));
        }
    }

    private Optional<Draft> findDivorceDraft(String jwt, String secret, DraftList draftList) {
        if (draftList != null && !draftList.getData().isEmpty()) {
            Optional<Draft> divorceDraft = draftList.getData().stream()
                .filter(draft -> modelFactory.isDivorceDraft(draft))
                .findFirst();
            if (!divorceDraft.isPresent()) {
                if (draftList.getPaging().getAfter() != null) {
                    log.info("Divorce session draft could not be found on the current page with drafts. "
                        + "Going to next page");
                    return findDivorceDraft(jwt, secret,
                        draftStoreClient.getAll(jwt, secret, draftList.getPaging().getAfter()));
                }else {
                    log.info("no draft draftList.getPaging().getAfter() is not null");
                }
            } else {
                log.info("Divorce session draft found");
                return divorceDraft;
            }
        }
        log.info("Divorce session draft could not be found");
        return Optional.empty();
    }
}
