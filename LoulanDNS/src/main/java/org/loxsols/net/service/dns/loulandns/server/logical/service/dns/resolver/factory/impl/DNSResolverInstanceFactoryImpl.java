
package org.loxsols.net.service.dns.loulandns.server.logical.service.dns.resolver.factory.impl;


import java.time.ZonedDateTime;
import java.util.*;

import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.common.constants.LoulanDNSConstants;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;
import org.loxsols.net.service.dns.loulandns.server.logical.model.DNSResolverInstanceInfo;
import org.loxsols.net.service.dns.loulandns.server.logical.model.DNSResolverPropertiesInfo;
import org.loxsols.net.service.dns.loulandns.server.logical.model.DNSResolverTypeInfo;
import org.loxsols.net.service.dns.loulandns.server.logical.model.UserInfo;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.*;
import org.loxsols.net.service.dns.loulandns.server.logical.service.LoulanDNSLogicalModelService;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.resolver.IDNSResolverInstance;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.resolver.factory.IDNSResolverInstanceFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.resolver.impl.DNSResolverInstanceBaseImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.resolver.impl.outbound.*;

import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * DNSリゾルバインスタンスのファクトリクラスのインターフェース
 */
public class DNSResolverInstanceFactoryImpl implements IDNSResolverInstanceFactory
{

    @Autowired
    @Qualifier("loulanDNSLogicalModelServiceImpl")
    LoulanDNSLogicalModelService loulanDNSLogicalModelService;


    Map<String, IDNSResolverInstance> onMemoryInstanceCacheTable = new HashMap<String, IDNSResolverInstance>();


    LoulanDNSUtils loulanDNSUtils = new LoulanDNSUtils();


    /**
     * DNSリゾルバインスタンスのファクトリメソッド.
     * オブジェクトが既にメモリ上に実体化されている場合はそれを返す.
     * オブジェクトが実体化されていない場合は新規に作成する.
     */
    public IDNSResolverInstance getOrCreateResolverInstance(long dnsResolverInstanceID) throws DNSServiceCommonException
    {

        DNSResolverInstanceInfo info = loadLogicalModel(dnsResolverInstanceID);
        UserInfo userInfo = loulanDNSLogicalModelService.getUserInfo( info.getUserID() );

        IDNSResolverInstance instance = getDNSResolverInstanceCacheRecord( userInfo.getUserName(), info.getDNSResolverInstanceName() );
        if ( instance != null )
        {
            return instance;
        }

        instance = createDNSResolverInstance(dnsResolverInstanceID);
        
        // 生成したインスタンスオブジェクトをキャッシュに登録する.
        registDNSResolverInstanceCacheRecord(userInfo.getUserName(), info.getDNSResolverInstanceName(), instance);

        return instance;
    }

    /**
     * DNSリゾルバインスタンスのファクトリメソッド.
     * オブジェクトが既にメモリ上に実体化されている場合はそれを返す.
     * オブジェクトが実体化されていない場合は新規に作成する.
     */
    public IDNSResolverInstance getOrCreateResolverInstance(String userName, String dnsResolverInstanceName) throws DNSServiceCommonException
    {

        IDNSResolverInstance resolverInstance = getDNSResolverInstanceCacheRecord(userName, dnsResolverInstanceName);
        if ( resolverInstance != null )
        {
            // キャッシュがある場合はそれを返す.
            return resolverInstance;
        }

        DNSResolverInstanceInfo info = loadLogicalModel(userName, dnsResolverInstanceName);

        resolverInstance = getOrCreateResolverInstance( info.getDNSResolverInstanceID() );
        return resolverInstance;
    }


