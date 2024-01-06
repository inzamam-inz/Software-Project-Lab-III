package com.unishare.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@CrossOrigin(origins = "*")
@SpringBootApplication
//@EnableSwagger2
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}
