package org.loxsols.net.service.dns.loulandns.client.impl.simple;

import org.loxsols.net.service.dns.loulandns.client.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.*;

import org.loxsols.net.service.dns.loulandns.client.common.DNSClientCommonException;
import org.loxsols.net.service.dns.loulandns.client.common.LoulanDNSClientConstants;
import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSDebugUtils;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSProtocolUtils;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.SimpleDNSMessageFactoryImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.model.SimpleDNSProtocolModelInstanceFactoryImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.IDNSMessageFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.model.IDNSProtocolModelInstanceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.loxsols.net.service.dns.loulandns.util.DNSUtils;

import org.xbill.DNS.NioClient;

import com.google.android.util.AbstractMessageParser.Link;

import android.util.DebugUtils;

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
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
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
import org.springframework.http.HttpEntity;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;


/**
 * DoHメッセージトランスポーターの実装クラス.
 */
@ComponentScan
public class SimpleDoHMessageTransporterImpl extends SimpleUDPMessageTransporterImpl implements IDNSMessageTransporter
{


    // DoHサーバーのフルURL : (例 "https://1.1.1.1/doh-query" )
    URL dohServerURL = null;

    // HTTPメソッドタイプ
    String httpMethodType = null;

    String httpContentType = null;

    String httpAcceptType = null;


    /**
     * DoHサーバーのURLを設定する.
     * 
     * @param url
     * @throws DNSClientCommonException
     */
    public void setDoHServerURL(String urlString) throws DNSClientCommonException
    {

        URL url;
        try
        {
            URI uri = buildURI(urlString);
            url = uri.toURL();
        }
        catch(MalformedURLException cause)
        {
            String msg = String.format("Failed to set DoH serverURL. urlString=%s", urlString);
            DNSClientCommonException exception = new DNSClientCommonException(msg, cause);
            throw exception;
        }

        this.dohServerURL = url;
    }

    public URL getDoHServeURL() throws DNSClientCommonException
    {
        return this.dohServerURL;
    }

    public URI getDoHServerURI() throws DNSClientCommonException
    {
        URL url = getDoHServeURL();

        URI uri;
        try
        {
            uri = url.toURI();
        }
        catch(URISyntaxException cause)
        {
            String msg = String.format("Failed to get DoH server URI. URL is %s", url );
            DNSClientCommonException exception = new DNSClientCommonException(msg, cause);
            throw exception;
        }

        return uri;
    }

    public void setHttpMethodType(String methodType ) throws DNSClientCommonException
    {
        if ( methodType == null )
        {
            String msg = String.format("Failed to set HTTP method type, caused by null. Specified HTTP method type is %s", methodType);
            DNSClientCommonException exception = new DNSClientCommonException(msg);
            throw exception;
        }

        if ( methodType.equals( LoulanDNSClientConstants.HTTP_METHOD_TYPE_GET ) || methodType.equals( LoulanDNSClientConstants.HTTP_METHOD_TYPE_GET.toLowerCase() ) ) 
        {
            this.httpMethodType = LoulanDNSClientConstants.HTTP_METHOD_TYPE_GET;
        }
        else if ( methodType.equals( LoulanDNSClientConstants.HTTP_METHOD_TYPE_POST ) || methodType.equals( LoulanDNSClientConstants.HTTP_METHOD_TYPE_POST.toLowerCase() )) 
        {
            this.httpMethodType = LoulanDNSClientConstants.HTTP_METHOD_TYPE_POST;
        }
        else
        {
            String msg = String.format("Failed to set HTTP method type, caused by Invalid HTTP method type. Specified HTTP method type is %s", methodType);
            DNSClientCommonException exception = new DNSClientCommonException(msg);
            throw exception;
        }

    }

    public String getHttpMethodType() throws DNSClientCommonException
    {
        return this.httpMethodType;
    }

    public void setHttpContentType(String contentType ) throws DNSClientCommonException
    {
        this.httpContentType = contentType;
    }

    public String getHttpContentType() throws DNSClientCommonException
    {
        return this.httpContentType;
    }

    public void setHttpAcceptType(String acceptType ) throws DNSClientCommonException
    {
        this.httpAcceptType = acceptType; 
    }

