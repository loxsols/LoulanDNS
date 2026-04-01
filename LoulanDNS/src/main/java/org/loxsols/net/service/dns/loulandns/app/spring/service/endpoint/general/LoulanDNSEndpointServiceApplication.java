package org.loxsols.net.service.dns.loulandns.app.spring.service.endpoint.general;


import org.springframework.context.ApplicationContext;

import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

import java.util.Properties;

import org.loxsols.net.service.dns.loulandns.app.spring.test.SpringTestRunnerCommandApplicationConfig;
import org.loxsols.net.service.dns.loulandns.app.spring.service.endpoint.udp.UDPServiceApplicationConfig;
import org.loxsols.net.service.dns.loulandns.client.IDNSLookupClient;
import org.loxsols.net.service.dns.loulandns.client.command.formatter.DNSMessageFormatterCommandApplication;
import org.loxsols.net.service.dns.loulandns.client.command.lookup.simple.SimpleDNSLookupCommandApplication;
import org.loxsols.net.service.dns.loulandns.client.common.DNSClientCommonException;
import org.loxsols.net.service.dns.loulandns.client.impl.simple.SimpleUDPResolverImpl;
import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.common.constants.LoulanDNSConstants;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSCommonUtils;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.resolver.IDNSResolverInstance;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.resolver.factory.IDNSResolverInstanceFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.IDNSServiceInstance;

import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.endpoint.*;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.endpoint.factory.IDNSServiceEndpointInstanceFactory;
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
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


/**
 * コマンドライン引数で指定した任意のDNSServiceEndpointを起動するアプリケーション.
 * 
 */
@SpringBootApplication
@ComponentScan
@EntityScan("org.loxsols.net.service.dns.loulandns.server.http.spring")
@Import(LoulanDNSEndpointServiceApplicationConfig.class)
public class LoulanDNSEndpointServiceApplication implements CommandLineRunner 
{



    @Autowired
    @Qualifier("dnsServiceEndpointInstanceFactoryImpl")
    IDNSServiceEndpointInstanceFactory dnsServiceEndpointInstanceFactory;


    @Autowired
    @Qualifier("dnsServiceInstanceFactoryImpl")
    IDNSServiceInstanceFactory dnsServiceInstanceFactory;

    
    @Autowired
    @Qualifier("dnsResolverInstanceFactoryImpl")
    IDNSResolverInstanceFactory dnsResolerInstanceFactory;


    @Autowired
    @Qualifier("loulanDNSLoggerFactoryImpl")
    ILoulanDNSLoggerFactory loggerFactoryInstance;


    LoulanDNSUtils loulanDNSUtils = new LoulanDNSUtils();


    private IDNSServiceEndpointInstance getDNSServiceEndpointInstanceByDNSServiceEndpointInstanceID(long dnsServiceEndpointInstanceID) throws DNSServiceCommonException
    {
        IDNSServiceEndpointInstance instance = dnsServiceEndpointInstanceFactory.getOrCreateDNSServiceEndpointInstance(dnsServiceEndpointInstanceID);
        return instance;
    }

    
    private IDNSServiceEndpointInstance getDNSServiceEndpointInstanceByDNSServiceInstance(String serviceUserName, String serviceInstanceName, String endpointInstanceName ) throws DNSServiceCommonException
    {
        IDNSServiceEndpointInstance instance = dnsServiceEndpointInstanceFactory.getOrCreateDNSServiceEndpointInstance(serviceUserName, serviceInstanceName, endpointInstanceName);
        return instance;
    }



    private IDNSServiceEndpointInstance createTemporaryDNSServiceEndpointInstance(String endpointUserName, String endpointInstanceName, String endpointInstanceType, Properties endpointProperties, IDNSServiceInstance bindServiceInstance) throws DNSServiceCommonException 
    {
        IDNSServiceEndpointInstance endpointInstance = dnsServiceEndpointInstanceFactory.getOrCreateTemporaryDNSServiceEndpointInstance(endpointUserName, endpointInstanceType, endpointProperties, endpointInstanceName, bindServiceInstance );
        return endpointInstance;
    }


