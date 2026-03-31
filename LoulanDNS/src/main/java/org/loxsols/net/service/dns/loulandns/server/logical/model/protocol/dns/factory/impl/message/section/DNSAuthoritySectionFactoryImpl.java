package org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.section;

import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.model.IDNSProtocolModelInstanceFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.section.*;

import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;

import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.*;

import java.util.List;

import org.loxsols.net.service.dns.loulandns.server.common.*;
import org.loxsols.net.service.dns.loulandns.server.common.util.*;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class DNSAuthoritySectionFactoryImpl implements IDNSAuthoritySectionFactory
{


    LoulanDNSProtocolUtils dnsProtocolUtils = new LoulanDNSProtocolUtils();

    public IDNSAuthoritySection createAuthoritySection(IDNSResourceRecord[] rrSet) throws DNSServiceCommonException
    {
        IDNSAuthoritySection authoritySection = new DNSAuthoritySectionImpl();
        authoritySection.setDNSResourceRecords(rrSet);

        return authoritySection;
    }

    public IDNSAuthoritySection createAuthoritySection(List<IDNSResourceRecord> rrSet) throws DNSServiceCommonException
    {
        IDNSResourceRecord[] rrSetArray = new IDNSResourceRecord[rrSet.size()];

        int index = 0;
        for( int i=0; i < rrSet.size(); i++)
        {
            IDNSResourceRecord rr = rrSet.get(i);
            rrSetArray[i] = rr;
        }

        IDNSAuthoritySection authoritySection = createAuthoritySection(rrSetArray);
        return authoritySection;
    }

    public IDNSAuthoritySection createSimpleAuthoritySection(String rname, int rtype, int rclass, int rTTL, byte[] rdata) throws DNSServiceCommonException
    {
        IDNSResourceRecord rr = new DNSResourceRecordImpl();

        rr.setDNSResourceName( rname );
        rr.setResourceType( rtype );
        rr.setResourceClass( rclass );
        rr.setResourceTTL( rTTL );
        rr.setResourceRDLength( rdata.length );
        rr.setResourceRData( rdata );

        IDNSResourceRecord[] rrSet = new IDNSResourceRecord[]{rr};
        IDNSAuthoritySection authoritySection = createAuthoritySection(rrSet);

        return authoritySection;
    }

}