    public String getHttpAcceptType() throws DNSClientCommonException
    {
        return this.httpAcceptType;
    }


    public String getDoHServerHost() throws DNSClientCommonException
    {
        URL url = getDoHServeURL();
        if ( url == null )
        {
            String msg = String.format("Failed to get DoH server host, due to server URL is null.");
            DNSClientCommonException exception = new DNSClientCommonException(msg);
            throw exception;
        }

        String host = url.getHost();
        return host;
    }

    public int getDoHServerPort() throws DNSClientCommonException
    {
        URL url = getDoHServeURL();
        if ( url == null )
        {
            String msg = String.format("Failed to get DoH server port, due to server URL is null.");
            DNSClientCommonException exception = new DNSClientCommonException(msg);
            throw exception;
        }

        int port = url.getPort();
        return port;
    }


    public String getDoHServerContextPath() throws DNSClientCommonException
    {
        URL url = getDoHServeURL();
        if ( url == null )
        {
            String msg = String.format("Failed to get DoH server context path, due to server URL is null.");
            DNSClientCommonException exception = new DNSClientCommonException(msg);
            throw exception;
        }

        String contextPath = url.getPath();
        return contextPath;
    }





    public SimpleDoHMessageTransporterImpl() throws DNSClientCommonException
    {
        super();
    }


    public void init(HashMap<String, String> properties) throws DNSClientCommonException
    {

        // TODO : ここにDoHサーバーのURLパスを解析する処理を追加する.

        // DoHサーバーのフルURL (オプション1) : <full-URL> | <URL-scheme> <host> <port> <URL-conextpath> のいずれかを指定)
        String fullURL = properties.get( LoulanDNSClientConstants.PROP_KEY_DOH_SERVER_FULL_URL );
        if ( fullURL != null )
        {
            // DoHサーバーのフルURLを設定する.
            setDoHServerURL(fullURL);
        }
        else
        {
            InetAddress serverHost = getServerAddress();

            int serverPort = getServerPort();

            // DoHサーバーのURLスキーム (オプション2)
            String urlScheme = properties.get( LoulanDNSClientConstants.PROP_KEY_DOH_SERVER_URL_SCHEME );

            // DoHサーバーのURLのコンテキストパス (オプション2)
            String urlContextPath = properties.get( LoulanDNSClientConstants.PROP_KEY_DOH_SERVER_URL_CONTEXT_PATH );

            // 与えられたパラメータからフルURLを構築.
            fullURL = String.format("%s://%s:%d%s", urlScheme, serverHost, serverPort, urlContextPath );

            // DoHサーバーのフルURLを設定する.
            setDoHServerURL(fullURL);
        }


        // DoHサーバーのHTTPメソッドタイプ (必須)
        String httpMethodType = properties.get( LoulanDNSClientConstants.PROP_KEY_DOH_SERVER_HTTP_METHOD_TYPE );
        setHttpMethodType(httpMethodType);

        // DoHサーバーのHTTPコンテントタイプ (必須)
        String httpContentType = properties.get( LoulanDNSClientConstants.PROP_KEY_DOH_SERVER_HTTP_CONTENT_TYPE );
        setHttpContentType(httpContentType);
    
        // DoHサーバーのHTTPアクセプトタイプ (必須)
        String httpAcceptType = properties.get( LoulanDNSClientConstants.PROP_KEY_DOH_SERVER_HTTP_ACCEPT_TYPE );  
        setHttpAcceptType(httpAcceptType);

    }

