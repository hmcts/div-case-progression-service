package uk.gov.hmcts.reform.divorce.transformservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.divorce.draftservice.service.DraftsService;

@Service
@Slf4j
public class PetitionService {

    @Autowired
    private DraftsService draftsService;

    public ResponseEntity<JsonNode> retrieveDraft(String jwt) {
        log.debug("Received request to retrieve a divorce session draft");
        JsonNode draft = draftsService.getDraft(jwt);
        if (draft != null) {
            return ResponseEntity.ok(draft);
        }
        return ResponseEntity.notFound().build();
    }
}
