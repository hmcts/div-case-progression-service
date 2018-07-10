package uk.gov.hmcts.reform.divorce.transformservice.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CoreCaseData;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.DivorceSession;

@Mapper(componentModel = "spring", uses = DocumentCollectionMapper.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class DivorceCaseToCCDSubmissionMapper extends DivorceCaseToCCDMapper {

    @Mapping(target = "createdDate",
            expression =
                    "java(java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern(\"yyyy-MM-dd\")))")
    public abstract CoreCaseData divorceCaseDataToCourtCaseData(DivorceSession divorceSession);
}
