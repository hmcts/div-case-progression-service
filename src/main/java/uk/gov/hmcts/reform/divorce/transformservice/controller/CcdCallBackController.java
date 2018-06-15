package uk.gov.hmcts.reform.divorce.transformservice.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.divorce.notifications.service.EmailService;
import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.CreateEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CoreCaseData;
import uk.gov.hmcts.reform.divorce.transformservice.domain.transformservice.CCDCallbackResponse;
import uk.gov.hmcts.reform.divorce.transformservice.service.UpdateService;
import uk.gov.hmcts.reform.divorce.validationservice.domain.ValidationResponse;
import uk.gov.hmcts.reform.divorce.validationservice.service.ValidationService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.MediaType;

@RestController
@RequestMapping(path = "/caseprogression")
@Api(value = "Transformation API", consumes = "application/json", produces = "application/json")
public class CcdCallBackController {

    private enum Courts {
        eastMidlands("East Midlands Regional Divorce Centre"),
        westMidlands("West Midlands Regional Divorce Centre"),
        southWest("South West Regional Divorce Centre"),
        northWest("North West Regional Divorce Centre");

        private String displayName;

        private Courts(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    @Autowired
    private UpdateService updateService;
    @Autowired
    private EmailService emailService;

    @Autowired
    private ValidationService validationService;

    @PostMapping(path = "/petition-issued", consumes = MediaType.APPLICATION_JSON,
        produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Generate and add a pdf of the petition to the case")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "A pdf of the petition has been generated and added to the case",
            response = CCDCallbackResponse.class),
        @ApiResponse(code = 400, message = "Bad Request")
        })
    public ResponseEntity<CCDCallbackResponse> addPdf(
        @RequestHeader(value = "Authorization", required = false) String authorizationToken,
        @RequestBody @ApiParam("CaseData") CreateEvent caseDetailsRequest) {

        ValidationResponse validationResponse = validationService.validateCoreCaseData(
            caseDetailsRequest.getCaseDetails().getCaseData()
        );
        if (isNotValidCoreCaseData(validationResponse)) {
            return ResponseEntity.ok(new CCDCallbackResponse(
                caseDetailsRequest.getCaseDetails().getCaseData(), 
                validationResponse.getErrors(), 
                validationResponse.getWarnings()
            ));
        }
        CoreCaseData coreCaseData = updateService.addPdf(caseDetailsRequest, authorizationToken);
        return ResponseEntity.ok(new CCDCallbackResponse(coreCaseData, new ArrayList<>(), new ArrayList<>()));
    }

    @PostMapping(path = "/petition-submitted",
        consumes = MediaType.APPLICATION_JSON,
        produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Generate/dispatch a notification email to the petitioner when the application is submitted")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "An email notification has been generated and dispatched",
            response = CCDCallbackResponse.class),
        @ApiResponse(code = 400, message = "Bad Request")
        })
    public ResponseEntity<CCDCallbackResponse> petitionSubmitted(
        @RequestHeader(value = "Authorization", required = false) String authorizationToken,
        @RequestBody @ApiParam("CaseData") CreateEvent caseDetailsRequest) {

        String petitionerEmail = caseDetailsRequest.getCaseDetails().getCaseData().getD8PetitionerEmail();

        if (StringUtils.isNotBlank(petitionerEmail)) {
            Map<String, String> templateVars = new HashMap<>();
            CoreCaseData        caseData     = caseDetailsRequest.getCaseDetails().getCaseData();

            templateVars.put("email address", petitionerEmail);
            templateVars.put("first name",    caseData.getD8PetitionerFirstName());
            templateVars.put("last name",     caseData.getD8PetitionerLastName());
            templateVars.put("RDC name",      Courts.valueOf(caseData.getD8DivorceUnit()).getDisplayName());
            templateVars.put("CCD reference", caseData.getD8caseReference());
            emailService.sendSubmissionNotificationEmail(petitionerEmail, templateVars);
        }
  
        return ResponseEntity.ok(new CCDCallbackResponse(null, new ArrayList<>(), new ArrayList<>()));
    }
  
    private boolean isNotValidCoreCaseData(ValidationResponse response) {
        boolean hasErrors = response.getErrors() != null && response.getErrors().size() > 0;
        boolean hasWarnings = response.getWarnings() != null && response.getWarnings().size() > 0;
        
        return hasErrors || hasWarnings;
    }
}
