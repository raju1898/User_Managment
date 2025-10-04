package com.SpringSecurtiyOwn.SpringSecurtiyOwn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(scanBasePackages = { "com.SpringSecurtiyOwn.SpringSecurtiyOwn" })
@EntityScan("com.SpringSecurtiyOwn.SpringSecurtiyOwnModel")
@ComponentScan(basePackages = {
	    "com.SpringSecurtiyOwn.SpringSecurtiyOwnController",
	    "com.SpringSecurtiyOwn.SpringSecurtiyOwnService",
	    " com.SpringSecurtiyOwn.SpringSecurtiyOwnSecurityConfig",
	     "com.SpringSecurtiyOwn.SpringSecurtiyOwnJwtHelper"
	})

public class SpringSecurtiyOwnApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringSecurtiyOwnApplication.class, args);
	}

}
