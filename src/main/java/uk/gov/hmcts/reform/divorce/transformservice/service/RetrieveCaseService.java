package uk.gov.hmcts.reform.divorce.transformservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.divorce.transformservice.client.CcdClient;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CaseDataContent;

import java.util.List;

@Component
@Slf4j
public class RetrieveCaseService {

    @Autowired
    private CcdClient ccdClient;

    /**
     * Get case by user id and query params
     *
     * @param userToken to be used of the searched
     * @param params used to search
     */
    public List<CaseDataContent> getCase(String userToken, String params) {

        return ccdClient.getCase(userToken, params);

    }
}
