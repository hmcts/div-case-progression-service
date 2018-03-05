package uk.gov.hmcts.reform.divorce.draftservice.factory;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.divorce.draftservice.domain.CreateDraft;
import uk.gov.hmcts.reform.divorce.draftservice.domain.Draft;
import uk.gov.hmcts.reform.divorce.draftservice.domain.UpdateDraft;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DraftModelFactoryTest {

    @Value("${draft.store.api.document.type}")
    private String draftType;

    @Value("${draft.store.api.max.age}")
    private int maxAge;

    @Mock
    private JsonNode jsonNode;

    @Mock
    private Draft draft;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Autowired
    private DraftModelFactory underTest;

    @Test
    public void createDraftShouldCreateADivorceDraft() {
        CreateDraft createDraft = underTest.createDraft(jsonNode);

        assertEquals(draftType, createDraft.getType());
        assertEquals(maxAge, createDraft.getMaxAge());
    }

    @Test
    public void updateDraftShouldCreateADivorceDraft() {
        UpdateDraft updateDraft = underTest.updateDraft(jsonNode);

        assertEquals(draftType, updateDraft.getType());
    }

    @Test
    public void isDivorceDraftShouldReturnTrueForADivorceDraft() {
        given(draft.getType()).willReturn(draftType);

        assertTrue(underTest.isDivorceDraft(draft));
    }

    @Test
    public void isDivorceDraftShouldIgnoreTheCase() {
        given(draft.getType()).willReturn(draftType.toLowerCase());

        assertTrue(underTest.isDivorceDraft(draft));
    }

    @Test
    public void isDivorceDraftShouldReturnFalseForANonDivorceDraft() {
        given(draft.getType()).willReturn("cmcdraft");

        assertFalse(underTest.isDivorceDraft(draft));
    }
}