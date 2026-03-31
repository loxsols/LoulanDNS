package org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.test;


import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.util.test.*;


import static org.junit.Assert.*;

import java.net.InetAddress;
import java.util.Arrays;
import java.net.UnknownHostException;

import org.junit.Test;
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

import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSDebugUtils;


/*
 * DNSメッセージのデコード試験クラス
 * 
 */
public class DNSMessageDecodeTest
{

    DNSMessageTestUtils messageTestUtils = new DNSMessageTestUtils();

    
    /*
     * DNS問い合わせメッセージのデコード試験001
     * 		# 試験内容
     * 			google.co.jpドメインに対する問い合わせクエリのDNSメッセージをデコードする.
     *
     *		# 期待結果
     *			DNSメッセージが正常にデコードできることを確認する.
     * 
     */
    @Test
    public void testDecodeDNSQuestionMessage001()
    {

    	final String QNAME = "google.co.jp.";
    	final int QTYPE = 1;
    	final int QCLASS = 1;
    	IDNSQueryPart dnsQuery = null;

    	byte[] dnsMessageBytes;
    	try
    	{
    		dnsMessageBytes = messageTestUtils.createDNSQueryMessage(QNAME, QTYPE, QCLASS);

    	}
    	catch(MalformedDNSRequestException cause) 
    	{
    		fail("試験用DNSメッセージの作成に失敗.");
    		return ;
    	}

		

		// DNS問い合わせセクションのbyte配列を生成する.
		// DNSメッセージ電文の先頭からヘッダーセクション(12byte)を取り除いた部分.
		byte[] dnsQueryPartBytes = Arrays.copyOfRange(dnsMessageBytes, DNSProtocolConstants.SIZE_OF_DNS_HEADER_SECION, dnsMessageBytes.length  );
    	try
    	{
    		dnsQuery = new DNSQueryPartImpl();

    		dnsQuery.setDNSQueryBytes(dnsQueryPartBytes);

			// assertEquals( "DNSメッセージのQuestionセクションのQDCountが1であること",  1, dnsQuery.getQDCOUNT() );

    		assertEquals( "DNSメッセージのクエリ部のQTypeが1(A)になること",  QNAME, dnsQuery.getDNSQueryName() );
    		assertEquals( "DNSメッセージのクエリ部のQTypeが1(A)になること",  QTYPE, dnsQuery.getDNSQueryType() );
    		assertEquals( "DNSメッセージのクエリ部のQClassが1(IN)になること", QCLASS, dnsQuery.getDNSQueryClass() );

			// assertEquals( "DNSメッセージのクエリ部のバイト長が18(14+2+2)になること",  1, dnsQuery.getQDCOUNT() );
			// assertEquals( "DNSメッセージのQuestionセクションのバイト長が18になること",  1, dnsQuery.getQDCOUNT() );
			// assertEquals( "DNSメッセージのバイト長が22(ヘッダ部14+Question18)になること",  1, dnsQuery.getQDCOUNT() );


    	}
    	catch(DNSServiceCommonException cause)
    	{
			cause.printStackTrace();

    		fail("DNSメッセージのデコード処理に失敗.");
    		return;
    	}

    }
    
    
    
