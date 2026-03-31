package org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.util.test;

import java.net.UnknownHostException;
import java.net.InetAddress;

import org.loxsols.net.service.dns.loulandns.server.common.*;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSDebugUtils;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSProtocolUtils;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.IDNSHeaderSection;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.IDNSQuestionSection;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.IDNSQueryPart;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.model.SimpleDNSProtocolModelInstanceFactoryImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.model.IDNSProtocolModelInstanceFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.xbill.XbillDNSQueryPartImpl;
import org.xbill.DNS.Message;

import android.filterfw.geometry.Quad;


public class DNSMessageTestUtils
{


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



    public byte[] createDNSQueryMessage(String name, int qtype, int qclass) throws MalformedDNSRequestException
    {
        org.xbill.DNS.Name nameObj;
        try
        {
            nameObj = org.xbill.DNS.Name.fromString(name);
        }
        catch(org.xbill.DNS.TextParseException cause)
        {
            String msg = String.format("Failed to parse DNS name field. name=%s", name);
            MalformedDNSRequestException exception = new MalformedDNSRequestException(msg, cause);
            throw exception;
        }


        org.xbill.DNS.Record rec = org.xbill.DNS.Record.newRecord(nameObj, qtype, qclass);
        org.xbill.DNS.Message query = org.xbill.DNS.Message.newQuery(rec);

        byte[] bytes = query.toWire();
        return bytes;
    }
    
    
    
    // ダミーのDNSレスポンスメッセージを生成する.
    public byte[] createDNSResponseMessage(String name, int qtype, int qclass, int rtype, int dclass, byte[] rdata) throws MalformedDNSResponseException
    {
    	
    	        
        org.xbill.DNS.Message dnsQueryMessage;
        try
        {

            // 一旦、DNS問い合わせメッセージを生成する.
            byte[] requestMessageBytes = createDNSQueryMessage(name, qtype, qclass);

            org.xbill.DNS.Message requestMessage = new org.xbill.DNS.Message( requestMessageBytes );
            org.xbill.DNS.Record queryRec = requestMessage.getQuestion();

            dnsQueryMessage = org.xbill.DNS.Message.newQuery(queryRec);

        }
        catch (MalformedDNSRequestException cause)
        {
            String msg = String.format("Failed to generate DNS request message. name=%s", name);
            MalformedDNSResponseException exception = new MalformedDNSResponseException(msg, cause);
            throw exception;
        }
        catch( java.io.IOException cause )
        {
            String msg = String.format("Failed to get DNS ququest section field. name=%s", name);
            MalformedDNSResponseException exception = new MalformedDNSResponseException(msg, cause);
            throw exception;
        }
        

        Message dnsResponseMessage;
        try
        {

            // 問い合わせセクションを生成する.
            dnsResponseMessage = new Message(dnsQueryMessage.getHeader().getID());
            dnsResponseMessage.getHeader().setFlag(org.xbill.DNS.Flags.QR);
            if (dnsQueryMessage.getHeader().getFlag(org.xbill.DNS.Flags.RD))
            {
                dnsResponseMessage.getHeader().setFlag(org.xbill.DNS.Flags.RD);
            }

            org.xbill.DNS.Record queryRecord = dnsQueryMessage.getQuestion();

            LoulanDNSDebugUtils.printDebug( this.getClass(), "createDNSResponseMessage() : queryRecord.getType()", Integer.toString( queryRecord.getType() ) );

            dnsResponseMessage.addRecord(queryRecord, org.xbill.DNS.Section.QUESTION);

            // レスポンスセクションを生成する.
            org.xbill.DNS.Name nameObj  = org.xbill.DNS.Name.fromString(name);

            // TODO : とりあえず、Aレコード固定とする.
            // org.xbill.DNS.ARecordクラスのコンストラクタの定義 : Record(Name name, int dclass, long ttl, InetAddress address)
            org.xbill.DNS.Record answerRec = new org.xbill.DNS.ARecord(nameObj, dclass, 3600, InetAddress.getByAddress(rdata) );

            dnsResponseMessage.addRecord(answerRec, org.xbill.DNS.Section.ANSWER);
        }
        catch(org.xbill.DNS.TextParseException cause)
        {
            String msg = String.format("Failed to parse DNS name field. name=%s", name);
            MalformedDNSResponseException exception = new MalformedDNSResponseException(msg, cause);
            throw exception;
        }
        catch( UnknownHostException cause)
        {
            String msg = String.format("Failed to parse IP address bytes. rdata.length=%d", rdata.length );
            MalformedDNSResponseException exception = new MalformedDNSResponseException(msg, cause);
            throw exception;
        }


        System.out.println( String.format( "[DEBUG] DNSMessageTestUtils.createDNSResponseMessage() : DNS header ID=%d", dnsResponseMessage.getHeader().getID() ) );
        System.out.println( String.format( "[DEBUG] DNSMessageTestUtils.createDNSResponseMessage() : DNS header flag(0)=%B", dnsResponseMessage.getHeader().getFlag(0) ) ) ;    
       // System.out.println( String.format( "[DEBUG] DNSMessageTestUtils.createDNSResponseMessage() : DNS header flag(1)=%B", dnsResponseMessage.getHeader().getFlag(1) ) ) ;
        System.out.println( String.format( "[DEBUG] DNSMessageTestUtils.createDNSResponseMessage() : DNS header rcode=%d", dnsResponseMessage.getHeader().getRcode() ) ) ;
        System.out.println( String.format( "[DEBUG] DNSMessageTestUtils.createDNSResponseMessage() : DNS header opcode=%d", dnsResponseMessage.getHeader().getOpcode() ) );
        System.out.println( String.format( "[DEBUG] DNSMessageTestUtils.createDNSResponseMessage() : DNS header count(0)=%d", dnsResponseMessage.getHeader().getCount(0) ) );
        System.out.println( String.format( "[DEBUG] DNSMessageTestUtils.createDNSResponseMessage() : DNS header count(1)=%d", dnsResponseMessage.getHeader().getCount(1) ) );


        byte[] bytes = dnsResponseMessage.toWire();

        LoulanDNSDebugUtils.printDebug( this.getClass(), "createDNSResponseMessage() : dnsResponseMessage.getQuestion().getType()",  Integer.toString( dnsResponseMessage.getQuestion().getType() ) );
        LoulanDNSDebugUtils.printHexString( this.getClass(), "createDNSResponseMessage() : bytes",  bytes );

        
        return bytes;
    }
    
    
    
