package uk.gov.hmcts.reform.divorce.transformservice.client;

public interface CcdClientConfiguration {

    String getCreateCaseUrl(Long jwtId);

    String getSubmitCaseUrl(Long jwtId);

    String getStartEventUrl(String jwtId, Long caseId, String eventId);

    String getCreateCaseEventUrl(String jwtId, Long caseId);
}