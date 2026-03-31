
package org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.section;

import java.util.List;

import org.springframework.beans.factory.annotation.*;

import org.loxsols.net.service.dns.loulandns.server.common.*;

import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.*;

public interface IDNSAnswerSectionFactory
{

    public IDNSAnswerSection createAnswerSection(IDNSResourceRecord[] rrSet)  throws DNSServiceCommonException;
    public IDNSAnswerSection createAnswerSection(List<IDNSResourceRecord> rrSet)  throws DNSServiceCommonException;

    public IDNSAnswerSection createSimpleAnswerSection(String rname, int rtype, int rclass, int rTTL, byte[] rdata) throws DNSServiceCommonException;

}