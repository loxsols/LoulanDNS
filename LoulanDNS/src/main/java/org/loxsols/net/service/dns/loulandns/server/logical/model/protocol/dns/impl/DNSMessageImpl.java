package org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl;


import java.util.*;
import java.io.ByteArrayOutputStream;
import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.ComponentScan;
import org.xbill.DNS.ExtendedResolver;
import org.loxsols.net.service.dns.loulandns.server.common.*;
import org.loxsols.net.service.dns.loulandns.server.common.constants.DNSProtocolConstants;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSDebugUtils;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;


import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.*;
import org.loxsols.net.service.dns.loulandns.util.DNSUtils;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.model.*;

import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.rr.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.rr.edns.*;



// DNSメッセージの実装クラス.
@ComponentScan
public class DNSMessageImpl implements IDNSMessage
{

    IDNSProtocolModelInstanceFactory dnsProtocolModelInstanceFactory;


    IDNSHeaderSection headerSection;
    IDNSQuestionSection questionSection;
    IDNSAnswerSection answerSection;
    IDNSAuthoritySection authoritySection;
    IDNSAdditionalSection additionalSection;

    DNSUtils dnsUtils = new DNSUtils();

    public DNSMessageImpl(IDNSProtocolModelInstanceFactory dnsProtocolModelInstanceFactory) throws DNSServiceCommonException
    {
        this.dnsProtocolModelInstanceFactory = dnsProtocolModelInstanceFactory;
    }

    public IDNSHeaderSection getDNSHeaderSection() throws DNSServiceCommonException
    {
        return headerSection;
    }

    public void setDNSHeaderSection(IDNSHeaderSection headerSection) throws DNSServiceCommonException
    {
        this.headerSection = headerSection;
    }


    public IDNSQuestionSection getDNSQuestionSection() throws DNSServiceCommonException
    {
        return questionSection;
    }

    // 問い合わせセクションを設定する.
    public void setDNSQuestionSection(IDNSQuestionSection questionSection) throws DNSServiceCommonException
    {
        this.questionSection = questionSection;

        // 問い合わせセクションを設定したので、そのレコード数をヘッダーセクション側に反映する.
        int qdCount = 0;
        if ( questionSection != null )
        {
            qdCount = questionSection.getQueryCount();
        }
        IDNSHeaderSection headerSection = getDNSHeaderSection();
        headerSection.setQDCOUNT(qdCount);

    }


    public IDNSAnswerSection getDNSAnswerSection() throws DNSServiceCommonException
    {
        return answerSection;
    }

    // Answerセクションを設定する.
    public void setDNSAnswerSection(IDNSAnswerSection answerSection) throws DNSServiceCommonException
    {
        this.answerSection = answerSection;


        // Authorityセクションを設定したので、そのレコード数をヘッダーセクション側に反映する.
        int anCount = 0;
        if ( answerSection != null )
        {
            anCount = answerSection.getDNSResourceRecords().length;
            IDNSHeaderSection headerSection = getDNSHeaderSection();
        }
        headerSection.setANCOUNT(anCount);

    }


    public IDNSAuthoritySection getDNSAuthoritySection() throws DNSServiceCommonException
    {
        return authoritySection;
    }

    // Authorityセクションを設定する.
    public void setDNSAuthoritySection(IDNSAuthoritySection authoritySection) throws DNSServiceCommonException
    {
        this.authoritySection = authoritySection;

        // Authorityセクションを設定したので、そのレコード数をヘッダーセクション側に反映する.
        int nsCount = 0;
        if ( authoritySection != null )
        {
            nsCount = authoritySection.getDNSResourceRecords().length;
        }
        IDNSHeaderSection headerSection = getDNSHeaderSection();
        headerSection.setNSCOUNT(nsCount);

    }


    public IDNSAdditionalSection getDNSAdditionalSection() throws DNSServiceCommonException
    {
        return additionalSection;
    }

    // Additionalセクションを設定する.
    public void setDNSAdditionalSection(IDNSAdditionalSection additionalSection) throws DNSServiceCommonException
    {
        this.additionalSection = additionalSection;

        // Additionalセクションを設定したので、そのレコード数をヘッダーセクション側に反映する.
        int arCount = 0;
        if ( additionalSection != null )
        {
            arCount = additionalSection.getDNSResourceRecords().length;
            IDNSHeaderSection headerSection = getDNSHeaderSection();
        }
        headerSection.setARCOUNT(arCount);

    }