    /*
     * DNSレスポンスメッセージのデコード試験001
     * 		# 試験内容
     * 			google.co.jp.ドメインに対する問い合わせクエリのDNSメッセージをデコードする.
     *
     *		# 期待結果
     *			DNSメッセージが正常にデコードできることを確認する.
     * 
     */
    @Test
    public void testDecodeDNSResponseMessage001()
    {

    	final String qname = "google.co.jp.";
    	final int qtype = 1;
    	final int qclass = 1;


    	byte[] dnsMessageBytes;
    	try
    	{
    		dnsMessageBytes = messageTestUtils.createDNSResponseAMessage(qname, "127.0.0.1");

    	}
    	catch(MalformedDNSResponseException cause) 
    	{
    		fail();
    		return ;
    	}

		int qdCount = 0;
		IDNSHeaderSection dnsHeader = new DNSHeaderSectionImpl();
		try
		{
			dnsHeader.setDNSHeaderBytes(dnsMessageBytes);
			qdCount = (int)dnsHeader.getQDCOUNT();
		}
    	catch(DNSServiceCommonException cause)
    	{
			cause.printStackTrace();
			fail();
		}


		int dnsQuestionSectionSize = 0;
		if ( qdCount > 0 )
		{
			// DNS問い合わせセクションの可変長サイズを取得するために、先に問い合わせセクションの内容を解析する.
			// DNS問い合わせセクションのbyte配列を生成する.
			// DNSメッセージ電文の先頭からヘッダーセクション(12byte)を取り除いた部分.
			byte[] dnsQueryPartBytes = Arrays.copyOfRange(dnsMessageBytes, DNSProtocolConstants.SIZE_OF_DNS_HEADER_SECION, dnsMessageBytes.length  );
			try
			{
				IDNSQueryPart dnsQuery = new DNSQueryPartImpl();
				dnsQuery.setDNSQueryBytes(dnsQueryPartBytes);

				// DNS問い合わせセクションのサイズを取得する.
				dnsQuestionSectionSize = dnsQuery.getDNSQueryBytes().length;

			}
			catch(DNSServiceCommonException cause)
			{
				cause.printStackTrace();

				String hexString = messageTestUtils.toHexDumpString(dnsMessageBytes);

				System.out.println( String.format("[TEST] DNSMessageDecodeTest.testDecodeDNSResponseMessage001() : dnsMessageBytes.length()=%d", dnsMessageBytes.length ) );
				System.out.println( "******************************");
				System.out.print( hexString );
				System.out.println( "******************************");

				fail();
				return;
			}
		}



		// TODO : dnsQuestionSectionSizeが259というおかしな値になるので修正する.
		// For DEBUG.
		System.out.println("[DEBUG] testDecodeDNSResponseMessage001() dnsQuestionSectionSize=" + dnsQuestionSectionSize );
		System.out.flush();
		


		// DNS回答セクションのbyte配列を生成する.
		// DNSメッセージ電文の先頭からヘッダーセクション(12byte) + 問い合わせセクション(可変長) を取り除いた部分.
		byte[] dnsAnswerSectionBytes = Arrays.copyOfRange(dnsMessageBytes, DNSProtocolConstants.SIZE_OF_DNS_HEADER_SECION + dnsQuestionSectionSize , dnsMessageBytes.length  );


		LoulanDNSDebugUtils.printDebug(this.getClass(), "testDecodeDNSResponseMessage001() : qdCount", Integer.toString(qdCount) );
		LoulanDNSDebugUtils.printDebug(this.getClass(), "testDecodeDNSResponseMessage001() : dnsQuestionSectionSize", Integer.toString(dnsQuestionSectionSize) );
		LoulanDNSDebugUtils.printHexString(this.getClass(), "testDecodeDNSResponseMessage001() : dnsMessageBytes", dnsMessageBytes);
		LoulanDNSDebugUtils.printHexString(this.getClass(), "testDecodeDNSResponseMessage001() : dnsAnswerSectionBytes", dnsAnswerSectionBytes);

		try
		{
			IDNSAnswerSection dnsAnswerSection = new DNSAnswerSectionImpl();
			dnsAnswerSection.setDNSRRSetBytes( 1, dnsAnswerSectionBytes, dnsMessageBytes);

			IDNSResourceRecord[] rrSet = dnsAnswerSection.getDNSResourceRecords();

			assertEquals( 1, rrSet.length );
    		assertEquals( qname, rrSet[0].getDNSResourceName() );

			byte[] rdata =  rrSet[0].getResourceRData();
			InetAddress inetAddress = InetAddress.getByAddress(rdata);
    		assertEquals( InetAddress.getByName("127.0.0.1").toString() , inetAddress.toString() );

		}
		catch(DNSServiceCommonException cause)
    	{
			cause.printStackTrace();

			String hexString = messageTestUtils.toHexDumpString(dnsMessageBytes);

			System.out.println( String.format("[TEST] DNSMessageDecodeTest.testDecodeDNSResponseMessage001() : dnsMessageBytes.length()=%d", dnsMessageBytes.length ) );
			System.out.println( "******************************");
			System.out.print( hexString );
			System.out.println( "******************************");

    		fail();
    		return;
    	}
		catch(UnknownHostException cause)
		{
			cause.printStackTrace();
			fail();
			return;
		}

    }
    

    /*
     * DNS問い合わせ部のデコード試験001
     * 		# 試験内容
     * 			google.co.jpドメインに対する問い合わせクエリのDNSクエリ部をデコードする.
     *
     *		# 期待結果
     *			DNSメッセージが正常にデコードできることを確認する.
     * 
     */
    @Test
    public void testDecodeDNSQuestionPart001()
    {

    	final String QNAME = "google.co.jp.";
    	final int QTYPE = 1;
    	final int QCLASS = 1;
    	IDNSQueryPart dnsQuery = null;

    	byte[] dnsMessageBytes;
    	try
    	{
    		dnsMessageBytes = messageTestUtils.createDNSQueryMessage(QNAME, QTYPE, QCLASS);

    	}
    	catch(MalformedDNSRequestException cause) 
    	{
    		fail("試験用DNSメッセージの作成に失敗.");
    		return ;
    	}

		// DNS問い合わせセクションのbyte配列を生成する.
		// DNSメッセージ電文の先頭からヘッダーセクション(12byte)を取り除いた部分.
		byte[] dnsQueryPartBytes = Arrays.copyOfRange(dnsMessageBytes, DNSProtocolConstants.SIZE_OF_DNS_HEADER_SECION, dnsMessageBytes.length  );
    	try
    	{
    		dnsQuery = new DNSQueryPartImpl();

    		dnsQuery.setDNSQueryBytes(dnsQueryPartBytes);

			// assertEquals( "DNSメッセージのQuestionセクションのQDCountが1であること",  1, dnsQuery.getQDCOUNT() );

    		assertEquals( "DNSメッセージのクエリ部のQNameが「google.co.jp.」になること",  QNAME, dnsQuery.getDNSQueryName() );
    		assertEquals( "DNSメッセージのクエリ部のQTypeが1(A)になること",  QTYPE, dnsQuery.getDNSQueryType() );
    		assertEquals( "DNSメッセージのクエリ部のQClassが1(IN)になること", QCLASS, dnsQuery.getDNSQueryClass() );

			// assertEquals( "DNSメッセージのクエリ部のバイト長が18(14+2+2)になること",  1, dnsQuery.getQDCOUNT() );
			// assertEquals( "DNSメッセージのQuestionセクションのバイト長が18になること",  1, dnsQuery.getQDCOUNT() );
			// assertEquals( "DNSメッセージのバイト長が22(ヘッダ部14+Question18)になること",  1, dnsQuery.getQDCOUNT() );


    	}
    	catch(DNSServiceCommonException cause)
    	{
			cause.printStackTrace();

    		fail("DNSメッセージのデコード処理に失敗.");
    		return;
    	}

    }


}