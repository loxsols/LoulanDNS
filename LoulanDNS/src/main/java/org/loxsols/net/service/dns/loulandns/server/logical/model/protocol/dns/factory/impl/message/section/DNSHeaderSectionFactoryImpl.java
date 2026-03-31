package org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.section;


import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.section.*;

import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.section.*;

import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;

import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.*;

import org.loxsols.net.service.dns.loulandns.server.common.*;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSProtocolUtils;

import org.loxsols.net.service.dns.loulandns.server.common.util.*;

public class DNSHeaderSectionFactoryImpl implements IDNSHeaderSectionFactory
{

    LoulanDNSProtocolUtils dnsProtocolUtils = new LoulanDNSProtocolUtils();

    public IDNSHeaderSection createDNSQueryHeaderSection(int id, boolean tc, boolean rd, int qd)  throws DNSServiceCommonException
    {

        IDNSHeaderSection dnsHeaderSection = new DNSHeaderSectionImpl();
        dnsHeaderSection.setID(id);
        dnsHeaderSection.setTC( dnsProtocolUtils.booleanToInt(tc) );
        dnsHeaderSection.setRD( dnsProtocolUtils.booleanToInt(rd) );
        dnsHeaderSection.setQDCOUNT( qd );

        return dnsHeaderSection;
    }




}