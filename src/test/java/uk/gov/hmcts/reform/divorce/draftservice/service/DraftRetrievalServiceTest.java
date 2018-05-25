/*
package uk.gov.hmcts.reform.divorce.draftservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.hmcts.reform.divorce.draftservice.client.DraftStoreClient;
import uk.gov.hmcts.reform.divorce.draftservice.domain.Draft;
import uk.gov.hmcts.reform.divorce.draftservice.domain.DraftList;
import uk.gov.hmcts.reform.divorce.draftservice.factory.EncryptionKeyFactory;
import uk.gov.hmcts.reform.divorce.idam.models.UserDetails;
import uk.gov.hmcts.reform.divorce.idam.services.UserService;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

*/
/**
 * TODO: write about the class
 *
 * @author wjtlopez
 *//*

@RunWith(MockitoJUnitRunner.class)
public class DraftRetrievalServiceTest {

    private static final String JWT = "Bearer XYZ";
    private static final String USER_ID = "userId";
    private static final UserDetails USER_DETAILS = UserDetails.builder().id(USER_ID).build();
    private static final String SECRET = "secret";

    private DraftRetrievalService underTest;

    @Mock
    private UserService mockUserService;
    @Mock
    private EncryptionKeyFactory mockKeyFactory;
    @Mock
    private DraftStoreClient mockDraftStoreClient;

    @Before
    public void setUp() throws Exception {
        underTest = new DraftRetrievalService(mockUserService, mockKeyFactory, mockDraftStoreClient);
    }

    @Test
    public void getDraft_should_call_userService_to_retrieve_user_details() {

        // given

        // when
        underTest.getDraft(JWT);

        // then
        verify(mockUserService).getUserDetails(JWT);
    }

    @Test
    public void getDraft_should_call_keyFactory_to_create_encryption_key() {

        // given
        given(mockUserService.getUserDetails(JWT))
                .willReturn(USER_DETAILS);

        // when
        underTest.getDraft(JWT);

        // then
        verify(mockKeyFactory).createEncryptionKey(USER_ID);
    }

    @Test
    public void getDraft_should_return_draft_retrieved_from_draft_store() {

        // given
        given(mockUserService.getUserDetails(JWT))
                .willReturn(USER_DETAILS);

        given(mockKeyFactory.createEncryptionKey(USER_ID))
                .willReturn(SECRET);

        List<Draft> drafts = ImmutableList.of(new Draft("draftId", ));
        DraftList draftStoreResult = new DraftList(drafts, );
        given(mockDraftStoreClient.getAll(JWT, SECRET))
                .willReturn(draftStoreResult);

        // when
        //underTest.getDraft()
        // then
    }
}
*/
