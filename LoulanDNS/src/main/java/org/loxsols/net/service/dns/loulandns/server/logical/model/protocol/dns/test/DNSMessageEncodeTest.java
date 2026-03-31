package org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.test;


import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.util.test.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.Assert.*;

import java.net.InetAddress;
import java.util.Arrays;
import java.net.UnknownHostException;

import org.junit.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;

import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;


import org.loxsols.net.service.dns.loulandns.client.command.lookup.DNSLookupCommandApplicationConfig;
import org.loxsols.net.service.dns.loulandns.client.command.lookup.DNSLookupCommandApplication;
import org.loxsols.net.service.dns.loulandns.server.common.*;
import org.loxsols.net.service.dns.loulandns.server.common.constants.DNSProtocolConstants;


import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.DNSQueryPartImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.xbill.XbillDNSMessageImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.DNSMessageFactoryImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.model.xbill.XbillDNSProtocolModelInstanceFactoryImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.IDNSMessageFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.DNSAnswerSectionImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.DNSHeaderSectionImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.DNSMessageImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.IDNSQueryPart;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.IDNSAnswerSection;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.IDNSResourceRecord;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.IDNSHeaderSection;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.IDNSQuestionSection;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;

import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSDebugUtils;

import org.loxsols.net.service.dns.loulandns.client.*;

import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.model.IDNSProtocolModelInstanceFactory;

import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.model.*;

import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.*;

/*
 * DNSメッセージのエンコード試験クラス
 * 
 * SpringRunnerクラスやSpringJUnit4ClassRunnerクラスのアノテーションを使用すると、junit5の試験が通らなくなるので注意.
 */
// @RunWith(SpringJUnit4ClassRunner.class)
// @RunWith(SpringRunner.class)
// @ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DNSLookupCommandApplicationConfig.class)
public class DNSMessageEncodeTest
{

    DNSMessageTestUtils messageTestUtils = new DNSMessageTestUtils();

    @Autowired
    @Qualifier("dnsMessageFactoryImpl")
    public IDNSMessageFactory dnsMessageFactory;

	/*
	@Autowired
	void setTitleRepository(IDNSMessageFactory dnsMessageFactory) {
		DNSLookupCommandApplicationConfig config = new DNSLookupCommandApplicationConfig();
		this.dnsMessageFactory = config.configDNSMessageFactory();
	}
	*/
	

    
    /*
	 *
	 * 801-Q001
	 * 
	 * 
     * DNS問い合わせメッセージのエンコード試験001
     * 		# 試験内容
     * 			google.co.jpドメインに対する問い合わせクエリのDNSメッセージをエンコードする.
     *
     *		# 期待結果
     *			DNSメッセージが正常にエンコードできることを確認する.
     * 
     */
	@Test
    public void testEncodeDNSQuestionMessage001()
    {
    	final String QNAME = "google.co.jp.";
    	final int QTYPE = 1;
    	final int QCLASS = 1;

		if ( this.dnsMessageFactory == null )
		{
			// fail("@Autowired(dnsMessageFactoryImpl) is not functioned.");

			try
			{
				this.dnsMessageFactory = new SimpleDNSMessageFactoryImpl();
			}
			catch(DNSServiceCommonException exception)
			{
				exception.printStackTrace();
				fail();
			}
		}

    	byte[] dnsMessageBytes;
    	try
    	{
			IDNSQuestionMessage questionMessage = dnsMessageFactory.createQuestionDNSMesssage(QNAME, QTYPE, QCLASS);
			dnsMessageBytes = questionMessage.getDNSMessageBytes();

			if ( questionMessage.getDNSQuestionSection().getDNSQueries()[0].getDNSQueryType() != QTYPE )
			{
				fail(String.format("QType is not equals. QTYPE=%d, message.QType=%d", QTYPE, questionMessage.getDNSQuestionSection().getDNSQueries()[0].getDNSQueryType() ) );
			}
			

    	}
    	catch(DNSServiceCommonException cause) 
    	{
    		fail("試験用DNSメッセージの作成に失敗.");
    		return ;
    	}



		try
    	{
			IDNSProtocolModelInstanceFactory dnsProtocolModelInstanceFactory 
						= dnsMessageFactory.getDNSProtocolModelInstanceFactory();
			XbillDNSMessageImpl xbillDNSMessageImpl = new XbillDNSMessageImpl(dnsProtocolModelInstanceFactory, dnsMessageBytes);
			IDNSQuestionSection questionSection = xbillDNSMessageImpl.getDNSQuestionSection();

			assertEquals( "DNSメッセージのクエリ部の個数が1件になること",  1, questionSection.getQueryCount() );

			IDNSQueryPart dnsQuery = questionSection.getDNSQueries()[0];
    		assertEquals( "DNSメッセージのクエリ部のQNameが「google.co.jp.」になること",  QNAME, dnsQuery.getDNSQueryName() );
    		assertEquals( "DNSメッセージのクエリ部のQTypeが1(A)になること",  QTYPE, dnsQuery.getDNSQueryType() );
    		assertEquals( "DNSメッセージのクエリ部のQClassが1(IN)になること", QCLASS, dnsQuery.getDNSQueryClass() );



		}
    	catch(DNSServiceCommonException cause)
    	{
			cause.printStackTrace();

    		fail("DNSメッセージのエンコード試験に失敗.");
    		return;
		}

    }
    
    



}