package org.loxsols.net.service.dns.loulandns.client.command.formatter;


import java.io.IOException;
import java.net.InetAddress;

import org.loxsols.net.service.dns.loulandns.client.impl.*;
import org.loxsols.net.service.dns.loulandns.server.common.constants.LoulanDNSConstants;

import org.loxsols.net.service.dns.loulandns.server.http.spring.common.security.provider.LoulanDNSAuthenticationProvider;

import org.loxsols.net.service.dns.loulandns.server.http.spring.service.LoulanDNSDBService;
import org.loxsols.net.service.dns.loulandns.server.http.spring.service.LoulanDNSLogicalDBService;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.DNSMessageFactoryImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.section.DNSAnswerSectionFactoryImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.section.DNSHeaderSectionFactoryImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.section.DNSQuestionSectionFactoryImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.model.DNSProtocolModelInstanceFactoryImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.IDNSMessageFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.section.IDNSAnswerSectionFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.section.IDNSHeaderSectionFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.section.IDNSQuestionSectionFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.model.IDNSProtocolModelInstanceFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.service.LoulanDNSLogicalModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.xbill.DNS.ZoneTransferException;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import org.loxsols.net.service.dns.loulandns.client.*;

@SpringBootApplication
@ComponentScan
@EntityScan("org.loxsols.net.service.dns.loulandns.server.http.spring.model")
public class DNSMessageFormatterCommandApplication implements CommandLineRunner
{



    @Autowired
    @Qualifier("dnsMessageFormatterImpl")
    IDNSMessageFormatter dnsMessageFormatter;


    @Autowired
    @Qualifier("dnsMessageFactoryImpl")
    IDNSMessageFactory dnsMessageFactory;




    @Override
    public void run(String... args) throws Exception
    {
        System.out.println("[DEBUG] start to DNSMessageFormatterCommandApplication.run() called");

        String qname = "google.co.jp.";
        int qtype = 1;
        int qclass = 1;

        IDNSMessage dnsMessage  = dnsMessageFactory.createQuestionDNSMesssage(qname, qtype, qclass);
        String hexString = dnsMessageFormatter.toHexString(dnsMessage);

        System.out.println( hexString );
    }

    public static void main(String[] args)
    {
        System.out.println("[DEBUG] start to DNSMessageFormatterCommandApplication");


        SpringApplicationBuilder applicationBuilder 
                        = new SpringApplicationBuilder(DNSMessageFormatterCommandApplication.class)
                                    .web(WebApplicationType.NONE);

        applicationBuilder.run(args);
    }

}
