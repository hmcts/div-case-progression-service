package uk.gov.hmcts.reform.divorce.transformservice.mapping;

import org.mapstruct.*;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CollectionMember;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.Document;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.DocumentLink;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.UploadedFile;

@Mapper(componentModel = "spring", uses = DocumentTypeMapper.class)
public abstract class DocumentCollectionMapper {
    @Mapping(source = "fileName", target = "value.documentFileName")
    @Mapping(source = "createdOn", dateFormat = "yyyy-MM-dd", target = "value.documentDateAdded")
    @Mapping(target = "value.documentEmailContent", constant = "")
    @Mapping(target = "value.documentComment", constant = "")
    @Mapping(source = "fileType", target = "value.documentType")

    public abstract CollectionMember<Document> map(UploadedFile uploadedFile);

    @AfterMapping
    protected void mapDocumentUrlToDocumentLinkObject(UploadedFile uploadedFile,
            @MappingTarget CollectionMember<Document> result) {
        
        result.getValue().setDocumentLink(DocumentLink.builder()
                .documentUrl(uploadedFile.getFileUrl()).build());
    }
}
