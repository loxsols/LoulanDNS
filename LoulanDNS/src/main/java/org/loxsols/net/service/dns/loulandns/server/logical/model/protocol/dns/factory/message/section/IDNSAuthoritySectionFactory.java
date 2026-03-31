
package org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.section;

import java.util.List;

import org.springframework.beans.factory.annotation.*;

import org.loxsols.net.service.dns.loulandns.server.common.*;

import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.*;

public interface IDNSAuthoritySectionFactory
{

    public IDNSAuthoritySection createAuthoritySection(IDNSResourceRecord[] rrSet)  throws DNSServiceCommonException;
    public IDNSAuthoritySection createAuthoritySection(List<IDNSResourceRecord> rrSet)  throws DNSServiceCommonException;

    public IDNSAuthoritySection createSimpleAuthoritySection(String rname, int rtype, int rclass, int rTTL, byte[] rdata) throws DNSServiceCommonException;

}