    // DNSメッセージのbyte配列を返す.
    public byte[] getDNSMessageBytes() throws DNSServiceCommonException
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        // ヘッダーセクション.
        IDNSHeaderSection headerSec = getDNSHeaderSection();
        if ( headerSec != null )
        {
            byte[] headerBytes = headerSec.getDNSHeaderBytes();
            stream.write( headerBytes, 0, headerBytes.length );

            LoulanDNSDebugUtils.printDebug( this.getClass(), "getDNSMessageBytes()", String.format("headerBytes.length=%d", headerBytes.length ) );

        }
        else
        {
            String msg = "Fialed to build DNS message bytes. DNS Header Section is not exists.";
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        
        // 問い合わせセクション
        IDNSQuestionSection questionSec = getDNSQuestionSection();
        if ( questionSec != null )
        {
            byte[] questionBytes = questionSec.getDNSQuestionSectionBytes();
            stream.write( questionBytes, 0, questionBytes.length );

            
            LoulanDNSDebugUtils.printDebug( this.getClass(), "getDNSMessageBytes()", String.format("questionBytes.length=%d", questionBytes.length ) );

        }
        else
        {
            // 問い合わせセクションが無いDNSメッセージはありえないので例外をスロー.
            String msg = "Fialed to build DNS message bytes. DNS Question Section is not exists.";
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;          
        }

        // アンサーセクション
        IDNSAnswerSection answerSec = getDNSAnswerSection();
        if ( answerSec != null )
        {
            byte[] answerBytes = answerSec.getDNSRRSetBytes();
            stream.write( answerBytes, 0, answerBytes.length );

            LoulanDNSDebugUtils.printDebug( this.getClass(), "getDNSMessageBytes()", String.format("answerBytes.length=%d", answerBytes.length ) );

        }


        // オーソリティセクション
        IDNSAuthoritySection authoritySec = getDNSAuthoritySection();
        if ( authoritySec != null )
        {
            byte[] authorityBytes = authoritySec.getDNSRRSetBytes();
            stream.write( authorityBytes, 0, authorityBytes.length );

            LoulanDNSDebugUtils.printDebug( this.getClass(), "getDNSMessageBytes()", String.format("authorityBytes.length=%d", authorityBytes.length ) );

        }


        // アディショナルセクション
        IDNSAdditionalSection additionalSec = getDNSAdditionalSection();
        if ( additionalSec != null )
        {
            byte[] additionalBytes = additionalSec.getDNSRRSetBytes();
            stream.write( additionalBytes, 0, additionalBytes.length );

            LoulanDNSDebugUtils.printDebug( this.getClass(), "getDNSMessageBytes()", String.format("additionalBytes.length=%d", additionalBytes.length ) );

        }

        // DNSメッセージを構築.
        byte[] messageBytes = stream.toByteArray();


        LoulanDNSDebugUtils.printDebug( this.getClass(), "getDNSMessageBytes()", String.format("messageBytes.length=%d", messageBytes.length ) );


        return messageBytes;
    }




