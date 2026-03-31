
package org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.model;

import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.model.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.section.DNSAdditionalSectionFactoryImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.section.DNSAnswerSectionFactoryImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.section.DNSAuthoritySectionFactoryImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.section.DNSHeaderSectionFactoryImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.section.DNSQuestionSectionFactoryImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.section.*;


import org.loxsols.net.service.dns.loulandns.server.common.*;


import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.ComponentScan;


// Springの機能を使用せずにコンポートネントを静的に設定するファクトリクラス
public class SimpleDNSProtocolModelInstanceFactoryImpl implements IDNSProtocolModelInstanceFactory
{

    public IDNSHeaderSectionFactory headerSectionFactory;
    public IDNSQuestionSectionFactory questionSectionFactory;
    public IDNSAnswerSectionFactory answerSectionFactory;
    public IDNSAuthoritySectionFactory authoritySectionFactory;
    public IDNSAdditionalSectionFactory additionalSectionFactory;


    public void setHeaderSectionFactory(IDNSHeaderSectionFactory instance)
    {
        this.headerSectionFactory = instance;
    }

    public void setQuestionSectionFactory(IDNSQuestionSectionFactory instance)
    {
        this.questionSectionFactory = instance;
    }

    public void setAnswerSectionFactory(IDNSAnswerSectionFactory instance)
    {
        this.answerSectionFactory = instance;
    }

    public void setAuthoritySectionFactory(IDNSAuthoritySectionFactory instance)
    {
        this.authoritySectionFactory = instance;
    }

    public void setAdditionalSectionFactory(IDNSAdditionalSectionFactory instance)
    {
        this.additionalSectionFactory = instance;
    }




    public SimpleDNSProtocolModelInstanceFactoryImpl() throws DNSServiceCommonException
    {

        setHeaderSectionFactory( new DNSHeaderSectionFactoryImpl() );
        setQuestionSectionFactory( new DNSQuestionSectionFactoryImpl(this) );
        setAnswerSectionFactory( new DNSAnswerSectionFactoryImpl() );
        setAuthoritySectionFactory( new DNSAuthoritySectionFactoryImpl() );
        setAdditionalSectionFactory( new DNSAdditionalSectionFactoryImpl() );

    }


    public IDNSMessage createDNSMesssageInstance()  throws DNSServiceCommonException
    {
        IDNSMessage dnsMessage = new DNSMessageImpl( this );
        return dnsMessage;
    }

    public IDNSQuestionMessage createDNSQuestionMessageInstance()  throws DNSServiceCommonException
    {
        IDNSQuestionMessage dnsQuestionMessage = new DNSQuestionMessageImpl( this );
        return dnsQuestionMessage;
    } 

    public IDNSResponseMessage createDNSResponseMessageInstance()  throws DNSServiceCommonException
    {
        IDNSResponseMessage dnsResponseMessage = new DNSResponseMessageImpl( this );
        return dnsResponseMessage;
    }

    public IDNSHeaderSection createDNSHeaderSectionInstance()  throws DNSServiceCommonException
    {
        IDNSHeaderSection headerSection = new DNSHeaderSectionImpl();
        return headerSection;
    }


    public IDNSQuestionSection createDNSQuestionSectionInstance()  throws DNSServiceCommonException
    {
        IDNSQuestionSection questionSection = new DNSQuestionSectionImpl(this);
        return questionSection;
    }

    public IDNSAnswerSection createDNSAnswerSectionInstance()  throws DNSServiceCommonException
    {
        IDNSAnswerSection answerSection = new DNSAnswerSectionImpl();
        return answerSection;
    }

    public IDNSAuthoritySection createDNSAuthoritySectionInstance()  throws DNSServiceCommonException
    {
        IDNSAuthoritySection authoritySection = new DNSAuthoritySectionImpl();
        return authoritySection;
    }


    public IDNSAdditionalSection createDNSAdditionalSectionInstance()  throws DNSServiceCommonException
    {
        IDNSAdditionalSection additionalSection = new DNSAdditionalSectionImpl();
        return additionalSection;
    }

    public IDNSQueryPart createDNSQueryPart()  throws DNSServiceCommonException
    {
        IDNSQueryPart queryPart = new DNSQueryPartImpl();
        return queryPart;
    }



    // DNSクエリのヘッダセクションを生成する.
    // id : ランダムなID値(16bit)
    // tc : Truncated Message  : パケットサイズが512バイトを超えるならtrue
    // rd : Recursion Desired) : リクエストを受けたサーバーが該当するレコードを所持していない場合、再帰的な名前解決を依頼する場合はtrue
    // qd : QDCOUNT(16bit)     : Questionセクションに含まれるエントリの数
    public IDNSHeaderSection createDNSQueryHeaderSectionInstance(int id, boolean tc, boolean rd, int qd)  throws DNSServiceCommonException
    {
        IDNSHeaderSection header = createDNSHeaderSectionInstance();
        header.setID(id);

        if ( tc )
        {
            header.setTC(1);
        }
        if ( rd )
        {
            header.setRD(1);
        }

        header.setQDCOUNT(qd);

        return header;
    }


    // シンプルなDNS問い合わせセクションを生成する.
    // qname : FQDN名
    // qtype : Query Type
    // qclas : Query Class
    public IDNSQuestionSection createSimpleDNSQuestionSectionInstance(String qname, int qtype, int qclass) throws DNSServiceCommonException
    {
        IDNSQuestionSection questionSection = questionSectionFactory.createQuestionSection(qname, qtype, qclass);
        return questionSection;
    }



    // DNS問い合わせレコードを作成する.
    public IDNSQueryPart createDNSQueryRecord() throws DNSServiceCommonException
    {
        IDNSQueryPart queryRecord = new DNSQueryPartImpl();
        return queryRecord;
    }


    // DNSリソースレコードのインスタンスを生成する.
    public IDNSResourceRecord createDNSResourceRecord() throws DNSServiceCommonException
    {
        IDNSResourceRecord rr = new DNSResourceRecordImpl();
        return rr;
    }

    // DNSリソースレコードを作成する.
    public IDNSResourceRecord createDNSResourceRecord(String rname, int rtype, int rclass, int rTTL, byte[] rdata) throws DNSServiceCommonException
    {
        IDNSResourceRecord rr = new DNSResourceRecordImpl();

        rr.setDNSResourceName(rname);
        rr.setResourceType(rtype);
        rr.setResourceClass(rclass);
        rr.setResourceTTL(rTTL);
        rr.setDNSResourceRecordBytes(rdata);

        return rr;
    }



}