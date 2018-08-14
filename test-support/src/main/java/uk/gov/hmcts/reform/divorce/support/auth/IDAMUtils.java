package uk.gov.hmcts.reform.divorce.support.auth;

/**
 * IDAM Utility class used across divorce projects.
 */
public interface IDAMUtils {

    /**
     * Create user in Idam with values
     *
     * @param username to be created
     * @param password to be used
     */
    void createUserInIdam(String username, String password);

    /**
     *
     * Create a divorce case worker in idam.
     *
     * @param username of the caseworker
     * @param password of caseworker
     */
    void createDivorceCaseworkerUserInIdam(String username, String password);

    /**
     * Generate a user token roles
     *
     * @param username for the token generation.
     * @param password for the generation
     * @return token with no roles.
     */
    String generateUserTokenWithNoRoles(String username, String password);
}