    // DNSメッセージのbyte配列を元に本クラスの各値を設定する.
    public void setDNSMessageBytes(byte[] messageBytes ) throws DNSServiceCommonException
    {


        // ヘッダセクションを解析.
        if ( messageBytes.length < 12 )
        {
            // DNSヘッダセクション分の長さにも満たない不十分なDNSメッセージ.
            String msg = String.format("Insufficient DNS Message. Failed to parse DNS Header section. DNS Message length=%d", messageBytes.length );
            InsufficientDNSMessageException exception = new InsufficientDNSMessageException(msg);
            throw exception;
        }

        byte[] headerBytes = Arrays.copyOfRange(messageBytes, 0, 12);
        IDNSHeaderSection headerSection = dnsProtocolModelInstanceFactory.createDNSHeaderSectionInstance();
        headerSection.setDNSHeaderBytes( headerBytes );
        this.setDNSHeaderSection( headerSection );

        int msgIndex = 12; // ヘッダセクションのサイズは12バイト.


        // 問い合わせセクションを解析.
        int qdCount = (int)headerSection.getQDCOUNT();
        if ( qdCount > 0 )
        {
            IDNSQuestionSection questionSection = dnsProtocolModelInstanceFactory.createDNSQuestionSectionInstance();

            byte[] questionSecBytes = Arrays.copyOfRange(messageBytes, msgIndex, messageBytes.length );
            questionSection.setDNSQuestionSectionBytes(qdCount, questionSecBytes);

            this.setDNSQuestionSection(questionSection);

            msgIndex += questionSection.getDNSQuestionSectionBytes().length;
        }


        // 回答セクションを解析.
        int anCount = (int)headerSection.getANCOUNT();
        if ( anCount > 0 )
        {
            IDNSAnswerSection answerSection = dnsProtocolModelInstanceFactory.createDNSAnswerSectionInstance();

            byte[] answerSecBytes = Arrays.copyOfRange(messageBytes, msgIndex, messageBytes.length );
            msgIndex += answerSection.setDNSRRSetBytes(anCount, answerSecBytes, messageBytes);

            this.setDNSAnswerSection(answerSection);
        }

        // Authorityセクションを解析
        int nsCount = (int)headerSection.getNSCOUNT();
        if ( nsCount > 0 )
        {
            IDNSAuthoritySection authoritySection = dnsProtocolModelInstanceFactory.createDNSAuthoritySectionInstance();

            byte[] authoritySecBytes = Arrays.copyOfRange(messageBytes, msgIndex, messageBytes.length );
            msgIndex += authoritySection.setDNSRRSetBytes(nsCount, authoritySecBytes, messageBytes);

            this.setDNSAuthoritySection(authoritySection);
        } 


        // Additionalセクションを解析
        int arCount = (int)headerSection.getARCOUNT();
        if ( arCount > 0 )
        {
            IDNSAdditionalSection addtionalSection = dnsProtocolModelInstanceFactory.createDNSAdditionalSectionInstance();

            byte[] additionalSecBytes = Arrays.copyOfRange(messageBytes, msgIndex, messageBytes.length );
            msgIndex += addtionalSection.setDNSRRSetBytes(arCount, additionalSecBytes, messageBytes);

            this.setDNSAdditionalSection(addtionalSection);
        }


    }


    public IDNSProtocolModelInstanceFactory getDNSProtocolModelInstanceFactory() throws DNSServiceCommonException
    {
        return dnsProtocolModelInstanceFactory;
    }


    // このDNSメッセージがEDNS(0)[RFC6891]で拡張されている場合はtrueを返す.
    public boolean isEDNS0Message() throws DNSServiceCommonException
    {
        // EDNS(0)[RFC6891]はAdditionalセクションにOPTリソースレコードを追加することで実装されている.
        
        if ( getDNSHeaderSection().getARCOUNT() == 0 )
        {
            // ヘッダセクションでADDITIONALセクションの個数が0に設定されているためEDNSではない.
            return false;
        }

        if ( getDNSAdditionalSection() == null )
        {
            return false;
        }

        if ( getDNSAdditionalSection().getDNSRRCount() == 0 )
        {
            return false;
        }

        IDNSResourceRecord[] optRRSet = getDNSAdditionalSection().selectDNSResourceRecords(DNSProtocolConstants.DNS_RR_TYPE_OPT);
        if ( optRRSet != null )
        {
            return false;
        }

        if ( optRRSet.length == 0 )
        {
            return false;
        }

        return true;
    }


    // このDNSメッセージがEDNS(0)[RFC6891]で拡張されている場合、EDNS0拡張情報のOPTリソースレコードを返す.
    public DNSResourceRecordTypeOPTForEDNS0Impl getEDNS0OPTResourceRecord() throws DNSServiceCommonException
    {

        // EDNS(0)[RFC6891]はAdditionalセクションにOPTリソースレコードを追加することで実装されている.
        if ( getDNSHeaderSection().getARCOUNT() == 0 )
        {
            // ヘッダセクションでADDITIONALセクションの個数が0に設定されているためEDNSではない.
            return null;
        }

        if ( getDNSAdditionalSection() == null )
        {
            // AdditionalセクションがないのでENDS情報は存在しない.
            return null;
        }

        if ( getDNSAdditionalSection().getDNSRRCount() == 0 )
        {
            // Additionalセクション内のリソースレコードが0件.
            return null;
        }

        // Additionalセクション内のOPTリソースレコード(TYPE 41)を検索する.
        IDNSResourceRecord[] optRRSet = getDNSAdditionalSection().selectDNSResourceRecords(DNSProtocolConstants.DNS_RR_TYPE_OPT);
        if ( optRRSet == null || optRRSet.length == 0 )
        {
            return null;
        }


        DNSResourceRecordTypeOPTForEDNS0Impl edns0Record = null;
        for( IDNSResourceRecord rr : optRRSet )
        {
            if ( dnsUtils.isEDNS0OptRR( rr ) == true )
            {
                edns0Record = new DNSResourceRecordTypeOPTForEDNS0Impl(rr);
                break;
            }
        }

        return edns0Record;
    }


}