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
import uk.gov.hmcts.reform.divorce.fees.models.Fee;
import uk.gov.hmcts.reform.divorce.fees.services.FeesAndPaymentService;
import uk.gov.hmcts.reform.divorce.notifications.service.EmailService;
import uk.gov.hmcts.reform.divorce.pay.services.PaymentService;
import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.CreateEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CoreCaseData;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.OrderSummary;
import uk.gov.hmcts.reform.divorce.transformservice.domain.transformservice.CCDCallbackResponse;
import uk.gov.hmcts.reform.divorce.transformservice.service.UpdateService;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import static java.time.format.DateTimeFormatter.ofPattern;

@RestController
@RequestMapping(path = "/caseprogression")
@Api(value = "Transformation API", consumes = "application/json", produces = "application/json")
public class CcdCallBackController {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private UpdateService updateService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private FeesAndPaymentService feesAndPaymentService;

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
            CoreCaseData caseData = caseDetailsRequest.getCaseDetails().getCaseData();

            templateVars.put("email address", petitionerEmail);
            templateVars.put("first name", caseData.getD8PetitionerFirstName());
            templateVars.put("last name", caseData.getD8PetitionerLastName());
            templateVars.put("RDC name", Courts.valueOf(
                caseData.getD8DivorceUnit().toUpperCase(Locale.UK)).getDisplayName()
            );
            templateVars.put("CCD reference", formatReferenceId(caseDetailsRequest.getCaseDetails().getCaseId()));

            emailService.sendSubmissionNotificationEmail(petitionerEmail, templateVars);
        }

        return ResponseEntity.ok(new CCDCallbackResponse(null, new ArrayList<>(), new ArrayList<>()));
    }

    private String formatReferenceId(String referenceId) {
        return String.format("%s-%s-%s-%s",
            referenceId.substring(0, 4),
            referenceId.substring(4, 8),
            referenceId.substring(8, 12),
            referenceId.substring(12));
    }

    @PostMapping(path = "/petition-issue-fees",
        consumes = MediaType.APPLICATION_JSON,
        produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Return a order summary for petition issue")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Petition issue fee amount is send to CCD as callback response",
            response = CCDCallbackResponse.class),
        @ApiResponse(code = 400, message = "Bad Request")
        })
    public ResponseEntity<CCDCallbackResponse> getPetitionIssueFees(@RequestBody @ApiParam("CaseData")
                                                                        CreateEvent caseDetailsRequest) {
        Fee issueFee = feesAndPaymentService.getPetitionIssueFee();
        CoreCaseData caseData = caseDetailsRequest.getCaseDetails().getCaseData();
        OrderSummary orderSummary = new OrderSummary();
        orderSummary.add(issueFee);
        caseData.setOrderSummary(orderSummary);
        CCDCallbackResponse ccdCallbackResponse = null;
        ccdCallbackResponse = new CCDCallbackResponse(caseData, new ArrayList<>(), new ArrayList<>());
        return ResponseEntity.ok(ccdCallbackResponse);
    }

    @PostMapping(path = "/process-pba-payment", consumes = MediaType.APPLICATION_JSON,
        produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Solicitor pay callback")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Callback to receive payment from the solicitor",
            response = CCDCallbackResponse.class),
        @ApiResponse(code = 400, message = "Bad Request")
        })
    public ResponseEntity<CCDCallbackResponse> processPBAPayment(
        @RequestHeader(value = "Authorization") String authorizationToken,
        @RequestBody @ApiParam("CaseData")
            CreateEvent caseDetailsRequest) {
        CoreCaseData caseData = caseDetailsRequest.getCaseDetails().getCaseData();
        setOrderSummary(caseData); //TODO: please remove me.
        paymentService.processPBAPayments(authorizationToken, caseDetailsRequest);
        return ResponseEntity.ok(new CCDCallbackResponse(caseData, new ArrayList<>(), new ArrayList<>()));
    }

    @PostMapping(path = "/solicitor-create", consumes = MediaType.APPLICATION_JSON,
        produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Solicitor pay callback")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Callback to populate missing requirement fields when "
            + "creating solicitor cases.", response = CCDCallbackResponse.class),
        @ApiResponse(code = 400, message = "Bad Request")
        })
    public ResponseEntity<CCDCallbackResponse> solicitorCreate(
        @RequestBody @ApiParam("CaseData") CreateEvent caseDetailsRequest) {

        CoreCaseData caseData = caseDetailsRequest.getCaseDetails().getCaseData();
        caseData.setCreatedDate(LocalDate.now().format(ofPattern("yyyy-MM-dd")));
        caseData.setD8DivorceUnit("northWest");
        caseData.setD8SelectedDivorceCentreSiteId("AA04");
        return ResponseEntity.ok(new CCDCallbackResponse(caseData, new ArrayList<>(), new ArrayList<>()));
    }

    private void setOrderSummary(CoreCaseData coreCaseData) {
        Fee issueFee = feesAndPaymentService.getPetitionIssueFee();
        OrderSummary orderSummary = new OrderSummary();
        orderSummary.add(issueFee);
        coreCaseData.setOrderSummary(orderSummary);
    }

    private enum Courts {
        EASTMIDLANDS("East Midlands Regional Divorce Centre"),
        WESTMIDLANDS("West Midlands Regional Divorce Centre"),
        SOUTHWEST("South West Regional Divorce Centre"),
        NORTHWEST("North West Regional Divorce Centre");

        private String displayName;

        private Courts(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }


}
