
package org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.endpoint;


import java.util.*;

import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.*;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.IDNSServiceInstance;

/**
 * DNSサービスのサービスエンドポイント(外部公開I/FであるUDPやDoHなど)のインターフェース
 */
public interface IDNSServiceEndpointInstance
{

    /**
     * DNSサービスエンドポイントの名前を設定する.
     * 
     * @param bindEndpointName
     * @throws DNSServiceCommonException
     */
    public void setDNSServiceEndpointName(String bindEndpointName) throws DNSServiceCommonException;


    /**
     * DNSサービスインスタンスを設定する.
     * 
     * @param dnsServiceInstance
     * @throws DNSServiceCommonException
     */
    public void setDNSServiceInstance(IDNSServiceInstance dnsServiceInstance) throws DNSServiceCommonException;


    /**
     * DNSサービスエンドポイントのパラメーターを設定する.
     * @param properties
     * @throws DNSServiceCommonException
     */
    public void initDNSServiceEndpoint(Properties properties) throws DNSServiceCommonException;

    /**
     * DNSサービスエンドポイントを開始する.
     * @throws DNSServiceCommonException
     */
    public void startDNSServiceEndpoint() throws DNSServiceCommonException;

    /**
     * DNSサービスエンドポイントを停止する.
     * @throws DNSServiceCommonException
     */
    public void stopDNSServiceEndpoint() throws DNSServiceCommonException;

}