    private IDNSServiceInstance createTemporaryDNSServiceInstance(String serviceUserName, String serviceInstanceName, String serviceInstanceType, Properties serviceInstanceProperties, String resolverInstanceName, String resovlerInstanceType, Properties resolverInstanceProperties ) throws DNSServiceCommonException
    {

        long serviceInstaneTypeCode = loulanDNSUtils.serviceTypeNameToServiceTypeCode(serviceInstanceType);
        long resolverInstanceTypeCode = loulanDNSUtils.resolverTypeNameToResolverTypeCode(resovlerInstanceType);

        IDNSResolverInstance resolverInstance = dnsResolerInstanceFactory.getOrCreateTemporaryResolverInstance( serviceUserName, resolverInstanceName, resolverInstanceTypeCode, resolverInstanceProperties);
        
        IDNSServiceInstance serviceInstance = dnsServiceInstanceFactory.getOrCreateTemporaryDNSServiceInstance(serviceUserName, serviceInstanceName, resolverInstanceTypeCode, serviceInstanceProperties, resolverInstance);


        return serviceInstance;
    }

    private IDNSServiceInstance createDefaultDNSServiceInstance()  throws DNSServiceCommonException
    {
        IDNSServiceInstance serviceInstance = dnsServiceInstanceFactory.getOrCreateDefaultDNSServiceInstance();
        return serviceInstance;
    }


    public void exec(String[] args) throws DNSServiceCommonException
    {

        LoulanDNSEndpointServiceApplicationCommandLineParser parser = new LoulanDNSEndpointServiceApplicationCommandLineParser();

        if ( args.length == 0 )
        {
            parser.printUsage(System.out);

            String msg = String.format("ERROR : Unknown command line options.");
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        parser.parse(args);

        IDNSServiceEndpointInstance endpointInstance;
        if ( parser.isSystemTypeDatabase() )
        {
            // ロード対象のシステムタイプにデータベースが選択された.
            // -load database -su <ServiceUserName> -sn <ServiceInstanceName> -en <EndpointInstanceName> 
            String serviceUserName = parser.getServiceUserName();
            String serviceInstanceName = parser.getServiceInstanceName();
            String endpointInstanceName = parser.getEndpointInstanceName();

            endpointInstance = getDNSServiceEndpointInstanceByDNSServiceInstance( serviceUserName, serviceInstanceName, endpointInstanceName );
        }
        else if ( parser.isSystemTypeTemporary() )
        {
            // ロード対象のシステムタイプにtemporaryが選択された.
            // -load temporary -su <ServiceUserName> -sn <ServiceInstanceName> -st <ServiceType> [-sargs <key1=value1;>...] -en <EndpointInstanceName> -et <EndpointType> [-eargs <key1=value1;...>] -rt <ResolverType>  [-rargs <key1=value1;...>]

            IDNSServiceInstance bindServiceInstance = createTemporaryDNSServiceInstance( parser.getServiceUserName(), parser.getServiceInstanceName(), parser.getServiceInstanceType(), parser.getServiceInstanceProperties(), parser.getResolverInstanceName(), parser.getResolverInstanceType(), parser.getResolverInstanceProperties() );
            endpointInstance = createTemporaryDNSServiceEndpointInstance( parser.getEndpointUserName(), parser.getEndpointInstanceName(), parser.getEndpointInstanceType(), parser.getEndpointInstanceProperties(), bindServiceInstance );
        }
        else
        {
            String msg = String.format("ERROR : Unknown command line options.");
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }


        // DNSサービスエンドポイントのサービスを開始する.
        endpointInstance.startDNSServiceEndpoint();

        // エンドポイントのサービスが終了したらここに到達する.
        String msgDone = String.format("DNS Endpoint Service is done.");
        System.out.println(msgDone);

        return ;
    }



    @Override
    public void run(String... args) throws Exception
    {

        String loggerName = "UDPServiceApplication";
        Properties loggerProperties = new Properties();
        ILoulanDNSLogger logger = loggerFactoryInstance.getOrCreateLogger(loggerName, loggerProperties);
        logger.info( String.format("Logger is created. loggerName=%s, LoggerClass=%s", loggerName, logger.getClass().getName() ) );

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
            = new SpringApplicationBuilder(LoulanDNSEndpointServiceApplication.class)
                            .child(LoulanDNSEndpointServiceApplicationConfig.class)
                            .web(WebApplicationType.NONE);
                            
        applicationBuilder.run(args);

    }

}



