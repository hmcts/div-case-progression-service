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
import java.util.stream.Collectors;

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
        List<Map<String, Object>> nonRejectedCases = getAllNonRejectedCases(caseData);

        if (CollectionUtils.isEmpty(nonRejectedCases) || nonRejectedCases.size() == 0) {

            DraftList draftList = draftStoreClient.getAll(jwt, secret);
            Optional<Draft> divorceDraft = findDivorceDraft(jwt, secret, draftList);
            log.info("Checking Draftstore for the saved draft for userId {}", userId);

            return DraftResponseFactory.buildDraftResponseFromDraft(divorceDraft.orElse(null));

        } else {

            log.info("Checking CCD for an existing case as draft not found for userId {}", userId);
            return DraftResponseFactory.buildDraftResponseFromCaseData(nonRejectedCases);
        }
    }

    // what happens if listOfCasesInCCD is empty?   & return this back to private?
    protected List<Map<String, Object>> getAllNonRejectedCases(List<Map<String, Object>> listOfCasesInCCD) {

        List<Map<String, Object>> listOfNonRejectedCasesInCCD = listOfCasesInCCD.stream()
            .filter((Map<String, Object> state) -> state.get("state") != "rejected")
            .collect(Collectors.toList());

        return listOfNonRejectedCasesInCCD;
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
                    return findDivorceDraft(jwt, secret,
                        draftStoreClient.getAll(jwt, secret, draftList.getPaging().getAfter()));
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
