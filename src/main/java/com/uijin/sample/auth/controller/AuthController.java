package com.uijin.sample.auth.controller;

import com.uijin.sample.auth.service.AuthService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @GetMapping("/test")
  public void authGoogleCode(@RequestParam String code) throws IOException {
    authService.loginByGoogle(code);
  }
}
