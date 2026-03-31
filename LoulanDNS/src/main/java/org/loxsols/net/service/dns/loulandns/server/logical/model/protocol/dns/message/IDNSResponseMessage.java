package org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message;


import java.util.*;
import java.time.ZonedDateTime;

import org.loxsols.net.service.dns.loulandns.server.common.*;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;


import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.*;

// DNSレスポンスメッセージのインターフェース
public interface IDNSResponseMessage extends IDNSMessage
{

    public void init(String qname, int qtype, int qclass, String rname, int rtype, int rclass, int rTTL, byte[] rdata) throws DNSServiceCommonException;

    public void init(String qname, int qtype, int qclass, IDNSResourceRecord rr) throws DNSServiceCommonException;

    public void init(String qname, int qtype, int qclass, List<IDNSResourceRecord> rrSet) throws DNSServiceCommonException;

    public void init(IDNSHeaderSection headerSection, IDNSQuestionSection questionSection, IDNSAnswerSection answerSection ) throws DNSServiceCommonException;

    public void init(IDNSHeaderSection headerSection, IDNSQuestionSection questionSection, IDNSAnswerSection answerSection, IDNSAuthoritySection authoritySection, IDNSAdditionalSection additionalSection) throws DNSServiceCommonException;

    public void init(byte[] dnsMessageBytes) throws DNSServiceCommonException;

    public void validate() throws DNSServiceCommonException;

    public boolean isSucceedDNSResponse() throws DNSServiceCommonException;



}