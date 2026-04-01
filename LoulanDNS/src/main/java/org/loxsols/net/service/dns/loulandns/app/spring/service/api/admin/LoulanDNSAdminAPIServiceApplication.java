package org.loxsols.net.service.dns.loulandns.app.spring.service.api.admin;

import org.springframework.context.ApplicationContext;
import org.loxsols.net.service.dns.loulandns.app.spring.service.endpoint.doh.DoHServiceApplicationConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@EntityScan("org.loxsols.net.service.dns.loulandns.server.http.spring.model")
public class LoulanDNSAdminAPIServiceApplication
{

    public static void main(String[] args)
    {
        SpringApplication.run(LoulanDNSAdminAPIServiceApplicationConfig.class, args);
    }


}