    // ダミーのDNSレスポンスメッセージを生成する.
    public byte[] createDNSResponseAMessage(String domainName, InetAddress address) throws MalformedDNSResponseException
    {

        int qtype = 1;
        int qclass = 1;

        int rtype = 1;
        int dclass = 1;

    	byte[] rdata = address.getAddress();
    	byte[] responseMessageBytes = createDNSResponseMessage(domainName, qtype, qclass, rtype, dclass, rdata);

        return responseMessageBytes;
    
    }
    

    // ダミーのDNSレスポンスメッセージを生成する.
    public byte[] createDNSResponseAMessage(String domainName, String ipAddressString) throws MalformedDNSResponseException
    {
        InetAddress address;
        
        try
        {
            address = InetAddress.getByName(ipAddressString);
        }
        catch (UnknownHostException cause)
        {
            String msg = String.format("Faield to parse IP address string : %s", ipAddressString);
            MalformedDNSResponseException exception = new MalformedDNSResponseException(msg, cause);
            throw exception;
        }

        byte[] responseMessageBytes = createDNSResponseAMessage(domainName, address);
        return responseMessageBytes;
    }
    


    // DNSメッセージをデバッグ用文字列に変換する.
    // 
    //  本メソッドはxbillで実装する.(LoulanDNSがバグったときのデバッグ用のため.)
    //  xbillを使用しないデバッグ用文字列化メソッドは以下のメソッドを使用すること.
    //      org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSProtocolUtils.toDNSDebugMessage()
    // 
    public String toDNSDebugMessage(byte[] dnsMessageBytes) throws DNSServiceCommonException
    {
        // TODO : 未実装.
        throw new DNSServiceCommonException("Not implemented.");
    }


    public String toHexDumpString(byte[] bytes)
    {
        LoulanDNSProtocolUtils utils = new LoulanDNSProtocolUtils();
        String hexString = utils.toDebugHexString(bytes);
        
        return hexString;
    }


