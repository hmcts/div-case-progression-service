package uk.gov.hmcts.reform.divorce.transformservice.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CaseDataContent;
import uk.gov.hmcts.reform.divorce.transformservice.service.RetrieveCaseService;

import javax.ws.rs.core.MediaType;
import java.util.List;

@RestController
@RequestMapping(path = "/caseprogression")
@Api(value = "Transformation API", consumes = "application/json", produces = "application/json")
public class CcdRetrieveCaseController {

    @Autowired
    private RetrieveCaseService retrieveCaseService;

    @GetMapping(path = "/retrieve-case", consumes = MediaType.APPLICATION_JSON,
        produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Generate and add a pdf of the petition to the case")
    public List<CaseDataContent> retrieveCase(
        @RequestHeader(value = "Authorization", required = false) String authorizationToken ) {
        return retrieveCaseService.getCase(authorizationToken, "");
    }
}
