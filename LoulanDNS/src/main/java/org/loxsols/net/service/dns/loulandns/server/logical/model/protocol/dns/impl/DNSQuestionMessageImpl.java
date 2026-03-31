package org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl;


import java.util.*;
import java.io.ByteArrayOutputStream;
import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.ComponentScan;
import org.loxsols.net.service.dns.loulandns.server.common.*;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSProtocolUtils;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;


import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.section.IDNSHeaderSectionFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.section.IDNSQuestionSectionFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.model.IDNSProtocolModelInstanceFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.*;


// DNS問い合わせメッセージの実装クラス.
@ComponentScan
public class DNSQuestionMessageImpl extends DNSMessageImpl implements IDNSMessage, IDNSQuestionMessage
{

    LoulanDNSProtocolUtils protocolUtils = new LoulanDNSProtocolUtils();

    public DNSQuestionMessageImpl(IDNSProtocolModelInstanceFactory dnsProtocolModelInstanceFactory) throws DNSServiceCommonException
    {
        super(dnsProtocolModelInstanceFactory);
    }

    public void init(String qname, int qtype, int qclass) throws DNSServiceCommonException
    {
        short id = protocolUtils.getRandom16bitValue(); // id : ランダムなID値(16bit)
        boolean tc = false; // tc : Truncated Message  : パケットサイズが512バイトを超えるならtrue
        boolean rd = false; // rd : Recursion Desired) : リクエストを受けたサーバーが該当するレコードを所持していない場合、再帰的な名前解決を依頼する場合はtrue
        int qd = 1; // qd : QDCOUNT(16bit)     : Questionセクションに含まれるエントリの数


        IDNSHeaderSection headerSection = buildHeaderSection(id, tc, rd, qd);
        IDNSQuestionSection questionSection = buildQuestionSection(qname, qtype, qclass);

        init(headerSection, questionSection);

    }

    public void init(IDNSHeaderSection headerSection, IDNSQuestionSection questionSection) throws DNSServiceCommonException
    {
        init(headerSection, questionSection, null, null);
    }

    public void init(IDNSHeaderSection headerSection, IDNSQuestionSection questionSection, IDNSAuthoritySection authoritySection, IDNSAdditionalSection additionalSection) throws DNSServiceCommonException
    {
        this.setDNSHeaderSection( headerSection );
        this.setDNSQuestionSection( questionSection );

        // 回答セクションは問い合わせメッセージの場合は常にNULLになる.
        this.setDNSAnswerSection( null );

        this.setDNSAuthoritySection( authoritySection );
        this.setDNSAdditionalSection( additionalSection );

    }


    private IDNSHeaderSection buildHeaderSection(int id, boolean tc, boolean rd, int qd) throws DNSServiceCommonException
    {
        IDNSHeaderSection headerSection = dnsProtocolModelInstanceFactory.createDNSQueryHeaderSectionInstance(id, false, false, qd);
        return headerSection;
    }

    private IDNSQuestionSection buildQuestionSection(String qname, int qtype, int qclass) throws DNSServiceCommonException
    {
        IDNSQuestionSection questionSection = dnsProtocolModelInstanceFactory.createSimpleDNSQuestionSectionInstance(qname, qtype, qclass);
        return questionSection;
    }



    

}