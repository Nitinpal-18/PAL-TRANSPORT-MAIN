package com.pal_transport.application.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverDTO {
    private Long id;

    @NotBlank(message = "Driver name cannot be blank")
    @Size(min = 2, max = 100, message = "Driver name must be between 2 and 100 characters")
    private String name;

    private String status;

    @DecimalMin(value = "0.0", message = "Rating cannot be negative")
    @DecimalMax(value = "5.0", message = "Rating cannot exceed 5.0")
    private double rating;

    @NotBlank(message = "License plate cannot be blank")
    private String licensePlate;

    @NotBlank(message = "Vehicle type cannot be blank")
    private String vehicleType;

    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Invalid phone number format")
    private String phoneNumber;

    private String imageUrl;
    private Long orderId;

    @Future(message = "License validity date must be in the future")
    private LocalDate licenseValidTill;

    @Min(value = 0, message = "Years of experience cannot be negative")
    private int yearsOfExperience;

    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Invalid emergency contact format")
    private String emergencyContact;

    @PastOrPresent(message = "Join date cannot be in the future")
    private LocalDate joinDate;

    @Min(value = 0, message = "Total trips cannot be negative")
    private int totalTrips;

    @DecimalMin(value = "0.0", message = "Total career distance cannot be negative")
    private double totalCareerDistance;
}
