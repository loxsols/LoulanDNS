package org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part;


import java.util.*;
import java.time.ZonedDateTime;

import org.loxsols.net.service.dns.loulandns.server.common.*;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;


import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.*;


// DNSクエリ部のインターフェース
public interface IDNSQueryPart
{

    public String getDNSQueryName()  throws DNSServiceCommonException;
    public void setDNSQueryName(String qname)  throws DNSServiceCommonException;

    public int getDNSQueryType()  throws DNSServiceCommonException;
    public void setDNSQueryType(int qtype)  throws DNSServiceCommonException;

    public int getDNSQueryClass()  throws DNSServiceCommonException;
    public void setDNSQueryClass(int qclass)  throws DNSServiceCommonException;


    // DNSクエリのbyte配列を返す.
    public byte[] getDNSQueryBytes() throws DNSServiceCommonException;

    // DNSクエリのbyte配列を元に本クラスの各値を設定する.
    public int setDNSQueryBytes(byte[] queryBytes) throws DNSServiceCommonException;


}