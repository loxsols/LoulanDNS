
package org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.model.xbill;

import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.xbill.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.*;

import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.model.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.model.*;

import org.loxsols.net.service.dns.loulandns.server.common.*;


public class XbillDNSProtocolModelInstanceFactoryImpl extends SimpleDNSProtocolModelInstanceFactoryImpl implements IDNSProtocolModelInstanceFactory
{

    public XbillDNSProtocolModelInstanceFactoryImpl() throws DNSServiceCommonException
    {
        super();
    }


    public IDNSMessage createDNSMesssageInstance()  throws DNSServiceCommonException
    {
        IDNSMessage dnsMessageInstance = new XbillDNSMessageImpl( this );
        return dnsMessageInstance;
    }

    public IDNSQueryPart createDNSQueryPart()  throws DNSServiceCommonException
    {
        IDNSQueryPart queryPart = new XbillDNSQueryPartImpl();
        return queryPart;
    }



}