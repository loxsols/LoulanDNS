package org.loxsols.net.service.dns.loulandns.server.http.spring.service.test;



import static org.junit.Assert.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.junit.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.loxsols.net.service.dns.loulandns.server.common.*;
import org.loxsols.net.service.dns.loulandns.server.common.constants.DNSProtocolConstants;


import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.DNSQueryPartImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.DNSAnswerSectionImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.DNSHeaderSectionImpl;


import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.IDNSQueryPart;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.util.test.DNSMessageTestUtils;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.IDNSServiceInstance;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.factory.IDNSServiceInstanceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import org.springframework.test.context.junit.jupiter.SpringExtension;



import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.IDNSAnswerSection;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.IDNSResourceRecord;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.IDNSHeaderSection;

import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSDebugUtils;
import org.loxsols.net.service.dns.loulandns.server.http.spring.test.LoulanDNDSpringTestException;
import org.loxsols.net.service.dns.loulandns.server.http.spring.test.LoulanDNSSSpringTestTargeImpltBase;
import org.loxsols.net.service.dns.loulandns.server.http.spring.test.LoulanDNSSSpringTestTarget;
import org.loxsols.net.service.dns.loulandns.server.logical.model.*;

import org.loxsols.net.service.dns.loulandns.server.http.spring.service.*;
import org.loxsols.net.service.dns.loulandns.app.spring.test.*;


@ComponentScan
@RunWith(SpringRunner.class)
@Import(SpringTestRunnerCommandApplicationConfig.class)
@TestPropertySource(locations = "/application-test.properties")
@ComponentScan("org.loxsols.net.service.dns.loulandns.server.http.spring")
public class LoulanDNSLogicalDBServiceTest extends LoulanDNSSSpringTestTargeImpltBase implements LoulanDNSSSpringTestTarget
{

    
    DNSMessageTestUtils messageTestUtils = new DNSMessageTestUtils();


    @Autowired
    @Qualifier("loulanDNSLogicalDBServiceImpl")
    LoulanDNSLogicalDBService logicalDBService;


    @Test
    public void testGetUserInfoList001() throws LoulanDNDSpringTestException
    {

        Path path = Paths.get("").toAbsolutePath();
        printTestLog("testGetUserInfoList001 : current directory=" + path.toString() );

        try
        {


            List<UserInfo> list = logicalDBService.getUserInfoList();

            printTestLog("testGetUserInfoList001 : list.size()=" + list.size() );
            printTestLog("testGetUserInfoList001 : spring.datasource.driverClassName=" + System.getProperty("spring.datasource.driverClassName") );

            String msg = String.format("testGetUserInfoList001 : " );
            Assert.notNull(list, msg + "UserInfoList is null.");
            Assert.notEmpty(list, msg + "UserInfoList is empty.");

            printTestLog(msg + " UserInfoList.size() = " + list.size() );

        }
        catch(LoulanDNSSystemServiceException exception)
        {
            printTestLog("Test failed.");

            exception.printStackTrace();
            fail(exception.toString());
        }

    }

    @Test
    public void testGetUserInfo001() throws LoulanDNDSpringTestException
    {

        String userName = "admin";

        try
        {
            UserInfo userInfo = logicalDBService.getUserInfo(userName);

            String msg = String.format("testGetUserInfo001 : userName=%s :", userName);
            Assert.notNull(userInfo, msg + "UserInfo is null" );
            Assert.isTrue( (userInfo.userName.equals(userName) ), msg + "userName is not " + userName  + ", userInfo.userName=" + userInfo.userName );

        }
        catch(LoulanDNSSystemServiceException exception)
        {
            printTestLog("Test failed.");

            exception.printStackTrace();
            fail(exception.toString());
        }

    }


    /**
     * DNSサービスインスタンスの取得テスト001
     * 
     */
    @Test
    public void testGetDNSServiceInstanceInfo001() throws LoulanDNDSpringTestException
    {
        String userName = "admin";

        String msg = null;
        try
        {
            UserInfo userInfo = logicalDBService.getUserInfo(userName);

            msg = String.format("testGetDNSServiceInstanceInfo001 : userName=%s :", userName);
            printTestLog(msg);

            Assert.notNull(userInfo, msg + "UserInfo is null" );
            Assert.isTrue( (userInfo.userName.equals(userName) ), msg + "userName is not " + userName  + ", userInfo.userName=" + userInfo.userName );


            List<DNSServiceInstanceInfo> dnsServiceInstanceInfoList = userInfo.getDNSServiceInstanceInfoList();
            msg = String.format("testGetDNSServiceInstanceInfo001 : userName=%s, getDNSServiceInstanceInfoList().size=%d", userName, dnsServiceInstanceInfoList.size() );
            printTestLog(msg);

            msg = String.format("testGetDNSServiceInstanceInfo001 : DNSServiceInstance is empty. : userName=%s, getDNSServiceInstanceInfoList().size=%d", userName, dnsServiceInstanceInfoList.size() );
            Assert.notEmpty(dnsServiceInstanceInfoList, msg);

            for( DNSServiceInstanceInfo dnsServiceInstanceInfo : dnsServiceInstanceInfoList )
            {
                msg = String.format("testGetDNSServiceInstanceInfo001 : userName=%s, dnsServiceInstanceInfo.getDNSServiceInstanceName()=%s", userName, dnsServiceInstanceInfo.getDNSServiceInstanceName() );
                printTestLog(msg);

                Assert.notNull(dnsServiceInstanceInfo.getDNSServiceInstanceName(), "DNSServiceInstanceName is null.");
            }

        }
        catch(LoulanDNSSystemServiceException exception)
        {
            printTestLog("Test failed.");

            exception.printStackTrace();
            fail(exception.toString());
        }


    }




}