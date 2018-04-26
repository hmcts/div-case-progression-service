package uk.gov.hmcts.reform.divorce.draftservice.factory;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EncryptionKeyFactory {

    @Value("${draft.store.api.encryption.key.template}")
    private String encryptionKeyTemplate;

    @Value("${draft.store.api.encryption.key}")
    private String encryptionKeyPrefix;

    public String createEncryptionKey(String userId) {

        return Base64.encodeBase64String(
            String.format(
                encryptionKeyTemplate,
                encryptionKeyPrefix,
                userId
            ).getBytes());
    }
}
