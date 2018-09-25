package uk.gov.hmcts.reform.divorce.petition.api;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CoreCaseData;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.DivorceSession;

@FeignClient(name = "cfs-api", url = "${cfs.api.url}")
public interface CfsApiClient {

    @RequestMapping(method = RequestMethod.POST, value = "caseformatter/version/1/to-divorce-format")
    DivorceSession transform(@RequestBody CoreCaseData coreCaseData);
}
