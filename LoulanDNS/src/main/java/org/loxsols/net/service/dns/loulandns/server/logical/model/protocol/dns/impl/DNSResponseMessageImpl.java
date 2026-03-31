package org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl;


import java.util.*;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.loxsols.net.service.dns.loulandns.server.common.*;
import org.loxsols.net.service.dns.loulandns.server.common.constants.DNSProtocolConstants;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSDebugUtils;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSProtocolUtils;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.model.IDNSProtocolModelInstanceFactory;




// DNS問い合わせメッセージの実装クラス.
public class DNSResponseMessageImpl extends DNSMessageImpl implements IDNSMessage, IDNSResponseMessage
{

    LoulanDNSProtocolUtils protocolUtils = new LoulanDNSProtocolUtils();


    public DNSResponseMessageImpl(IDNSProtocolModelInstanceFactory dnsProtocolModelInstanceFactory) throws DNSServiceCommonException
    {
        super( dnsProtocolModelInstanceFactory );
    }
    

    public void init(String qname, int qtype, int qclass, String rname, int rtype, int rclass, int rTTL, byte[] rdata) throws DNSServiceCommonException
    {
        IDNSAnswerSection answerSection = getDNSProtocolModelInstanceFactory().createDNSAnswerSectionInstance();

        IDNSResourceRecord rr = getDNSProtocolModelInstanceFactory().createDNSResourceRecord(rname, rtype, rclass, rTTL, rdata);
        answerSection.addDNSResourceRecord(rr);

        IDNSResourceRecord[] rrSetArray = answerSection.getDNSResourceRecords();

        init( qname, qtype, qclass, rrSetArray[0] );

    }

    public void init(String qname, int qtype, int qclass, IDNSResourceRecord rr) throws DNSServiceCommonException
    {
        List<IDNSResourceRecord> rrSet = new ArrayList<IDNSResourceRecord>();
        rrSet.add( rr );

        init(qname, qtype, qclass, rrSet );
    }

    public void init(String qname, int qtype, int qclass, List<IDNSResourceRecord> rrSet) throws DNSServiceCommonException
    {
        // id : ランダムなID値(16bit)
        short id = protocolUtils.getRandom16bitValue();
        
        IDNSHeaderSection headerSection = getDNSProtocolModelInstanceFactory().createDNSHeaderSectionInstance();
        headerSection.init(id, true, 0, false, false, false, false, false, false, false, 0, 1, rrSet.size(), 0, 0 );

        IDNSQuestionSection questionSection = getDNSProtocolModelInstanceFactory().createSimpleDNSQuestionSectionInstance(qname, qtype, qclass);

        IDNSAnswerSection answerSection = getDNSProtocolModelInstanceFactory().createDNSAnswerSectionInstance();
        for( IDNSResourceRecord rr : rrSet )
        {
            answerSection.addDNSResourceRecord(rr);
        }
        
        init( headerSection, questionSection, answerSection);
    }

    public void init(IDNSHeaderSection headerSection, IDNSQuestionSection questionSection, IDNSAnswerSection answerSection ) throws DNSServiceCommonException
    {
        init( headerSection, questionSection, answerSection, null, null);
    }

    public void init(IDNSHeaderSection headerSection, IDNSQuestionSection questionSection, IDNSAnswerSection answerSection, IDNSAuthoritySection authoritySection, IDNSAdditionalSection additionalSection) throws DNSServiceCommonException
    {
        this.setDNSHeaderSection( headerSection );
        this.setDNSQuestionSection( questionSection );
        this.setDNSAnswerSection( answerSection );
        this.setDNSAuthoritySection( authoritySection );
        this.setDNSAdditionalSection( additionalSection );

        validate();
    }


    public void init(byte[] dnsMessageBytes) throws DNSServiceCommonException
    {
        setDNSMessageBytes(dnsMessageBytes);

        validate();
    }


    public void validate() throws DNSServiceCommonException
    {

        if ( getDNSHeaderSection() == null )
        {
            String msg = String.format("Failed to build DNSResponseMessage instance : Header Section is null.");
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }



        if ( getDNSQuestionSection() == null )
        {
            String msg = String.format("Failed to build DNSResponseMessage instance : Question Section is null.");
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        if ( isSucceedDNSResponse() )
        {

            // DNSレスポンスコードが成功なのにレスポンスデータが存在しない.
            if ( getDNSAnswerSection() == null && getDNSAuthoritySection() == null && getDNSAdditionalSection() == null )
            {
                String msg = String.format("Failed to build DNSResponseMessage instance : All response sections is not set.");
                DNSServiceCommonException exception = new DNSServiceCommonException(msg);
                throw exception;
            }

        }

        // TODO : 各DNSレスポンスコードに応じてDNSレスポンスデータの内容を解析する処理が必要.


    }


    public boolean isSucceedDNSResponse() throws DNSServiceCommonException
    {
        if ( getDNSHeaderSection() == null )
        {
            String msg = String.format("DNS header section is not set.");
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        long rCode = getDNSHeaderSection().getRCode();
        
        if ( rCode != DNSProtocolConstants.DNS_RCODE_NOERROR )
        {
            return false;
        }

        return true;
    }



    public String toString()
    {
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outputStream);

        try
        {

            // Headerセクション
            IDNSHeaderSection headerSection = getDNSHeaderSection();
            long qdCount = headerSection.getQDCOUNT();
            long anCount = headerSection.getANCOUNT();

            out.println("--------------------------------------");
            out.println("Header Section");
            out.println(String.format("RCode = %d", headerSection.getRCode()) );
            out.println(String.format("QDCount = %d", qdCount) );
            out.println(String.format("ANCount = %d", anCount) );
            out.println("--------------------------------------");



            // Questionセクション
            IDNSQuestionSection questionSection = getDNSQuestionSection();
            out.println("--------------------------------------\n");
            out.println("Question Section\n");
            out.println(String.format("QDCount = %d", qdCount) );
            out.println("--------------------------------------\n");
            if ( questionSection != null )
            {
                for( IDNSQueryPart query : questionSection.getDNSQueries() )
                {
                    String qname = query.getDNSQueryName();
                    out.println("qname : " + qname + "\n");
                }
            }

            // Answerセクション
            IDNSAnswerSection answerSection = getDNSAnswerSection();
            out.println("--------------------------------------\n");
            out.println("Answer Section\n");
            out.println(String.format("ANCount = %d", anCount) );
            out.println("--------------------------------------\n");
            if ( answerSection != null )
            {
                for( IDNSResourceRecord rr : answerSection.getDNSResourceRecords() )
                {
                    String rname = rr.getDNSResourceName();
                    byte[] rdata = rr.getResourceRData();

                    out.println("rname : " + rname + "\n");
                }
            }

            out.flush();
            return outputStream.toString();
        }
        catch(DNSServiceCommonException exception)
        {
            return exception.toString();
        }

        
    }


    

}