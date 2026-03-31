package org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.rr.edns;


import java.util.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;

import org.loxsols.net.service.dns.loulandns.util.*;


import org.loxsols.net.service.dns.loulandns.server.common.*;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSProtocolUtils;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;
import org.loxsols.net.service.dns.loulandns.server.common.constants.DNSProtocolConstants;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.DNSResourceRecordImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.*;

import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.rr.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.*;



// OPT DNSリソースレコードのEDNS0専用実装クラス.
public class DNSResourceRecordTypeOPTForEDNS0Impl extends DNSResourceRecordTypeOPTImpl implements IDNSResourceRecord
{

    LoulanDNSProtocolUtils protocolUtils = new LoulanDNSProtocolUtils();


    public DNSResourceRecordTypeOPTForEDNS0Impl(byte[] rrBytes) throws DNSServiceCommonException
    {
        super(rrBytes);
    }

    public DNSResourceRecordTypeOPTForEDNS0Impl(IDNSResourceRecord baseRR) throws DNSServiceCommonException
    {
        super(baseRR);
    }


    // 指定したRRデータがEDNS0のRRかを判定する.
    public static boolean isEDNS0RRBytes(IDNSResourceRecord baseRR)
    {
        try
        {
            DNSResourceRecordTypeOPTForEDNS0Impl edns0RR = 
                new DNSResourceRecordTypeOPTForEDNS0Impl(baseRR);

            int ednsVersion = edns0RR.getEDNS0Version();

            if ( ednsVersion == 0 )
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        catch(DNSServiceCommonException cause)
        {
            // 本メソッドの機能は、指定されたRRがEDNS0かを判定するにとどまるため、例外発生時はfalseを返す.
            return false;
        }

    }

    // EDNS0で拡張されたUDPペイロードサイズ
    // RFC6891 pp.7より、CLASSフィールドの値はUDPペイロードサイズ(requestor's UDP payload size)を示す.
    public int getEDNS0UDPPayloadSize() throws DNSServiceCommonException
    {
        // RFC6891 pp.7より、CLASSフィールドの値はUDPペイロードサイズ(requestor's UDP payload size)を示す.
        int udpPayloadSize = super.getRclass();

        if ( udpPayloadSize < 0 || udpPayloadSize > 65535 )
        {
            // UDPペイロードサイズの値が不正.
            String msg = String.format("Invalid EDNS0 UDP payload size : %d", udpPayloadSize );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        return udpPayloadSize;
    }

    // EDNS0で拡張されたEXTENDED-RCODE
    // RFC6891 pp.8より、DNSリソースレコードのTTL値(32bit)の上位8bitを使用してEXTENDED-RCODEを設定する.
    public int getEDNS0ExtendedRcode() throws DNSServiceCommonException
    {
        int ttl = getRTTL();
        int exRCode = protocolUtils.getEDNS0ExtendedRCodeFromTTL(ttl);

        return exRCode;
    }

    // EDNS0で拡張されたEDNSのVERSION
    // RFC6891 pp.8より、DNSリソースレコードのTTL値(32bit)の先頭から2byte目(8bit)を使用してVERSIONを設定する.
    public int getEDNS0Version() throws DNSServiceCommonException
    {
        int ttl = getRTTL();
        int ednsVersion = protocolUtils.getEDNS0VersionFromTTL(ttl);
        
        return ednsVersion;
    }

    // EDNS0で拡張されたEDNSのDO(DNSSEC OK bit)
    // RFC6891 pp.8より、DNSリソースレコードのTTL値(32bit)の先頭から17bit目(1bit)を使用してDOを設定する.
    public int getEDNS0DO() throws DNSServiceCommonException
    {
        int ttl = getRTTL();
        int doFlg = protocolUtils.getEDNS0DOFromTTL(ttl);
        
        return doFlg;
    }

    // EDNS0で拡張されたEDNSのZ(将来拡張に備えて予約)
    // RFC6891 pp.8より、DNSリソースレコードのTTL値(32bit)の先頭から18bit目以降(15bit)がZとして予約されている.
    // 通常は0で埋められており、受け取った側も無視するように実装される.
    public int getEDNS0ReservedZ() throws DNSServiceCommonException
    {
        int ttl = getRTTL();
        int z = protocolUtils.getEDNS0ReservedZFromTTL(ttl);

        return z;
    }


    // OPT RRの可変領域(RDATAフィールド)の具象化データを返す.
    public List<IDNSRROptPseudoRRData> getOptions() throws DNSServiceCommonException
    {
        List<IDNSRROptPseudoRRData> list = new ArrayList<IDNSRROptPseudoRRData>();


        byte[] rdata = getResourceRData();

        int posOfRData = 0;
        while( posOfRData < rdata.length )
        {   
            IDNSRROptPseudoRRData optionData = new DNSRROptPseudoRRDataImpl();

            byte[] leftRData = new byte[ rdata.length - posOfRData ];
            System.arraycopy(rdata, posOfRData, leftRData, 0, leftRData.length );
            int optionSize = optionData.setOptionData(leftRData);
            list.add( optionData );

            posOfRData += optionSize;
        }

        return list;

    }


}