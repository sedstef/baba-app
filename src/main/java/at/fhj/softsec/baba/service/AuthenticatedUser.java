package at.fhj.softsec.baba.service;

public final class AuthenticatedUser {

    private final String userId;

    public AuthenticatedUser(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
