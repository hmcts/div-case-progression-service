package uk.gov.hmcts.reform.divorce.validationservice.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import uk.gov.hmcts.reform.divorce.validationservice.domain.ValidationRequest;
import uk.gov.hmcts.reform.divorce.validationservice.domain.ValidationResponse;
import uk.gov.hmcts.reform.divorce.validationservice.service.ValidationService;

import javax.ws.rs.core.MediaType;

@RestController
@Api(value = "Validation API", consumes = "application/json", produces = "application/json")
public class ValidationController {

    @Autowired
    private ValidationService validationService;

    @PostMapping(path = "/validate", consumes = MediaType.APPLICATION_JSON,
        produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Validate the data sent by the request body")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "A list of warnings and/or errors is returned",
            response = ValidationResponse.class)
        })
    public ResponseEntity<ValidationResponse> validate(
        @RequestBody @ApiParam("ValidationRequest") ValidationRequest validationRequest) {

        ValidationResponse validationResponse = validationService.validate(validationRequest);

        return ResponseEntity.ok(validationResponse);
    }
}
