package org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl;


import java.util.*;
import java.io.ByteArrayOutputStream;
import java.time.ZonedDateTime;

import java.nio.ByteBuffer;

import org.loxsols.net.service.dns.loulandns.server.common.*;
import org.loxsols.net.service.dns.loulandns.server.common.constants.DNSProtocolConstants;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSProtocolUtils;


import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.*;

import android.R.layout;


// DNS回答セクションの実装クラス.
public class DNSAnswerSectionImpl implements IDNSAnswerSection
{

    List<IDNSResourceRecord> rrSet = new ArrayList<IDNSResourceRecord>();

    LoulanDNSProtocolUtils protocolUtils = new LoulanDNSProtocolUtils();

    public IDNSResourceRecord[] getDNSResourceRecords() throws DNSServiceCommonException
    {
        IDNSResourceRecord[] rrSetArray = new IDNSResourceRecord[ getDNSRRCount() ];
        for( int i=0; i < rrSetArray.length; i++ )
        {
            rrSetArray[ i ] = rrSet.get(i);
        }

        return rrSetArray;
    }

    public void setDNSResourceRecords(IDNSResourceRecord[] dnsResourceRecords) throws DNSServiceCommonException
    {
        rrSet.clear();

        for( int i=0; i<dnsResourceRecords.length; i++)
        {
            rrSet.add( dnsResourceRecords[i] );
        }
    }


    public void addDNSResourceRecord(IDNSResourceRecord rr) throws DNSServiceCommonException
    {
        rrSet.add( rr );    
    }





    public int getDNSRRCount() throws DNSServiceCommonException
    {
        int count = rrSet.size();
        return count;
    }


    // DNSセクションのbyte配列を返す.
    public byte[] getDNSRRSetBytes() throws DNSServiceCommonException
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        for( IDNSResourceRecord rr : rrSet )
        {
            byte[] rrBytes = rr.getDNSResourceRecordBytes();

            try
            {
                bos.write( rrBytes);
            }
            catch(java.io.IOException cause)
            {
                String msg = String.format("Failed to build DNS answer section bytes." );
                DNSServiceCommonException exception = new DNSServiceCommonException(msg, cause);
                throw exception;
            }
        }

        byte[] rrSetBytes = bos.toByteArray();
        return rrSetBytes;
    }


    // DNSリソースレコードのbyte配列を本クラスに設定する.
    // 第3引数のfullDNSMessageBytesはオプション
    public int setDNSRRSetBytes(int count, byte[] rrSetBytes, byte[] fullDNSMessageBytes)throws DNSServiceCommonException
    {
        
        int bufIndex = 0;
        for( int i=0; i < count; i++ )
        {
            int copySize = DNSProtocolConstants.MAX_RESOURCE_RECORD_SIZE;
            if ( copySize > ( rrSetBytes.length - bufIndex ) )
            {
                copySize = rrSetBytes.length - bufIndex;
            }

            byte[] rrBytes = Arrays.copyOfRange(rrSetBytes, bufIndex, bufIndex + copySize );


            IDNSResourceRecord rr = new DNSResourceRecordImpl();
            int rrBytesSize = rr.setDNSResourceRecordBytes(rrBytes);

            // ドメイン名圧縮が適用されているかを判定.
            if ( protocolUtils.isCompressedDomainName( rrBytes ) == true )
            {
                // ドメイン名圧縮が適用されている場合は、DNSメッセージ先頭からのオフセット位置の文字列を使用する必要がある.
                if ( fullDNSMessageBytes == null )
                {
                    // ドメイン名圧縮の場合はDNSメッセージ全体が必要になる.
                    String msg = String.format("Compressed DomainName bytes is specified without FULL DNS Message bytes. count=%d, rrSetBytes.length=%d", count, rrSetBytes.length );
                    DNSServiceCommonException exception = new DNSServiceCommonException(msg);
                    throw exception;
                }

                int offset = protocolUtils.calcCompressedDomainNameOffset( rrBytes );

                // DNSメッセージ全体の先頭からのオフセット位置にあるDNS名を解析する.
                // TODO : ドメイン名圧縮の多段指定には対応していない.
                byte[] prevRRBytes = Arrays.copyOfRange(fullDNSMessageBytes, offset, fullDNSMessageBytes.length - 1 );
                
                if ( protocolUtils.isCompressedDomainName( prevRRBytes) == true )
                {
                    // TODO : ドメイン名圧縮の多段指定には対応していない. とりあえず例外をスローする.
                    String msg = String.format("Not supported for multi steps of Compressed DomainName.");
                    DNSServiceCommonException exception = new DNSServiceCommonException(msg);
                    throw exception;
                }

                // ドメイン名圧縮されている場合のみここでdnameを設定する.
                String dname = protocolUtils.parseDomainName( prevRRBytes );
                rr.setDNSResourceName( dname );
            }
        
            this.rrSet.add( rr );
            bufIndex += rrBytesSize;
        }

        return bufIndex;
    }



    // DNSセクションのbyte配列を元に本クラスの各値を設定する.
    // ドメイン名圧縮されたリソースレコードは解析できない.
    public int setDNSRRSetBytes(int count, byte[] rrSetBytes)throws DNSServiceCommonException
    {
        int size = setDNSRRSetBytes(count, rrSetBytes, null);
        return size;
    }


    // 指定したタイプのリソースレコードの一覧を返す.
    public IDNSResourceRecord[] selectDNSResourceRecords(int rrType) throws DNSServiceCommonException
    {
        List<IDNSResourceRecord> selectedRRSet = new ArrayList<IDNSResourceRecord>();
        
        IDNSResourceRecord[] rrSet = getDNSResourceRecords();
        for( IDNSResourceRecord rr : rrSet )
        {
            if( rr.getResourceType() == rrType )
            {
                selectedRRSet.add( rr );
            }
        }

        IDNSResourceRecord[] selectedRRSetArray = new IDNSResourceRecord[ selectedRRSet.size() ];

        for( int i=0; i < selectedRRSetArray.length; i++ )
        {
            selectedRRSetArray[i] = selectedRRSet.get(i);
            i++;
        }

        return selectedRRSetArray;
    }


}