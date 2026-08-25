
package org.loxsols.net.service.dns.loulandns.server.logical.service.dns.resolver.impl.outbound;


import java.net.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.List;

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
public class DoHResolverInstanceImpl extends UDPResolverInstanceImpl implements IDNSResolverInstance
{

    URI outboundDoHServerURI;
    String outboundDoHQueryHttpMethodType;
    String outboundDoHQueryHttpContentType;
    String outboundDoHQueryHttpAcceptType;


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


    public void setOutboundDoHQueryHttpContentType(String httpContentType) throws DNSServiceCommonException
    {
        this.outboundDoHQueryHttpContentType = httpContentType;
    }
    
    public String getOutboundDoHQueryHttpContentType() throws DNSServiceCommonException
    {
        return this.outboundDoHQueryHttpContentType;
    }


    public void setOutboundDoHQueryHttpAcceptType(String httpAcceptType) throws DNSServiceCommonException
    {
        this.outboundDoHQueryHttpAcceptType = httpAcceptType;
    }
    
    public String getOutboundDoHQueryHttpAcceptType() throws DNSServiceCommonException
    {
        return this.outboundDoHQueryHttpAcceptType;
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

        super.init(properties);


        // DNSリゾルバクライアントのオブジェクトを設定.
        DoHResolverImpl client = new DoHResolverImpl();
        client.setDoHServerURI( getOutboundDoHServerURI() );
        client.setDoHServerHttpMethodType( getOutboundDoHQueryHttpMethodType() );
        client.setDoHServerHttpContentType( getOutboundDoHQueryHttpContentType() );
        client.setDoHServerHttpAcceptType( getOutboundDoHQueryHttpAcceptType() );
        
        setDNSLookupClient(client);

    }



    // パラメータを設定する.
    public void setProperty(String key, String value) throws DNSServiceCommonException
    {

        // 基底クラスのプロパティも併せて設定する.
        super.setProperty(key, value);

        if ( key.equals(LoulanDNSConstants.PROP_KEY_RESOLVER_OUTBOUND_DOH_SERVER_URI_PRIMARY) )
        {
            // 外部DoHサーバーのURIを設定.
            String primaryDoHServerURIString = value;
            setOutboundDoHServerURI(primaryDoHServerURIString);
        }
        else if ( key.equals( LoulanDNSConstants.PROP_KEY_RESOLVER_OUTBOUND_DOH_HTTP_METHOD_TYPE_PRIMARY ) )
        {
            // 外部DoHサーバーの問い合わせに使用するHTTPメソッドタイプを設定.
            String primaryDoHServerHttpMethodType = value;
            setOutboundDoHQueryHttpMethodType(primaryDoHServerHttpMethodType);
        }
        else if ( key.equals( LoulanDNSConstants.PROP_KEY_RESOLVER_OUTBOUND_DOH_HTTP_CONTENT_TYPE_PRIMARY ) )
        {
            // 外部DoHサーバーの問い合わせに使用するHTTP Contentタイプを設定.
            String primaryDoHServerHttpContentType = value;
            setOutboundDoHQueryHttpContentType(primaryDoHServerHttpContentType);
        }
        else if ( key.equals(LoulanDNSConstants.PROP_KEY_RESOLVER_OUTBOUND_DOH_HTTP_ACCEPT_TYPE_PRIMARY) )
        {
            // 外部DoHサーバーの問い合わせに使用するHTTP Acceptタイプを設定.
            String primaryDoHServerHttpAcceptType = value;
            setOutboundDoHQueryHttpAcceptType(primaryDoHServerHttpAcceptType);
        }
        else
        {
            // 本クラスの規定パラメータ以外が指定された.
            // サブクラスでキャッチされる可能性があるから何もせずにスルーする.
        }
    }


    // 必須パラメータキーの一覧を取得する.
    public List<String> getRequiredParameterKeys() throws DNSServiceCommonException
    {

        List<String> list = new ArrayList<String>();

        list.add( LoulanDNSConstants.PROP_KEY_RESOLVER_OUTBOUND_SERVER_HOST_PRIMARY );
        list.add( LoulanDNSConstants.PROP_KEY_RESOLVER_OUTBOUND_SERVER_PORT_PRIMARY );

        return list;
    }



}