    public byte[] lookup(byte[] wiredQuestionMessage) throws DNSClientCommonException
    {

        HttpClient httpClient = HttpClient.newHttpClient();
        
        String requestBody = toBase64String( wiredQuestionMessage );
        HttpRequest request;

        Properties headers = new Properties();
        headers.put( LoulanDNSClientConstants.HTTP_HEADER_KEY_CONTENT_TYPE, getHttpContentType() );
        headers.put( LoulanDNSClientConstants.HTTP_HEADER_KEY_ACCEPT_TYPE, getHttpAcceptType() );
        
        if ( getHttpMethodType().equals( LoulanDNSClientConstants.HTTP_METHOD_TYPE_GET) )
        {
            // URI uri = UriComponentsBuilder.fromUri( getDoHServerURI() ).queryParam("dns", requestBody).build().toUri();

            Properties queryParamProerties = new Properties();
            queryParamProerties.put( LoulanDNSClientConstants.HTTP_QUERY_PARAMETER_TYPE_DNS, requestBody );

            byte[] responseBytes = lookupByGet(getDoHServerURI(), queryParamProerties, headers);
            return responseBytes;
        }
        else if ( getHttpMethodType().equals( LoulanDNSClientConstants.HTTP_METHOD_TYPE_POST) )
        {
            byte[] responseBytes = lookupByPost(getDoHServerURI(), wiredQuestionMessage, headers);
            return responseBytes;
        }
        else
        {
            // HTTPメソッドタイプが不正.
            String msg = String.format("Failed to lookup DNS request to DoH server, due to Invalid HTTP method type : %s. URL=%s",  getHttpMethodType(), getDoHServeURL() );
            DNSClientCommonException exception = new DNSClientCommonException(msg);
            throw exception;
        }

    }



    /**
     * DoHサーバーに対してGETリクエストを送信する.
     * 
     * @param siteURI
     * @param wiredDNSRequest
     * @param requestHeaders
     * @return
     * @throws DNSClientCommonException
     */
    public byte[] lookupByGet(URI siteURI, byte[] wiredDNSRequest, Properties requestHeaders) throws DNSClientCommonException
    {
        String base64DNSRequest = toBase64String( wiredDNSRequest );

        Properties queryParamProerties = new Properties();
        queryParamProerties.put( LoulanDNSClientConstants.HTTP_QUERY_PARAMETER_TYPE_DNS, base64DNSRequest );

        byte[] responseBytes = lookupByGet(siteURI, wiredDNSRequest, requestHeaders);

        return responseBytes;
    }


    /**
     * DoHサーバーに対してGETリクエストを送信する.
     * DNSクエリは、"https://1.1.1.1?dns=y0IBAAABAAAAAAAABmdvb2dsZQJjbwJqcAAAAQAB" のような形式で最終的に送信する.
     * (HTTPSヘッダー中にDNSクエリのBase64表現が存在するため、DNSクエリは暗号化されない.)
     * queryParamProerties引数に、dnsキーのエントリが含まれていない場合は例外をスローする.
     * 
     * @param uri
     * @param queryParamProerties "dns=y0IBAAABAAAAAAAABmdvb2dsZQJjbwJqcAAAAQAB"のようなHTTPクエリパラメータ
     * @param requestHeaders
     * 
     * @return
     * @throws DNSClientCommonException
     */
    public byte[] lookupByGet(URI siteURI, Properties queryParamProerties, Properties requestHeaders) throws DNSClientCommonException
    {
        
        if ( queryParamProerties.get(LoulanDNSClientConstants.HTTP_QUERY_PARAMETER_TYPE_DNS) == null )
        {
            // DoHサーバーに送信するHTTPクエリパラメータ[dns=XXXXXX]が存在しない.
            String msg = String.format("Failed to lookup to DoH server. HTTP Query parameter [dns] is not specified. DoH server is %s.", siteURI.toString() );
            DNSClientCommonException exception = new DNSClientCommonException(msg);
            throw exception;
        }


        URI buildedURI = buildQueryParameterURI(siteURI, queryParamProerties);

        HttpRequest.Builder builder = HttpRequest.newBuilder( buildedURI ).GET();
        for( var key : requestHeaders.keySet() )
        {
            String value = (String)requestHeaders.get(key);
            builder.setHeader( (String)key, value);
        }

        HttpRequest request = builder.build();

        // DoHリクエストを送信する.
        byte[] responseBytes = lookupDoHRequest(request);

        return responseBytes;
    }


    /**
     * DoHサーバーに対してPOSTリクエストを送信する.
     * DNSクエリはPOSTリクエストのボディの中に格納される(HTTPSで暗号化されたメッセージ中に存在する).
     */
    public byte[] lookupByPost(URI siteURI, byte[] wiredDNSRequest, Properties requestHeaders) throws DNSClientCommonException
    {


        HttpRequest.Builder builder = 
            HttpRequest.newBuilder( siteURI ).POST(BodyPublishers.ofByteArray(wiredDNSRequest));
        for( var key : requestHeaders.keySet() )
        {
            String value = (String)requestHeaders.get(key);
            builder.setHeader( (String)key, value);
        }

        HttpRequest request = builder.build();

        // DoHリクエストを送信する.
        byte[] responseBytes = lookupDoHRequest(request);

        return responseBytes;
    }


