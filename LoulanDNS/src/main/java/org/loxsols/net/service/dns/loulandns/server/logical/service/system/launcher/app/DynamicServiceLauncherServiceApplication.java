package org.loxsols.net.service.dns.loulandns.server.logical.service.system.launcher.app;


import java.util.*;


import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.loxsols.net.service.dns.loulandns.app.spring.service.endpoint.doh.DoHServiceApplicationConfig;
import org.loxsols.net.service.dns.loulandns.app.spring.service.endpoint.general.LoulanDNSEndpointServiceApplicationConfig;
import org.loxsols.net.service.dns.loulandns.app.spring.service.endpoint.udp.UDPServiceApplication;
import org.loxsols.net.service.dns.loulandns.app.spring.service.endpoint.udp.UDPServiceApplicationConfig;
import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.launcher.IDyanmicServiceDescriptor;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.launcher.IDynamicServiceLauncher;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.launcher.factory.*;

import org.loxsols.net.service.dns.loulandns.server.logical.service.system.log.logger.ILoulanDNSLogger;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.log.logger.factory.ILoulanDNSLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.deployer.spi.app.AppDeployer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.beans.factory.annotation.Qualifier;


@SpringBootApplication
@ComponentScan
@Import(DynamicServiceLauncherServiceApplicationConfig.class)
public class DynamicServiceLauncherServiceApplication implements CommandLineRunner 
{


    @Autowired
    @Qualifier("loulanDNSLoggerFactoryImpl")
    ILoulanDNSLoggerFactory loggerFactoryInstance;


    @Autowired
    @Qualifier("loulanDNSDynamicServiceLauncherFactoryImpl")
    IDynamicServiceLauncherFactory dynamicServiceLauncherFactory;





    public void exec(String mainClass, String[] serviceArgs, Properties jvmProperties) throws DNSServiceCommonException
    {
        IDynamicServiceLauncher serviceLauncher = 
            dynamicServiceLauncherFactory.getOrCreateDynamicServiceLauncher();

        IDyanmicServiceDescriptor serviceDescriptor = serviceLauncher.createDynamicServiceDiscriptor("test-service", mainClass, serviceArgs, jvmProperties);
        serviceDescriptor.startDynamicService();
    }





    @Override
    public void run(String... args) throws Exception
    {

        Properties loggerProperties = new Properties();
        ILoulanDNSLogger logger = loggerFactoryInstance.getOrCreateLogger("DynamicServiceLauncherServiceApplication", loggerProperties);


        if ( args.length < 1 )
        {
            printUsage();
            return ;
        }

        String mainClass = args[0];
        String[] coArgs = new String[ args.length -1 ];
        for( int i=0; i < coArgs.length; i++)
        {
            coArgs[i] = args[i + 1];
        }

        //TODO
        Properties jvmProperties = new Properties();

        try
        {
            exec(mainClass, coArgs, jvmProperties);
        }
        catch(DNSServiceCommonException exception)
        {
            String msg = String.format("DynamicServiceLauncherServiceApplication is stopped, caused by Critical situation.");
            logger.alert(msg, exception);

            throw exception;
        }


        
    }



    private void printUsage()
    {
        System.out.println("Usage : <mainClass> [args...]");
    }



    public static void main(String[] args)
    {

        /*
        SpringApplicationBuilder applicationBuilder 
            = new SpringApplicationBuilder(DynamicServiceLauncherServiceApplication.class)
                            .child(DynamicServiceLauncherServiceApplicationConfig.class)
                            .web(WebApplicationType.NONE);
        */

        SpringApplicationBuilder applicationBuilder 
            = new SpringApplicationBuilder(DynamicServiceLauncherServiceApplication.class)
                            .child(DynamicServiceLauncherServiceApplicationConfig.class);        
                            
        ConfigurableApplicationContext context = applicationBuilder.run(args);

        // 子プロセスにコマンドを投げたら、自分（親）のSpringコンテキストを即座に閉じる
        // これによりHikariCP等の接続プールが解放され、HSQLDBのファイルロックが外れます
        // ((ConfigurableApplicationContext) context).close();

    }





}