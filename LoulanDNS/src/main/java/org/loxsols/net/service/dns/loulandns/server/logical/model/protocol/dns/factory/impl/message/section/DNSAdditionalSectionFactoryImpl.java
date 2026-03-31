package org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.section;

import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.model.IDNSProtocolModelInstanceFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.section.*;

import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;

import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.section.*;

import java.util.List;

import org.loxsols.net.service.dns.loulandns.server.common.*;
import org.loxsols.net.service.dns.loulandns.server.common.util.*;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class DNSAdditionalSectionFactoryImpl implements IDNSAdditionalSectionFactory {


    LoulanDNSProtocolUtils dnsProtocolUtils = new LoulanDNSProtocolUtils();

    public IDNSAdditionalSection createAdditionalSection(IDNSResourceRecord[] rrSet) throws DNSServiceCommonException
    {
        IDNSAdditionalSection additionalSection = new DNSAdditionalSectionImpl();
        additionalSection.setDNSResourceRecords(rrSet);

        return additionalSection;
    }

    public IDNSAdditionalSection createAdditionalSection(List<IDNSResourceRecord> rrSet) throws DNSServiceCommonException
    {
        IDNSResourceRecord[] rrSetArray = new IDNSResourceRecord[rrSet.size()];

        int index = 0;
        for( int i=0; i < rrSet.size(); i++)
        {
            IDNSResourceRecord rr = rrSet.get(i);
            rrSetArray[i] = rr;
        }

        IDNSAdditionalSection additionalSection = createAdditionalSection(rrSetArray);
        return additionalSection;
    }

    public IDNSAdditionalSection createSimpleAdditionalSection(String rname, int rtype, int rclass, int rTTL, byte[] rdata) throws DNSServiceCommonException
    {
        IDNSResourceRecord rr = new DNSResourceRecordImpl();

        rr.setDNSResourceName( rname );
        rr.setResourceType( rtype );
        rr.setResourceClass( rclass );
        rr.setResourceTTL( rTTL );
        rr.setResourceRDLength( rdata.length );
        rr.setResourceRData( rdata );

        IDNSResourceRecord[] rrSet = new IDNSResourceRecord[]{rr};
        IDNSAdditionalSection additionalSection = createAdditionalSection(rrSet);

        return additionalSection;
    }

}