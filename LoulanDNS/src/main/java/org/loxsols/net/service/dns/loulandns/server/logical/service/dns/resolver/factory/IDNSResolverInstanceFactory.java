
package org.loxsols.net.service.dns.loulandns.server.logical.service.dns.resolver.factory;

import java.util.Properties;

import org.loxsols.net.service.dns.loulandns.server.common.*;

import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.*;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.resolver.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.model.DNSProtocolModelInstanceFactoryImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.model.IDNSProtocolModelInstanceFactory;

/**
 * DNSリゾルバインスタンスのファクトリクラス.
 */
public interface IDNSResolverInstanceFactory
{

    /**
     * DNSリゾルバインスタンスのファクトリメソッド.
     * オブジェクトが既にメモリ上に実体化されている場合はそれを返す.
     * オブジェクトが実体化されていない場合は新規に作成する.
     */
    public IDNSResolverInstance getOrCreateResolverInstance(long dnsResolverInstanceID ) throws DNSServiceCommonException;

    /**
     * DNSリゾルバインスタンスのファクトリメソッド.
     * オブジェクトが既にメモリ上に実体化されている場合はそれを返す.
     * オブジェクトが実体化されていない場合は新規に作成する.
     */
    public IDNSResolverInstance getOrCreateResolverInstance(String userName, String dnsResolverInstanceName) throws DNSServiceCommonException;



    /**
     * テンポラリDNSリゾルバインスタンスのファクトリメソッド.
     * DBにアクセスせずに即興でオンメモリのオブジェクトを生成して返す.
     * 
     * オブジェクトが既にメモリ上に実体化されている場合はそれを返す.
     * オブジェクトが実体化されていない場合は新規に作成する.
     * 
     * @param userName
     * @param dnsResolverInstanceName
     * @param resolverTypeCode
     * @param resolverProperties
     * @return
     * @throws DNSServiceCommonException
     */
    public IDNSResolverInstance getOrCreateTemporaryResolverInstance(String userName, String dnsResolverInstanceName, long resolverTypeCode, Properties resolverProperties) throws DNSServiceCommonException;


}

