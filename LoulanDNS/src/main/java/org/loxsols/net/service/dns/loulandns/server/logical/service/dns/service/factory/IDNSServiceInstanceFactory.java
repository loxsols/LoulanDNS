
package org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.factory;


import java.util.*;

import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.*;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.resolver.IDNSResolverInstance;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.*;

/**
 * DNSサービスインスタンスのファクトリクラスのインターフェース
 */
public interface IDNSServiceInstanceFactory
{


    /**
     * 初期化処理
     * 
     * @param properties
     * @throws DNSServiceCommonException
     */
    public void init(Properties properties) throws DNSServiceCommonException;


    /**
     * DNSサービスインスタンスを取得する.
     * サービスインスタンスが存在しなければ新規に作成して返す.
     * サービスインスタンスが既に存在する場合は登録済みのインスタンスを返却する.
     * 
     * @param dnsServiceInstanceID
     * @return DNSサービスインスタンス
     * @throws DNSServiceCommonException
     */
    public IDNSServiceInstance getOrCreateDNSServiceInstance(long dnsServiceInstanceID ) throws DNSServiceCommonException;


    /**
     * DNSサービスインスタンス(IDNSServiceInstance)のファクトリメソッド.
     * 既に生成済みのサービスインスタンスがメモリ上に存在する場合はそれを返す.
     * サービスインスタンスが存在しなければ新規作成して返す.
     * 
     * @param userName
     * @param dnsServiceInstanceName
     * @return
     * @throws DNSServiceCommonException
     */
    public IDNSServiceInstance getOrCreateDNSServiceInstance(String userName, String dnsServiceInstanceName) throws DNSServiceCommonException;


    /**
     * デフォルトDNSサービスインスタンス(IDNSServiceInstance)のファクトリメソッド.
     * 既に生成済みのデフォルトDNSサービスインスタンスがメモリ上に存在する場合はそれを返す.
     * デフォルトDNSサービスインスタンスが存在しなければ新規作成して返す.
     * 
     * @return
     * @throws DNSServiceCommonException
     */
    public IDNSServiceInstance getOrCreateDefaultDNSServiceInstance() throws DNSServiceCommonException;




    /**
     * テンポラリDNSサービスインスタンス(IDNSServiceInstance)のファクトリメソッド.
     * オンメモリで即興のDNSサービスインスタンスを作成する.
     * DBの既存レコードを参照せずにインスタンスを生成できる.
     * 
     * @param userName
     * @param dnsServiceInstanceName
     * @param dnsServiceInstanceTypeCode
     * @param dnsServiceInstanceProperties
     * @param dnsResolverInstance
     * @return
     * @throws DNSServiceCommonException
     */
    public IDNSServiceInstance getOrCreateTemporaryDNSServiceInstance(String userName, String dnsServiceInstanceName, long dnsServiceInstanceTypeCode, Properties dnsServiceInstanceProperties, IDNSResolverInstance dnsResolverInstance ) throws DNSServiceCommonException;



    /**
     * 生成済みのDNSサービスインスタンスを廃棄する.
     * 
     * @param userName
     * @param dnsServiceInstanceName
     * @throws DNSServiceCommonException
     */
    public void destoryDNSServiceInstance(String userName, String dnsServiceInstanceName) throws DNSServiceCommonException;


    /**
     * DNSサービスインスタンスが生成済みかを判定する.
     * 
     * @param userName
     * @param dnsServiceInstanceName
     * @throws DNSServiceCommonException
     */
    public boolean isExistsOnMemoryDNSServiceInstance(String userName, String dnsServiceInstanceName) throws DNSServiceCommonException;


    /**
     * DNSサービスインスタンスが生成済みかを判定する.
     * 
     * @param dnsServiceInstanceID
     * @throws DNSServiceCommonException
     */
    public boolean isExistsOnMemoryDNSServiceInstance(long dnsServiceInstanceID) throws DNSServiceCommonException;



}