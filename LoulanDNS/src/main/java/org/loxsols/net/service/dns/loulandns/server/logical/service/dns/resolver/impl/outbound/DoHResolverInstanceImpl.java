
package org.loxsols.net.service.dns.loulandns.server.logical.service.dns.resolver.impl.outbound;


import java.net.*;
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
 * DoHリゾルバインスタンスの実装クラス.
 */
public class DoHResolverInstanceImpl extends DNSResolverInstanceBaseImpl implements IDNSResolverInstance
{

    URI outboundDoHServerURI;
    String outboundDoHQueryHttpMethodType;


    public void setOutboundDoHServerURI(URI uri) throws DNSServiceCommonException
    {
        this.outboundDoHServerURI = uri;
    }

    public void setOutboundDoHServerURI(String uriString) throws DNSServiceCommonException
    {
        URI uri;
        
        try
        {
            uri = new URI(uriString);
        }
        catch(URISyntaxException cause)
        {
            String msg = String.format("Failed to set DoH Server URI, caused by Illgegal URI format. value=%s", uriString);
            DNSServiceCommonException exception = new DNSServiceCommonException(msg, cause);
            throw exception;
        }

        setOutboundDoHServerURI(uri);
    }


    public URI getOutboundDoHServerURI() throws DNSServiceCommonException
    {
        return this.outboundDoHServerURI;
    }

    public void setOutboundDoHQueryHttpMethodType(String httpMethodType) throws DNSServiceCommonException
    {
        this.outboundDoHQueryHttpMethodType = httpMethodType;
    }
    

    public String getOutboundDoHQueryHttpMethodType() throws DNSServiceCommonException
    {
        return this.outboundDoHQueryHttpMethodType;
    }


    /**
     * コンストラクタ
     * 
     */
    public DoHResolverInstanceImpl(Properties properties) throws DNSServiceCommonException
    {
        super(properties);
    }



    /**
     * 初期化メソッド.
     * 
     */
    public void init(Properties properties) throws DNSServiceCommonException
    {

        // 外部DoHサーバーのURIを設定.
        String primaryDoHServerURIString = properties.getProperty(LoulanDNSConstants.PROP_KEY_RESOLVER_OUTBOUND_DOH_SERVER_URI_PRIMARY);
        if ( primaryDoHServerURIString == null || primaryDoHServerURIString.equals("") )
        {
            String msg = String.format("Failed to initialize DoHResolverInstance. Outbound primary DoH server URI is not specified. key = %s", LoulanDNSConstants.PROP_KEY_RESOLVER_OUTBOUND_DOH_SERVER_URI_PRIMARY);
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        setOutboundDoHServerURI(primaryDoHServerURIString);


        // 外部DoHサーバーの問い合わせに使用するHTTPメソッドタイプを設定.
        String primaryDoHServerHttpMethodType = properties.getProperty(LoulanDNSConstants.PROP_KEY_RESOLVER_OUTBOUND_DOH_HTTP_METHOD_TYPE_PRIMARY);
        if ( primaryDoHServerHttpMethodType == null || primaryDoHServerHttpMethodType.equals("") )
        {
            String msg = String.format("Failed to initialize DoHResolverInstance. Outbound primary DNS server port is not specified. key = %s", LoulanDNSConstants.PROP_KEY_RESOLVER_OUTBOUND_DOH_HTTP_METHOD_TYPE_PRIMARY);
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        setOutboundDoHQueryHttpMethodType(primaryDoHServerHttpMethodType);


        // DNSリゾルバクライアントのオブジェクトを設定.
        DoHResolverImpl client = new DoHResolverImpl();
        client.setDoHServerURI( getOutboundDoHServerURI() );
        client.setDoHServerHttpMethodType( getOutboundDoHQueryHttpMethodType() );

        setDNSLookupClient(client);

    }


}