package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DoctorCreateDTO {
    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("specialization")
    private String specialization;

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

        @JsonProperty("role")
        private String role;

        @JsonProperty("profilePicture")
        private String profilePicture;

        public UserCreateDTO() {}

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

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getProfilePicture() {
            return profilePicture;
        }

        public void setProfilePicture(String profilePicture) {
            this.profilePicture = profilePicture;
        }
    }

    public DoctorCreateDTO() {}

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

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getPhoneNumber() {
        return phonenumber;
    }

    public void setPhoneNumber(String phonenumber) {
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
