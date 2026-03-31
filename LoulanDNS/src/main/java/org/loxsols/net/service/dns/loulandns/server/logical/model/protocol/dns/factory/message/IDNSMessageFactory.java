
package org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message;

import org.loxsols.net.service.dns.loulandns.server.common.*;

import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.model.DNSProtocolModelInstanceFactoryImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.model.IDNSProtocolModelInstanceFactory;

public interface IDNSMessageFactory
{

    public IDNSMessage createDNSMesssage(byte[] dnsMessageBytes)  throws DNSServiceCommonException;
  
    public IDNSQuestionMessage createQuestionDNSMesssage(IDNSHeaderSection headerSection, IDNSQuestionSection questionSection )  throws DNSServiceCommonException;
    public IDNSQuestionMessage createQuestionDNSMesssage(String qname, int qtype, int qclass )  throws DNSServiceCommonException;
    public IDNSQuestionMessage createQuestionDNSMesssage(byte[] dnsMessageBytes)  throws DNSServiceCommonException;
   
    public IDNSResponseMessage createResponseDNSMesssage(IDNSHeaderSection headerSection, IDNSQuestionSection questionSection, IDNSAnswerSection answerSection )  throws DNSServiceCommonException;
    public IDNSResponseMessage createResponseDNSMesssage(byte[] dnsMessageBytes)  throws DNSServiceCommonException;


    public IDNSResponseMessage createFullDNSMesssage(IDNSHeaderSection headerSection, IDNSQuestionSection questionSection, IDNSAnswerSection answerSection, IDNSAuthoritySection authoritySec, IDNSAdditionalSection additionalSection)  throws DNSServiceCommonException;


    
        
    // 問い合わせに対する単一のリソースレコード応答メッセージを作成する.
    // rname, rtype, rclassは問い合わせ部の値を使いまわす.
    public IDNSResponseMessage createSimpleResponseDNSMesssage(String qname, int qtype, int qclass, int rTTL, byte[] rdata) throws DNSServiceCommonException;


    // 問い合わせに対する単一のリソースレコード応答メッセージを作成する.
    public IDNSResponseMessage createSimpleResponseDNSMesssage(String qname, int qtype, int qclass, String rname, int rtype, int rclass, int rTTL, byte[] rdata) throws DNSServiceCommonException;

    public IDNSProtocolModelInstanceFactory getDNSProtocolModelInstanceFactory() throws DNSServiceCommonException;

}

