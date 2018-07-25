package uk.gov.hmcts.reform.divorce.errorhandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import uk.gov.hmcts.reform.divorce.pay.exceptions.FeesNotFoundException;
import uk.gov.hmcts.reform.divorce.pay.exceptions.PaymentFailedException;
import uk.gov.hmcts.reform.divorce.transformservice.client.pdf.PdfGeneratorException;
import uk.gov.hmcts.reform.divorce.transformservice.controller.CcdCallBackController;
import uk.gov.hmcts.reform.divorce.transformservice.domain.transformservice.CCDCallbackResponse;
import uk.gov.hmcts.reform.divorce.transformservice.service.InvalidPetitionException;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;



@ControllerAdvice(basePackageClasses = CcdCallBackController.class)
public class CcdCallbackExceptionHandler {

    private static final String GENERIC_EXCEPTION_MESSAGE = "{0} Exception message : {1}";

    private static final Logger log = LoggerFactory
        .getLogger(CcdCallbackExceptionHandler.class);

    @ExceptionHandler(PdfGeneratorException.class)
    public ResponseEntity<CCDCallbackResponse> handlePdfGeneratorException(
        PdfGeneratorException exception,
        HttpServletRequest request) {

        String customMessage = MessageFormat.format(GENERIC_EXCEPTION_MESSAGE, "Pdf Generator error",
            exception.getMessage());
        return generateBadRequestResponse(customMessage, request);
    }

    @ExceptionHandler(InvalidPetitionException.class)
    public ResponseEntity<CCDCallbackResponse> handleInvalidPetitionException(
        InvalidPetitionException exception,
        HttpServletRequest request) {

        String customMessage = "The Document Type has not been set for one of the uploaded documents. "
            + "This must be set before a new PDF can be created";
        return generateBadRequestResponse(customMessage, request);
    }

    @ExceptionHandler(FeesNotFoundException.class)
    public ResponseEntity<CCDCallbackResponse> handleInvalidFeesNotFoundException(
        FeesNotFoundException exception,
        HttpServletRequest request) {

        String customMessage = "Fees not found !";
        return generateBadRequestResponse(customMessage, request);
    }

    @ExceptionHandler(PaymentFailedException.class)
    public ResponseEntity<CCDCallbackResponse> handleFailedPaymentException(
        PaymentFailedException exception,
        HttpServletRequest request) {

        String customMessage = "Payment has failed, please try again.";
        return generateBadRequestResponse(customMessage, request);
    }

    private ResponseEntity<CCDCallbackResponse> generateBadRequestResponse(String customMessage,
                                                                           HttpServletRequest request) {
        log.error(customMessage);
        List<String> errors = new ArrayList<>();
        errors.add(customMessage);
        return ResponseEntity.badRequest().body(new CCDCallbackResponse(null, errors, new ArrayList<>()));
    }

}