    /**
     * DoHサーバーに対してDoHクエリを送信する.
     * 
     * @param dohRequest : DoHのHTTPリクエスト
     * @return
     * @throws DNSClientCommonException
     */
    public byte[] lookupDoHRequest(HttpRequest dohRequest) throws DNSClientCommonException
    {

        HttpResponse<byte[]> response;
        try
        {
            // 同期呼び出し
            HttpClient httpClient = HttpClient.newHttpClient();
            response = httpClient.send(dohRequest, HttpResponse.BodyHandlers.ofByteArray());
        }
        catch(InterruptedException cause)
        {
            String msg = String.format("Failed to lookup DNS request to DoH server, caused by InterruptedException. URL=%s, request=%s", getDoHServeURL(), dohRequest.toString() );
            DNSClientCommonException exception = new DNSClientCommonException(msg);
            throw exception;
        }
        catch(IOException cause)
        {
            String msg = String.format("Failed to lookup DNS request to DoH server, caused by IOException. URL=%s, request=%s", getDoHServeURL(), dohRequest.toString());
            DNSClientCommonException exception = new DNSClientCommonException(msg);
            throw exception;
        }


        byte[] responseBytes =  null;
        if ( response.statusCode() ==  LoulanDNSClientConstants.HTTP_STATUS_CODE_OK )
        {
            // HTTPレスポンスが成功(200)の場合
            Optional<String> contentType = response.headers().firstValue( LoulanDNSClientConstants.HTTP_HEADER_KEY_CONTENT_TYPE);
            if ( contentType.get() == null || contentType.get().equals(getHttpAcceptType()) == false )
            {
                // レスポンスメッセージのContent-Typeが、リクエストで指定したAccept-Typeと異なる.
                String msg = String.format("Failed to lookup DNS request to DoH server, caused by Invalid HTTP Content-Type of response message. URL=%s, request=%s, response=%s, Content-Type=%s, Expected Accept-Type=%s", getDoHServeURL(), dohRequest.toString(), response.toString(), contentType.get(), getHttpAcceptType() );
                DNSClientCommonException exception = new DNSClientCommonException(msg);
                throw exception;
            }

            responseBytes = response.body();
        }
        else if ( response.statusCode() ==  LoulanDNSClientConstants.HTTP_STATUS_CODE_MOVED_PERMANENTLY )
        {
            // HTTP 301 Moved Permanently
            // Locationヘッダーに新しいURLが含まれている.
            Optional<String> headerLocation = response.headers().firstValue("Location");

            String msg = String.format("Failed to lookup DNS request to DoH server.HTTP Status Code is HTTP/301(Moved).  URL=%s, request=%s, response=%s, movedURL=%s", getDoHServeURL(), dohRequest.toString(), response.toString(), headerLocation.get() );
            DNSClientCommonException exception = new DNSClientCommonException(msg);
            throw exception;
        }
        else
        {
            // HTTPレスポンスが失敗した場合.
            String msg = String.format("Failed to lookup DNS request to DoH server.HTTP Status Code is not Invalid. URL=%s, request=%s, response=%s", getDoHServeURL(), dohRequest.toString(), response.toString() );
            DNSClientCommonException exception = new DNSClientCommonException(msg);
            throw exception;
        }

        return responseBytes;

    }



    public String toBase64String(byte[] bytes) throws DNSClientCommonException
    {
        LoulanDNSProtocolUtils protocolUtils = new LoulanDNSProtocolUtils();

        String base64String;
        try
        {
            base64String = protocolUtils.toBase64String(bytes);
        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to convert from bytes to base64 String. Size of bytes is %d.", bytes.length );
            DNSClientCommonException exception = new DNSClientCommonException(msg, cause);
            throw exception;
        }

        return base64String;
    }

