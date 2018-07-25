package uk.gov.hmcts.reform.divorce.transformservice.service.validation;

import lombok.extern.java.Log;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.DivorceSession;

@Component
@Log
public class PetitionerCorrespondenceAddress implements SubmissionValidator {

    @Override
    public boolean isValid(DivorceSession divorceSession) {

        boolean isValid = divorceSession.getPetitionerCorrespondenceAddress() != null;

        if (!isValid) {
            log.warning("Failing validation as petitioner correspondence address is null");
        }
        return isValid;
    }
}
