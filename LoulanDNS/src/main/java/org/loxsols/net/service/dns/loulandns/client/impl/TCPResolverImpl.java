package org.loxsols.net.service.dns.loulandns.client.impl;


import java.net.InetAddress;
import java.util.HashMap;

import org.loxsols.net.service.dns.loulandns.client.*;
import org.loxsols.net.service.dns.loulandns.client.impl.simple.*;
import org.loxsols.net.service.dns.loulandns.client.common.DNSClientCommonException;
import org.loxsols.net.service.dns.loulandns.client.common.LoulanDNSClientConstants;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.model.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSQuestionMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSResponseMessage;
import org.loxsols.net.service.dns.loulandns.server.common.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;


@ComponentScan
public class TCPResolverImpl extends SimpleTCPResolverImpl implements IDNSLookupClient
{

    public TCPResolverImpl() throws DNSServiceCommonException
    {
        super();
    }


    @Autowired
    @Qualifier("dnsMessageFactoryImpl")
    public void setDNSMessageFacotry(IDNSMessageFactory instance)
    {
        super.setDNSMessageFacotry(instance);
    }


    @Autowired
    @Qualifier("dnsMessageTCPTransporterImpl")
    public void setDNSMessageTCPTransporter(IDNSMessageTransporter instance) throws DNSServiceCommonException
    {

        if ( instance instanceof SimpleTCPMessageTransporterImpl )
        {
            // 指定されたメッセージトランスポーターはTCP#52プロトコル用ではない.
            String msg = String.format("Failed to set DNSMessageTCPTransporter. instance is %s", instance.getClass().getName() );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        super.setDNSMessageTransporter(instance);
    }

}