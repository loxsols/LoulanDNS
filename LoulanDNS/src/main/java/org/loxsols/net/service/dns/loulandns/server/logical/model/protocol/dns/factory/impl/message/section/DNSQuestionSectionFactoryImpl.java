package org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.section;


import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.model.IDNSProtocolModelInstanceFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.section.*;

import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.IDNSQueryPart;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.*;

import org.loxsols.net.service.dns.loulandns.server.common.*;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSProtocolUtils;

import org.loxsols.net.service.dns.loulandns.server.common.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class DNSQuestionSectionFactoryImpl implements IDNSQuestionSectionFactory
{


    LoulanDNSProtocolUtils dnsProtocolUtils = new LoulanDNSProtocolUtils();

    IDNSProtocolModelInstanceFactory dnsProtocolInstanceFactory;


    public DNSQuestionSectionFactoryImpl(IDNSProtocolModelInstanceFactory dnsProtocolInstanceFactory) throws DNSServiceCommonException
    {
        this.dnsProtocolInstanceFactory = dnsProtocolInstanceFactory;
    }


    public IDNSQuestionSection createQuestionSection(String qname, int qtype, int qclass)  throws DNSServiceCommonException
    {

        IDNSQuestionSection questionSection = new DNSQuestionSectionImpl( this.dnsProtocolInstanceFactory );

        IDNSQueryPart queryRecord = createQueryRecord(qname, qtype, qclass);
        IDNSQueryPart[] queryRecords = new IDNSQueryPart[]{ queryRecord };
        questionSection.setDNSQueries( queryRecords );

        return questionSection;

    }

    public IDNSQuestionSection createQuestionSection(int qdCount, byte[] questionSectionBytes, byte[] fullDNSMessageBytes)  throws DNSServiceCommonException
    {        
        IDNSQuestionSection questionSection = new DNSQuestionSectionImpl( this.dnsProtocolInstanceFactory );

        // TODO : DNSメッセージ全体を解析してドメイン名圧縮を解析する機能が未実装.
        // ただ、問い合わせセクションでいきなりドメイン名圧縮が採用されているケースは通常はないと思われる.
        // あるとすると、複数の問い合わせが同時にDNSメッセージ中に含まれるような変わったDNSメッセージの場合のみ.
        // 実用上はまず存在しない.

        questionSection.setDNSQuestionSectionBytes(qdCount, questionSectionBytes);
        return questionSection;
    }

    public IDNSQueryPart createQueryRecord(String qname, int qtype, int qclass)  throws DNSServiceCommonException
    {
        IDNSQueryPart queryRecord = new DNSQueryPartImpl();

        queryRecord.setDNSQueryName(qname);
        queryRecord.setDNSQueryType(qtype);
        queryRecord.setDNSQueryClass(qclass);

        LoulanDNSDebugUtils.printDebug(getClass(), "createQueryRecord", String.format("qtype=%s, queryRecord.getDNSQueryType()=%s", qtype, queryRecord.getDNSQueryType() ));

        return queryRecord;
    }




}