package coop.bancocredicoop.guv.persistor.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class PersistorConfig {

    	/*
	@Bean
	public Module vavrModule() {
		return new VavrModule();
	}*/


    @Primary
    @Bean
    public Module jdkModule() {
        return new Jdk8Module();
    }

}
