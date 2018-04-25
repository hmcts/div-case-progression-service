package uk.gov.hmcts.reform.divorce.idam;

import org.junit.Test;
import uk.gov.hmcts.reform.divorce.idam.models.UserDetails;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class UserDetailsTest {

    @Test
    public void UserAndUserDetailsTest() {

        List<String> roles = Collections.singletonList("oneRole");
        UserDetails userDetails = UserDetails.builder()
                .id("id1")
                .roles(roles)
                .email("one@email.com")
                .forename("two")
                .surname("one")
                .build();

        assertThat(userDetails.getId()).isEqualTo("id1");
        assertThat(userDetails.getRoles().get(0)).isEqualTo("oneRole");
        assertThat(userDetails.getEmail()).isEqualTo("one@email.com");
        assertThat(userDetails.getForename()).isEqualTo("two");
        assertThat(userDetails.getSurname()).isEqualTo("one");
        assertThat(UserDetails.builder().toString()).isEqualTo("UserDetails.UserDetailsBuilder(id=null, email=null, forename=null, surname=null, roles=null)");
    }

}