    // DNSヘッダセクションのbyte配列を生成する.
    // id                   : ID (16bit)                            リクエストに割り当てられるランダムなID.
    // queryResponse        : QR : Query Response (1bit)            リクエストは０、レスポンスは１
    // opCode               : OPCODE : Operation Code (4bit)        問い合わせの種類を表す. 0が通常のクエリ、4がNotify、5がUpdate.
    // authoritativeAnswer  : AA : Authoritative Answer (1bit)      応答するサーバーが権威サーバー（自分自身の所持するDNSレコードを返している）かどうかを表すフラグ。
    // truncatedMessage     : TC  : Truncated Message (1bit)        パケットサイズが512バイトを超えるなら１.
    // recursionDesired     : RD  : Recursion Desired (1bit)        リクエストを受けたサーバーが該当するレコードを所持していない場合、再帰的にな名前解決をすべきかリクエストの送信側が指定するためのフラグ。
    // recursionAvailable   : RA  : Recursion Available (1bit)      サーバーが再帰的な名前解決が可能かを提示するフラグ
    // reservedZ            : Z  : Reserved (1bit)                  将来的な拡張のために利用される領域.
    // authenticData        : AD  : Authentic Data (1bit)           DNSSEC検証に成功したことを表すフラグ
    // checkingDisabled     : CD : Checking Disabled (1bit)         DNSSEC検証の禁止を指定するフラグ
    // rCode                : RCode : Response Code (4bit)          サーバーがレスポンスの状態（成功、失敗など）をクライアントに提示するために使用されるコード
    // qdCount              : QDCOUNT  : Question Count (16bit)     Questionセクションに含まれるエントリの数
    // nsCount              : NSCOUNT  : Authority Count (16bit)    Authorityセクションに含まれるエントリの数
    // arCount              : ARCOUNT   : Additional Count (16bit)  Additionalセクションに含まれるエントリの数
    public byte[] createDNSHeaderSectionBytes(int id, boolean queryResponse, int opCode, boolean authoritativeAnswer, boolean truncatedMessage, boolean recursionDesired, boolean recursionAvailable, boolean reservedZ, boolean authenticData, boolean checkingDisabled, int rCode,  int qdCount, int anCount, int nsCount, int arCount) throws DNSServiceCommonException
    {
        IDNSHeaderSection dnsHeader1  = new DNSHeaderSectionImpl();
        dnsHeader1.init(id, queryResponse, opCode, authoritativeAnswer, truncatedMessage, recursionDesired, recursionAvailable, reservedZ, authenticData, checkingDisabled, rCode,  qdCount, anCount, nsCount, arCount);

        if ( id != dnsHeader1.getID() )
        {
            String msg = String.format("Failed to build DNS Header Field ID : expected=%d, actual=%d", id, dnsHeader1.getID() );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        byte[] bytes = dnsHeader1.getDNSHeaderBytes();

        IDNSHeaderSection dnsHeader2  = new DNSHeaderSectionImpl();
        dnsHeader2.setDNSHeaderBytes(bytes);

        if ( id != dnsHeader2.getID() )
        {
            String msg = String.format("Failed to check DNS Header Field ID : expected=%d, actual=%d", id, dnsHeader2.getID() );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        
        return bytes;
    }


    // DNSヘッダセクションのbyte配列を生成する.
    // id                   : ID (16bit)                            リクエストに割り当てられるランダムなID.
    // flags                : FLAGS (16bit)                         OPCODEなどを格納する16bitのフィールド
    // qdCount              : QDCOUNT  : Question Count (16bit)     Questionセクションに含まれるエントリの数
    // nsCount              : NSCOUNT  : Authority Count (16bit)    Authorityセクションに含まれるエントリの数
    // arCount              : ARCOUNT   : Additional Count (16bit)  Additionalセクションに含まれるエントリの数
    public byte[] createDNSHeaderSectionBytes(int id,  int flags, int qdCount, int anCount, int nsCount, int arCount) throws DNSServiceCommonException
    {

        IDNSHeaderSection dnsHeader1  = new DNSHeaderSectionImpl();
        dnsHeader1.init(id, flags, qdCount, anCount, nsCount, arCount);

        byte[] bytes = dnsHeader1.getDNSHeaderBytes();

        IDNSHeaderSection dnsHeader2  = new DNSHeaderSectionImpl();
        dnsHeader2.setDNSHeaderBytes(bytes);

        if ( id != dnsHeader2.getID() )
        {
            String msg = String.format("Failed to check DNS Header Field ID : expected=%d, actual=%d", id, dnsHeader2.getID() );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        int flags2 = ( 0xff & dnsHeader2.getDNSHeaderBytes()[2] );
        flags2 = flags2 << 8 | ( 0xff & dnsHeader2.getDNSHeaderBytes()[3] ); 
        if ( flags != flags2 )
        {
            String msg = String.format("Failed to check DNS Header Field FLAGS : expected=%d, actual=%d", flags, flags2 );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        return bytes;

    }


    // DNSQuestionセクションのbyte配列を生成する.
    public byte[] createDNSQuestionSectionBytes(String qname, int qtype, int qclass) throws DNSServiceCommonException
    {
        // 試験用データの作成なので、OSSのxbillを使用してデータを生成する.
        
        // DNS問い合わせメッセージ全体を生成してから、先頭のヘッダーセクションだけを消去する.
        byte[] dnsQueryMessageBytes = createDNSQueryMessage(qname, qtype, qclass);

        byte[] questionSectionBytes = new byte[ dnsQueryMessageBytes.length - 12];
        System.arraycopy(dnsQueryMessageBytes, 12, questionSectionBytes, 0, questionSectionBytes.length );
    
        return questionSectionBytes;
    }


}