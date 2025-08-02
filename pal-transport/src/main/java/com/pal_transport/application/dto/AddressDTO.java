package com.pal_transport.application.dto;

import com.pal_transport.application.enums.AddressType;
import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AddressDTO {
    private Long id;

    @NotBlank(message = "Address Line 1 is required")
    @Size(max = 255, message = "Address Line 1 must not exceed 255 characters")
    private String addressLine1;

    @Size(max = 255, message = "Address Line 2 must not exceed 255 characters")
    private String addressLine2;

    @Size(max = 255, message = "Address Line 3 must not exceed 255 characters")
    private String addressLine3;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @NotBlank(message = "State is required")
    @Size(max = 100, message = "State must not exceed 100 characters")
    private String state;

    @NotBlank(message = "Pin code is required")
    @Size(max = 10, message = "Pin code must not exceed 10 characters")
    private String pinCode;

    @NotBlank(message = "Country is required")
    @Size(max = 100, message = "Country must not exceed 100 characters")
    private String country;

    @NotNull(message = "Address type is required")
    private AddressType addressType;
}