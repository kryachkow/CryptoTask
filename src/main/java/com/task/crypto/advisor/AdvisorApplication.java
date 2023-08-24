package com.task.crypto.advisor;

import com.task.crypto.advisor.configuration.CsvStorageProperties;
import com.task.crypto.advisor.configuration.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
@EnableConfigurationProperties({RsaKeyProperties.class, CsvStorageProperties.class})
@EnableAspectJAutoProxy
public class AdvisorApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdvisorApplication.class, args);
    }

}
