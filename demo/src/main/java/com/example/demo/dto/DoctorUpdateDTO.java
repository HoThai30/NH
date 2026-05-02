package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DoctorUpdateDTO {
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
    private UserUpdateDTO user;

    public static class UserUpdateDTO {
        @JsonProperty("email")
        private String email;

        @JsonProperty("phone")
        private String phone;

        @JsonProperty("profilePicture")
        private String profilePicture;

        public UserUpdateDTO() {}

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getProfilePicture() {
            return profilePicture;
        }

        public void setProfilePicture(String profilePicture) {
            this.profilePicture = profilePicture;
        }
    }

    public DoctorUpdateDTO() {}

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

    public UserUpdateDTO getUser() {
        return user;
    }

    public void setUser(UserUpdateDTO user) {
        this.user = user;
    }
}
