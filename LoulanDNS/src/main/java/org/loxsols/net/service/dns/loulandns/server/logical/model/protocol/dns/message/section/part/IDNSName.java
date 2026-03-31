package org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part;


import java.util.*;
import java.time.ZonedDateTime;

import org.loxsols.net.service.dns.loulandns.server.common.*;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;


import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.*;


// DNSのnameデータのモデルクラス.
public interface IDNSName
{

    public String getName()  throws DNSServiceCommonException;
    public void setName(String name)  throws DNSServiceCommonException;
    public void setName(byte[] dnsNameBytes) throws DNSServiceCommonException;

    public int getByteSize()  throws DNSServiceCommonException;
    public void setByteSize(int size)  throws DNSServiceCommonException;


}