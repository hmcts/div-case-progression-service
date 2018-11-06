package uk.gov.hmcts.reform.divorce.transformservice.service;

import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.DivorceSession;

import java.util.Date;

@Service
public class SeparationDateService {

    public void updateSeparationDate(DivorceSession divorceSession) {
        Date separationDate = divorceSession.getReasonForDivorceDecisionDate();

        if (separationDate == null
            || (divorceSession.getReasonForDivorceLivingApartDate() != null
            && separationDate.before(divorceSession.getReasonForDivorceLivingApartDate()))) {
            separationDate = divorceSession.getReasonForDivorceLivingApartDate();
        }

        //if both living apart and decision date are null then use the separation date
        if (separationDate == null) {
            separationDate = divorceSession.getReasonForDivorceSeperationDate();
        }

        divorceSession.setReasonForDivorceSeperationDate(separationDate);
    }
}
