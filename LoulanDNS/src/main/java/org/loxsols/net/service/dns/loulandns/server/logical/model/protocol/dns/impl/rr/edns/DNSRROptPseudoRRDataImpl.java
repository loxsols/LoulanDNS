package org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.rr.edns;


import java.util.*;
import java.util.Arrays;
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



// DNSのOPTリソースレコードのRDATA部分を構成するオプションデータ(疑似RR)のモデルクラス.
// 定義はRFC6891のpp.8にある.
// OPT RRのRDATA部は本クラスのデータが複数件(0個の場合もある)含まれている.
public class DNSRROptPseudoRRDataImpl implements IDNSRROptPseudoRRData
{
    int optionCode;
    int optionLength;
    byte[] optionData;

    LoulanDNSProtocolUtils loulanDNSProtocolUtils = new LoulanDNSProtocolUtils();

    public int getOptionCode() throws DNSServiceCommonException
    {
        return optionCode;
    }

    public int getOptionLength() throws DNSServiceCommonException
    {
        return optionLength;
    }

    public byte[] getOptionData() throws DNSServiceCommonException
    {
        return optionData;
    }

    // 本オプションデータがRDATA部に占めるサイズ.
    public int sizeOfThisOption() throws DNSServiceCommonException
    {
        int size = toBytes().length;
        return size;
    }

    // 本オプションデータのRDATA部におけるbyte配列表現.
    public byte[] toBytes() throws DNSServiceCommonException
    {
        // RFC 6891 pp.8のフォーマット.
        // OPTION-CODE   : 16bit
        // OPTION-LENGTH : 16bit
        // OPTION-DATA   : 可変長(前記OPTION-LENGTHで指定したオクテット長) 

        byte[] bytes = new byte[ 2 + 2 + getOptionLength() ];


        // OPTION-CODE 2byteのコピー.
        byte[] codeBytes= loulanDNSProtocolUtils.shortToBytes( (short)getOptionCode() );
        System.arraycopy(codeBytes, 0, bytes, 0, codeBytes.length );

        // OPTION-LENGTH 2byteのコピー.
        byte[] lengthBytes= loulanDNSProtocolUtils.shortToBytes( (short)getOptionCode() );
        System.arraycopy(lengthBytes, 0, bytes, codeBytes.length, lengthBytes.length );

        // OPTION-DATA 可変長のコピー.
        byte[] dataBytes= this.getOptionData();
        System.arraycopy(dataBytes, 0, bytes, codeBytes.length + lengthBytes.length, dataBytes.length ); 

        return bytes;
    }


    // 本クラスの内容を初期化する.
    // 復帰値はOPT RRのrdata部の占有サイズ.
    public int setOptionData(int optCode, int optLength, byte[] optData) throws DNSServiceCommonException
    {

        if ( optCode < 0 || optCode > 65535 )
        {
            String msg  = String.format("Invalid OPT RR data. OPTION-CODE is not valid value(0-65535) : %d.", optCode);
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        if ( optLength < 0 || optLength > 65535 )
        {
            String msg  = String.format("Invalid OPT RR data. OPTION-LENGTH is not valid value(0-65535) : %d.", optLength);
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        if ( optData == null )
        {
            String msg  = String.format("Invalid OPT RR data. OPTION-DATA is null.");
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }


        if ( optLength != optData.length )
        {
            String msg  = String.format("Invalid OPT RR data. OPTION-LENGTH is not equals data bytes size. OPTION-LENGTH  is %d, data bytes size is %d.", optLength, optData.length);
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }


        this.optionCode = optCode;
        this.optionLength = optLength;
        this.optionData = optData;

        int ret = sizeOfThisOption();
        return ret;
    }

    // 本本クラスの内容を指定したrdataフィールドの値で初期化する.なお、rdataフィールドのデータが後続する場合は読み飛ばす.
    // 復帰値はOPT RRのrdata部の占有サイズ.
    public int setOptionData(byte[] rdata) throws DNSServiceCommonException
    {
        int optCode = (int)loulanDNSProtocolUtils.bytesToUInt16(rdata, 0);
        int optLen = (int)loulanDNSProtocolUtils.bytesToUInt16(rdata, 2);

        byte[] optBytes = new byte[rdata.length - 4];
        System.arraycopy(rdata, 4, optBytes, 0, optBytes.length);

        int ret = setOptionData(optCode, optLen, optBytes);
        return ret;
    }






}