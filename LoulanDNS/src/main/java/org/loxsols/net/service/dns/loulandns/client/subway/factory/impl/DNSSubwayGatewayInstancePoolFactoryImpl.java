package org.loxsols.net.service.dns.loulandns.client.subway.factory.impl;


import java.net.InetAddress;
import java.util.HashMap;

import org.loxsols.net.service.dns.loulandns.client.*;
import org.loxsols.net.service.dns.loulandns.client.impl.simple.*;
import org.loxsols.net.service.dns.loulandns.client.subway.common.DNSSubwayCommonException;
import org.loxsols.net.service.dns.loulandns.client.subway.factory.IDNSSubwayGatewayInstancePoolFactory;
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

import org.loxsols.net.service.dns.loulandns.client.subway.*;
import org.loxsols.net.service.dns.loulandns.client.subway.impl.ssh.*;


import java.util.Properties;


/**
 * DNSSubwayのトンネルのプールインスタンスのファクトリクラス.
 * 
 * DNSSubwayGatewayInstancePoolFactoryImpl
 */
public class DNSSubwayGatewayInstancePoolFactoryImpl implements IDNSSubwayGatewayInstancePoolFactory
{

    public IDNSSubwayGatewayInstancePool createDNSSubwayGatewayInstancePool(Properties properties) throws DNSSubwayCommonException
    {
        IDNSSubwayGatewayInstancePool instance = new DNSSubwayGatewayInstancePoolImpl();
        instance.init(properties);

        return instance;
    }
    

}