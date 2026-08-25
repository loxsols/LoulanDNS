package org.loxsols.net.service.dns.loulandns.client.subway;


import java.net.InetAddress;
import java.util.HashMap;

import org.loxsols.net.service.dns.loulandns.client.*;
import org.loxsols.net.service.dns.loulandns.client.impl.simple.*;
import org.loxsols.net.service.dns.loulandns.client.subway.common.DNSSubwayCommonException;
import org.loxsols.net.service.dns.loulandns.client.subway.factory.*;
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


import org.loxsols.net.service.dns.loulandns.client.subway.common.*;
import org.loxsols.net.service.dns.loulandns.client.subway.common.IDNSSubwayGatewayDescriptor;


import java.util.Properties;


/**
 * DNSSubwayGatewayのインスタンスのプールI/F.
 * 動的に構築したゲートウェイを管理するためのプールI/F.
 * 
 * IDNSSubwayGatewayInstancePool
 */
public interface IDNSSubwayGatewayInstancePool
{
    public void init(Properties properties) throws DNSSubwayCommonException;

    public IDNSSubwayGatewayDescriptor getOrCreateDNSSubwayDescriptor(String subwayLineType, String dname, String protocolType, int[] portList, Properties options) throws DNSSubwayCommonException;

    public IDNSSubwayGatewayDescriptor getDNSSubwayDescriptor(String subwayLineType, String dname, String protocolType, int[] portList, Properties options) throws DNSSubwayCommonException;
    public boolean isExists(String subwayLineType, String dname, String protocolType, int[] portList, Properties options) throws DNSSubwayCommonException;


    public IDNSSubwayGatewayDescriptor createDNSSubwayDescriptor(String subwayLineType, String dname, String protocolType, int[] portList, Properties options) throws DNSSubwayCommonException;


    public void destroy(IDNSSubwayGatewayDescriptor descriptor) throws DNSSubwayCommonException;
}