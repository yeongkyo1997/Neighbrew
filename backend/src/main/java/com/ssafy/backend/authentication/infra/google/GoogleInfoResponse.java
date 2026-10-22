package com.ssafy.backend.authentication.infra.google;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ssafy.backend.authentication.domain.oauth.OAuthInfoResponse;
import com.ssafy.backend.authentication.domain.oauth.OAuthProvider;

import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true) // 필요한 데이터만 가져옴
public class GoogleInfoResponse implements OAuthInfoResponse {
    @JsonProperty("response")
    private Response response;

    public GoogleInfoResponse(Response response) {
        this.response = response;
    }

    @Override
    public String getOAuthId() {
        return response == null ? null : response.id;
    }

    @Override
    public String getEmail() {
        return response == null ? null : response.email;
    }

    @Override
    public String getName() {
        return response == null ? null : response.name;
    }

    @Override
    public OAuthProvider getOAuthProvider() {
        return OAuthProvider.GOOGLE;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Response {
        public String id;
        public String email;
        @JsonProperty("verified_email")
        public Boolean verifiedEmail;
        public String name;
        public String givenName;
        public String familyName;
        public String picture;
        public String locale;

    }
}
