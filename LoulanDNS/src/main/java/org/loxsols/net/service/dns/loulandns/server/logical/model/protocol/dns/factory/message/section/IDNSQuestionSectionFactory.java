package org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.section;

import org.loxsols.net.service.dns.loulandns.server.common.*;

import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.*;

public interface IDNSQuestionSectionFactory
{

    public IDNSQuestionSection createQuestionSection(String qname, int qtype, int qclass)  throws DNSServiceCommonException;

    public IDNSQuestionSection createQuestionSection(int qdCount, byte[] questionSectionBytes, byte[] fullDNSMessageBytes)  throws DNSServiceCommonException;

}