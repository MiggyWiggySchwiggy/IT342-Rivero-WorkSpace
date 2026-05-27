package edu.cit.rivero.workspace.features.auth;

public class GoogleLoginRequest {
    private String idToken;

    public GoogleLoginRequest() {}

    public String getIdToken() { return idToken; }
    public void setIdToken(String idToken) { this.idToken = idToken; }
}
