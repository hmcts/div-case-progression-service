package uk.gov.hmcts.reform.divorce.transformservice.mapping;

import org.mapstruct.*;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.AddressBaseUk;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class AddressBaseUkMapper {
    @Mapping(source = "addressLine1", target = "county")
    public abstract AddressBaseUk map(AddressBaseUk addressBaseUk);
}
