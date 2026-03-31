package org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.xbill;


import java.util.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.*;

import org.loxsols.net.service.dns.loulandns.server.common.*;
import org.loxsols.net.service.dns.loulandns.server.common.constants.DNSProtocolConstants;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;


import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.model.DNSProtocolModelInstanceFactoryImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.model.*;

import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.*;



import org.xbill.DNS.*;



// DNSメッセージの実装クラス.
public class XbillDNSMessageImpl extends DNSMessageImpl implements IDNSMessage
{

    org.xbill.DNS.Message xbillDNSMessage;

    public XbillDNSMessageImpl(IDNSProtocolModelInstanceFactory dnsProtocolModelInstanceFactory) throws DNSServiceCommonException
    {
        super(dnsProtocolModelInstanceFactory);
    }

    public XbillDNSMessageImpl(IDNSProtocolModelInstanceFactory dnsProtocolModelInstanceFactory, org.xbill.DNS.Message xbillDNSMessage) throws DNSServiceCommonException
    {
        super(dnsProtocolModelInstanceFactory);
        this.xbillDNSMessage = xbillDNSMessage;
    }

    public XbillDNSMessageImpl(IDNSProtocolModelInstanceFactory dnsProtocolModelInstanceFactory, byte[] dnsMessageBytes) throws DNSServiceCommonException
    {
        super(dnsProtocolModelInstanceFactory);
        setDNSMessageBytes(dnsMessageBytes);
    }

    public void setDNSMessageBytes(byte[] dnsMessageBytes) throws DNSServiceCommonException
    {
        try
        {
            this.xbillDNSMessage = new org.xbill.DNS.Message( dnsMessageBytes );
        }
        catch(IOException cause)
        {
            String msg ="Failed to create Xbill DNSMessage instance.";
            DNSServiceCommonException exception = new DNSServiceCommonException(msg, cause);
            throw exception;
        }
    }


    public org.xbill.DNS.Message getXbillDNSMessage()
    {
        return this.xbillDNSMessage;
    }

    public IDNSHeaderSection getDNSHeaderSection() throws DNSServiceCommonException
    {
        IDNSHeaderSection headerSection = toDNSHeaderSection( getXbillDNSMessage() );
        return headerSection;
    }

    public void setDNSHeaderSection(IDNSHeaderSection headerSection) throws DNSServiceCommonException
    {
        String msg = "Unsupported Method. Because of Xbill instance support getter method only.";
        DNSServiceCommonException exception  =new DNSServiceCommonException(msg);
        throw exception;
    }


    public IDNSQuestionSection getDNSQuestionSection() throws DNSServiceCommonException
    {
        IDNSQuestionSection questionSection = toDNSQuestionSection(getXbillDNSMessage());
        return questionSection;
    }

    public void setDNSQuestionSection(IDNSQuestionSection questionSection) throws DNSServiceCommonException
    {
        String msg = "Unsupported Method. Because of Xbill instance support getter method only.";
        DNSServiceCommonException exception  =new DNSServiceCommonException(msg);
        throw exception;
    }


    public IDNSAnswerSection getDNSAnswerSection() throws DNSServiceCommonException
    {
        IDNSAnswerSection answerSection = toDNSAnswerSection( getXbillDNSMessage() );
        return answerSection;
    }

    public void setDNSAnswerSection(IDNSAnswerSection answerSection) throws DNSServiceCommonException
    {
        String msg = "Unsupported Method. Because of Xbill instance support getter method only.";
        DNSServiceCommonException exception  =new DNSServiceCommonException(msg);
        throw exception;
    }


    public IDNSAuthoritySection getDNSAuthoritySection() throws DNSServiceCommonException
    {
        IDNSAuthoritySection authoritySection = toDNSAuthoritySection( getXbillDNSMessage() );
        return authoritySection;
    }

    public void setDNSAuthoritySection(IDNSAuthoritySection authoritySection) throws DNSServiceCommonException
    {
        String msg = "Unsupported Method. Because of Xbill instance support getter method only.";
        DNSServiceCommonException exception  =new DNSServiceCommonException(msg);
        throw exception;
    }


    public IDNSAdditionalSection getDNSAdditionalSection() throws DNSServiceCommonException
    {
        IDNSAdditionalSection additionalSection = toDNSAdditionalSection( getXbillDNSMessage() );
        return additionalSection;
    }

