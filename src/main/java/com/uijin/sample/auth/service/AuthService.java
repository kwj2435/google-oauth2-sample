package com.uijin.sample.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uijin.sample.auth.external.GoogleTokenRequest;
import com.uijin.sample.auth.external.GoogleTokenResponse;
import com.uijin.sample.auth.external.GoogleUserInfo;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class AuthService {
  @Value("${spring.security.oauth2.client.registration.google.client-id}")
  private String googleClientId;
  @Value("${spring.security.oauth2.client.registration.google.client-secret}")
  private String googleClientSecret;
  @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
  private String googleRedirectUrl;

  private ObjectMapper objectMapper;
  private RestTemplate restTemplate;

  @PostConstruct
  void init() {
    objectMapper = new ObjectMapper();
    restTemplate = new RestTemplate();
  }

  public void loginByGoogle(String authorizationCode) throws IOException {
    // 1. AuthorizationToken 과 AccessToken 교환
    GoogleTokenResponse googleToken = getGoogleToken(authorizationCode);

    // 2. AccessToken 이용 사용자 정보 획득
    GoogleUserInfo googleUserInfo = getGoogleUserInfo(googleToken.getAccessToken());

    // todo 유저 정보가 DB에 있을 경우 로그인 진행
    // todo 유저 정보가 없을 경우 회원 가입 진행
  }

  /** Authorization Token 이용 Google Token 발급 */
  private GoogleTokenResponse getGoogleToken(String authorizationCode)
      throws JsonProcessingException {
    GoogleTokenRequest googleTokenRequest = GoogleTokenRequest
        .create(authorizationCode, googleClientId, googleClientSecret, googleRedirectUrl);

    URI getTokenUri = UriComponentsBuilder
        .fromUriString("https://oauth2.googleapis.com")
        .path("/token").encode().build().toUri();

    ResponseEntity<String> googleResponse =
        restTemplate.postForEntity(getTokenUri, googleTokenRequest, String.class);
    return objectMapper.readValue(googleResponse.getBody(), GoogleTokenResponse.class);
  }

  /** Token 이용 사용자 정보 조회 */
  private GoogleUserInfo getGoogleUserInfo(String accessToken) throws JsonProcessingException {
    URI getTokenInfoUri = UriComponentsBuilder
        .fromUriString("https://www.googleapis.com")
        .path("/oauth2/v2/userinfo")
        .queryParam("access_token", accessToken).encode().build().toUri();

    ResponseEntity<String> googleTokenInfoResponse =
        restTemplate.getForEntity(getTokenInfoUri, String.class);

    return objectMapper.readValue(googleTokenInfoResponse.getBody(), GoogleUserInfo.class);
  }
}
