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

import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSDebugUtils;

import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.model.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.model.*;


/*
 * DNSメッセージのヘッダセクションデコード試験クラス
 * 
 */
public class DNSHeaderSectionDecodeTest
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
     * DNS問い合わせメッセージのヘッダセクションのデコード試験001
     * 		# 試験内容
     * 			DNSメッセージのヘッダセクションをデコードする.
     *
     *		# 期待結果
     *			DNSメッセージのヘッダセクションが正常にデコードできることを確認する.
     * 
     */
    @Test
    public void testDecodeDNSHeaderSection001()
    {

		final int ID = 0;

		final int QR = 0;
		final int OPCODE = 0;
		final int AA = 0;
		final int TC = 0;
		final int RD = 0;
		final int RA = 1;
		final int Z = 0;
		final int RCODE = 0;

		final int QDCOUNT = 1;
		final int ANCOUNT = 0;
		final int NSCOUNT = 0;
		final int ARCOUNT = 0;

		IDNSHeaderSection dnsHeader;

    	byte[] dnsMessageBytes;
    	try
    	{
			
    		dnsMessageBytes = messageTestUtils.createDNSQueryMessage("www.google.co.jp.", 1, 1);
		}
    	catch(MalformedDNSRequestException cause) 
    	{
    		fail("試験用DNSメッセージの作成に失敗.");
    		return ;
    	}

		
		// DNSヘッダセクションのbyte配列を生成する.
		// DNSメッセージ電文の先頭からヘッダーセクション(12byte)のみを取得する.
		byte[] dnsHeaderSectionBytes = Arrays.copyOfRange(dnsMessageBytes, 0, DNSProtocolConstants.SIZE_OF_DNS_HEADER_SECION  );
		
		try
    	{
			dnsHeader =factory.createDNSHeaderSectionInstance();

			// デコード処理.
			dnsHeader.setDNSHeaderBytes(dnsHeaderSectionBytes);


    		// assertEquals( "DNSヘッダーセクションのID == " + ID,   ID, dnsHeader.getID() );
    		
			assertEquals( "DNSヘッダーセクションのQR == " + QR,  QR, dnsHeader.getQR() );
			assertEquals( "DNSヘッダーセクションのOPCODE == " + OPCODE,  OPCODE, dnsHeader.getOPCode() );
			assertEquals( "DNSヘッダーセクションのAA == " + AA,  AA, dnsHeader.getAA() );
			assertEquals( "DNSヘッダーセクションのTC == " + TC,  TC, dnsHeader.getTC() );
			assertEquals( "DNSヘッダーセクションのRD == " + RD,  RD, dnsHeader.getRD() );
			assertEquals( "DNSヘッダーセクションのRA == " + RA,  RA, dnsHeader.getRA() );
			assertEquals( "DNSヘッダーセクションのZ == " + Z,  Z, dnsHeader.getZ() );
			assertEquals( "DNSヘッダーセクションのRCODE == " + RCODE,  RCODE, dnsHeader.getRCode() );

    		assertEquals( "DNSヘッダーセクションのQDCOUNT == " + QDCOUNT,  QDCOUNT, dnsHeader.getQDCOUNT() );
    		assertEquals( "DNSヘッダーセクションのANCOUNT == " + ANCOUNT,  ANCOUNT, dnsHeader.getANCOUNT() );
			assertEquals( "DNSヘッダーセクションのNSCOUNT == " + NSCOUNT,  NSCOUNT, dnsHeader.getNSCOUNT() );
			assertEquals( "DNSヘッダーセクションのARCOUNT == " + ARCOUNT,  ARCOUNT, dnsHeader.getARCOUNT() );

    	}
    	catch(DNSServiceCommonException cause)
    	{
			cause.printStackTrace();

    		fail("DNSメッセージのデコード処理に失敗.");
    		return;
    	}

    }



	/*
     * DNS問い合わせメッセージのヘッダセクションのIDフィールドのデコード試験
     * 		# 試験内容
     * 			DNSメッセージのヘッダセクションのIDフィールドを0x00000-0xffffまでセットしてデコード値を比較する.
     *
     *		# 期待結果
     *			DNSメッセージのヘッダセクションのIDフィールド値が正常にデコードできることを確認する.
     * 
     */
    @Test
    public void testDecodeDNSHeaderSectionIDField001()
    {

		// final int ID = 0;

		final boolean QR = false;
		final int OPCODE = 0;
		final boolean AA = false;
		final boolean TC = false;
		final boolean RD = false;
		final boolean RA = false;
		final boolean Z = false;
		final boolean AD = false;
		final boolean CD = false;

		final int RCODE = 0;

		final int QDCOUNT = 1;
		final int ANCOUNT = 0;
		final int NSCOUNT = 0;
		final int ARCOUNT = 0;

		IDNSHeaderSection dnsHeader;

    	byte[] dnsMessageBytes;
    	try
    	{
			for( int ID=0; ID < 0xffff; ID++)
			{
				dnsMessageBytes = messageTestUtils.createDNSHeaderSectionBytes(ID, QR, OPCODE, AA, TC, RD, RA, Z, AD, CD, RCODE, QDCOUNT, ANCOUNT, NSCOUNT, ARCOUNT);

				dnsHeader =factory.createDNSHeaderSectionInstance();
				dnsHeader.setDNSHeaderBytes(dnsMessageBytes);

				assertEquals( "DNSヘッダーセクションのID == " + ID,   ID, dnsHeader.getID() );

				assertEquals( "DNSヘッダーセクションのQR == " + QR,  QR, dnsHeader.getBooleanQR() );
				assertEquals( "DNSヘッダーセクションのOPCODE == " + OPCODE,  OPCODE, dnsHeader.getOPCode() );
				assertEquals( "DNSヘッダーセクションのAA == " + AA,  AA, dnsHeader.getBooleanAA() );
				assertEquals( "DNSヘッダーセクションのTC == " + TC,  TC, dnsHeader.getBooleanTC() );
				assertEquals( "DNSヘッダーセクションのRD == " + RD,  RD, dnsHeader.getBooleanRD() );
				assertEquals( "DNSヘッダーセクションのRA == " + RA,  RA, dnsHeader.getBooleanRA() );
				assertEquals( "DNSヘッダーセクションのZ == " + Z,  Z, dnsHeader.getBooleanZ() );
				assertEquals( "DNSヘッダーセクションのRCODE == " + RCODE,  RCODE, dnsHeader.getRCode() );
				assertEquals( "DNSヘッダーセクションのRD == " + AD,  AD, dnsHeader.getBooleanAD() );
				assertEquals( "DNSヘッダーセクションのRA == " + CD,  CD, dnsHeader.getBooleanCD() );		

				assertEquals( "DNSヘッダーセクションのQDCOUNT == " + QDCOUNT,  QDCOUNT, dnsHeader.getQDCOUNT() );
				assertEquals( "DNSヘッダーセクションのANCOUNT == " + ANCOUNT,  ANCOUNT, dnsHeader.getANCOUNT() );
				assertEquals( "DNSヘッダーセクションのNSCOUNT == " + NSCOUNT,  NSCOUNT, dnsHeader.getNSCOUNT() );
				assertEquals( "DNSヘッダーセクションのARCOUNT == " + ARCOUNT,  ARCOUNT, dnsHeader.getARCOUNT() );

			}

		}
    	catch(DNSServiceCommonException cause) 
    	{
			cause.printStackTrace();
    		fail("試験用DNSメッセージのヘッダセクションの処理に失敗.");
    		return ;
    	}

    }


	/*
     * DNS問い合わせメッセージのヘッダセクションのFLAGSフィールドのデコード試験
     * 		# 試験内容
     * 			DNSメッセージのヘッダセクションのFLAGSフィールドを0x00000-0xffffまでセットしてデコード値を比較する.
     *
     *		# 期待結果
     *			DNSメッセージのヘッダセクションのFLAGSフィールド値が正常にデコードできることを確認する.
     * 
     */
    @Test
    public void testDecodeDNSHeaderSectionFLAGSField001()
    {

		final int ID = 0;

		final boolean QR = false;
		final int OPCODE = 0;
		final boolean AA = false;
		final boolean TC = false;
		final boolean RD = false;
		final boolean RA = false;
		final boolean Z = false;
		final boolean AD = false;
		final boolean CD = false;

		final int RCODE = 0;

		final int QDCOUNT = 1;
		final int ANCOUNT = 0;
		final int NSCOUNT = 0;
		final int ARCOUNT = 0;

		IDNSHeaderSection dnsHeader;

    	byte[] dnsMessageBytes;
    	try
    	{
			for( int FLAGS=0; FLAGS < 0xffff; FLAGS++)
			{
				dnsMessageBytes = messageTestUtils.createDNSHeaderSectionBytes(ID, FLAGS, QDCOUNT, ANCOUNT, NSCOUNT, ARCOUNT);

				dnsHeader =factory.createDNSHeaderSectionInstance();
				dnsHeader.setDNSHeaderBytes(dnsMessageBytes);

				assertEquals( "DNSヘッダーセクションのID == " + ID,   ID, dnsHeader.getID() );
				
				int flags2 = 0xff & dnsHeader.getDNSHeaderBytes()[2];
				flags2 = flags2 << 8 | (0xff & dnsHeader.getDNSHeaderBytes()[3]);
				assertEquals( "DNSヘッダーセクションのFLAGS == " + FLAGS,  FLAGS, flags2 );


				assertEquals( "DNSヘッダーセクションのQDCOUNT == " + QDCOUNT,  QDCOUNT, dnsHeader.getQDCOUNT() );
				assertEquals( "DNSヘッダーセクションのANCOUNT == " + ANCOUNT,  ANCOUNT, dnsHeader.getANCOUNT() );
				assertEquals( "DNSヘッダーセクションのNSCOUNT == " + NSCOUNT,  NSCOUNT, dnsHeader.getNSCOUNT() );
				assertEquals( "DNSヘッダーセクションのARCOUNT == " + ARCOUNT,  ARCOUNT, dnsHeader.getARCOUNT() );

			}

		}
    	catch(DNSServiceCommonException cause) 
    	{
			cause.printStackTrace();
    		fail("試験用DNSメッセージのヘッダセクションの処理に失敗.");
    		return ;
    	}

    }
    
    
    


}