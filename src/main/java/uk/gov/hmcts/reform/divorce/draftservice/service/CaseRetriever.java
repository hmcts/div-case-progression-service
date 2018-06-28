package uk.gov.hmcts.reform.divorce.draftservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.divorce.transformservice.client.RetrieveCcdClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class CaseRetriever {

    private final RetrieveCcdClient retrieveCcdClient;
    private final Boolean checkCcdEnabled;

    @Autowired
    public CaseRetriever(RetrieveCcdClient retrieveCcdClient,
                         @Value("${draft.api.ccd.check.enabled}") String checkCcdEnabled) {
        this.retrieveCcdClient = retrieveCcdClient;
        this.checkCcdEnabled = Boolean.valueOf(checkCcdEnabled);
    }

    public List<Map<String, Object>> getCases(String userId, String jwt) {

        if (!checkCcdEnabled) {
            return Collections.emptyList();
        }

        List<Map<String, Object>> cases = retrieveCcdClient.getCases(userId, jwt);

        if (cases.isEmpty()) {
            log.debug("No cases found");
            return Collections.emptyList();
        }

        log.info(String.format("Found %s cases", cases.size()));

        return cases;
    }
}
