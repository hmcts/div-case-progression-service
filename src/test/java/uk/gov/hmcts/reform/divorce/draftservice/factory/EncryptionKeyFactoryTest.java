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
import uk.gov.hmcts.reform.divorce.transformservice.domain.Jwt;
import uk.gov.hmcts.reform.divorce.common.JwtFactory;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EncryptionKeyFactoryTest {

    private static final String JWT = "Bearer hgsdja87wegqeuf...";
    private static final long USER_ID = 123;

    @Value("${draft.store.api.encryption.key.template}")
    private String encryptionKeyTemplate;

    @Value("${draft.store.api.encryption.key}")
    private String encryptionKeyPrefix;

    @Autowired
    private EncryptionKeyFactory underTest;

    @MockBean
    private JwtFactory jwtFactory;

    @Mock
    private Jwt jwt;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        given(jwtFactory.create(JWT)).willReturn(jwt);

        given(jwt.getId()).willReturn(USER_ID);
    }

    @Test
    public void createEncryptionKeyShouldCreateAnEncryptionKeyUsingTheTemplateACommonSecretAndTheUserId() {
        assertEquals(
                Base64.encodeBase64String(
                        String.format(encryptionKeyTemplate, encryptionKeyPrefix, USER_ID).getBytes()),
                underTest.createEncryptionKey(JWT));
    }
}