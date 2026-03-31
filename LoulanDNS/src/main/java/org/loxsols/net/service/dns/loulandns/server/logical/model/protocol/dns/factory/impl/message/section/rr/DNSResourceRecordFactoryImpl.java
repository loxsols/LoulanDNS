package org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.section.rr;

import java.util.*;

import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.common.constants.DNSProtocolConstants;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSDebugUtils;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSProtocolUtils;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.section.rr.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.IDNSResourceRecord;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.IDNSRROptPseudoRRData;
import org.xbill.DNS.TTL;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.section.rr.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.rr.DNSResourceRecordTypeOPTImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.rr.edns.DNSResourceRecordTypeOPTForEDNS0Impl;


// DNSリソースレコードのファクトリクラス
public class DNSResourceRecordFactoryImpl implements IDNSResourceRecordFactory
{

    LoulanDNSProtocolUtils protocolUtils = new LoulanDNSProtocolUtils();

    // 指定したタイプに該当するRRクラスの新しいインスタンスを生成する.
    public IDNSResourceRecord createResourceRecord(String rname, int rtype, int rclass, int rTTL, byte[] rdata)  throws DNSServiceCommonException
    {
        // 一度、汎用的なRRのクラスを生成し、RRのbyte配列を生成してから解析する.
        IDNSResourceRecord tmpRR = new DNSResourceRecordImpl();
        tmpRR.setDNSResourceName(rname);
        tmpRR.setResourceType(rtype);
        tmpRR.setResourceClass(rclass);
        tmpRR.setResourceTTL(rTTL);
        tmpRR.setResourceRData(rdata);
        tmpRR.setResourceRDLength(rdata.length);

        byte[] rrBytes = tmpRR.getDNSResourceRecordBytes();
        
        IDNSResourceRecord rr = createResourceRecord(rtype, rrBytes);
        return rr;
    }


    // EDNS(RFC2671)のOPT-RRを生成する.
    public IDNSResourceRecord createEDNSResourceRecord(int ednsPayloadSize, int ednsExtendedRCode, int ednsVersion, int ednsReservedZ, List<IDNSRROptPseudoRRData> pseudoRRSetList) throws DNSServiceCommonException
    {

        // RFC 2671で定義されたEDNSのOPTリソースレコードの内容
        // フィールド名  フィールドタイプ      説明
        // ------------------------------------------------------
        // NAME          ドメイン名            空(ルートドメイン)
        // TYPE          u_int16_t         OPT
        // CLASS         u_int16_t         送信者のUDPペイロードサイズ
        // TTL           u_int32_t             拡張RCODEおよびフラグ
        // RDLEN         u_int16_t             RDATAを記述
        // RDATA         オクテットストリーム  {属性, 値}の組

        String rname = "";
        int rtype = DNSProtocolConstants.DNS_RR_TYPE_OPT;
        int rclass = ednsPayloadSize;

        //   +0 (MSB)                            +1 (LSB)
        //   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
        //0: |  EXTENDED-RCODE(拡張RCODE)    |            VERSION            |
        //   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
        //2: |                               Z                               |
        //   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
        int rttl = protocolUtils.getTTLFromEDNS0Parameters(ednsExtendedRCode, ednsVersion, rclass, ednsReservedZ);

        byte[] rdata = protocolUtils.getRDataBytesFromPseudoOptRR(pseudoRRSetList);


        IDNSResourceRecord baseRR = createResourceRecord(rname, rtype, rclass, rttl, rdata);

        LoulanDNSDebugUtils.printDebug( this.getClass(), "createEDNSResourceRecord", "baseRR.getResourceName() = " + baseRR.getDNSResourceName() );
        LoulanDNSDebugUtils.printDebug( this.getClass(), "createEDNSResourceRecord", "baseRR.getResourceType() = " + baseRR.getResourceType() );
        LoulanDNSDebugUtils.printDebug( this.getClass(), "createEDNSResourceRecord", "baseRR.getResourceTTL() = " + baseRR.getResourceTTL() );
        LoulanDNSDebugUtils.printDebug( this.getClass(), "createEDNSResourceRecord", "baseRR.getResourceRDLength() = " + baseRR.getResourceRDLength() );



        IDNSResourceRecord ednsRR = new DNSResourceRecordTypeOPTForEDNS0Impl(baseRR);

        return ednsRR;
    }




