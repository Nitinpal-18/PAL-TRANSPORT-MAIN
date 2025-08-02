package com.pal_transport.application.dto;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientDTO {

    private Long id;

    @NotBlank(message = "Company name is required")
    @Size(max = 255, message = "Company name must not exceed 255 characters")
    private String companyName;

    @NotBlank(message = "Contact email is required")
    @Email(message = "Contact email must be valid")
    @Size(max = 100, message = "Contact email must not exceed 100 characters")
    private String contactEmail;

    @NotBlank(message = "Contact person name is required")
    @Size(max = 100, message = "Contact person name must not exceed 100 characters")
    private String contactPersonName;

    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Contact number must be 10-15 digits")
    private String contactNumber;

    /*@Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Alternate contact must be 10-15 digits")*/
    private String alternateContact;

    @Pattern(regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$",
            message = "GST number format is invalid")
    private String gstNumber;

    @Size(max = 100, message = "Contact manager name must not exceed 100 characters")
    private String contactManager;

    @Min(value = 0, message = "Total orders cannot be negative")
    private Integer totalOrders;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastOrderDate;

    @Valid
    private List<AddressDTO> addresses;

}