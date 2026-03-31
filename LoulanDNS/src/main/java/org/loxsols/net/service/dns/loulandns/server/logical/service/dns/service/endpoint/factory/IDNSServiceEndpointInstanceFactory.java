
package org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.endpoint.factory;


import java.util.*;

import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.*;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.resolver.IDNSResolverInstance;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.IDNSServiceInstance;

import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.endpoint.*;

/**
 * DNSサービスのサービスエンドポイント(外部公開I/FであるUDPやDoHなど)のインターフェースのファクトリクラス
 */
public interface IDNSServiceEndpointInstanceFactory
{

    /**
     * DNSサービスエンドポイントインスタンスのファクトリメソッド.
     * オブジェクトが既にメモリ上に実体化されている場合はそれを返す.
     * オブジェクトが実体化されていない場合は新規に作成する.
     */
    public IDNSServiceEndpointInstance getOrCreateDNSServiceEndpointInstance(long dnsServiceEndpointInstanceID) throws DNSServiceCommonException;


    /**
     * DNSサービスエンドポイントインスタンスのファクトリメソッド.
     * 指定したユーザに所属するエンドポイントインスタンスをを返す.
     * オブジェクトが既にメモリ上に実体化されている場合はそれを返す.
     * オブジェクトが実体化されていない場合は新規に作成する.
     * 
     * @param userName
     * @param serviceInstanceName
     * @param endpointInstanceName
     * @return
     * @throws DNSServiceCommonException
     */
    public IDNSServiceEndpointInstance getOrCreateDNSServiceEndpointInstance(String userName, String serviceInstanceName, String endpointInstanceName) throws DNSServiceCommonException;




    /**
     * テンポラリDNSサービスエンドポイントインスタンスのファクトリメソッド.
     * DBにエンドポイント定義がなくても、本メソッドの引数だけでエンドポイントインスタンスを生成する.
     * 
     * @param endpointUserName
     * @param serviceEndpointTypeName
     * @param properties
     * @param endpointInstanceName
     * @param bindServiceInstance
     * @return
     * @throws DNSServiceCommonException
     */
    public IDNSServiceEndpointInstance getOrCreateTemporaryDNSServiceEndpointInstance(String endpointUserName, String serviceEndpointTypeName, Properties properties, String endpointInstanceName,  IDNSServiceInstance bindServiceInstance) throws DNSServiceCommonException;

}