    public byte[] base64StringToBytes(String base64String) throws DNSClientCommonException
    {
        LoulanDNSProtocolUtils protocolUtils = new LoulanDNSProtocolUtils();

        byte[] bytes;

        try
        {
            bytes = protocolUtils.base64StringToBytes(base64String);
        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to convert from base64 String to Bytes. base64String is [%s].", base64String );
            DNSClientCommonException exception = new DNSClientCommonException(msg, cause);
            throw exception;
        }

        return bytes;
    }


    // HTTPのクエリパラメータURIを生成する.
    public URI buildQueryParameterURI(URI originalURI, Properties properties) throws DNSClientCommonException
    {

        String buildURL = originalURI.toString();

        for( var keyObj : properties.keySet() )
        {
            String key = (String)keyObj;
            String val = (String)properties.get(key);

            buildURL = String.format("%s?%s=%s", buildURL, key, val );
        }

        URI uri;
        try
        {
            uri = new URI(buildURL);
        }
        catch(URISyntaxException cause)
        {
            String msg = String.format("Failed to build query paramter URI. originalURI=%s, buildURL=%s", buildURL);
            DNSClientCommonException exception = new DNSClientCommonException(msg, cause);
            throw exception;
        }

        return uri;
    }


    public URI buildURI(String url) throws DNSClientCommonException
    {
        URI uri;

        try
        {
            uri = new URI( url ); 
        }
        catch(URISyntaxException cause)
        {
            String msg = String.format("Invalid URI format : %s", url );
            DNSClientCommonException exception = new DNSClientCommonException(msg);
            throw exception;
        }

        return uri;
    }

    



   public static void main(String[] args)
   {

        if ( args.length < 2 )
        {
            System.out.println("Usage : <DoH Server URL> <qname>");
            System.out.println("Ex) \"https://1.1.1.1/dns-query\" google.co.jp");
            return;
        }

        String dohServerURL = args[0];
        String qname = args[1];

        try
        {
            DNSUtils utils = new DNSUtils(); 
            byte[] queryBytes = utils.toWireDNSQuery(qname);

            HashMap<String,String> properties = new HashMap<String,String>();

            // DoHサーバーのURL
            properties.put( LoulanDNSClientConstants.PROP_KEY_DOH_SERVER_FULL_URL, dohServerURL );

            // DoHサーバーのHTTPメソッドタイプ (必須)
            // properties.put(LoulanDNSClientConstants.PROP_KEY_DOH_SERVER_HTTP_METHOD_TYPE, LoulanDNSClientConstants.HTTP_METHOD_TYPE_GET);
            properties.put(LoulanDNSClientConstants.PROP_KEY_DOH_SERVER_HTTP_METHOD_TYPE, LoulanDNSClientConstants.HTTP_METHOD_TYPE_POST);
            

            // DoHサーバーのHTTP Content-type (必須)
            properties.put(LoulanDNSClientConstants.PROP_KEY_DOH_SERVER_HTTP_CONTENT_TYPE, LoulanDNSClientConstants.HTTP_CONTENT_TYPE_APPLICATION_DNS_MESSAGE);

            // DoHサーバーのHTTP Accept-type (必須)
            properties.put( LoulanDNSClientConstants.PROP_KEY_DOH_SERVER_HTTP_ACCEPT_TYPE, LoulanDNSClientConstants.HTTP_ACCEPTT_TYPE_APPLICATION_DNS_MESSAGE);
    

            SimpleDoHMessageTransporterImpl dnsMessageTransporter = new SimpleDoHMessageTransporterImpl();
            dnsMessageTransporter.init(properties);
            byte[] responseBytes = dnsMessageTransporter.lookup(queryBytes);


            LoulanDNSDebugUtils debugUtils  = new LoulanDNSDebugUtils();
            debugUtils.printHexString(SimpleDoHMessageTransporterImpl.class, "request  : qname", queryBytes);
            debugUtils.printHexString(SimpleDoHMessageTransporterImpl.class, "response : qname", responseBytes);

            LoulanDNSProtocolUtils protocolUtils = new LoulanDNSProtocolUtils();
            String msg = protocolUtils.toDNSDebugMessage(responseBytes);
            System.out.println(msg);

        }
        catch(DNSClientCommonException exception)
        {
            exception.printStackTrace();
        }
        catch(DNSServiceCommonException exception)
        {
            exception.printStackTrace();
        }
   }



}
