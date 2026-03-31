package org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.xbill;


import java.util.*;
import java.util.Arrays;

import javax.management.Query;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;

import org.loxsols.net.service.dns.loulandns.util.*;


import org.loxsols.net.service.dns.loulandns.server.common.*;
import org.loxsols.net.service.dns.loulandns.server.common.constants.DNSProtocolConstants;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;


import org.xbill.DNS.*;


import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.model.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.model.xbill.*;

import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.*;

import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.model.*;



@Getter
@Setter
public class XbillDNSQueryPartImpl implements IDNSQueryPart
{

    String qname;
    int qtype;
    int qclass;

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
    public byte[] getDNSQueryBytes() throws DNSServiceCommonException
    {
        byte[] bytes = createDNSQueryBytesUsingXbill(qname, qtype, qclass);
        return bytes;
    }

    // DNSリソースレコードのbyte配列を元に本クラスの各値を設定する.
    public int setDNSQueryBytes(byte[] queryBytes) throws DNSServiceCommonException
    {

        // 適当なDNSヘッダを付与してからDNSメッセージ電文として解析する.
        IDNSProtocolModelInstanceFactory factory = new DNSProtocolModelInstanceFactoryImpl();
        IDNSHeaderSection header = factory.createDNSQueryHeaderSectionInstance(0, false, false, 1);
        byte[] headerBytes = header.getDNSHeaderBytes();

        ByteBuffer buf = ByteBuffer.allocate( DNSProtocolConstants.SIZE_OF_DNS_HEADER_SECION + queryBytes.length );
        buf.put( headerBytes );
        buf.put( queryBytes );

        byte[] queryMessageBytes = buf.array();
        IDNSQueryPart tmpQuery = toDNSQuery(queryMessageBytes);
        
        this.setDNSQueryName( tmpQuery.getDNSQueryName() );
        this.setDNSQueryType( tmpQuery.getDNSQueryType() );
        this.setQclass( tmpQuery.getDNSQueryClass() );

        int querySize = qname.length() + 1 + 2 + 2;
        return querySize;
        
    }


    public static byte[] createDNSQueryBytesUsingXbill(String name, int qtype, int qclass) throws DNSServiceCommonException
    {
        Name nameObj;
        try
        {
            nameObj = Name.fromString(name);
        }
        catch (org.xbill.DNS.TextParseException cause)
        {
            String msg = String.format("Faield to parse DNS name field. name=%s", name);
            DNSServiceCommonException exception = new DNSServiceCommonException(msg, cause);
            throw exception;
        }

        org.xbill.DNS.Record rec = org.xbill.DNS.Record.newRecord(nameObj, qtype, qclass);
        org.xbill.DNS.Message query = Message.newQuery(rec);

        byte[] bytes = query.toWire();
        return bytes;
    }

    private static IDNSQueryPart toDNSQuery(byte[] bytes) throws DNSServiceCommonException
    {
        Message message;
        try
        {
            message = new Message(bytes);
        }
        catch (IOException cause)
        {
            String msg = String.format("Faield to parse DNS message bytes. bytes.length=%d", bytes.length );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg, cause);
            throw exception;
        }

        IDNSQueryPart query = toDNSQuery(message);
        return query;
    }

    private static IDNSQueryPart toDNSQuery(Message message) throws DNSServiceCommonException
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

    

}