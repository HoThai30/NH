package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReceptionistCreateDTO {
    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("department")
    private String department;

    @JsonProperty("phonenumber")
    private String phonenumber;

    @JsonProperty("profilePicture")
    private String profilePicture;

    @JsonProperty("user")
    private UserCreateDTO user;

    public static class UserCreateDTO {
        @JsonProperty("email")
        private String email;

        @JsonProperty("passwordHash")
        private String passwordHash;

        // Getters and Setters
        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPasswordHash() {
            return passwordHash;
        }

        public void setPasswordHash(String passwordHash) {
            this.passwordHash = passwordHash;
        }
    }

    // Getters and Setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public UserCreateDTO getUser() {
        return user;
    }

    public void setUser(UserCreateDTO user) {
        this.user = user;
    }
}