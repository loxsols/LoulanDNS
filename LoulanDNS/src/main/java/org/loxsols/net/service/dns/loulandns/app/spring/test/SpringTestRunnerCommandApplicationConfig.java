package org.loxsols.net.service.dns.loulandns.app.spring.test;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


import org.xbill.DNS.ZoneTransferException;

import org.loxsols.net.service.dns.loulandns.server.http.spring.repository.UserRepository;
import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.common.constants.LoulanDNSConstants;

import org.loxsols.net.service.dns.loulandns.server.http.spring.service.LoulanDNSDBService;
import org.loxsols.net.service.dns.loulandns.server.http.spring.service.LoulanDNSLogicalDBService;
import org.loxsols.net.service.dns.loulandns.server.http.spring.service.test.LoulanDNSLogicalDBServiceTest;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.model.IDNSProtocolModelInstanceFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.factory.IDNSServiceInstanceFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.service.LoulanDNSLogicalModelService;

import org.loxsols.net.service.dns.loulandns.server.http.spring.common.security.provider.LoulanDNSAuthenticationProvider;
import org.loxsols.net.service.dns.loulandns.server.http.spring.common.security.provider.impl.*;
import org.loxsols.net.service.dns.loulandns.server.http.spring.common.security.provider.impl.mock.*;

import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.*;


import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.model.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.model.DNSProtocolModelInstanceFactoryImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.IDNSMessageFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.*;

import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.*;

import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.service.*;


@Configuration
@EnableAutoConfiguration
@EnableJpaRepositories( basePackages = "org.loxsols.net.service.dns.loulandns.server.http.spring.repository" )
@EntityScan("org.loxsols.net.service.dns.loulandns.server.http.spring")
@ComponentScan("org.loxsols.net.service.dns.loulandns.server.http.spring")
@ComponentScan("org.loxsols.net.service.dns.loulandns.server.http.spring.repository")
@ComponentScan("org.loxsols.net.service.dns.loulandns.server.http.spring.model")
@ComponentScan("org.loxsols.net.service.dns.loulandns.server.http.spring.controller")
@ComponentScan("org.loxsols.net.service.dns.loulandns.server.http.spring.common.security")
@ComponentScan("org.loxsols.net.service.dns.loulandns.server.http.spring.common.security.provider")
public class SpringTestRunnerCommandApplicationConfig
{


    @Bean(name = "loulanDNSDBServiceImpl")
    LoulanDNSDBService configLoulanDNSDBServiceImpl() throws IOException, ZoneTransferException
    {
        LoulanDNSDBService dbService = new LoulanDNSDBService();
        return dbService;
    }


    @Bean(name = "loulanDNSLogicalDBServiceImpl")
    LoulanDNSLogicalDBService configLoulanDNSLogicalDBServiceImpl() throws IOException, ZoneTransferException
    {
        LoulanDNSLogicalDBService logicalDBService = new LoulanDNSLogicalDBService();
        return logicalDBService;
    }


    @Bean(name = "loulanDNSLogicalModelServiceImpl")
    LoulanDNSLogicalModelService configLoulanDNSLogicalModelServiceImpl() throws IOException, ZoneTransferException
    {
        // 本クラス内のファクトリメソッドを利用して、LoulanDNSLogicalDBServiceクラスの実体を取得する.
        LoulanDNSLogicalDBService logicalDBService = configLoulanDNSLogicalDBServiceImpl();

        LoulanDNSLogicalModelService serviceImpl = new LoulanDNSLogicalModelService(logicalDBService);
        return serviceImpl;
    }
    


    @Bean(name = "loulanDNSAuthenticationProviderImpl")
    LoulanDNSAuthenticationProvider connfigLoulanDNSUserBasicAuthenticationProviderImpl()
    {
        LoulanDNSAuthenticationProvider provider = new LoulanDNSUserBasicAuthenticationProvider();
        return provider;
    }


    static IDNSProtocolModelInstanceFactory dnsProtocolModelInstanceFactory = null;
    static void initDNSProtocolModelInstanceFactory() throws DNSServiceCommonException
    {
        if ( dnsProtocolModelInstanceFactory == null )
        {
            dnsProtocolModelInstanceFactory = new DNSProtocolModelInstanceFactoryImpl();
        }
    }

    @Bean(name = "dnsProtocolModelInstanceFactoryImpl" )
    IDNSProtocolModelInstanceFactory configDNSProtocolModelInstanceFactory() throws DNSServiceCommonException
    {
        initDNSProtocolModelInstanceFactory();

        return dnsProtocolModelInstanceFactory;
    }


    @Bean( name="dnsHeaderSectionFactoryImpl")
    IDNSHeaderSectionFactory configDNSHeaderSectionFactory()
    {
        IDNSHeaderSectionFactory dnsQuestionSectionFactoryImpl = new DNSHeaderSectionFactoryImpl();
        return dnsQuestionSectionFactoryImpl;
    }


    @Bean( name="dnsQuestionSectionFactoryImpl")
    IDNSQuestionSectionFactory configDNSQuestionSectionFactory()
    {
        try
        {
            initDNSProtocolModelInstanceFactory();

            IDNSQuestionSectionFactory dnsQuestionSectionFactoryImpl = new DNSQuestionSectionFactoryImpl( dnsProtocolModelInstanceFactory );
            return dnsQuestionSectionFactoryImpl;
        }
        catch(DNSServiceCommonException cause)
        {
            cause.printStackTrace();

            return null;
        }
    }

    @Bean( name="dnsAnswerSectionFactoryImpl" )
    IDNSAnswerSectionFactory configDNSAnswerSectionFactory()
    {
        IDNSAnswerSectionFactory dnsAnswerSectionFactoryImpl = new DNSAnswerSectionFactoryImpl();
        return dnsAnswerSectionFactoryImpl;
    }


    @Bean( name="dnsAuthoritySectionFactoryImpl" )
    IDNSAuthoritySectionFactory configDNSAuthoritySectionFactory()
    {
        IDNSAuthoritySectionFactory dnsAuthoritySectionFactoryImpl = new DNSAuthoritySectionFactoryImpl();
        return dnsAuthoritySectionFactoryImpl;
    }

    @Bean( name="dnsAdditionalSectionFactoryImpl" )
    IDNSAdditionalSectionFactory configDNSAdditionalSectionFactory()
    {
        IDNSAdditionalSectionFactory dnsAdditonalSectionFactoryImpl = new DNSAdditionalSectionFactoryImpl();
        return dnsAdditonalSectionFactoryImpl;
    }


    @Bean( name="dnsMessageFactoryImpl" )
    IDNSMessageFactory configDNSMessageFactory() throws DNSServiceCommonException
    {
        IDNSMessageFactory dnsMessageFactoryImpl = new DNSMessageFactoryImpl();
        return dnsMessageFactoryImpl;
    }


    @Bean( name="dnsServiceInstanceFactoryImpl" )
    IDNSServiceInstanceFactory configDNSServiceInstanceFactory() throws DNSServiceCommonException
    {
        IDNSServiceInstanceFactory dnsServiceInstanceFactoryImpl = new DNSServiceInstanceFactoryImpl();

        // プロパティを設定する.
        Properties properties = System.getProperties();
        dnsServiceInstanceFactoryImpl.init( properties );

        return dnsServiceInstanceFactoryImpl;
    }


    @Bean
    LoulanDNSLogicalDBServiceTest configLoulanDNSLogicalDBServiceTest() throws  DNSServiceCommonException
    {
        LoulanDNSLogicalDBServiceTest testImpl = new LoulanDNSLogicalDBServiceTest();
        
        return testImpl;
    }









}



