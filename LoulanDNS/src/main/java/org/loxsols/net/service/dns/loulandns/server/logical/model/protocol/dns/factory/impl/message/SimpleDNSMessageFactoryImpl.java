package org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message;


import org.springframework.beans.factory.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.model.SimpleDNSProtocolModelInstanceFactoryImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.model.IDNSProtocolModelInstanceFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.loxsols.net.service.dns.loulandns.client.subway.common.DNSSubwayCommonException;
import org.loxsols.net.service.dns.loulandns.server.common.*;
import org.loxsols.net.service.dns.loulandns.server.common.constants.DNSProtocolConstants;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.section.*;

import org.loxsols.net.service.dns.loulandns.server.common.*;

// Springを使用しないファクトリクラス.
public class SimpleDNSMessageFactoryImpl implements IDNSMessageFactory
{

    IDNSProtocolModelInstanceFactory dnsProtocolModelInstanceFactory;


    public SimpleDNSMessageFactoryImpl() throws DNSServiceCommonException
    {
        setDNSProtocolModelInstanceFactory( new SimpleDNSProtocolModelInstanceFactoryImpl() );
        
    }


    public void setDNSProtocolModelInstanceFactory(IDNSProtocolModelInstanceFactory instanceFactory)
    {
        this.dnsProtocolModelInstanceFactory = instanceFactory;
    }



    public IDNSMessage createDNSMesssage(byte[] dnsMessageBytes)  throws DNSServiceCommonException
    {
        IDNSMessage message = dnsProtocolModelInstanceFactory.createDNSMesssageInstance();
        message.setDNSMessageBytes(dnsMessageBytes);

        return message;
    }
  
    public IDNSQuestionMessage createQuestionDNSMesssage(IDNSHeaderSection headerSection, IDNSQuestionSection questionSection )  throws DNSServiceCommonException
    {
        IDNSQuestionMessage message = dnsProtocolModelInstanceFactory.createDNSQuestionMessageInstance();
        message.init(headerSection, questionSection );

        return message;
    }

    public IDNSQuestionMessage createQuestionDNSMesssage(String qname, int qtype, int qclass )  throws DNSServiceCommonException
    {
        IDNSQuestionMessage message = dnsProtocolModelInstanceFactory.createDNSQuestionMessageInstance();
        message.init(qname, qtype, qclass);

        return message;
    }

    public IDNSQuestionMessage createQuestionDNSMesssage(byte[] dnsMessageBytes)  throws DNSServiceCommonException
    {
        IDNSQuestionMessage message = dnsProtocolModelInstanceFactory.createDNSQuestionMessageInstance();
        message.setDNSMessageBytes(dnsMessageBytes);

        return message;
    }

   
    public IDNSResponseMessage createResponseDNSMesssage(IDNSHeaderSection headerSection, IDNSQuestionSection questionSection, IDNSAnswerSection answerSection )  throws DNSServiceCommonException
    {
        IDNSResponseMessage message = dnsProtocolModelInstanceFactory.createDNSResponseMessageInstance();
        message.init(headerSection, questionSection, answerSection );

        return message;
    }


    public IDNSResponseMessage createResponseDNSMesssage(byte[] dnsMessageBytes)  throws DNSServiceCommonException
    {
        IDNSResponseMessage message = dnsProtocolModelInstanceFactory.createDNSResponseMessageInstance();
        message.init(dnsMessageBytes);

        return message;
    }




    public IDNSResponseMessage createFullDNSMesssage(IDNSHeaderSection headerSection, IDNSQuestionSection questionSection, IDNSAnswerSection answerSection, IDNSAuthoritySection authoritySection, IDNSAdditionalSection additionalSection)  throws DNSServiceCommonException
    {
        IDNSResponseMessage message = dnsProtocolModelInstanceFactory.createDNSResponseMessageInstance();
        message.init(headerSection, questionSection, answerSection, authoritySection, additionalSection );

        return message;
    }

    
        
    // 問い合わせに対する単一のリソースレコード応答メッセージを作成する.
    // rname, rtype, rclassは問い合わせ部の値を使いまわす.
    public IDNSResponseMessage createSimpleResponseDNSMesssage(String qname, int qtype, int qclass, int rTTL, byte[] rdata) throws DNSServiceCommonException
    {
        String rname = qname;
        int rtype = qtype;
        int rclass = qclass;
        IDNSResponseMessage message = createSimpleResponseDNSMesssage( qname, qtype, qclass, rname, rtype, rclass, rTTL, rdata);
        
        return message;  
    }


    // 問い合わせに対する単一のリソースレコード応答メッセージを作成する.
    public IDNSResponseMessage createSimpleResponseDNSMesssage(String qname, int qtype, int qclass, String rname, int rtype, int rclass, int rTTL, byte[] rdata) throws DNSServiceCommonException
    {
        IDNSResponseMessage message = dnsProtocolModelInstanceFactory.createDNSResponseMessageInstance();
        message.init( qname, qtype, qclass, rname, rtype, rclass, rTTL, rdata);

        return message;
    }


    // 問い合わせに対するシンプルなAレコード応答メッセージを作成する.
    public IDNSResponseMessage createSimpleResponseDNSMesssageA(String qname, int qtype, int qclass, int rTTL, String addressString) throws DNSServiceCommonException
    {

        InetAddress address;
        try
        {
            address = InetAddress.getByName(addressString);
        }
        catch(UnknownHostException cause)
        {
            String msg = String.format("Invalid IP address. address=%s", addressString );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg, cause);
            throw exception;
        }

        IDNSResponseMessage response = createSimpleResponseDNSMesssageA(qname, qtype, qclass, rTTL, address);
        return response;
    }



    // 問い合わせに対するシンプルなAレコード応答メッセージを作成する.
    public IDNSResponseMessage createSimpleResponseDNSMesssageA(String qname, int qtype, int qclass, int rTTL, InetAddress address) throws DNSServiceCommonException
    {
        IDNSResponseMessage response;
        try
        {
            byte[] rdata = address.getAddress();
            response = createSimpleResponseDNSMesssage(qname, qtype, qclass, rTTL, rdata);
        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to craete DNS ResponseMessage. qname=%s, qtype=%d, qclass=%d, rTTL=%d, address=%s", qname, qtype, qclass, rTTL, address );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg, cause);
            throw exception;
        }

        return response;
    }


    // 問い合わせに対するNODATA応答メッセージを作成する.
    public IDNSResponseMessage createNODATAResponseDNSMesssage(String qname, int qtype, int qclass) throws DNSServiceCommonException
    {
        String rname = qname;
        int rtype = qtype;
        int rclass = qclass;

        int rTTL = 3600;
        byte[] rdata = new byte[]{};

        // 一旦、シンプルなDNSレスポンスを生成してからNODATA応答を生成する.
        IDNSResponseMessage message = createSimpleResponseDNSMesssage(qname, qtype, qclass, rname, rtype, rclass, rTTL, rdata);

        // RCODEは0(NOERROR)をセット
        message.getDNSHeaderSection().setRCode( DNSProtocolConstants.DNS_RCODE_NOERROR );

        // ANCOUNT(回答個数)を0にセット
        message.getDNSHeaderSection().setANCOUNT(0);

        // Answerセクションを0個のRRSetで初期化する.
        IDNSAnswerSection answerSection = message.getDNSAnswerSection();
        answerSection.setDNSResourceRecords(new IDNSResourceRecord[]{} );

        return message;
    }



    public IDNSProtocolModelInstanceFactory getDNSProtocolModelInstanceFactory() throws DNSServiceCommonException
    {
        return this.dnsProtocolModelInstanceFactory;
    }




}
