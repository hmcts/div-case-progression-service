package uk.gov.hmcts.reform.divorce.petition.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.divorce.petition.domain.Petition;
import uk.gov.hmcts.reform.divorce.petition.service.PetitionService;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.DivorceSession;

import javax.ws.rs.core.MediaType;

@RestController
@RequestMapping(path = "/petition/version/1")
@Api(value = "DN Petition Service", consumes = "application/json", produces = "application/json")
public class PetitionController {

    private final PetitionService petitionService;

    @Autowired
    public PetitionController(PetitionService petitionService) {
        this.petitionService = petitionService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Retrieves a divorce case draft")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "A Petition exists. The petition is in the response body"),
        @ApiResponse(code = 404, message = "No petition found with the expected case state")})
    public ResponseEntity<Petition> retrievePetition(
        @RequestHeader("Authorization")
        @ApiParam(value = "JWT authorisation token issued by IDAM", required = true) final String jwtToken) {

        Petition petition = petitionService.retrievePetition(jwtToken);

        if (petition == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(petition);
    }

}
