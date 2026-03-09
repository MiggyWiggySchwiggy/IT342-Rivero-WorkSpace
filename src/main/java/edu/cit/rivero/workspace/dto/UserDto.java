package edu.cit.rivero.workspace.dto;

public class UserDto {
    private String email;
    private String firstname;
    private String lastname;
    private String role;

    public UserDto(String email, String firstname, String lastname, String role) {
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.role = role;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) { this.firstname = firstname; }

    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}