package uk.gov.hmcts.reform.divorce.transformservice.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.ValueMapping;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.DocumentType;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.UploadedFileType;

@Mapper(componentModel = "spring")
public interface DocumentTypeMapper {
    @ValueMapping(source = "<NULL>", target = "OTHER")
    @ValueMapping(source = "PETITION", target = "PETITION")
    @ValueMapping(source = "<ANY_REMAINING>", target = "OTHER")
    DocumentType map(UploadedFileType uploadedFileType);
}
