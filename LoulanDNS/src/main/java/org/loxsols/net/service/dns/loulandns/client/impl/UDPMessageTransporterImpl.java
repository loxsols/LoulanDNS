package org.loxsols.net.service.dns.loulandns.client.impl;

import org.loxsols.net.service.dns.loulandns.client.*;
import org.loxsols.net.service.dns.loulandns.client.impl.simple.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;

import org.loxsols.net.service.dns.loulandns.client.common.DNSClientCommonException;
import org.loxsols.net.service.dns.loulandns.client.common.LoulanDNSClientConstants;
import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSDebugUtils;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.IDNSMessageFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.model.IDNSProtocolModelInstanceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.xbill.DNS.NioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.io.*;


import java.io.EOFException;
import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.ComponentScan;


@ComponentScan
public class UDPMessageTransporterImpl extends SimpleUDPMessageTransporterImpl implements IDNSMessageTransporter
{

    public UDPMessageTransporterImpl() throws DNSClientCommonException
    {
        super();
    }

    @Autowired
    @Qualifier("dnsProtocolModelInstanceFactoryImpl")
    public void setDNSProtocolModelInstanceFactory(IDNSProtocolModelInstanceFactory instance)
    {
        super.setDNSProtocolModelInstanceFactory(instance);
    }

    @Autowired
    @Qualifier("dnsMessageFactoryImpl")
    public void setDNSMessageFacotry(IDNSMessageFactory instance)
    {
        super.setDNSMessageFacotry(instance);
    }

}
