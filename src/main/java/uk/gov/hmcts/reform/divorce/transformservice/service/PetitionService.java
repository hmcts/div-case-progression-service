package uk.gov.hmcts.reform.divorce.transformservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.divorce.draftservice.service.DraftsService;

import java.util.Optional;

@Service
@Slf4j
public class PetitionService {

    @Autowired
    private DraftsService draftsService;

    public JsonNode retrieveCase(String jwt) {

        // set to true to use new functionality
        boolean checkDraftsAndThenCCD = true;

        if(checkDraftsAndThenCCD){
            return retrieveDraft(jwt);
        }else{
            return retrieveCaseFromDraftStore(jwt);
        }
    }

    public JsonNode retrieveCaseFromDraftStore(String jwt) {
        log.debug("Received request to retrieve a divorce session draft");
        return Optional.ofNullable(draftsService.getDraft(jwt)).orElse(null);
    }

    // Checks draft store for case and if not found checks CCD
    public JsonNode retrieveDraft(String jwt) {
        log.debug("Received request to retrieve a divorce session draft");
        return Optional.ofNullable(draftsService.getDraft(jwt)).orElse(null);

        // 2) call RetrieveService and get "status"
        // return retrieveCaseFromCCD();

            /* retrieveFromDraftStore()
        |
        | \
        |  \  if found
        |              \ return case
        |
         \
          \ else call
                       \
                         getCaseFromCCD()
                            \
                             |\
                             |   if found
                             |           \
                             |             return case
                              \
                                else
                                     \
                                      return nada
        */
    }
}

/*
Summary
A modified endpoint is required to allow a case for a given user to be retrieved from CCD if it is not in the draft store.

1. A case on CCD for a given user ID can be retrieved
2. It is possible to determine what status the retrieved case is in
3. Any errors encountered in response to a request are logged
 */
