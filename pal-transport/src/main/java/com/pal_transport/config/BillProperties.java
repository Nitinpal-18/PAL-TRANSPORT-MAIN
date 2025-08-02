package com.pal_transport.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "bill.from")
@Getter
@Setter
public class BillProperties {
    private String companyName;
    private String address;
    private String phone;
    private String bankDetails;
}
