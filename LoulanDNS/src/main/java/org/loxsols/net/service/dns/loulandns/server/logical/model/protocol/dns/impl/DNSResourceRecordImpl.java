package org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl;


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


import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.*;



@Getter
@Setter
public class DNSResourceRecordImpl implements IDNSResourceRecord
{
    public String rname;
    public int rtype;
    public int rclass;
    public int rTTL;
    public int rdlength;
    public byte[] rdata;

    private byte[] originaryRRBytes;


    LoulanDNSProtocolUtils protocolUtils = new LoulanDNSProtocolUtils();


    public String getDNSResourceName() throws DNSServiceCommonException
    {
        return rname;
    }

    public void setDNSResourceName(String rname) throws DNSServiceCommonException
    {
        this.rname = rname;
    }



    public int getResourceType() throws DNSServiceCommonException
    {
        return rtype;
    }

    public void setResourceType(int rtype) throws DNSServiceCommonException
    {
        this.rtype = rtype;
    }



    public int getResourceClass() throws DNSServiceCommonException
    {
        return rclass;
    }

    public void setResourceClass(int rclass) throws DNSServiceCommonException
    {
        this.rclass = rclass;
    }



    public int getResourceTTL() throws DNSServiceCommonException
    {
        return rTTL;
    }

    public void setResourceTTL(int rTTL) throws DNSServiceCommonException
    {
        this.rTTL = rTTL;
    }



    public int getResourceRDLength() throws DNSServiceCommonException
    {
        return rdlength;
    }

    public void setResourceRDLength(int rdlength) throws DNSServiceCommonException
    {
        this.rdlength = rdlength;
    }



    public byte[] getResourceRData() throws DNSServiceCommonException
    {
        return rdata;
    }

    public void setResourceRData(byte[] rdata) throws DNSServiceCommonException
    {
        this.rdata = rdata;
    }


    // DNSリソースレコードのbyte配列を返す.
    public byte[] getDNSResourceRecordBytes() throws DNSServiceCommonException
    {

        byte[] rnameBytes = protocolUtils.toDomainNameBytes(rname);
        byte[] rtypeBytes = protocolUtils.shortToBytes( (short)rtype);
        byte[] rclassBytes = protocolUtils.shortToBytes( (short)rclass);
        byte[] ttlBytes = protocolUtils.intToBytes(rTTL);
        byte[] rdlengthBytes = protocolUtils.shortToBytes( (short)rdlength);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try
        {
            stream.write( rnameBytes );
            stream.write( rtypeBytes );
            stream.write( rclassBytes );
            stream.write( ttlBytes );
            stream.write( rdlengthBytes );
            stream.write( rdata );
        }
        catch( IOException cause )
        {
            String msg = String.format("Failed to build DNS Resrouce Record bytes. rname=%s", rname);
            DNSServiceCommonException exception = new DNSServiceCommonException(msg, cause);
            throw exception;
        }

        byte[] rrBytes = stream.toByteArray();
        return rrBytes;
    }

    // DNSリソースレコードのbyte配列を元に本クラスの各値を設定する.
    public int setDNSResourceRecordBytes(byte[] rrBytes) throws DNSServiceCommonException
    {

        String name;
        int type, clazz, ttl, rdlength;
        byte[] rdata;

        int sizeOfRRBytes;
        try
        {
            int index = 0;

            byte[] nameBytes = Arrays.copyOfRange(rrBytes, index, rrBytes.length );
            int dnameSize = protocolUtils.calcDomainNameByteSize(nameBytes);

            if ( protocolUtils.isCompressedDomainName(nameBytes) == true )
            {
                // ドメイン名圧縮されているため本メソッド内ではdnameを解析することができない.
                // nameをnullに設定して進める.
                name = null;
            }
            else
            {
                name = protocolUtils.parseDomainName(nameBytes);
            }

            index = dnameSize;
        
            byte[] typeBytes = Arrays.copyOfRange(rrBytes, index, index + 2 );
            type = (int)protocolUtils.bytesToUInt16(typeBytes);
            index += 2;

            byte[] clazzBytes = Arrays.copyOfRange(rrBytes, index, index + 2 );
            clazz = (int)protocolUtils.bytesToUInt16(clazzBytes);
            index += 2;

            byte[] ttlBytes = Arrays.copyOfRange(rrBytes, index, index + 4 );
            ttl = (int)protocolUtils.bytesToUInt32(ttlBytes);
            index += 4;

            byte[] rdlengthBytes = Arrays.copyOfRange(rrBytes, index, index + 2 );
            rdlength = (int)protocolUtils.bytesToUInt16(rdlengthBytes);
            index += 2;

            rdata = Arrays.copyOfRange(rrBytes, index, index + rdlength);

            sizeOfRRBytes = index + rdlength;
        }
        catch( ArrayIndexOutOfBoundsException cause)
        {
            String msg = String.format("Faield to parse DNS Resource Recrod. Insufficient rrBytes(=%d).", rrBytes.length);
            InsufficientDNSMessageException exception = new InsufficientDNSMessageException(msg, cause);
            throw exception;           
        }
        


        if ( name != null )
        {
            if ( name.length() > DNSProtocolConstants.MAX_DOMAIN_NAME_LENGTH )
            {
                String msg = "Excess max length of DNS ResourceRecord Name field.";
                DNSServiceCommonException exception = new DNSServiceCommonException(msg);
                throw exception;
            }
        }
        

        this.setDNSResourceName( name );
        this.setResourceType( type );
        this.setResourceClass( clazz );
        this.setResourceTTL( ttl );
        this.setResourceRDLength( rdlength );
        this.setResourceRData( rdata );


        this.originaryRRBytes = Arrays.copyOfRange(rrBytes, 0, sizeOfRRBytes );


        // For DEBUG.
        String csvRecord = getCSVRecord();
        System.out.println("[DEBUG] DNSResourceRescord.setDNSResourceRecordBytes() : DNS RR = " + csvRecord );

        return sizeOfRRBytes;
    }



    public String getCSVRecord() throws DNSServiceCommonException
    {
        String record = String.format("%s, %d, %d, %d, %d, %d", rname, rtype, rclass, rTTL, rdlength, rdata.length );
        return record;
    }


    // オリジナルのDNSリソースレコードのサイズを返す.
    // (リソースレコード内でドメイン名圧縮が使用されている場合は、オリジナルのRRのサイズと、再エンコードしたサイズが異なる.)
    public int getOriginalRRBytesSize() throws DNSServiceCommonException
    {
        byte[] originaryRRBytes = getOriginalRRBytes();
        int size = originaryRRBytes.length;
        return size;
    }

    // オリジナルのDNSリソースレコードのbyte配列を返す.
    // (リソースレコード内でドメイン名圧縮が使用されている場合は、オリジナルのRRのbyte配列と、再エンコードしたbyte配列が異なる.)
    public byte[] getOriginalRRBytes() throws DNSServiceCommonException
    {
        if ( this.originaryRRBytes == null )
        {
            String msg = String.format("Original RR Bytes is not set yet.");
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        return this.originaryRRBytes;
    }

    // DNSリソースレコードのデータ部(rdata)の文字列表現を返す.
    // 例) Aレコードの場合はIPv4アドレスの文字列.
    public String getResourceRDataString() throws DNSServiceCommonException
    {
        String rdataText = protocolUtils.toDNSRRDataString( getResourceType(), getResourceRData());
        return rdataText;
    }




}