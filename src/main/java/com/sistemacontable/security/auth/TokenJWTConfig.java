package com.sistemacontable.security.auth;

import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

public class TokenJWTConfig {
    private static final String SECRET =
            "tLm1uA6g3TveDXeuUSEDlnVuMvTUKDUMK6rb+fZc11AYWRXsOhCC469L+0sD+lebtzYdJIpYXnAcZdymnkTNGA";
    public static final SecretKey SECRET_KEY =
            Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
}
