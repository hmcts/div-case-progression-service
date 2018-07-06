package uk.gov.hmcts.reform.divorce.draftservice.factory;

import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EncryptionKeyFactoryTest {

    private static final String JWT = "Bearer hgsdja87wegqeuf...";
    private static final String USER_ID = "123";

    @Value("${draft.store.api.encryption.key.template}")
    private String encryptionKeyTemplate;

    @Value("${draft.store.api.encryption.key}")
    private String encryptionKeyPrefix;

    @Autowired
    private EncryptionKeyFactory underTest;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void createEncryptionKeyShouldCreateAnEncryptionKeyUsingTheTemplateACommonSecretAndTheUserId() {
        assertEquals(
            Base64.encodeBase64String(
                String.format(encryptionKeyTemplate, encryptionKeyPrefix, USER_ID).getBytes()),
            underTest.createEncryptionKey(USER_ID));
    }
}
