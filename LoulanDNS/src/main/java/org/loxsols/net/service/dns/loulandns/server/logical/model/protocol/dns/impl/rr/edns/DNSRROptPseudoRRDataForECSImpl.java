package org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.rr.edns;


import java.util.*;
import java.util.Arrays;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.net.*;

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
import org.loxsols.net.service.dns.loulandns.server.common.util.*;



// EDNS Client Subnet (RFC 7871)のサブネット表現の専用データクラス.
// DNSのOPTリソースレコードのRDATA部分を構成するオプションデータ(疑似RR)のモデルクラスの派生クラスとして実装する.
// RFC 7871 Client Subnet in DNS Queries(May 2016)の 6.Option Format(pp.8)のデータ構造図をもとに
// ECSのデータのモデルクラスを実装する.
public class DNSRROptPseudoRRDataForECSImpl extends DNSRROptPseudoRRDataImpl implements IDNSRROptPseudoRRData
{

    // ECSのデータ構造
    // OPTION-CODE              : 2byte     : ECSを表すオプションコード.8固定(0x0008).
    // OTPION-LENGTH            : 2byte     : このオプションデータのペイロードサイズ(疑似RRのデータ部の長さのバイト長) (4byte(FAMILY, SOURCE PREFIX-LENGTH, SCOPE PREFIX-LENGTH) + アドレス部可変長).
    // FAMILY                   : 2byte     : IANAのアドレスファミリ番号.IPv4=1, IPv6=2, ...etc.
    //                                  そのほかの値についてはIANAのWebサイト(Address Family Numbers)を参照.
    //                                  https://www.iana.org/assignments/address-family-numbers/address-family-numbers.xhtml
    // SOURCE PREFIX-LENGTH     : 1byte     : 問い合わせメッセージに設定されるアドレスのビットサイズ.レスポンスには同一値をコピーする.
    // SCOPE PREFIX-LENGTH      : 1byte     : レスポンスメッセージがカバーするアドレスのビットサイズ.問い合わせ時は常に0に設定されなければならない(MUST).
    // ADDRESS                  : 可変長     :　アドレス値(可変長). SOURCE PREFIX-LENGTHで指定したビット長に切り詰めて表現されなければならない(MUST)が、必要に応じて残りの桁数を0でパディングしてもよい.


    LoulanDNSProtocolUtils dnsProtocolUtils = new LoulanDNSProtocolUtils();




    // 本クラスの内容を指定したネットワーク情報(EDNS Client Subnet)で初期化する.
    // 復帰値はOPT RRのrdata部の占有サイズ.
    public int setEDNSClientSubnetInfo(InetAddress inetAddress, int sourceBitSize, int scopeBitSize) throws DNSServiceCommonException
    {
        int addressFamily;
        if ( inetAddress.getAddress().length == 4 )
        {
            // IPv4(1)
            addressFamily = DNSProtocolConstants.IANA_ADDRESS_FAMILY_TYPE_IP;
        }
        else
        {
            // IPv6(2)
            addressFamily = DNSProtocolConstants.IANA_ADDRESS_FAMILY_TYPE_IP6;
        }

        int ret = setEDNSClientSubnetInfo(addressFamily, inetAddress.getAddress(), sourceBitSize, scopeBitSize);

        return ret;
    }


    // 本クラスの内容を指定したネットワーク情報(EDNS Client Subnet)で初期化する.
    // 復帰値はOPT RRのrdata部の占有サイズ.
    public int setEDNSClientSubnetInfo(int addressFamily, byte[] address, int sourceBitSize, int scopeBitSize) throws DNSServiceCommonException
    {
        // ECSの疑似RRコード(8)
        int optionCode = DNSProtocolConstants.EDNS_PSUEDO_RR_TYPE_ECS; 

        // 疑似RRのデータ部の長さ
        // 4byte(FAMILY, SOURCE PREFIX-LENGTH, SCOPE PREFIX-LENGTH) + アドレス部可変長
        int opitonLength = 4 + address.length;

        // 疑似RRのデータ部.
        // データ部の構造は以下の通り.
        // | FAMILY(2byte) | SOURCE PREFIX-LENGTH(1byte) | SCOPE PREFIX-LENGTH(1byte) | ADDRESS(可変長) |
        byte[] data = buildPseudoDataPayload(addressFamily, sourceBitSize, scopeBitSize, address);

        // 基底クラスのフィールドをセット.
        super.setOptionData(optionCode, opitonLength, data);

        // 戻り値は疑似RRのデータ部の長さを返す.
        int ret = data.length;
        return ret;
    }


    // 疑似RRのデータペイロードからIANAアドレスファミリ値を取得する.
    public int getFamily() throws DNSServiceCommonException
    {
        byte[] data = getOptionData();
        int family = (int)dnsProtocolUtils.bytesToUInt16(data,0);

        return family;
    }

    // 疑似RRのデータペイロードからSOURCE PREFIX-LENGTHを取得する.
    public int getSourcePrefixLength() throws DNSServiceCommonException
    {
        byte[] data = getOptionData();
        int sourcePrefixLength = (int)dnsProtocolUtils.bytesToUInt8(data, 2);

        return sourcePrefixLength;
    }