    /**
     * テンポラリDNSリゾルバインスタンスのファクトリメソッド.
     * DBにアクセスせずに即興でオンメモリのオブジェクトを生成して返す.
     * 
     * オブジェクトが既にメモリ上に実体化されている場合はそれを返す.
     * オブジェクトが実体化されていない場合は新規に作成する.
     * 
     * @param dnsResolverInstanceName
     * @param resolverTypeCode
     * @param resolverProperties
     * @return
     * @throws DNSServiceCommonException
     */
    public IDNSResolverInstance getOrCreateTemporaryResolverInstance(String userName, String dnsResolverInstanceName, long resolverTypeCode, Properties resolverProperties) throws DNSServiceCommonException
    {
        IDNSResolverInstance resolverInstance = getDNSResolverInstanceCacheRecord(userName, dnsResolverInstanceName);

        if ( resolverInstance != null )
        {
            // キャッシュがある場合はそれを返す.
            return resolverInstance;
        }


        DNSResolverInstanceInfo resolverInstanceInfo = new DNSResolverInstanceInfo();

        resolverInstanceInfo.setDnsResolverInstanceID(null);
        resolverInstanceInfo.setDnsResolverInstanceName(dnsResolverInstanceName);
        resolverInstanceInfo.setDnsResolverInstanceExplain(null);
        resolverInstanceInfo.setDnsResolverTypeCode(resolverTypeCode);

        resolverInstanceInfo.setRecordStatus(LoulanDNSConstants.DB_CONST_VALUE_RECORD_STATUS_ACTIVE);
        resolverInstanceInfo.setMemo(null);

        // レコードの作成/更新時刻は現在時刻を設定する.
        ZonedDateTime currentDateTime = ZonedDateTime.now();
        String currentDateTimeString = loulanDNSUtils.toDateTimeString(currentDateTime);
        resolverInstanceInfo.setCreateDate(currentDateTimeString);
        resolverInstanceInfo.setUpdateDate(currentDateTimeString);

        List<DNSResolverPropertiesInfo> propList = toTemporaryDNSResolverPropertiesInfoList(resolverProperties);
        resolverInstanceInfo.setDNSResolverPropertiesInfoList(propList);


        // テンポラリなDNSリゾルバインスタンスを生成する.
        resolverInstance = createDNSResolverInstance(resolverInstanceInfo);

        // 生成したインスタンスオブジェクトをキャッシュに登録する.
        registDNSResolverInstanceCacheRecord(userName, dnsResolverInstanceName, resolverInstance);

        return resolverInstance;

    }



    /**
     * テンポラリなDNSResolverPropertiesInfoのリストをPropertiesから作成する.
     * 
     * @param properties
     * @return
     * @throws DNSServiceCommonException
     */
    private List<DNSResolverPropertiesInfo> toTemporaryDNSResolverPropertiesInfoList(Properties properties) throws DNSServiceCommonException
    {

        List<DNSResolverPropertiesInfo> propList = new ArrayList<DNSResolverPropertiesInfo>();
        for( var keyObj : properties.keySet() )
        {
            String key = (String)keyObj;
            String value = (String)properties.get(key);
            String explain = null;

            DNSResolverPropertiesInfo info = toTemporaryDNSResolverPropertiesInfo(key, value, explain);
            propList.add(info);
        }

        return propList;
    }

    /**
     * テンポラリなDNSResolverPropertiesInfoオブジェクトを作成する.
     * 
     * @param key
     * @param value
     * @param explain
     * @return
     * @throws DNSServiceCommonException
     */
    private DNSResolverPropertiesInfo toTemporaryDNSResolverPropertiesInfo(String key, String value, String explain) throws DNSServiceCommonException
    {

        DNSResolverPropertiesInfo info = new DNSResolverPropertiesInfo();

        info.setDNSResolverPropertyID(null);
        info.setDnsResolverID(null);
        info.setDnsResolverPropertyKey(key);
        info.setDnsResolverPropertyValue(value);
        info.setDnsResolverPropertyExplain(explain);
        info.setRecordStatus(LoulanDNSConstants.DB_CONST_VALUE_RECORD_STATUS_ACTIVE);
        info.setMemo(null);

        ZonedDateTime currentDateTime = ZonedDateTime.now();
        String currentDateTimeString = loulanDNSUtils.toDateTimeString(currentDateTime);
        info.setCreateDate(currentDateTimeString);
        info.setUpdateDate(currentDateTimeString);

        return info;
    }



    /**
     * DNSリゾルバインスタンスを新規にインスタンス化する.
     * 
     * @param dnsResolverInstanceID
     * @return
     * @throws DNSServiceCommonException
     */
    protected IDNSResolverInstance createDNSResolverInstance(long dnsResolverInstanceID) throws DNSServiceCommonException
    {
        DNSResolverInstanceInfo resolverInstanceInfo = loadLogicalModel(dnsResolverInstanceID);
        IDNSResolverInstance dnsResolverInstance = createDNSResolverInstance(resolverInstanceInfo);
        return dnsResolverInstance;
    }



