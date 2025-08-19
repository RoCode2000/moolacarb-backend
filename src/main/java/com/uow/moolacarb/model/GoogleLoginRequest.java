package com.uow.moolacarb.model;

public class GoogleLoginRequest {
        private String idToken;
        private String userId;
        private String email;
        private String familyName;
        private String givenName;
        private String name;
        private String photoUrl;

        // getters and setters
        public String getIdToken() { return idToken; }
        public void setIdToken(String idToken) { this.idToken = idToken; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getFamilyName() { return familyName; }
        public void setFamilyName(String familyName) { this.familyName = familyName; }
        public String getGivenName() { return givenName; }
        public void setGivenName(String givenName) { this.givenName = givenName; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getPhotoUrl() { return photoUrl; }
        public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
}