    // 疑似RRのデータペイロードからSCOPE PREFIX-LENGTHを取得する.
    public int getScopePrefixLength() throws DNSServiceCommonException
    {
        byte[] data = getOptionData();
        int scopePrefixLength = (int)dnsProtocolUtils.bytesToUInt8(data, 3);

        return scopePrefixLength;
    }

    // 疑似RRのデータペイロードからアドレスデータを取得する
    public byte[] getAddress() throws DNSServiceCommonException
    {
        int optionLength = getOptionLength();

        if ( optionLength == 0 )
        {
            String msg = String.format("Failed to get ECS Address data, because of Un Initialized optionLength value. opitonLength=%d", optionLength);
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        byte[] data = getOptionData();
        byte[] address = new byte[optionLength - 3];
        
        System.arraycopy(data, 3, data, 0, address.length);

        return address;
    }




    // 疑似RRのデータ部を生成する.
    private byte[] buildPseudoDataPayload(int family, int sourceBitSize, int scopeBitSize, byte[] address) throws DNSServiceCommonException
    {
        // 疑似RRのデータ部の長さ
        // 4byte(FAMILY, SOURCE PREFIX-LENGTH, SCOPE PREFIX-LENGTH) + アドレス部可変長
        int opitonLength = 4 + address.length;


        // 疑似RRのデータ部.
        // データ部の構造は以下の通り.
        // | FAMILY(2byte) | SOURCE PREFIX-LENGTH(1byte) | SCOPE PREFIX-LENGTH(1byte) | ADDRESS(可変長) |
        byte[] data = new byte[opitonLength];

        byte[] familyBytes = dnsProtocolUtils.int16ToBytes(family);
        byte[] sourceBitBytes = dnsProtocolUtils.int8ToBytes(sourceBitSize);
        byte[] scopeBitByte = dnsProtocolUtils.int8ToBytes(scopeBitSize);
        
        int index = 0;
        
        System.arraycopy(familyBytes, 0, data, index, familyBytes.length);
        index += familyBytes.length;

        System.arraycopy(sourceBitBytes, 0, data, index, sourceBitBytes.length);
        index += sourceBitBytes.length;

        System.arraycopy(scopeBitByte, 0, data, index, scopeBitByte.length);
        index += scopeBitByte.length;

        // アドレスデータは指定されたビット数表現に変換(余りはゼロパディング)してから設定する.
        address = truncateAndPaddingZeroForAddress(address, sourceBitSize);

        System.arraycopy(address, 0, data, index, address.length);

        return data;
    }


    // アドレスデータを指定したビット数で切り詰めてから、不要なフィールドを0パディングする.
    // Ex1) ( baseAddress=255.255.255.255, bits=32 ) -> ret=255.255.255.255
    // Ex2) ( baseAddress=255.255.255.255, bits=24 ) -> ret=255.255.255.0
    // Ex3) ( baseAddress=255.255.255.255, bits=31 ) -> ret=255.255.255.254
    // Ex4) ( baseAddress=255.255.255.255, bits=1  ) -> ret=128(0b10000000).0.0.0
    public byte[] truncateAndPaddingZeroForAddress(byte[] baseAddress, int bits) throws DNSServiceCommonException
    {
        byte[] address = new byte[baseAddress.length];

        if ( bits == 0 )
        {
            // マスクビット数が0のためアドレス空間全体をゼロクリアする.
            Arrays.fill(address, (byte)0);
            return address;
        }

        if ( baseAddress.length < ( bits / 8 ) )
        {
            // 指定されたアドレス長はスコープbits空間より小さい.
            String msg = String.format("Failed to truncate Address, because of specified bits size is excess for address bits size. bits=%d, address bits size=%d", bits, (address.length * 8) );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        for( int index = 0; index < baseAddress.length; index++ )
        {
            byte octet = baseAddress[index];

            if ( (index + 1) <= (bits / 8) )
            {
                // スコープbitsの範囲内.
                // オクテット全体をコピーする.
                address[index] = octet;
            }
            else
            {
                // スコープbitsの範囲外.
                // オクテットの先頭から一部のビットのみを残して、その余をゼロクリアする.
                int restBits = ( (index + 1) * 8 ) - bits; // オクテット中の余計なbits(0-8)
                int reservedBits = 8 - restBits;    // オクテット中の残すbits(0-8)

                int mask = ( 0xff << restBits) & 0xff;
                int maskedOctetI32 = ( 0xff & octet) & mask;

                byte newOctet = (byte)(0xff & maskedOctetI32);
                address[index] = newOctet;

                String msg = String.format("restBits=%d, reservedBits=%d, mask=%d, maskedOctetI32=%d, newOctet=%d", restBits, reservedBits, mask ,maskedOctetI32, newOctet);
                LoulanDNSDebugUtils.printDebug(getClass(), "truncateAndPaddingZeroForAddress() : debug", msg);

            }
        }

        // For Debug.
        LoulanDNSDebugUtils.printDebug(getClass(), "truncateAndPaddingZeroForAddress() : bits", "bits=" + bits);
        LoulanDNSDebugUtils.printHexString(getClass(), "truncateAndPaddingZeroForAddress() : baseAddress", baseAddress);
        LoulanDNSDebugUtils.printHexString(getClass(), "truncateAndPaddingZeroForAddress() : address", address);

        return address;
    }



}