package uk.gov.hmcts.reform.divorce.errorhandler;

import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import uk.gov.hmcts.reform.divorce.draftservice.controller.DraftsController;
import uk.gov.hmcts.reform.divorce.draftservice.exception.DraftStoreUnavailableException;
import uk.gov.hmcts.reform.divorce.transformservice.controller.CcdSubmissionController;
import uk.gov.hmcts.reform.divorce.transformservice.domain.transformservice.CCDResponse;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.time.format.DateTimeParseException;
import java.util.Map;

@ControllerAdvice(basePackageClasses = {CcdSubmissionController.class, DraftsController.class})
public class SubmissionExceptionHandler {

    private static final String EXCEPTION_MESSAGE = "Request Id : {0} and Exception message : {1}, Exception response body: {2}";
    private static final String REQUEST_ID_HEADER_KEY = "requestId";
    private static final String GENERIC_EXCEPTION_MESSAGE = "Request Id : {0} and Exception message : {1}";
    private static final String ERROR = "error";

    private static final Logger log = LoggerFactory
            .getLogger(SubmissionExceptionHandler.class);

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<Object> handleClientException(
            HttpClientErrorException clientErrorException,
            HttpServletRequest request) {

        if (isDraftsRequest(request)) {
            return handleDraftsAPIClientError(clientErrorException, request);
        }

        final String errorMessage = MessageFormat.format(EXCEPTION_MESSAGE, request.getHeader(REQUEST_ID_HEADER_KEY), clientErrorException.getMessage(),
                clientErrorException.getResponseBodyAsString());
        log.error(errorMessage);
        return ResponseEntity.ok(new CCDResponse(0, errorMessage, ERROR));

    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolationException(
            ConstraintViolationException constraintViolationException,
            HttpServletRequest request) {

        ConstraintViolation violation =
                constraintViolationException.getConstraintViolations().iterator().next();
        String property = violation.getPropertyPath().toString();
        String message = violation.getMessage();

        final String errorMessage = MessageFormat.format(GENERIC_EXCEPTION_MESSAGE, request.getHeader(REQUEST_ID_HEADER_KEY),
                constraintViolationException.getMessage());
        log.error(errorMessage);

        String errorMessageToReturn= String.format("%s %s", property, message);
        log.error(errorMessageToReturn);

        return ResponseEntity.badRequest().body(errorMessageToReturn);
    }

    @ExceptionHandler(JwtParsingException.class)
    public ResponseEntity<CCDResponse> handleJwtParsingException(
            JwtParsingException jwtException,
            HttpServletRequest request) {

        final String errorMessage = MessageFormat.format(GENERIC_EXCEPTION_MESSAGE, request.getHeader(REQUEST_ID_HEADER_KEY), jwtException.getMessage());
        log.error(errorMessage);
        return ResponseEntity.ok(new CCDResponse(0, errorMessage, ERROR));
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<CCDResponse> handleAllException(
            HttpServerErrorException ex,
            HttpServletRequest request) {

        final String errorMessage = MessageFormat.format(EXCEPTION_MESSAGE, request.getHeader(REQUEST_ID_HEADER_KEY), ex.getMessage(), ex.getResponseBodyAsString());
        log.error(errorMessage);
        return ResponseEntity.ok(new CCDResponse(0, errorMessage, ERROR));
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<CCDResponse> handleResourceAccessException(
            ResourceAccessException resourceAccessException,
            HttpServletRequest request) {

        final String resourceExceptionMessage = "Request Id : {0} and ResourceAccessException message : {1}";
        final String errorMessage = MessageFormat.format(resourceExceptionMessage, request.getHeader(REQUEST_ID_HEADER_KEY), resourceAccessException.getMessage());
        log.error(errorMessage);
        return ResponseEntity.ok(new CCDResponse(0, errorMessage, ERROR));
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<CCDResponse> handleDateTimeParseException(
            DateTimeParseException dateTimeParseException,
            HttpServletRequest request) {

        final String errorMessage = MessageFormat.format(GENERIC_EXCEPTION_MESSAGE, request.getHeader(REQUEST_ID_HEADER_KEY), dateTimeParseException.getMessage());
        log.error(errorMessage);
        return ResponseEntity.ok(new CCDResponse(0, errorMessage, ERROR));
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<CCDResponse> handleFeignException(
            FeignException feignException,
            HttpServletRequest request) {

        final String errorMessage = MessageFormat.format(GENERIC_EXCEPTION_MESSAGE, request.getHeader(REQUEST_ID_HEADER_KEY), feignException.getMessage());
        log.error(errorMessage);
        return ResponseEntity.ok(new CCDResponse(0, errorMessage, ERROR));
    }

    @ExceptionHandler(UnsupportedEncodingException.class)
    public ResponseEntity<CCDResponse> handlUnsupportedEncodingException(
        UnsupportedEncodingException unsupportedEncodingException,
        HttpServletRequest request) {

        final String errorMessage = MessageFormat.format(GENERIC_EXCEPTION_MESSAGE, request.getHeader(REQUEST_ID_HEADER_KEY), unsupportedEncodingException.getMessage());
        log.error(errorMessage);
        return ResponseEntity.ok(new CCDResponse(0, errorMessage, ERROR));
    }

    @ExceptionHandler(DraftStoreUnavailableException.class)
    public ResponseEntity<Void> handleDraftStoreUnavailable() {
        return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
    }

    private boolean isDraftsRequest(HttpServletRequest request) {
        return request.getRequestURI().toLowerCase().contains("draftsapi");
    }

    private ResponseEntity<Object> handleDraftsAPIClientError(HttpClientErrorException clientErrorException, HttpServletRequest request) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        requestAttributes.setAttribute("javax.servlet.error.status_code", clientErrorException.getRawStatusCode(), RequestAttributes.SCOPE_REQUEST);
        requestAttributes.setAttribute("javax.servlet.error.error_code", clientErrorException.getStatusText(), RequestAttributes.SCOPE_REQUEST);
        requestAttributes.setAttribute("javax.servlet.error.request_uri", request.getRequestURI(), RequestAttributes.SCOPE_REQUEST);

        Map<String, Object> errorAttributes = new GlobalErrorAttributes().getErrorAttributes(requestAttributes, false);

        return new ResponseEntity<>(errorAttributes, clientErrorException.getStatusCode());
    }

}
