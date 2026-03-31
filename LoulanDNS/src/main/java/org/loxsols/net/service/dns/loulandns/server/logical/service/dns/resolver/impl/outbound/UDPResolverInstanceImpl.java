
package org.loxsols.net.service.dns.loulandns.server.logical.service.dns.resolver.impl.outbound;



import java.util.Properties;

import org.loxsols.net.service.dns.loulandns.client.IDNSLookupClient;
import org.loxsols.net.service.dns.loulandns.client.impl.simple.SimpleUDPResolverImpl;
import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.common.constants.LoulanDNSConstants;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.*;

import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.resolver.IDNSResolverInstance;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.resolver.impl.*;

import org.loxsols.net.service.dns.loulandns.client.*;
import org.loxsols.net.service.dns.loulandns.client.impl.*;

/**
 * UDPリゾルバインスタンスの実装クラス.
 */
public class UDPResolverInstanceImpl extends DNSResolverInstanceBaseImpl implements IDNSResolverInstance
{

    String outboundDNSServerHost;
    int outboundDNSServerPort;


    public void setOutboundDNSServerHost(String host) throws DNSServiceCommonException
    {
        this.outboundDNSServerHost = host;
    }

    public String getOutboundDNSServerHost() throws DNSServiceCommonException
    {
        return this.outboundDNSServerHost;
    }

    public void setOutboundDNSServerPort(int port) throws DNSServiceCommonException
    {
        this.outboundDNSServerPort = port;
    }
    
    public void setOutboundDNSServerPort(String portString) throws DNSServiceCommonException
    {
        int port;
        try
        {
            port = Integer.parseInt(portString);
        }
        catch(NumberFormatException cause)
        {
            String msg = String.format("Failed to parse port number. value=%s", portString);
            DNSServiceCommonException exception = new DNSServiceCommonException(msg, cause);
            throw exception;
        }
        setOutboundDNSServerPort(port);
    }

    public int getOutboundDNSServerPort()
    {
        return this.outboundDNSServerPort;
    }


    /**
     * コンストラクタ
     * 
     */
    public UDPResolverInstanceImpl(Properties properties) throws DNSServiceCommonException
    {
        super(properties);
    }



    /**
     * 初期化メソッド.
     * 
     */
    public void init(Properties properties) throws DNSServiceCommonException
    {

        // 外部DNSサーバーのホストを設定.
        String primaryDNSServerHost = properties.getProperty(LoulanDNSConstants.PROP_KEY_RESOLVER_OUTBOUND_SERVER_HOST_PRIMARY);
        if ( primaryDNSServerHost == null || primaryDNSServerHost.equals("") )
        {
            String msg = String.format("Failed to initialize DNSResolverInstance. Outbound primary DNS server host is not specified. key = %s", LoulanDNSConstants.PROP_KEY_RESOLVER_OUTBOUND_SERVER_HOST_PRIMARY);
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        setOutboundDNSServerHost(primaryDNSServerHost);


        // 外部DNSサーバーのポートを設定.
        String primaryDNSServerPort = properties.getProperty(LoulanDNSConstants.PROP_KEY_RESOLVER_OUTBOUND_SERVER_PORT_PRIMARY);
        if ( primaryDNSServerPort == null || primaryDNSServerPort.equals("") )
        {
            String msg = String.format("Failed to initialize DNSResolverInstance. Outbound primary DNS server port is not specified. key = %s", LoulanDNSConstants.PROP_KEY_RESOLVER_OUTBOUND_SERVER_PORT_PRIMARY);
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        setOutboundDNSServerPort(primaryDNSServerPort);


        // DNSリゾルバクライアントのオブジェクトを設定.
        IDNSLookupClient client = new UDPResolverImpl();
        client.setDNSServerAddress( getOutboundDNSServerHost() );
        client.setDNSServerPort( getOutboundDNSServerPort() );

        setDNSLookupClient(client);

    }


}