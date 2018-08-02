package uk.gov.hmcts.reform.divorce.petition.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import uk.gov.hmcts.reform.divorce.CaseProgressionApplication;
import uk.gov.hmcts.reform.divorce.petition.domain.Petition;
import uk.gov.hmcts.reform.divorce.petition.service.PetitionService;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.DivorceSession;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(PetitionController.class)
@ContextConfiguration(classes = CaseProgressionApplication.class)
@AutoConfigureMockMvc
public class PetitionControllerTest {

    private static final String JWT = "Bearer JWT";
    private static final String CASE_ID = "1234";
    @MockBean
    private PetitionService petitionService;

    @Autowired
    private MockMvc mockMVc;

    @Test
    public void shouldReturnNotFoundWhenPetitionServiceReturnsNull() throws Exception {

        // given
        when(petitionService.retrievePetition(JWT))
            .thenReturn(null);

        // when
        ResultActions result = mockMVc.perform(get("/petition/version/1")
            .header("Authorization", JWT));

        // then
        result.andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnOkWhenPetitionServiceReturnsPetition() throws Exception {

        // given
        DivorceSession mockDivorceSession = new DivorceSession();
        Petition petition = new Petition(CASE_ID, mockDivorceSession);
        when(petitionService.retrievePetition(JWT))
            .thenReturn(petition);

        // when
        ResultActions result = mockMVc.perform(get("/petition/version/1")
            .header("Authorization", JWT));

        // then
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.caseId", is(CASE_ID)));
    }
}
