package org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl;


import java.util.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.ZonedDateTime;

import java.nio.ByteBuffer;

import org.springframework.beans.factory.annotation.*;


import org.loxsols.net.service.dns.loulandns.server.common.*;
import org.loxsols.net.service.dns.loulandns.server.common.constants.LoulanDNSConstants;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;


import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.model.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.model.*;


// DNSヘッダーの実装クラス.
public class DNSQuestionSectionImpl implements IDNSQuestionSection
{

    IDNSProtocolModelInstanceFactory dnsProtocolModelInstanceFactory;


    List<IDNSQueryPart> queryList = new ArrayList<IDNSQueryPart>();

    public DNSQuestionSectionImpl(IDNSProtocolModelInstanceFactory dnsProtocolModelInstanceFactory) throws DNSServiceCommonException
    {
        this.dnsProtocolModelInstanceFactory = dnsProtocolModelInstanceFactory;
    }

    public IDNSQueryPart[] getDNSQueries() throws DNSServiceCommonException
    {
        IDNSQueryPart[] dnsQueries = new IDNSQueryPart[queryList.size()];
        
        int i=0;
        for( IDNSQueryPart dnsQuery : queryList )
        {
            dnsQueries[i] = queryList.get(i);
            i++;
        }



        return dnsQueries;
    }

    public void setDNSQueries(IDNSQueryPart[] dnsQueries) throws DNSServiceCommonException
    {
        // 既存の問い合わせ部は全てクリアしてから再設定する.
        queryList.clear();
        queryList.addAll( Arrays.asList( dnsQueries ) );
    }


    public void addDNSQuery( IDNSQueryPart query )  throws DNSServiceCommonException
    {
        queryList.add( query );
    }

    public int getQueryCount() throws DNSServiceCommonException
    {
        int count = queryList.size();
        return count;
    }

    // DNS問い合わせセクションのbyte配列を返す.
    public byte[] getDNSQuestionSectionBytes() throws DNSServiceCommonException
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        for( IDNSQueryPart query : getDNSQueries() )
        {
            byte[] queryBytes = query.getDNSQueryBytes();

            try
            {
                stream.write( queryBytes );
            }
            catch(IOException cause)
            {
                String msg = String.format("Failed to convert DNS Query to bytes. DNSQuery = %s", query.toString() );
                DNSServiceCommonException exception = new DNSServiceCommonException(msg, cause);
                throw exception;
            }
        }

        byte[] questionSectionBytes = stream.toByteArray();
        return questionSectionBytes;
    }

    // DNS問い合わせセクションのbyte配列を元に本クラスの各値を設定する.
    public int setDNSQuestionSectionBytes(int QDCount, byte[] questionSectionBytes) throws DNSServiceCommonException
    {
        int bytesIndex = 0;

        for( int i=0; i < QDCount; i++)
        {
            byte[] queryPartBytes = Arrays.copyOfRange( questionSectionBytes, bytesIndex, questionSectionBytes.length + 1 );

            IDNSQueryPart queryPart = dnsProtocolModelInstanceFactory.createDNSQueryPart();
            queryPart.setDNSQueryBytes( queryPartBytes );

            this.addDNSQuery(queryPart);

            bytesIndex += queryPart.getDNSQueryBytes().length;
        }


        if ( this.getDNSQueries().length < QDCount )
        {
            // DNSクエリ部の個数がヘッダセクションのQDカウントで指定された個数に満たない.
            String msg = String.format("Insufficient DNS Message. Failed to parse DNS Query Section. Query Section Bytes=%d", questionSectionBytes.length );
            InsufficientDNSMessageException exception = new InsufficientDNSMessageException(msg);
            throw exception;
        }


        return bytesIndex;
    }



}