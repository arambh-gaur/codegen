package com.vmware.cet.codegen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
public class CodegenApplication {

	public static void main(String[] args) {
		SpringApplication.run(CodegenApplication.class, args);
	}

}
