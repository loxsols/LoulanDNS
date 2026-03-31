package org.loxsols.net.service.dns.loulandns.util;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.net.UnknownHostException;

import javax.naming.NamingEnumeration;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;


import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.ResolverConfig;
import org.xbill.DNS.Message;
import org.xbill.DNS.Type;
import org.xbill.DNS.DClass;

import org.loxsols.net.service.dns.loulandns.server.common.*;
import org.loxsols.net.service.dns.loulandns.server.common.constants.DNSProtocolConstants;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.IDNSResourceRecord;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.rr.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.rr.edns.*;


// DNS処理のためのユーティリティクラス.
public class DNSUtils
{

    public byte[] toWireDNSQuery(String domainName) throws DNSServiceCommonException
    {
        Name name;
        
        try
        {
            name = Name.fromString(domainName, Name.root);
        }
        catch(org.xbill.DNS.TextParseException cause)
        {
            String msg = String.format("Failed to parse DNS Query.");
            DNSServiceCommonException exception = new MalformedDNSRequestException(msg, cause);
            throw exception;
        }

        int type = Type.A;
        int dclass = DClass.IN;

        Record rec = Record.newRecord(name, type, dclass);
        Message query = Message.newQuery(rec);

        byte[] queryBytes = query.toWire();

        return queryBytes;
    }


    // DNSリソースレコードのnameフィールドのようにNULL終端するASCII文字列を取得する.
    public String asciiBytesToString(byte[] bytes)
    {
        String str = new String(bytes, StandardCharsets.UTF_8);
        return str;
    }


    // ホストOSが参照するDNSサーバーのアドレス及びポート番号のリストを返却する.
    // xbill使用版.
    public List<InetSocketAddress> getSystemDNSServerAddressList() throws DNSServiceCommonException
    {
        List<InetSocketAddress> serverList =  org.xbill.DNS.ResolverConfig.getCurrentConfig().servers();
        return serverList;
    }


    // 指定したDNSリソースレコードがEDNS0のOPTレコードかを判定する.
    public boolean isEDNS0OptRR(IDNSResourceRecord rr) throws DNSServiceCommonException
    {
        if ( rr.getResourceType() != DNSProtocolConstants.DNS_RR_TYPE_OPT )
        {
            return false;
        }

        DNSResourceRecordTypeOPTForEDNS0Impl edns0OptRR = null;
        try
        {
            // EDNS0のリソースレコードとしてインスタンス化して問題があればfalseで復帰させる.
            edns0OptRR = new DNSResourceRecordTypeOPTForEDNS0Impl(rr);
        }
        catch(DNSServiceCommonException cause)
        {
            return false;
        }


        // ----- 以下はEDNS0 (RFC6891)に特化した細かいチェック.

        // dnameは0(".")になるはず.
        String dname = edns0OptRR.getDNSResourceName();
        if ( dname.equals(".") == false)
        {
            // RFC6891 pp.7より、EDNS0のNAMEフィールドは0(root domain)であると定義されている.
            return false;
        }

        // EDNS Versionは0になるはず.
        if ( edns0OptRR.getEDNS0Version() != 0 )
        {
            return false;
        }

        return true;

    }



    // 指定したバイトデータをIPアドレスとして解析して文字列(Ex."1.1.1.1")として返す.
    public String toIPAddressString(byte[] ipv4AddrBytes) throws DNSServiceCommonException
    {
        InetAddress inetAddr;
        
        try
        {
            inetAddr = InetAddress.getByAddress(ipv4AddrBytes);
        }
        catch(UnknownHostException cause)
        {
            String msg = "Failed to build InetAddress Object from IP addr bytes.";
            DNSServiceCommonException exception = new DNSServiceCommonException(msg, cause);
            throw exception;
        }
        
        // InetAddress.toString()メソッドは"<ホスト名>/<IPアドレス>"の文字列を返す.
        String hostIPString = toIPAddressString(inetAddr);

        return hostIPString;
    }


    public String toIPAddressString(InetAddress inetAddress) throws DNSServiceCommonException
    {
        // InetAddress.toString()メソッドは"<ホスト名>/<IPアドレス>"の文字列を返す.
        String hostIPString = inetAddress.toString();

        String[] array = hostIPString.split("/");

        if ( array.length != 2 )
        {
            // "<ホスト名>/<IPアドレス>"形式の文字列になっていない.
            String msg = String.format( "Failed to convert InetAddress to IP address String. InetAddress=%s", inetAddress.toString() );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        String ipString = array[1];

        return ipString;
    }


    




}