package com.rest.login.enums;

public enum EResponses {
    USER_UPDATED("User successfully updated!"),
    USER_REGISTERED("User registered successfully!"),
    USER_EXISTS("User exists in database!"),
    USER_NOT_FOUND("User not found in database!"),
    LISTING_ALL_USERS("All users from database."),
    CLIENT_CREATED("New client created successfully."),
    CLIENT_DELETED("Client deleted!"),
    CLIENT_UPDATED("Client updated."),
    CLIENT_FOUND("Returning client data."),
    CLIENT_NOT_FOUND("Error: Client not found in database!"),
    CLIENTS_NOT_FOUND("No clients found."),
    LISTING_ALL_CLIENTS("All clients from database."),
    EVALUATION_NOT_FOUND("Evaluation not found!"),
    EVALUATION_FOUND("Evaluation found."),
    EVALUATION_ADDED("Evaluation added."),
    NO_EVALUATIONS_FOR_CLIENT("Client does not have any evaluations created!"),
    LISTING_EVALUATIONS("Listing client's evaluations!"),
    USERNAME_TAKEN("Error: Username is already taken!"),
    EMAIL_ALREADY_USED("Error: Email is already in use!"),
    ROLE_NOT_FOUND("Error: Role is not found."),
    ADMIN_ROLE_NOT_REGISTREABLE("Error: Registering admin role is forbidden."),
    LOGOUT_SUCCESSFUL("Log out successful!"),
    BOARD_NOT_FOUND("Board was not found."),
    CLIENT_OWNED_BY_DIFFERENT_USER("Unauthorized access - client owned by different user!"),
    LISTING_ANSWERS_FROM_BOARD("Listing all answers from board!"),
    MISSING_NAME_IN_BODY("Error: Missing name in body!"),
    VALIDATION_FAILED("Validation failed for request body."),
    ID_NULL("Provided ID is null."),
    UNAUTHORIZED_ACCESS("Unauthorized access to resource.");


    private final String message;

    EResponses(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
