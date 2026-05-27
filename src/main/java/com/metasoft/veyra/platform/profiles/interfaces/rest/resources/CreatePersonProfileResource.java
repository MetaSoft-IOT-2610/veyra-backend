package com.metasoft.veyra.platform.profiles.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

public record CreatePersonProfileResource(
        @Schema(example = "12345678")        String dni,
        @Schema(example = "Carlos")          String firstName,
        @Schema(example = "Pérez")           String lastName,
        @Schema(example = "1990-05-15")      LocalDate birthDate,
        @Schema(example = "34")              Integer age,
        @Schema(example = "carlos@mail.com") String emailAddress,
        @Schema(example = "Av. Lima")        String street,
        @Schema(example = "123")             String number,
        @Schema(example = "Lima")            String city,
        @Schema(example = "15001")           String postalCode,
        @Schema(example = "Peru")            String country,
        @Schema(example = "+51987654321")    String phoneNumber,
        MultipartFile photo
) {}