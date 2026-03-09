package edu.cit.rivero.workspace.dto;

public class AuthResponseData {
    private UserDto user;
    private String accessToken;
    private String refreshToken;

    public AuthResponseData(UserDto user, String accessToken, String refreshToken) {
        this.user = user;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public UserDto getUser() { return user; }
    public void setUser(UserDto user) { this.user = user; }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
}