package org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section;


import java.util.*;
import java.time.ZonedDateTime;

import org.loxsols.net.service.dns.loulandns.server.common.*;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;


import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.*;


// DNS回答セクションのインターフェース
public interface IDNSAnswerSection
{

    public IDNSResourceRecord[] getDNSResourceRecords() throws DNSServiceCommonException;
    public void setDNSResourceRecords(IDNSResourceRecord[] dnsResourceRecords) throws DNSServiceCommonException;

    public void addDNSResourceRecord(IDNSResourceRecord rr) throws DNSServiceCommonException;


    public int getDNSRRCount() throws DNSServiceCommonException;

    // DNSセクションのbyte配列を返す.
    public byte[] getDNSRRSetBytes() throws DNSServiceCommonException;

    // DNSセクションのbyte配列を元に本クラスの各値を設定する.
    // 第3引数のfullDNSMessageBytesはオプション. nullを指定した場合は、ドメイン名圧縮されたリソースレコードは解析できない.
    public int setDNSRRSetBytes(int count, byte[] rrSetBytes, byte[] fullDNSMessageBytes)throws DNSServiceCommonException;


    // DNSセクションのbyte配列を元に本クラスの各値を設定する.
    // ドメイン名圧縮されたリソースレコードは解析できない.
    public int setDNSRRSetBytes(int count, byte[] rrSetBytes )throws DNSServiceCommonException;


    // 指定したタイプのリソースレコードの一覧を返す.
    public IDNSResourceRecord[] selectDNSResourceRecords(int rrType) throws DNSServiceCommonException;


}