package org.loxsols.net.service.dns.loulandns.app.spring.service.endpoint.doh;

import org.springframework.context.ApplicationContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
// @ComponentScan
@EntityScan("org.loxsols.net.service.dns.loulandns.server.http.spring.model")
public class DoHServiceApplication
{

    public static void main(String[] args)
    {
 
        // SpringApplication.run(DoHServiceApplication.class, args);

        // ApplicationContext context = new AnnotationConfigApplicationContext(DoHServiceApplicationConfig.class );
        SpringApplication.run(DoHServiceApplicationConfig.class, args);
    }

}



