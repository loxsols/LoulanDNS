package org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.service;


import static org.junit.Assert.*;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Properties;
import java.net.UnknownHostException;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

// import org.junit.jupiter.api.extension.ExtendWith;

import org.junit.runner.RunWith;
import org.loxsols.net.service.dns.loulandns.server.common.*;
import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.common.MalformedDNSRequestException;
import org.loxsols.net.service.dns.loulandns.server.common.constants.DNSProtocolConstants;


import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.DNSQueryPartImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.DNSAnswerSectionImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.DNSHeaderSectionImpl;


import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.IDNSQueryPart;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.util.test.*;

import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.IDNSServiceInstance;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.factory.IDNSServiceInstanceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.context.annotation.*;
import org.springframework.util.Assert;



import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.IDNSAnswerSection;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.IDNSResourceRecord;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.IDNSHeaderSection;

import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSDebugUtils;
import org.loxsols.net.service.dns.loulandns.app.spring.service.endpoint.doh.DoHServiceApplicationConfig;



// 以下のアノテーションはJUnit4の単体試験で必要だった.
// ** JUnit4の単体試験で使用するアノテーションの例***********
// @ExtendWith(SpringExtension.class)
// @RunWith(SpringRunner.class)
// @Import(DoHServiceApplicationConfig.class)
// *******************************************************
// 
// 以下のアノテーションはJUnit4の単体試験では不要だった.
// ----不要なアノテーションの例 ----------------------------
// @SpringBootTest
// @ContextConfiguration(classes = DoHServiceApplicationConfig.class)
// @ComponentScan("org.loxsols.net.service.dns")
// @TestConfiguration
// @RunWith(SpringRunner.class)
// --------------------------------
//




/**
 * IDNSServiceInstanceFactoryインターフェースのDIクラスのテスト
 * 
 */
// @ExtendWith(SpringExtension.class)
@RunWith(SpringRunner.class)
@Import(DoHServiceApplicationConfig.class)
public class DNSServiceInstanceFactoryImplTest
{

    DNSMessageTestUtils messageTestUtils = new DNSMessageTestUtils();


    @Autowired
    @Qualifier("dnsServiceInstanceFactoryImpl")
    IDNSServiceInstanceFactory dnsServiceInstanceFactory;


    /**
     * DNSサービスインスタンスの作成メソッドの試験001
     * 
     */
    @Test
    public void testCreateDNSServiceInstanceByName001()
    {
        String userName = "amdin";
        String instanceName = "default";

        String msg = String.format("DNSServiceInstance : userName=%s, instanceName=%s", userName, instanceName);


        System.out.println("testCreateDNSServiceInstanceByName001 : " + msg);

        Path path = Paths.get("").toAbsolutePath();
        System.out.println("testCreateDNSServiceInstanceByName001 : current directory=" + path.toString() );


        IDNSServiceInstance  dnsServiceInstance;
        try
        {
            dnsServiceInstance = dnsServiceInstanceFactory.getOrCreateDNSServiceInstance(userName, instanceName);

            Assert.notNull(dnsServiceInstance, msg );
        }
        catch(DNSServiceCommonException exception)
        {
            exception.printStackTrace();
            fail( exception.toString() );
        }
    }

}


