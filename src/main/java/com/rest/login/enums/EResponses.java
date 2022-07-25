package com.rest.login.enums;

public enum EResponses {
    USER_UPDATED("User successfully updated!"),
    USER_REGISTERED("User registered successfully!"),
    CLIENT_DELETED("Client deleted!"),
    CLIENT_UPDATED("Client updated."),
    CLIENT_NOT_FOUND("Error: Client not found in database!"),
    EVALUATION_NOT_FOUND("Error: Evaluation not found!"),
    EVALUATION_ADDED("Evaluation added."),
    NO_EVALUATIONS_FOR_CLIENT("Error: Client does not have any evaluations created!"),
    LISTING_EVALUATIONS("Listing client's evaluations!"),
    USERNAME_TAKEN("Error: Username is already taken!"),
    EMAIL_ALREADY_USED("Error: Email is already in use!"),
    ROLE_NOT_FOUND("Error: Role is not found."),
    ADMIN_ROLE_NOT_REGISTREABLE("Error: Registering admin role is forbidden."),
    LOGOUT_SUCCESSFUL("Log out successful!"),
    BOARD_NOT_FOUND("Board was not found."),
    CLIENT_OWNED_BY_DIFFERENT_USER("Unauthorized access - client owned by different user!"),
    LISTING_ANSWERS_FROM_BOARD("Listing all answers from board!"),
    MISSING_NAME_IN_BODY("Error: Missing name in body!");


    private final String message;

    EResponses(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
