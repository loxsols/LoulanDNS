package org.loxsols.net.service.dns.loulandns.server.impl.service.endpoint.factory.simple;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;


import org.xbill.DNS.tools.*;
import org.xbill.DNS.ZoneTransferException;




import org.xbill.DNS.Address;
import org.xbill.DNS.CNAMERecord;
import org.xbill.DNS.Cache;
import org.xbill.DNS.Credibility;
import org.xbill.DNS.DClass;
import org.xbill.DNS.DNAMERecord;
import org.xbill.DNS.ExtendedFlags;
import org.xbill.DNS.Flags;
import org.xbill.DNS.Header;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Message;
import org.xbill.DNS.Name;
import org.xbill.DNS.NameTooLongException;
import org.xbill.DNS.OPTRecord;
import org.xbill.DNS.Opcode;
import org.xbill.DNS.RRset;
import org.xbill.DNS.Rcode;
import org.xbill.DNS.Record;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.Section;
import org.xbill.DNS.SetResponse;
import org.xbill.DNS.TSIG;
import org.xbill.DNS.TSIGRecord;
import org.xbill.DNS.Type;
import org.xbill.DNS.Zone;
import org.xbill.DNS.ZoneTransferException;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.SimpleResolver;


import org.loxsols.net.service.dns.loulandns.server.common.constants.*;
import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceInsufficientDNSRequestException;
import org.loxsols.net.service.dns.loulandns.server.common.InsufficientDNSMessageException;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSCommonUtils;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSProtocolUtils;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;
import org.loxsols.net.service.dns.loulandns.server.http.spring.model.DNSServiceEndpointInstance;
import org.loxsols.net.service.dns.loulandns.server.impl.service.endpoint.base.DNSServiceEndpointInstanceImplBase;
import org.loxsols.net.service.dns.loulandns.server.impl.service.endpoint.udp.UDPServiceEndpointInstanceImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSQuestionMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSResponseMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.resolver.IDNSResolverInstance;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.factory.IDNSServiceInstanceFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.loxsols.net.service.dns.loulandns.server.logical.model.*;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.IDNSServiceInstance;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.endpoint.*;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.endpoint.factory.*;

import org.loxsols.net.service.dns.loulandns.server.impl.service.endpoint.factory.base.*;


/**
 * シンプルなDNSサービスエンドポイントのファクトリクラス.
 * SpringのDI機能を使用しない.
 * ただし、DBにもアクセスできない.
 */
public class SimpleDNSServiceEndpointInstanceFactoryImpl extends DNSServiceEndpointInstanceFactoryImplBase implements IDNSServiceEndpointInstanceFactory
{

    /**
     * DNSサービスエンドポイントインスタンスのファクトリメソッド.
     * オブジェクトが既にメモリ上に実体化されている場合はそれを返す.
     * オブジェクトが実体化されていない場合は新規に作成する.
     * 
     * 本クラスはSpringのDBアクセス機能を使用できないので常に例外をスローする.
     */
    public IDNSServiceEndpointInstance getOrCreateDNSServiceEndpointInstance(long dnsServiceEndpointInstanceID) throws DNSServiceCommonException
    {
        String msg = String.format("Not Implemented Function : IDNSServiceEndpointInstanceFactory.getOrCreateDNSServiceEndpointInstance(dnsServiceEndpointInstanceID). dnsServiceEndpointInstanceID=%d", dnsServiceEndpointInstanceID);
        DNSServiceCommonException exception = new DNSServiceCommonException(msg);
        throw exception;
    }


    public IDNSServiceEndpointInstance getOrCreateDNSServiceEndpointInstance(String userName, String serviceInstanceName, String endpointInstanceName) throws DNSServiceCommonException
    {

        String msg = String.format("Not Implemented Function : IDNSServiceEndpointInstanceFactory.getOrCreateDNSServiceEndpointInstance(String userName, String serviceInstanceName, String endpointInstanceName). userName=%s, serviceInstanceName=%s, endpointInstanceName=%s", userName, serviceInstanceName, endpointInstanceName );
        DNSServiceCommonException exception = new DNSServiceCommonException(msg);
        throw exception;

    }






}