    // 指定したRRのデータに適したRRクラスの新しいインスタンスを生成する.
    public IDNSResourceRecord createResourceRecord(byte[] rrBytes)  throws DNSServiceCommonException
    {
        // 指定したタイプに該当するRRクラスの新しいインスタンスを生成する.
        int rtype = getResourceTypeFromRRBytes(rrBytes);

        IDNSResourceRecord rr = createNewRRInstance(rtype, rrBytes);
        return rr;
    }


    // 指定したタイプに該当するRRクラスの新しいインスタンスを生成する.
    public IDNSResourceRecord createResourceRecord(int rtype, byte[] rrBytes)  throws DNSServiceCommonException
    {
        // 指定したタイプに該当するRRクラスの新しいインスタンスを生成する.
        IDNSResourceRecord rr = createNewRRInstance(rtype, rrBytes);
        return rr;
    }




    // 指定したタイプに該当するRRクラスの新しいインスタンスを生成する.
    // OPTリソースレコードのような特殊なRRは更にrdataに応じてサブクラスがあるので呼び出し元で調整する必要がある.
    public IDNSResourceRecord createNewRRInstance(int rtype, byte[] rrBytes) throws DNSServiceCommonException
    {
        // 指定したタイプに該当するRRクラスの新しいインスタンスを生成する.
        IDNSResourceRecord rr = null;
        if ( rtype == DNSProtocolConstants.DNS_RR_TYPE_OPT)
        {
            // OPT リソースレコードの場合.
            rr = createNewOPTRRInstance(rtype, rrBytes);
        }
        else
        {
            // デフォルトの場合は汎用的なRRの実装クラスを使用する.
            rr = new DNSResourceRecordImpl();
        }

        return rr;
    }


    // OPTリソースレコードの場合は実装クラスが複数存在するため、本メソッド内で生成する.
    private IDNSResourceRecord createNewOPTRRInstance(int rtype, byte[] rrBytes) throws DNSServiceCommonException
    {
        if ( rtype != DNSProtocolConstants.DNS_RR_TYPE_OPT )
        {
            String msg = String.format("Failed to create instance of OPT Resource Record (RR) class. rtype is not OPT(%d). rtype=%d", DNSProtocolConstants.DNS_RR_TYPE_OPT, rtype);
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        // OPTリソースレコードの汎用RRクラスを生成してRRのbyte配列を解析する.
        IDNSResourceRecord optRR = new DNSResourceRecordTypeOPTImpl(rrBytes);
        
        if ( optRR.getResourceType() != DNSProtocolConstants.DNS_RR_TYPE_OPT )
        {
            String msg = String.format("Failed to create instance of OPT Resource Record (RR) class. RR bytes is not OPT(%d) bytes data. type=%d", DNSProtocolConstants.DNS_RR_TYPE_OPT, optRR.getResourceType() );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }


        // EDNS0のRRかを判定する.
        if ( DNSResourceRecordTypeOPTForEDNS0Impl.isEDNS0RRBytes( optRR ) == false )
        {
            // OPT RRだが、EDNS0ではない.
            // OPT RRの汎用RRクラスを返す.
            return optRR;
        }


        // EDNS0のRRをインスタンス化して返す.
        DNSResourceRecordTypeOPTForEDNS0Impl edns0RR = new DNSResourceRecordTypeOPTForEDNS0Impl(optRR);
        return edns0RR;

    }


    // 指定したRRのデータからリソースタイプ(RType)を取得して返す.
    private int getResourceTypeFromRRBytes(byte[] rrBytes) throws DNSServiceCommonException
    {
        IDNSResourceRecord tmpRR = new DNSResourceRecordImpl();
        tmpRR.setDNSResourceRecordBytes(rrBytes);

        int rtype = tmpRR.getResourceType();
        return rtype;
    }


}