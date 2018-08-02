package uk.gov.hmcts.reform.divorce.petition.domain;

import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.DivorceSession;


public class Petition {

    private final String caseId;
    private final DivorceSession divorceCase;

    public Petition(String caseId, DivorceSession divorceCase) {
        this.caseId = caseId;
        this.divorceCase = divorceCase;
    }

    public String getCaseId() {
        return caseId;
    }

    public DivorceSession getDivorceCase() {
        return divorceCase;
    }
}
