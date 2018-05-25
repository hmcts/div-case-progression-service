package uk.gov.hmcts.reform.divorce.draftservice.controller;

import com.fasterxml.jackson.databind.JsonNode;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.divorce.draftservice.service.DraftsService;
import uk.gov.hmcts.reform.divorce.notifications.service.EmailService;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.MediaType;

@RestController
@RequestMapping(path = "/draftsapi/version/1")
@Api(value = "Drafts API", consumes = "application/json", produces = "application/json")
@Slf4j
@Validated
public class DraftsController {

    @Autowired
    private DraftsService draftsService;
    @Autowired
    private EmailService emailService;

    @PutMapping(consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Saves a divorce case draft")
    @ApiResponses(value = {
        @ApiResponse(code = 204, message = "Draft saved")
        })
    public ResponseEntity<Void> saveDraft(
        @RequestHeader("Authorization")
        @ApiParam(value = "JWT authorisation token issued by IDAM", required = true) final String jwt,
        @RequestBody
        @ApiParam(value = "The divorce case draft", required = true)
        @NotNull final JsonNode data,
        @RequestParam(value = "notificationEmail", required = false)
        @ApiParam(value = "The email address that will receive the notification that the draft has been saved")
        @Email final String notificationEmail) {
        log.debug("Received request to save a divorce session draft");
        draftsService.saveDraft(jwt, data);
        if (StringUtils.isNotBlank(notificationEmail)) {
            emailService.sendSaveDraftConfirmationEmail(notificationEmail);
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Retrieves a divorce case draft")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "A draft exists. The draft content is in the response body"),
        @ApiResponse(code = 404, message = "Draft does not exist")
        })
    public ResponseEntity<JsonNode> retrieveDraft(
        @RequestHeader("Authorization") @ApiParam(value = "JWT authorisation token issued by IDAM", required = true)
        final String jwt) {
        log.debug("Received request to retrieve a divorce session draft");
        JsonNode draft = draftsService.getDraft(jwt);
        if (draft != null) {
            return ResponseEntity.ok(draft);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping
    @ApiOperation(value = "Deletes a divorce case draft")
    @ApiResponses(value = {
        @ApiResponse(code = 204, message = "The divorce case draft has been deleted successfully")
        })
    public ResponseEntity<Void> deleteDraft(@RequestHeader("Authorization")
                                                @ApiParam(value = "JWT authorisation token issued by IDAM",
                                                    required = true) final String jwt) {
        log.debug("Received request to delete a divorce session draft");
        draftsService.deleteDraft(jwt);
        return ResponseEntity.noContent().build();
    }
}
