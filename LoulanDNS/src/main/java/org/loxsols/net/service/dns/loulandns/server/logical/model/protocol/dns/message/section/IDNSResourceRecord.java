package org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section;


import java.util.*;
import java.time.ZonedDateTime;

import org.loxsols.net.service.dns.loulandns.server.common.*;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;


import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.*;


public interface IDNSResourceRecord
{

    public String getDNSResourceName()  throws DNSServiceCommonException;
    public void setDNSResourceName(String rname)  throws DNSServiceCommonException;

    public int getResourceType()  throws DNSServiceCommonException;
    public void setResourceType(int rtype)  throws DNSServiceCommonException;

    public int getResourceClass()  throws DNSServiceCommonException;
    public void setResourceClass(int rclass)  throws DNSServiceCommonException;

    public int getResourceTTL()  throws DNSServiceCommonException;
    public void setResourceTTL(int rTTL)  throws DNSServiceCommonException;

    public int getResourceRDLength()  throws DNSServiceCommonException;
    public void setResourceRDLength(int rdlength)  throws DNSServiceCommonException;

    public byte[] getResourceRData()  throws DNSServiceCommonException;
    public void setResourceRData(byte[] rdata)  throws DNSServiceCommonException;



    // DNSリソースレコードのbyte配列を返す.
    public byte[] getDNSResourceRecordBytes() throws DNSServiceCommonException;

    // DNSリソースレコードのbyte配列を元に本クラスの各値を設定する.
    public int setDNSResourceRecordBytes(byte[] rrBytes) throws DNSServiceCommonException;
    

    // オリジナルのDNSリソースレコードのサイズを返す.
    // (リソースレコード内でドメイン名圧縮が使用されている場合は、オリジナルのRRのサイズと、再エンコードしたサイズが異なる.)
    public int getOriginalRRBytesSize() throws DNSServiceCommonException;

    // オリジナルのDNSリソースレコードのbyte配列を返す.
    // (リソースレコード内でドメイン名圧縮が使用されている場合は、オリジナルのRRのbyte配列と、再エンコードしたbyte配列が異なる.)
    public byte[] getOriginalRRBytes() throws DNSServiceCommonException;

    

    // DNSリソースレコードのデータ部(rdata)の文字列表現を返す.
    // 例) Aレコードの場合はIPv4アドレスの文字列.
    public String getResourceRDataString() throws DNSServiceCommonException;

}