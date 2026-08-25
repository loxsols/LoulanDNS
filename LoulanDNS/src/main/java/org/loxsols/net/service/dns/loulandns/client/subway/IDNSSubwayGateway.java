package org.loxsols.net.service.dns.loulandns.client.subway;


import java.net.InetAddress;
import java.util.HashMap;

import org.loxsols.net.service.dns.loulandns.client.*;
import org.loxsols.net.service.dns.loulandns.client.impl.simple.*;
import org.loxsols.net.service.dns.loulandns.client.subway.common.DNSSubwayCommonException;
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


import java.util.Properties;

public interface IDNSSubwayGateway
{

    public void init(Properties properties) throws DNSSubwayCommonException;

    public void open() throws DNSSubwayCommonException;
    public void close() throws DNSSubwayCommonException;

    public int write(byte[] bytes) throws DNSSubwayCommonException;
    public byte[] read(int size) throws DNSSubwayCommonException;

    public String getGatewayAddress() throws DNSSubwayCommonException;
    public int[] getGatewayPortList() throws DNSSubwayCommonException;

}