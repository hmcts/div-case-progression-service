package uk.gov.hmcts.reform.divorce.transformservice.service.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.DivorceSession;

import java.util.List;

@Service
public class SubmissionValidation {

    private final List<SubmissionValidator> validators;

    @Autowired
    public SubmissionValidation(List<SubmissionValidator> validators) {
        this.validators = validators;
    }

    public boolean validate(DivorceSession divorceSession) {
        for (SubmissionValidator validator : validators) {
            if (!validator.isValid(divorceSession)) {
                return false;
            }
        }
        return true;
    }
}
