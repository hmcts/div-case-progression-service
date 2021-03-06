package uk.gov.hmcts.reform.divorce.transformservice.mapping;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CollectionMember;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.Document;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.DocumentLink;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.UploadedFile;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class DocumentCollectionMapper {
    @Mapping(source = "fileName", target = "value.documentFileName")
    @Mapping(source = "createdOn", dateFormat = "yyyy-MM-dd", target = "value.documentDateAdded")
    @Mapping(target = "value.documentEmailContent", constant = "")
    @Mapping(target = "value.documentComment", constant = "")
    @Mapping(source = "fileType", target = "value.documentType", defaultValue = "other")
    public abstract CollectionMember<Document> map(UploadedFile uploadedFile);

    @Autowired
    private DocumentUrlRewrite documentUrlRewrite;

    @AfterMapping
    protected void mapDocumentUrlToDocumentLinkObject(UploadedFile uploadedFile,
                                                      @MappingTarget CollectionMember<Document> result) {

        String fileUrl = documentUrlRewrite.getDocumentUrl(uploadedFile.getFileUrl());

        result.getValue().setDocumentLink(DocumentLink.builder()
            .documentUrl(fileUrl).build());
    }
}
