package org.supplychain.supplychain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication

////@EnableJpaAuditing
public class SupplychainApplication {

    public static void main(String[] args) {
        SpringApplication.run(SupplychainApplication.class, args);
    }

}
