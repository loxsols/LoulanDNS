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
public class DoHResolverImpl extends SimpleDoHResolverImpl implements IDNSLookupClient
{

    public DoHResolverImpl() throws DNSServiceCommonException
    {
        super();
    }


    @Autowired
    @Qualifier("dnsMessageFactoryImpl")
    public void setDNSMessageFacotry(IDNSMessageFactory instance)
    {
        super.setDNSMessageFacotry(instance);
    }


    /**
     * DoHで使用するメッセージトランスポーターを設定する.
     */
    @Autowired
    @Qualifier("dohMessageTransporterImpl")
    public void setDNSMessageTransporter(IDNSMessageTransporter instance)
    {
        super.setDNSMessageTransporter(instance);
    }

}