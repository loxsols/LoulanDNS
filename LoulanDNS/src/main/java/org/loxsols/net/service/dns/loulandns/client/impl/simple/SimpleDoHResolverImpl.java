package org.loxsols.net.service.dns.loulandns.client.impl.simple;


import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import org.loxsols.net.service.dns.loulandns.client.*;
import org.loxsols.net.service.dns.loulandns.client.impl.simple.*;
import org.loxsols.net.service.dns.loulandns.client.common.DNSClientCommonException;
import org.loxsols.net.service.dns.loulandns.client.common.LoulanDNSClientConstants;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.SimpleDNSMessageFactoryImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.section.DNSAdditionalSectionFactoryImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.section.rr.DNSResourceRecordFactoryImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.section.IDNSAdditionalSectionFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.model.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.rr.edns.DNSRROptPseudoRRDataForECSImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSQuestionMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSResponseMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.IDNSAdditionalSection;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.IDNSResourceRecord;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.IDNSRROptPseudoRRData;
import org.loxsols.net.service.dns.loulandns.util.DNSUtils;
import org.loxsols.net.service.dns.loulandns.server.common.*;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSDebugUtils;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSProtocolUtils;
import org.loxsols.net.service.dns.loulandns.client.impl.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.section.rr.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;
import org.xbill.DNS.EDNSOption;


@ComponentScan
public class SimpleDoHResolverImpl extends SimpleUDPResolverImpl implements IDNSLookupClient
{

    URI dohServerURI;
    String dohServerHttpMethodType;


    public SimpleDoHResolverImpl() throws DNSServiceCommonException
    {
        init();
    }


    // SpringのDIを使用せずに機能するよう本クラスを初期化する.
    public void init() throws DNSServiceCommonException
    {
        super.init();

        // メッセージトランスポーターのみをDoHに変更する.
        // それ以外は基底クラスのUDPのものを使用する.
        setDNSMessageTransporter( new SimpleDoHMessageTransporterImpl() );

    }


    public URI getDoHServerURI() throws DNSClientCommonException
    {
        return this.dohServerURI;
    }

    public void setDoHServerURI(URI uri) throws DNSClientCommonException
    {
        this.dohServerURI = uri;
    }

    public String getDoHServerHttpMethodType() throws DNSClientCommonException
    {
        return this.dohServerHttpMethodType;
    }

    public void setDoHServerHttpMethodType(String methodType) throws DNSClientCommonException
    {
        this.dohServerHttpMethodType = methodType;
    }





}