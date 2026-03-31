package org.loxsols.net.service.dns.loulandns.app.spring.udp;


import org.springframework.context.ApplicationContext;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import org.loxsols.net.service.dns.loulandns.app.spring.test.SpringTestRunnerCommandApplicationConfig;
import org.loxsols.net.service.dns.loulandns.client.IDNSLookupClient;
import org.loxsols.net.service.dns.loulandns.client.command.formatter.DNSMessageFormatterCommandApplication;
import org.loxsols.net.service.dns.loulandns.client.command.lookup.simple.SimpleDNSLookupCommandApplication;
import org.loxsols.net.service.dns.loulandns.client.common.DNSClientCommonException;
import org.loxsols.net.service.dns.loulandns.client.impl.simple.SimpleUDPResolverImpl;
import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.common.constants.LoulanDNSConstants;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSCommonUtils;

import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.endpoint.*;
import org.loxsols.net.service.dns.loulandns.server.impl.service.endpoint.udp.*;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.factory.*;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.log.logger.ILoulanDNSLogger;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.log.logger.factory.ILoulanDNSLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@ComponentScan
@EntityScan("org.loxsols.net.service.dns.loulandns.server.http.spring")
@Import(UDPServiceApplicationConfig.class)
public class UDPServiceApplication implements CommandLineRunner 
{

    @Autowired
    @Qualifier("dnsServiceInstanceFactoryImpl")
    IDNSServiceInstanceFactory dnsServiceInstanceFactory;


    @Autowired
    @Qualifier("udpServiceEndpointInstanceImpl")
    IDNSServiceEndpointInstance udpServiceEndpointInstance;


    @Autowired
    @Qualifier("loulanDNSLoggerFactoryImpl")
    ILoulanDNSLoggerFactory loggerFactoryInstance;


    public void exec(String[] args) throws DNSServiceCommonException
    {
        udpServiceEndpointInstance.startDNSServiceEndpoint();
    }



    @Override
    public void run(String... args) throws Exception
    {

        Properties loggerProperties = new Properties();
        ILoulanDNSLogger logger = loggerFactoryInstance.getOrCreateLogger("UDPServiceApplication", loggerProperties);

        try
        {
            exec(args);
        }
        catch(DNSServiceCommonException exception)
        {
            String msg = String.format("UDPServiceApplication is stopped, caused by Critical situation.");
            logger.alert(msg, exception);

            throw exception;
        }
        
    }



    public static void main(String[] args)
    {

        SpringApplicationBuilder applicationBuilder 
            = new SpringApplicationBuilder(UDPServiceApplication.class)
                            .child(UDPServiceApplicationConfig.class)
                            .web(WebApplicationType.NONE);
                            
        applicationBuilder.run(args);

        // SpringApplication.run(UDPServiceApplicationConfig.class, args);
    }

}



