package org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message;


import java.util.*;
import java.time.ZonedDateTime;

import org.loxsols.net.service.dns.loulandns.server.common.*;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.rr.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.rr.edns.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.*;


// DNSメッセージのインターフェース
public interface IDNSMessage
{


    public IDNSHeaderSection getDNSHeaderSection() throws DNSServiceCommonException;
    public void setDNSHeaderSection(IDNSHeaderSection dnsHeader) throws DNSServiceCommonException;


    public IDNSQuestionSection getDNSQuestionSection() throws DNSServiceCommonException;
    public void setDNSQuestionSection(IDNSQuestionSection dnsQuestionSection) throws DNSServiceCommonException;


    public IDNSAnswerSection getDNSAnswerSection() throws DNSServiceCommonException;
    public void setDNSAnswerSection(IDNSAnswerSection dnsAnswerSection) throws DNSServiceCommonException;


    public IDNSAuthoritySection getDNSAuthoritySection() throws DNSServiceCommonException;
    public void setDNSAuthoritySection(IDNSAuthoritySection dnsAuthoritySection) throws DNSServiceCommonException;


    public IDNSAdditionalSection getDNSAdditionalSection() throws DNSServiceCommonException;
    public void setDNSAdditionalSection(IDNSAdditionalSection dnsAdditionalSection) throws DNSServiceCommonException;



    // DNSメッセージのbyte配列を返す.
    public byte[] getDNSMessageBytes() throws DNSServiceCommonException;

    // DNSメッセージのbyte配列を元に本クラスの各値を設定する.
    public void setDNSMessageBytes(byte[] messageBytes ) throws DNSServiceCommonException;


    // このDNSメッセージがEDNS(0)[RFC6891]で拡張されている場合はtrueを返す.
    public boolean isEDNS0Message() throws DNSServiceCommonException; 

    // このDNSメッセージがEDNS(0)[RFC6891]で拡張されている場合、EDNS0拡張情報のOPTリソースレコードを返す.
    public DNSResourceRecordTypeOPTForEDNS0Impl getEDNS0OPTResourceRecord() throws DNSServiceCommonException;

}