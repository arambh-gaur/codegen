package com.vmware.cet.codegen.config;

import net.bytebuddy.ByteBuddy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public ByteBuddy getByteBuddy() {
        return new ByteBuddy();
    }

}
