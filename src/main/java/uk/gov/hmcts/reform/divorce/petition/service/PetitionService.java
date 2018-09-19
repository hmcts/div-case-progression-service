package uk.gov.hmcts.reform.divorce.petition.service;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.divorce.petition.domain.Petition;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CaseDetails;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.DivorceSession;

@Service
@Slf4j
public class PetitionService  {

    @Autowired
    private CmsApiClient cmsApiClient;
    @Autowired
    private CfsApiClient cfsApiClient;

    public Petition retrievePetition(String jwtToken) {

        CaseDetails caseDetails;
        try {
            caseDetails = cmsApiClient.retrieveCaseDetails(jwtToken);
        } catch (FeignException e) {
            log.info(String.format(
                "Caught exception when retrieving case details from CMS with HTTP code=%s", e.status()));
            return null;
        }

        if (caseDetails == null) {
            log.info("Unable to retrieve case details for this user");
            return null;
        }

        if (!caseDetails.getState().equalsIgnoreCase("AwaitingDecreeNisi")) {
            log.info("Case state is not AwaitingDecreeNisi");
            return null;
        }

        DivorceSession divorceSession = cfsApiClient.transform(caseDetails.getCaseData());

        return new Petition(caseDetails.getCaseId(), divorceSession);
    }
}