    /**
     * DNSリゾルバインスタンスを新規にインスタンス化する.
     * 
     * @param resolverInstanceInfo
     * @return
     * @throws DNSServiceCommonException
     */
    protected IDNSResolverInstance createDNSResolverInstance(DNSResolverInstanceInfo resolverInstanceInfo) throws DNSServiceCommonException
    {

        long resolverType = resolverInstanceInfo.getDnsResolverTypeCode();
        Properties properties = resolverInstanceInfo.getProperties();

        IDNSResolverInstance dnsResolverInstance;

        // TODO : UDPリゾルバ以外のリゾルバのインスタンス化処理が未実装.
        if ( resolverType == LoulanDNSConstants.CONST_DNS_RESOLVER_TYPE_OUTBOUND_UDP )
        {
            // -- 				201 : UDP(DNS)問い合わせ
            dnsResolverInstance = new UDPResolverInstanceImpl(properties);
        }
        else if (resolverType == LoulanDNSConstants.CONST_DNS_RESOLVER_TYPE_OUTBOUND_DOH )
        {
            // -- 				205 : DoH問い合わせ
            dnsResolverInstance = new DoHResolverInstanceImpl(properties);
        }
        else
        {
            String msg = String.format("Failed to craete DNSResolverInstance. Unknown DNS resolver type code :%d", resolverType );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        return dnsResolverInstance;
    }


    protected DNSResolverInstanceInfo loadLogicalModel(long dnsResolverInstanceID) throws DNSServiceCommonException
    {
        DNSResolverInstanceInfo info = loulanDNSLogicalModelService.getDNSResolverInstanceInfo(dnsResolverInstanceID);
        return info;
    }

    protected DNSResolverInstanceInfo loadLogicalModel(String userName, String dnsResolverInstanceName) throws DNSServiceCommonException
    {
        UserInfo userInfo = loulanDNSLogicalModelService.getUserInfo(userName);

        DNSResolverInstanceInfo dnsResolverInstanceInfo = null;
        for( DNSResolverInstanceInfo info : userInfo.getDnsResolverInstanceInfoList() )
        {
            if ( info.getDNSResolverInstanceName().equals(dnsResolverInstanceName) )
            {
                dnsResolverInstanceInfo = info;
                break;
            }
        }

        return dnsResolverInstanceInfo;
    }


    protected IDNSResolverInstance getDNSResolverInstanceCacheRecord(String userName, String dnsResolverInstanceName) throws DNSServiceCommonException
    {
        IDNSResolverInstance instance;

        String cacheKey = buildDNSResolverInstanceCacheKey(userName, dnsResolverInstanceName);
        synchronized(this.onMemoryInstanceCacheTable)
        {
            instance = this.onMemoryInstanceCacheTable.get(cacheKey);
        }

        return instance;
    }


    protected void registDNSResolverInstanceCacheRecord(String userName, String dnsResolverInstanceName, IDNSResolverInstance instance) throws DNSServiceCommonException
    {
        String cacheKey = buildDNSResolverInstanceCacheKey(userName, dnsResolverInstanceName);

        synchronized(this.onMemoryInstanceCacheTable)
        {
            onMemoryInstanceCacheTable.put(cacheKey, instance);
        }
    }


    protected void clearDNSResolverInstanceCacheRecord(String userName, String dnsResolverInstanceName) throws DNSServiceCommonException
    {
        String cacheKey = buildDNSResolverInstanceCacheKey(userName, dnsResolverInstanceName);

        synchronized(this.onMemoryInstanceCacheTable)
        {
            onMemoryInstanceCacheTable.remove(cacheKey);
        }

    }


    /**
     * DNSリゾルバインスタンスのキャッシュのキー値を生成する.
     * 
     * @param userName
     * @param dnsResolverInstanceName
     * @return
     * @throws DNSServiceCommonException
     */
    protected String buildDNSResolverInstanceCacheKey(String userName, String dnsResolverInstanceName) throws DNSServiceCommonException
    {
        String key = String.format("%s/%s", userName, dnsResolverInstanceName);
        return key;
    }



}