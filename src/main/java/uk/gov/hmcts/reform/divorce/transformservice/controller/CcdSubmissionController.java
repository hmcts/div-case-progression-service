package uk.gov.hmcts.reform.divorce.transformservice.controller;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.DivorceEventSession;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.DivorceSession;
import uk.gov.hmcts.reform.divorce.transformservice.domain.transformservice.CCDResponse;
import uk.gov.hmcts.reform.divorce.transformservice.service.SubmissionService;
import uk.gov.hmcts.reform.divorce.transformservice.service.UpdateService;

import javax.ws.rs.core.MediaType;

@RestController
@RequestMapping(path = "/transformationapi")
@Api(value = "Transformation API", consumes = "application/json", produces = "application/json")
public class CcdSubmissionController {

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private UpdateService updateService;

    private static final String STATUS = "success";

    @PostMapping(path = "/version/1/submit", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Submits a divorce session to CCD")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Divorce session was submitted to CCD. The body payload indicates if the submit has been successful.", response = CCDResponse.class),
    })
    public ResponseEntity<CCDResponse> submitCase(
            @RequestBody @ApiParam(value = "The divorce session.", required = true) DivorceSession divorceSession,
            @RequestHeader("Authorization") @ApiParam(value = "JWT authorisation token issued by IDAM", required = true) final String jwt) {

        return ResponseEntity.ok(new CCDResponse(submissionService.submit(divorceSession, jwt), null, STATUS));
    }

    @PostMapping(path = "/version/1/updateCase/{caseId}", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Updates a divorce session in CCD")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "A request to update the divorce session was sent to CCD. The body payload indicates if the request has been successful.", response = CCDResponse.class),
    })
    public ResponseEntity<CCDResponse> updateCase(
            @RequestBody @ApiParam("The update event that requires the resubmission to CCD") DivorceEventSession divorceEventSession,
            @PathVariable("caseId") @ApiParam("Unique identifier of the session that was submitted to CCD") Long caseId,
            @RequestHeader("Authorization") @ApiParam(value = "JWT authorisation token issued by IDAM", required = true) final String jwt) {

        return ResponseEntity.ok(new CCDResponse(updateService.update(caseId, divorceEventSession, jwt), null, STATUS));
    }

}
