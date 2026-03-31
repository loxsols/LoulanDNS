
package org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.section.rr;

import java.util.List;

import org.loxsols.net.service.dns.loulandns.server.common.*;

import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.*;

public interface IDNSResourceRecordFactory
{
    public IDNSResourceRecord createResourceRecord(String rname, int rtype, int rclass, int rTTL, byte[] rdata)  throws DNSServiceCommonException;

    // EDNS(RFC2671)のOPT-RRを生成する.
    public IDNSResourceRecord createEDNSResourceRecord(int ednsPayloadSize, int ednsExtendedRCode, int ednsVersion, int ednsReservedZ, List<IDNSRROptPseudoRRData> pseudoRRSet) throws DNSServiceCommonException;

}