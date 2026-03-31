package org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section;


import java.util.*;
import java.time.ZonedDateTime;

import org.loxsols.net.service.dns.loulandns.server.common.*;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;


import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.*;



// DNS問い合わせセクションのインターフェース
public interface IDNSQuestionSection
{

    public IDNSQueryPart[] getDNSQueries() throws DNSServiceCommonException;
    public void setDNSQueries(IDNSQueryPart[] dnsQueries) throws DNSServiceCommonException;

    public int getQueryCount() throws DNSServiceCommonException;

    // DNS問い合わせセクションのbyte配列を返す.
    public byte[] getDNSQuestionSectionBytes() throws DNSServiceCommonException;

    // DNS問い合わせセクションのbyte配列を元に本クラスの各値を設定する.
    public int setDNSQuestionSectionBytes(int QDCount, byte[] questionSectionBytes) throws DNSServiceCommonException;


}