    public void setDNSAdditionalSection(IDNSAdditionalSection additionalSection) throws DNSServiceCommonException
    {
        String msg = "Unsupported Method. Because of Xbill instance support getter method only.";
        DNSServiceCommonException exception  =new DNSServiceCommonException(msg);
        throw exception;
    }




    public static IDNSQueryPart toDNSQuery(Message message) throws DNSServiceCommonException
    {
        String qname = message.getQuestion().getName().toString();
        int qtype = message.getQuestion().getType();
        int qclass = message.getQuestion().getDClass();

        IDNSQueryPart query = new XbillDNSQueryPartImpl();

        query.setDNSQueryName(qname);
        query.setDNSQueryType(qtype);
        query.setDNSQueryClass(qclass);

        return query;
    }


    public static IDNSResourceRecord[] toLoulanDNSRRSet(List<org.xbill.DNS.Record> xbillRecords) throws DNSServiceCommonException
    {
        IDNSResourceRecord[] rrSet = new IDNSResourceRecord[ xbillRecords.size() ];

        int index = 0;
        for( org.xbill.DNS.Record xbillRecord : xbillRecords)
        {
            String rname = xbillRecord.getName().toString();
            int rclass = xbillRecord.getDClass();
            int rtype = xbillRecord.getType();
            long ttl = xbillRecord.getTTL();
            byte[] rdata = xbillRecord.rdataToWireCanonical();

            IDNSResourceRecord record = new DNSResourceRecordImpl();
            record.setDNSResourceName(rname);
            record.setResourceClass(rclass);
            record.setResourceType(rtype);
            record.setResourceRData(rdata);

            rrSet[index] = record;

            index++;
        }

        return rrSet;
    }


    // org.xbill.DNS.MessageクラスからLoulanDNSのヘッダセクションを生成する.
    public static IDNSHeaderSection toDNSHeaderSection(Message message) throws DNSServiceCommonException
    {
        byte[] headerBytes = message.getHeader().toWire();

        IDNSHeaderSection headerSection = new DNSHeaderSectionImpl();
        headerSection.setDNSHeaderBytes(headerBytes);

        return headerSection;
    }


    // org.xbill.DNS.MessageクラスからLoulanDNSの問い合わせセクションを生成する.
    public IDNSQuestionSection toDNSQuestionSection(Message message) throws DNSServiceCommonException
    {
        IDNSQueryPart queryPart = toDNSQuery(message);

        DNSQuestionSectionImpl questionSection = new DNSQuestionSectionImpl( super.getDNSProtocolModelInstanceFactory() );
        questionSection.addDNSQuery( queryPart );

        return questionSection;
    }


    // org.xbill.DNS.MessageクラスからLoulanDNSの回答セクションを生成する.
    public static IDNSAnswerSection toDNSAnswerSection(Message message) throws DNSServiceCommonException
    {
        List<org.xbill.DNS.Record> xbillRecords = message.getSection(1);

        IDNSResourceRecord[] rrSet = toLoulanDNSRRSet(xbillRecords);

        IDNSAnswerSection section = new DNSAnswerSectionImpl();
        section.setDNSResourceRecords(rrSet);

        return section;
    }


    // org.xbill.DNS.MessageクラスからLoulanDNSのAuthorityセクションを生成する.
    public static IDNSAuthoritySection toDNSAuthoritySection(Message message) throws DNSServiceCommonException
    {
        List<org.xbill.DNS.Record> xbillRecords = message.getSection(2);

        IDNSResourceRecord[] rrSet = toLoulanDNSRRSet(xbillRecords);

        IDNSAuthoritySection section = new DNSAuthoritySectionImpl();
        section.setDNSResourceRecords(rrSet);

        return section;
    }

    // org.xbill.DNS.MessageクラスからLoulanDNSのAdditionalセクションを生成する.
    public static IDNSAdditionalSection toDNSAdditionalSection(Message message) throws DNSServiceCommonException
    {
        List<org.xbill.DNS.Record> xbillRecords = message.getSection(2);

        IDNSResourceRecord[] rrSet = toLoulanDNSRRSet(xbillRecords);

        IDNSAdditionalSection section = new DNSAdditionalSectionImpl();
        section.setDNSResourceRecords(rrSet);

        return section;
    }




}