package uk.gov.hmcts.reform.divorce.petition.api;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CaseDetails;

@FeignClient(name = "cms-api", url = "${cms.api.url}")
public interface CmsApiClient {

    @RequestMapping(method = RequestMethod.GET, value = "casemaintenance/version/1/retrievePetition?checkCcd=true")
    CaseDetails retrieveCaseDetails(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorisation);
}
