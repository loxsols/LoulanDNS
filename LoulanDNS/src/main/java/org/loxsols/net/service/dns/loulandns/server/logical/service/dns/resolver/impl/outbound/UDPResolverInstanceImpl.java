
package org.loxsols.net.service.dns.loulandns.server.logical.service.dns.resolver.impl.outbound;



import java.util.ArrayList;
import java.util.Properties;
import java.util.List;

import org.loxsols.net.service.dns.loulandns.client.IDNSLookupClient;
import org.loxsols.net.service.dns.loulandns.client.common.LoulanDNSClientConstants;
import org.loxsols.net.service.dns.loulandns.client.impl.simple.SimpleUDPResolverImpl;
import org.loxsols.net.service.dns.loulandns.client.subway.common.DNSSubwayConstants;
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

        super.init(properties);

        // DNSリゾルバクライアントのオブジェクトを設定.
        IDNSLookupClient client = new UDPResolverImpl();
        client.setDNSServerAddress( getOutboundDNSServerHost() );
        client.setDNSServerPort( getOutboundDNSServerPort() );

        setDNSLookupClient(client);

    }


    // 必須パラメータキーの一覧を取得する.
    public List<String> getRequiredParameterKeys() throws DNSServiceCommonException
    {
        List<String> list = new ArrayList<String>();

        list.add( LoulanDNSConstants.PROP_KEY_RESOLVER_OUTBOUND_SERVER_HOST_PRIMARY );
        list.add( LoulanDNSConstants.PROP_KEY_RESOLVER_OUTBOUND_SERVER_PORT_PRIMARY );

        return list;
    }

    // オプションパラメータキーの一覧を取得する.
    public List<String> getOptionalParameterKeys() throws DNSServiceCommonException
    {
        // 特にオプションパラメータはないので空のリストを返す.
        List<String> list = new ArrayList<String>();
        return list;
    }



    // パラメータを設定する.
    public void setProperty(String key, String value) throws DNSServiceCommonException
    {
        if ( key.equals(LoulanDNSConstants.PROP_KEY_RESOLVER_OUTBOUND_SERVER_HOST_PRIMARY) )
        {
            // 外部DNSサーバーのホストを設定.
            String primaryDNSServerHost = value;
            setOutboundDNSServerHost(primaryDNSServerHost);
        }
        else if ( key.equals(LoulanDNSConstants.PROP_KEY_RESOLVER_OUTBOUND_SERVER_PORT_PRIMARY) )
        {
            // 外部DNSサーバーのポートを設定.
            String primaryDNSServerPort = value;
            setOutboundDNSServerPort(primaryDNSServerPort);
        }
        else
        {
            // 本クラスの規定パラメータ以外が指定された.
            // サブクラスでキャッチされる可能性があるから何もせずにスルーする.
        }


    }

    // 現在のパラメータの設定値を確認する.
    public String getProperty(String key) throws DNSServiceCommonException
    {

        String value;

        if ( key.equals(LoulanDNSConstants.PROP_KEY_RESOLVER_OUTBOUND_SERVER_HOST_PRIMARY) )
        {
            // 外部DNSサーバーのホストを設定.
            value = getOutboundDNSServerHost();
        }
        else if ( key.equals(LoulanDNSConstants.PROP_KEY_RESOLVER_OUTBOUND_SERVER_PORT_PRIMARY) )
        {
            // 外部DNSサーバーのポートを設定.
            int port = getOutboundDNSServerPort();
            value = Integer.toString(port);
        }
        else
        {
            // 本クラスの規定外のパラメータキーが指定された.
            // サブクラス側で捕捉される可能性があるからスルーする.
            value = null;
        }   

        return value;
    }




}