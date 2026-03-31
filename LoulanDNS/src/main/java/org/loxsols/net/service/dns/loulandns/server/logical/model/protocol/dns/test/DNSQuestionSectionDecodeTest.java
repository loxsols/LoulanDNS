package org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.test;


import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.util.test.*;

import org.springframework.jdbc.object.RdbmsOperation;

import static org.junit.Assert.*;

import java.net.InetAddress;
import java.util.Arrays;
import java.net.UnknownHostException;

import org.junit.Test;

// import org.junit.jupiter.api.BeforeAll;

import org.loxsols.net.service.dns.loulandns.server.common.*;
import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.common.MalformedDNSRequestException;
import org.loxsols.net.service.dns.loulandns.server.common.constants.DNSProtocolConstants;


import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.DNSQueryPartImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.DNSAnswerSectionImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.DNSHeaderSectionImpl;


import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.IDNSQueryPart;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.IDNSAnswerSection;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.IDNSResourceRecord;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.IDNSHeaderSection;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.IDNSQuestionSection;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSDebugUtils;

import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.model.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.model.*;


/*
 * DNSメッセージのQuestionセクションのデコード試験クラス
 * 
 */
public class DNSQuestionSectionDecodeTest
{

    DNSMessageTestUtils messageTestUtils = new DNSMessageTestUtils();

	static IDNSProtocolModelInstanceFactory factory;

	static
	{
		try
		{
			factory = new SimpleDNSProtocolModelInstanceFactoryImpl();
		}
		catch(DNSServiceCommonException exception)
		{
			exception.printStackTrace();
		}

	}
    
    /*
     * DNS問い合わせメッセージのQuestionセクションのデコード試験001
     * 		# 試験内容
     * 			DNSメッセージのQuestionセクションをデコードする.
     *
     *		# 期待結果
     *			DNSメッセージのQuestionセクションが正常にデコードできることを確認する.
     * 
     */
    @Test
    public void testDecodeDNSQuestionSection001()
    {

		String qname = "www.google.co.jp.";
		int qtype = 1;
		int qclass = 1;


		IDNSQuestionSection dnsQuestion;

    	byte[] questionSecBytes;
    	try
    	{
			questionSecBytes = messageTestUtils.createDNSQuestionSectionBytes(qname, qtype, qclass);
		}
    	catch(DNSServiceCommonException cause) 
    	{
    		fail("試験用DNSメッセージの作成に失敗.");
    		return ;
    	}

		
		try
    	{

			dnsQuestion = factory.createDNSQuestionSectionInstance();

			// デコード処理.
			dnsQuestion.setDNSQuestionSectionBytes(1, questionSecBytes);

			assertEquals( "DNSQuestionセクションのqname == " + qname,  qname, dnsQuestion.getDNSQueries()[0].getDNSQueryName() );			
			assertEquals( "DNSQuestionセクションのqclass == " + qclass,  qclass, dnsQuestion.getDNSQueries()[0].getDNSQueryClass() );
			assertEquals( "DNSQuestionセクションのqtype == " + qtype,  qtype, dnsQuestion.getDNSQueries()[0].getDNSQueryType() );

    	}
    	catch(DNSServiceCommonException cause)
    	{
			cause.printStackTrace();

    		fail("DNSメッセージのデコード処理に失敗.");
    		return;
    	}

    }



	/*
     * DNS問い合わせメッセージのQuestionセクションのqclassフィールドのデコード試験
     * 		# 試験内容
     * 			DNSメッセージのQuestionセクションのqclassフィールドを0x00000-0xffffまでセットしてデコード値を比較する.
     *
     *		# 期待結果
     *			DNSメッセージのQuestionセクションのqclassフィールド値が正常にデコードできることを確認する.
     */
    @Test
    public void testDecodeDNSQuestionSectionQClassField001()
    {

		String qname = "www.google.co.jp.";
		int qtype = 1;


		IDNSQuestionSection dnsQuestion;

		try
    	{


			for( int i = 0; i <= 0xffff; i++)
			{
				int qclass = i;
				byte[] questionSecBytes = messageTestUtils.createDNSQuestionSectionBytes(qname, qtype, qclass);

				// デコード処理.
				dnsQuestion = factory.createDNSQuestionSectionInstance();
				dnsQuestion.setDNSQuestionSectionBytes(1, questionSecBytes);

				assertEquals( "DNSQuestionセクションのqname == " + qname,  qname, dnsQuestion.getDNSQueries()[0].getDNSQueryName() );			
				assertEquals( "DNSQuestionセクションのqclass == " + qclass,  qclass, dnsQuestion.getDNSQueries()[0].getDNSQueryClass() );
				assertEquals( "DNSQuestionセクションのqtype == " + qtype,  qtype, dnsQuestion.getDNSQueries()[0].getDNSQueryType() );
			}

    	}
    	catch(DNSServiceCommonException cause)
    	{
			cause.printStackTrace();

    		fail("DNSメッセージのデコード処理に失敗.");
    		return;
    	}

    }

    

	/*
     * DNS問い合わせメッセージのQuestionセクションのqtypeフィールドのデコード試験
     * 		# 試験内容
     * 			DNSメッセージのQuestionセクションのqtypeフィールドを0x00000-0xffffまでセットしてデコード値を比較する.
     *
     *		# 期待結果
     *			DNSメッセージのQuestionセクションのqtypeフィールド値が正常にデコードできることを確認する.
     */
    @Test
    public void testDecodeDNSQuestionSectionQTypeField001()
    {

		String qname = "www.google.co.jp.";
		int qclass = 1;


		IDNSQuestionSection dnsQuestion;

		try
    	{


			for( int i = 0; i <= 0xffff; i++)
			{
				int qtype = i;
				byte[] questionSecBytes = messageTestUtils.createDNSQuestionSectionBytes(qname, qtype, qclass);

				// デコード処理.
				dnsQuestion = factory.createDNSQuestionSectionInstance();
				dnsQuestion.setDNSQuestionSectionBytes(1, questionSecBytes);

				assertEquals( "DNSQuestionセクションのqname == " + qname,  qname, dnsQuestion.getDNSQueries()[0].getDNSQueryName() );			
				assertEquals( "DNSQuestionセクションのqclass == " + qclass,  qclass, dnsQuestion.getDNSQueries()[0].getDNSQueryClass() );
				assertEquals( "DNSQuestionセクションのqtype == " + qtype,  qtype, dnsQuestion.getDNSQueries()[0].getDNSQueryType() );
			}

    	}
    	catch(DNSServiceCommonException cause)
    	{
			cause.printStackTrace();

    		fail("DNSメッセージのデコード処理に失敗.");
    		return;
    	}

    }

    


}