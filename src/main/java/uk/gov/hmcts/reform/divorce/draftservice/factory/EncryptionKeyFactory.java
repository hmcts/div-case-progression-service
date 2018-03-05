package uk.gov.hmcts.reform.divorce.draftservice.factory;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.divorce.transformservice.domain.Jwt;
import uk.gov.hmcts.reform.divorce.common.JwtFactory;

@Component
public class EncryptionKeyFactory {

    @Value("${draft.store.api.encryption.key.template}")
    private String encryptionKeyTemplate;

    @Value("${draft.store.api.encryption.key}")
    private String encryptionKeyPrefix;

    @Autowired
    private JwtFactory jwtFactory;

    public String createEncryptionKey(String encodedJwt) {
        Jwt jwt = jwtFactory.create(encodedJwt);

        return Base64.encodeBase64String(
                String.format(
                        encryptionKeyTemplate,
                        encryptionKeyPrefix,
                        jwt.getId()
                ).getBytes());
    }
}
