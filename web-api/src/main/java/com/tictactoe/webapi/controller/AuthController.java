package com.tictactoe.webapi.controller;

import com.tictactoe.authmodule.service.JWTService;
import com.tictactoe.domain.JWTAuthRequest;
import com.tictactoe.domain.JWTAuthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(path = "/auth", produces = {APPLICATION_JSON_UTF8_VALUE})
public class AuthController {

  @Autowired
  private MapReactiveUserDetailsService userDetailsRepository;

  @Autowired
  private JWTService jwtService;

  @RequestMapping(method = POST, value = "/token")
  @CrossOrigin("*")
  public Mono<ResponseEntity<JWTAuthResponse>> token(@RequestBody JWTAuthRequest jwtAuthRequest) throws AuthenticationException {
    String username = jwtAuthRequest.getUsername();
    String password = jwtAuthRequest.getPassword();

    return userDetailsRepository.findByUsername(username)
        .map(user -> ok().contentType(APPLICATION_JSON_UTF8).body(
            new JWTAuthResponse(jwtService.generateToken(user.getUsername(), user.getAuthorities()), user.getUsername()))
        )
        .defaultIfEmpty(notFound().build());
  }
}
