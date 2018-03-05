package uk.gov.hmcts.reform.divorce.errorhandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import uk.gov.hmcts.reform.divorce.transformservice.client.pdf.PdfGeneratorException;
import uk.gov.hmcts.reform.divorce.transformservice.controller.CcdCallBackController;
import uk.gov.hmcts.reform.divorce.transformservice.domain.transformservice.CCDCallbackResponse;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice(basePackageClasses = CcdCallBackController.class)
public class CcdCallbackExceptionHandler {

    private static final String GENERIC_EXCEPTION_MESSAGE = "{0} Exception message : {1}";

    private static final Logger log = LoggerFactory
            .getLogger(CcdCallbackExceptionHandler.class);

    @ExceptionHandler(PdfGeneratorException.class)
    public ResponseEntity<CCDCallbackResponse> handlePdfGeneratorException(
            PdfGeneratorException exception,
            HttpServletRequest request) {

        String customMessage = "Pdf Generator error";
        return generateBadRequestResponse(customMessage, exception, request);
    }

    private ResponseEntity<CCDCallbackResponse> generateBadRequestResponse(String customMessage, Exception exception, HttpServletRequest request) {
        final String errorMessage = MessageFormat.format(GENERIC_EXCEPTION_MESSAGE, customMessage , exception.getMessage());
        log.error(errorMessage);
        List<String> errors = new ArrayList<>();
        errors.add(errorMessage);
        return ResponseEntity.badRequest().body(new CCDCallbackResponse(null, errors, new ArrayList<>()));
    }

}
