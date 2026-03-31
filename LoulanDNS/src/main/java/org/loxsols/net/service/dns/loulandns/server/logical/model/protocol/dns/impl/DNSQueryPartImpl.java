package org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl;


import java.util.*;
import java.util.Arrays;

import javax.management.Query;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;

import org.loxsols.net.service.dns.loulandns.util.*;


import org.loxsols.net.service.dns.loulandns.server.common.*;
import org.loxsols.net.service.dns.loulandns.server.common.constants.DNSProtocolConstants;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSProtocolUtils;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSDebugUtils;



import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.*;



import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.xbill.*;


@Getter
@Setter
public class DNSQueryPartImpl implements IDNSQueryPart
{

    String qname;
    int qtype;
    int qclass;


    LoulanDNSProtocolUtils protocolUtils = new LoulanDNSProtocolUtils();


    public String getDNSQueryName()  throws DNSServiceCommonException
    {
        return qname;
    }

    public void setDNSQueryName(String qname)  throws DNSServiceCommonException
    {
        this.qname = qname;
    }


    public int getDNSQueryType()  throws DNSServiceCommonException
    {

        LoulanDNSDebugUtils.printDebug( this.getClass(), "getDNSQueryType() : this.qtype", Integer.toString( qtype ) );


        return qtype;
    }

    public void setDNSQueryType(int qtype)  throws DNSServiceCommonException
    {
        this.qtype = qtype;
    }



    public int getDNSQueryClass()  throws DNSServiceCommonException
    {
        return qclass;
    }

    public void setDNSQueryClass(int qclass)  throws DNSServiceCommonException
    {
        this.qclass = qclass;
    }


    // DNSクエリレコードのbyte配列を返す.
    //  DNSクエリメッセージの構造 : | name(可変長) | type(2byte) | class(2byte) |
    //  typeはレコードタイプを表す.
    //  classは通常はインターネットを表す1が格納される.
    public byte[] getDNSQueryBytes() throws DNSServiceCommonException
    {

        byte[] dnameBytes = protocolUtils.toDomainNameBytes(qname);

        LoulanDNSDebugUtils.printDebug(this.getClass(), "getDNSQueryBytes() : ", String.format("dnameBytes.length=%d, qname=%s", dnameBytes.length, qname ) );
        
        short type_s = (short)this.getDNSQueryType();
        byte[] typeBytes = protocolUtils.shortToBytes(type_s);


        short class_s = (short)this.getDNSQueryClass();
        byte[] classBytes = protocolUtils.shortToBytes(class_s);

        byte[] queryBytes;
        try
        {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            stream.write( dnameBytes );
            stream.write( typeBytes );
            stream.write( classBytes );

            queryBytes = stream.toByteArray();
        }
        catch( IOException cause )
        {
            String msg = String.format("Failed to build DNS Query Bytes. qname=%s", qname);
            DNSServiceCommonException exception = new DNSServiceCommonException(msg, cause);
            throw exception;
        }

        return queryBytes;
    }

    // DNSクエリのbyte配列を元に本クラスの各値を設定する.
    public int setDNSQueryBytes(byte[] queryBytes) throws DNSServiceCommonException
    {

        int index = 0;

        LoulanDNSDebugUtils.printHexString(this.getClass(), "setDNSQueryBytes() : queryBytes", queryBytes );


        // DNSクエリ部のサイズ( 4:dname-len + N:dname(可変長) + 2:QType + 2:QClass) 以上を満たしているかチェックする.
        if ( queryBytes.length < 8 )
        {
            // DNSクエリ部のサイズが規定外.
            String msg = String.format("Insufficient DNS Message. Failed to parse DNS Query Part. Query Part Bytes(=%d) is less than 8 bytes.", queryBytes.length );
            InsufficientDNSMessageException exception = new InsufficientDNSMessageException(msg);
            throw exception;
        }


        byte[] dnameBytes = Arrays.copyOfRange( queryBytes, index, queryBytes.length - 4 );
        int dnameSize = protocolUtils.calcDomainNameByteSize(dnameBytes);


        // DNSクエリ部のドメイン名部分の長さが要件を満たしているかチェックする.
        if ( dnameBytes.length < dnameSize )
        {
            // DNSクエリ部のドメイン名部分の長さが規定外.
            String msg = String.format("Insufficient DNS Message. Failed to parse DNS Query Part. Length of dname bytes(=%d) is less than size of dname(=%d).", dnameBytes.length, dnameSize );
            InsufficientDNSMessageException exception = new InsufficientDNSMessageException(msg);
            throw exception;
        }

        String dname = protocolUtils.parseDomainName(dnameBytes);
        
        this.setDNSQueryName( dname );

        index += dnameSize;
        byte[] typeBytes = Arrays.copyOfRange(queryBytes, index, index + 2);
        int qtype = (int)protocolUtils.bytesToUInt16(typeBytes);
        this.setDNSQueryType(qtype);


        LoulanDNSDebugUtils.printHexString(this.getClass(), "setDNSQueryBytes() : typeBytes", typeBytes);
        LoulanDNSDebugUtils.printDebug( this.getClass(), "setDNSQueryBytes() : qtype(dec)", Integer.toString( qtype ) );
        LoulanDNSDebugUtils.printDebug( this.getClass(), "setDNSQueryBytes() : getDNSQueryType()", Integer.toString(this.getDNSQueryType()) );


        index += 2;
        byte[] classBytes = Arrays.copyOfRange(queryBytes, index , index + 2 );
        int qclass = (int)protocolUtils.bytesToUInt16(classBytes);
        this.setDNSQueryClass(qclass);


        LoulanDNSDebugUtils.printHexString(this.getClass(), "setDNSQueryBytes() : typeBytes", typeBytes);
        LoulanDNSDebugUtils.printDebug( this.getClass(), "setDNSQueryBytes() : qclass(dec)", Integer.toString( qclass ) );
        LoulanDNSDebugUtils.printDebug( this.getClass(), "setDNSQueryBytes() : getDNSClassType()", Integer.toString(this.getDNSQueryClass()) );




        int querySize = index + 2;
        return querySize;
        
    }




    

}