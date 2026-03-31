package org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message;


import org.springframework.beans.factory.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;


import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.model.IDNSProtocolModelInstanceFactory;
import org.loxsols.net.service.dns.loulandns.server.common.*;

import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.section.*;

import org.loxsols.net.service.dns.loulandns.server.common.*;

public class DNSMessageFactoryImpl extends SimpleDNSMessageFactoryImpl implements IDNSMessageFactory
{

    @Autowired
    @Qualifier("dnsProtocolModelInstanceFactoryImpl")
    IDNSProtocolModelInstanceFactory dnsProtocolModelInstanceFactory;


    public void setDNSProtocolModelInstanceFactory(IDNSProtocolModelInstanceFactory instanceFactory)
    {
        this.dnsProtocolModelInstanceFactory = instanceFactory;
    }


    public DNSMessageFactoryImpl() throws DNSServiceCommonException
    {
        super();
        
        // SpringのDIを使用するのでここでは何もしない.
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

    public IDNSProtocolModelInstanceFactory getDNSProtocolModelInstanceFactory() throws DNSServiceCommonException
    {
        return this.dnsProtocolModelInstanceFactory;
    }




}
