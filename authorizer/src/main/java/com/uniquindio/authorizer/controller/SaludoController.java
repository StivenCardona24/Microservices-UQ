package com.uniquindio.authorizer.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.uniquindio.authorizer.dto.UserDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/")
public class SaludoController {

    @Value("${jwt.secret}")
    private String secretKey;
    @GetMapping("saludo")
    public ResponseEntity<String> saludar (@RequestParam(required = false) String nombre,
                                           @RequestHeader(value = "Authorization", required = false) String authorizationHeader){

        // Verificar si se proporciona el nombre
        if(nombre==null){
            return new ResponseEntity<>("Solicitud no valida: El nombre es obligatorio", HttpStatus.BAD_REQUEST);
        }
        // Verificar la cabecera Authorization
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return new ResponseEntity<>("Solicitud no válida: Se requiere un token JWT en la cabecera Authorization", HttpStatus.UNAUTHORIZED);
        }

        String token = authorizationHeader.substring(7); // Eliminar "Bearer " del inicio

        try {
            // Verificar el JWT
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("ingesis.uniquindio.edu.co")
                    .build();
            DecodedJWT decodedJWT = verifier.verify(token);

            // Verificar que el nombre coincida con el sujeto del token
            String username = decodedJWT.getSubject();
            if (!nombre.equals(username)) {
                return new ResponseEntity<>("Solicitud no válida: El nombre en el token no coincide con el parámetro 'nombre'", HttpStatus.FORBIDDEN);
            }

            return new ResponseEntity<>("Hola " + nombre, HttpStatus.OK);
        } catch (JWTVerificationException exception) {
            return new ResponseEntity<>("Token inválido o expirado", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al procesar la solicitud", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("login")
    public ResponseEntity<String> login (@RequestBody UserDTO userDTO){
        if (userDTO.username() == null || userDTO.password() == null) {
            return new ResponseEntity<>("Solicitud no válida: Los atributos 'usuario' y 'clave' son obligatorios", HttpStatus.BAD_REQUEST);
        }

        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            String token = JWT.create()
                    .withSubject(userDTO.username())
                    .withIssuer("ingesis.uniquindio.edu.co")
                    .withIssuedAt(new Date())
                    .withExpiresAt(new Date(System.currentTimeMillis() + 3600000)) // 1 hora de vigencia
                    .sign(algorithm);

            return new ResponseEntity<>(token, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al generar el token", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}


