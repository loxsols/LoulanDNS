
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
 * TCPリゾルバインスタンスの実装クラス.
 */
public class TCPResolverInstanceImpl extends UDPResolverInstanceImpl implements IDNSResolverInstance
{

    /**
     * コンストラクタ
     * 
     */
    public TCPResolverInstanceImpl(Properties properties) throws DNSServiceCommonException
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
        IDNSLookupClient client = new TCPResolverImpl();
        client.setDNSServerAddress( getOutboundDNSServerHost() );
        client.setDNSServerPort( getOutboundDNSServerPort() );

        setDNSLookupClient(client);

    }


    // 必須パラメータキーの一覧を取得する.
    public List<String> getRequiredParameterKeys() throws DNSServiceCommonException
    {
        List<String> list = super.getRequiredParameterKeys();

        // TODO : EDNSなどのTCPクライアント固有の設定パラメータを追加.

        return list;
    }

    // オプションパラメータキーの一覧を取得する.
    public List<String> getOptionalParameterKeys() throws DNSServiceCommonException
    {
        // 特にオプションパラメータはないので空のリストを返す.
        List<String> list = new ArrayList<String>();

        // TODO : EDNSなどのTCPクライアント固有の設定パラメータを追加.

        return list;
    }



    // パラメータを設定する.
    public void setProperty(String key, String value) throws DNSServiceCommonException
    {
        super.setProperty(key, value);

    }

    // 現在のパラメータの設定値を確認する.
    public String getProperty(String key) throws DNSServiceCommonException
    {
        String value = super.getProperty(key);
        return value;
    }




}