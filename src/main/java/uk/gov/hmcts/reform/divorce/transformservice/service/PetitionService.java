package uk.gov.hmcts.reform.divorce.transformservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.divorce.draftservice.service.DraftsService;

import java.util.Optional;

@Service
@Slf4j
public class PetitionService {

    @Autowired
    private DraftsService draftsService;

    public JsonNode retrieveDraft(String jwt) {
        log.debug("Received request to retrieve a divorce session draft");
        return Optional.ofNullable(draftsService.getDraft(jwt)).orElse(null);
    }
}
