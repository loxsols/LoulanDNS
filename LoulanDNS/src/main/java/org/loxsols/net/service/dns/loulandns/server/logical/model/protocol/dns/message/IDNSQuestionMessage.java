package org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message;


import java.util.*;
import java.time.ZonedDateTime;

import org.loxsols.net.service.dns.loulandns.server.common.*;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;


import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.*;


// DNS問い合わせメッセージのインターフェース
public interface IDNSQuestionMessage extends IDNSMessage
{

    public void init(String qname, int qtype, int qclass) throws DNSServiceCommonException;

    public void init(IDNSHeaderSection headerSection, IDNSQuestionSection questionSection) throws DNSServiceCommonException;

    public void init(IDNSHeaderSection headerSection, IDNSQuestionSection questionSection, IDNSAuthoritySection authoritySection, IDNSAdditionalSection additionalSection) throws DNSServiceCommonException;



}