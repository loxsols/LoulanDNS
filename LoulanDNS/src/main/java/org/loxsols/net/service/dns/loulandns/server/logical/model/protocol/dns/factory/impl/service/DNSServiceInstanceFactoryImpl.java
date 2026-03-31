
package org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.service;

import java.time.ZonedDateTime;
import java.util.*;

import org.loxsols.net.service.dns.loulandns.server.common.*;
import org.loxsols.net.service.dns.loulandns.server.common.constants.LoulanDNSConstants;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;
import org.loxsols.net.service.dns.loulandns.server.logical.service.LoulanDNSLogicalModelService;

import org.loxsols.net.service.dns.loulandns.server.logical.model.DNSServiceInstanceInfo;
import org.loxsols.net.service.dns.loulandns.server.logical.model.UserInfo;

import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.factory.IDNSServiceInstanceFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.resolver.IDNSResolverInstance;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.resolver.factory.IDNSResolverInstanceFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.*;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.impl.DNSServiceInstanceImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.log.logger.factory.ILoulanDNSLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import android.providers.settings.GlobalSettingsProto.DateTime;

/**
 * DNSサービスインスタンスのファクトリクラス.
 */
public class DNSServiceInstanceFactoryImpl implements IDNSServiceInstanceFactory
{

    @Autowired
    @Qualifier("loulanDNSLogicalModelServiceImpl")
    LoulanDNSLogicalModelService loulanDNSLogicalModelService;


    @Autowired
    @Qualifier("dnsResolverInstanceFactoryImpl")
    IDNSResolverInstanceFactory dnsResolerInstanceFactory;


    @Autowired
    @Qualifier("loulanDNSLoggerFactoryImpl")
    ILoulanDNSLoggerFactory loulanDNSLoggerFactory;



    List<IDNSServiceInstance> dnsServiceInstanceList = new ArrayList<IDNSServiceInstance>();

    // 生成済みのDNSサービスインスタンスを格納するためのキャッシュ用のMap.
    Map<String, IDNSServiceInstance> dnsServiceInstanceCache = new HashMap<String, IDNSServiceInstance>();



    String defaultServiceInstanceUserName;
    String defaultServiceInstanceName;


    LoulanDNSUtils loulanDNSUtils = new LoulanDNSUtils();


    /**
     * デフォルトDNSサービスインスタンスが所属するユーザー名を返す.
     * @return
     * @throws DNSServiceCommonException
     */
    public String getDefaultServiceInstanceUserName() throws DNSServiceCommonException
    {
        return this.defaultServiceInstanceUserName;
    }

    /**
     * デフォルトDNSサービスインスタンスが所属するユーザー名を設定する.
     * @param userName
     * @throws DNSServiceCommonException
     */
    public void setDefaultServiceInstanceUserName(String userName) throws DNSServiceCommonException
    {
        this.defaultServiceInstanceUserName = userName;
    }


    /**
     * デフォルトDNSサービスインスタンスのインスタンス名を返す.
     * @return
     * @throws DNSServiceCommonException
     */
    public String getDefaultServiceInstanceName() throws DNSServiceCommonException
    {
        return this.defaultServiceInstanceName;
    }

    /**
     * デフォルトDNSサービスインスタンスのインスタンス名を設定する.
     * @param serviceInstanceName
     * @throws DNSServiceCommonException
     */
    public void setDefaultServiceInstanceName(String serviceInstanceName) throws DNSServiceCommonException
    {
        this.defaultServiceInstanceName = serviceInstanceName;
    }


    /**
     * 初期化処理
     * 
     * @param properties
     * @throws DNSServiceCommonException
     */
    public void init(Properties properties) throws DNSServiceCommonException
    {
        // デフォルトDNSインスタンスが所属するユーザー名.
        String userName = properties.getProperty( LoulanDNSConstants.PROP_KEY_DEFAULT_SERVICE_INSTANCE_USER_NAME );
        setDefaultServiceInstanceUserName(userName);


        // デフォルトDNSインスタンスのインスタンス名.
        String instanceName = properties.getProperty( LoulanDNSConstants.PROP_KEY_DEFAULT_SERVICE_INSTANCE_NAME );
        setDefaultServiceInstanceName(instanceName);

    }



    /**
     * DNSサービスインスタンスを取得する.
     * サービスインスタンスが存在しなければ新規に作成して返す.
     * サービスインスタンスが既に存在する場合は登録済みのインスタンスを返却する.
     * 
     * @param dnsServiceInstanceID
     * @return DNSサービスインスタンス
     * @throws DNSServiceCommonException
     */
    public IDNSServiceInstance getOrCreateDNSServiceInstance(long dnsServiceInstanceID ) throws DNSServiceCommonException
    {
        IDNSServiceInstance dnsServiceInstance = createDNSServiceInstance(dnsServiceInstanceID);
        return dnsServiceInstance;
    }

    /**
     * DNSサービスインスタンスを取得する.
     * サービスインスタンスが存在しなければ新規に作成して返す.
     * サービスインスタンスが既に存在する場合は登録済みのインスタンスを返却する.
     * 
     * @param userName
     * @param dnsServiceInstanceName
     * @return DNSサービスインスタンス
     * @throws DNSServiceCommonException
     */
    public IDNSServiceInstance getOrCreateDNSServiceInstance(String userName, String dnsServiceInstanceName) throws DNSServiceCommonException
    {
        IDNSServiceInstance dnsServiceInstance = createDNSServiceInstance(userName, dnsServiceInstanceName);
        return dnsServiceInstance;
    }


    /**
     * デフォルトDNSサービスインスタンス(IDNSServiceInstance)のファクトリメソッド.
     * 既に生成済みのデフォルトDNSサービスインスタンスがメモリ上に存在する場合はそれを返す.
     * デフォルトDNSサービスインスタンスが存在しなければ新規作成して返す.
     * 
     * @return
     * @throws DNSServiceCommonException
     */
    public IDNSServiceInstance getOrCreateDefaultDNSServiceInstance() throws DNSServiceCommonException
    {
        String userName = getDefaultServiceInstanceUserName();
        if ( userName == null )
        {
            String msg = String.format("Failed to get/create Default DNSServiceInstance. Invalid userName. userName=%s", userName );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        String instanceName = getDefaultServiceInstanceName();
        if ( instanceName == null )
        {
            String msg = String.format("Failed to get/create Default DNSServiceInstance. Invalid instanceName. instanceName=%s", instanceName );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        IDNSServiceInstance dnsServiceInstance = getOrCreateDNSServiceInstance(userName, instanceName);
        return dnsServiceInstance;
    }


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
    public IDNSServiceInstance getOrCreateTemporaryDNSServiceInstance(String userName, String dnsServiceInstanceName, long dnsServiceInstanceTypeCode, Properties dnsServiceInstanceProperties, IDNSResolverInstance dnsResolverInstance  ) throws DNSServiceCommonException
    {

        IDNSServiceInstance instance;

        // 既にキャッシュに載っている場合はそれを返す.
        instance = getDNSServiceInstanceCacheEntry(userName, dnsServiceInstanceName);
        if ( instance != null )
        {
            return instance;
        }

        // DNSサービスインスタンスのオブジェクトを新規に生成する.
        instance = createTemporaryDNSServiceInstance(userName, dnsServiceInstanceName, dnsServiceInstanceTypeCode, dnsServiceInstanceProperties, dnsResolverInstance);

        registDNSServiceInstanceCacheEntry( userName, dnsServiceInstanceName, instance);

        return instance;
    }



    /**
     * 生成済みのDNSサービスインスタンスを廃棄する.
     * 
     * @param userName
     * @param dnsServiceInstanceName
     * @throws DNSServiceCommonException
     */
    public void destoryDNSServiceInstance(String userName, String dnsServiceInstanceName) throws DNSServiceCommonException
    {

    }


    /**
     * DNSサービスインスタンスが生成済みかを判定する.
     * 
     * @param userName
     * @param dnsServiceInstanceName
     * @throws DNSServiceCommonException
     */
    public boolean isExistsOnMemoryDNSServiceInstance(String userName, String dnsServiceInstanceName) throws DNSServiceCommonException
    {

        IDNSServiceInstance instance = getDNSServiceInstanceCacheEntry(userName, dnsServiceInstanceName);

        if ( instance == null )
        {
            return false;
        }

        return true;
    }


    /**
     * DNSサービスインスタンスが生成済みかを判定する.
     * 
     * @param dnsServiceInstanceID
     * @throws DNSServiceCommonException
     */
    public boolean isExistsOnMemoryDNSServiceInstance(long dnsServiceInstanceID) throws DNSServiceCommonException
    {

        IDNSServiceInstance instance = getDNSServiceInstanceCacheEntry(dnsServiceInstanceID);

        if ( instance == null )
        {
            return false;
        }

        return true;

        

    }






    // DBから論理モデルを読み込んで本クラスに設定する.
    private DNSServiceInstanceInfo loadLogicalModel(String userName, String dnsServiceInstanceName ) throws DNSServiceCommonException
    {        
        UserInfo userInfo = loulanDNSLogicalModelService.getUserInfo(userName);
        if ( userInfo == null )
        {
            return null;
        }

        DNSServiceInstanceInfo dnsServiceInstanceInfo = userInfo.getDNSServiceInstanceInfo( dnsServiceInstanceName );
        return dnsServiceInstanceInfo;
    }

    // DBから論理モデルを読み込んで本クラスに設定する.
    private DNSServiceInstanceInfo loadLogicalModel(long dnsServiceInstanceID) throws DNSServiceCommonException
    {        
        DNSServiceInstanceInfo dnsServiceInstanceInfo = loulanDNSLogicalModelService.getDNSServiceInstanceInfo(dnsServiceInstanceID);
        return dnsServiceInstanceInfo;
    }


    /**
     * DNSサービスインスタンスを新規に生成する.
     * 
     * @param dnsServiceInstanceInfo
     * @return
     * @throws DNSServiceCommonException
     */
    protected IDNSServiceInstance createDNSServiceInstance(DNSServiceInstanceInfo dnsServiceInstanceInfo) throws DNSServiceCommonException
    {
        Long dnsResolverInstanceID = dnsServiceInstanceInfo.getDNSResolverInstanceID();
        IDNSResolverInstance resolverInstance = dnsResolerInstanceFactory.getOrCreateResolverInstance(dnsResolverInstanceID);

        IDNSServiceInstance dnsServiceInstance = new DNSServiceInstanceImpl(dnsServiceInstanceInfo, resolverInstance);

        // ロガークラスを設定.
        ( (DNSServiceInstanceImpl)dnsServiceInstance ).setLoulanDNSLoggerFactory( this.loulanDNSLoggerFactory );

        return dnsServiceInstance;
    }

    /**
     * DNSサービスインスタンスを新規に生成する.
     * 
     * @param userName
     * @param dnsServiceInstanceName
     * @return
     * @throws DNSServiceCommonException
     */
    protected IDNSServiceInstance createDNSServiceInstance(String userName, String dnsServiceInstanceName) throws DNSServiceCommonException
    {
        DNSServiceInstanceInfo dnsServiceInstanceInfo = loadLogicalModel(userName, dnsServiceInstanceName);
        if ( dnsServiceInstanceInfo == null )
        {
            String msg = String.format("Failed to create DNSServiceInstance. Specified DNSServiceInstance is not found. userName=%s, dnsServiceInstanceName=%s", userName, dnsServiceInstanceName );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }
        
        IDNSServiceInstance dnsServiceInstance = createDNSServiceInstance(dnsServiceInstanceInfo);
        return dnsServiceInstance;
    }

    /**
     * DNSサービスインスタンスを新規に生成する.
     * 
     * @param dnsServiceInstanceID
     * @return
     * @throws DNSServiceCommonException
     */
    protected IDNSServiceInstance createDNSServiceInstance(long dnsServiceInstanceID) throws DNSServiceCommonException
    {
        DNSServiceInstanceInfo dnsServiceInstanceInfo = loadLogicalModel(dnsServiceInstanceID);
        if ( dnsServiceInstanceInfo == null )
        {
            String msg = String.format("Failed to create DNSServiceInstance. Specified DNSServiceInstance is not found. dnsServiceInstanceID=%d", dnsServiceInstanceID );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }
        
        IDNSServiceInstance dnsServiceInstance = createDNSServiceInstance(dnsServiceInstanceInfo);
        return dnsServiceInstance;
    }


    /**
     * 一時的なDNSサービスインスタンスを生成する.
     * 
     * @param userName
     * @param dnsServiceInstanceName
     * @param dnsServiceInstanceTypeCode
     * @param dnsServiceInstanceProperties
     * @param resolverInstance
     * @return
     * @throws DNSServiceCommonException
     */
    public IDNSServiceInstance createTemporaryDNSServiceInstance(String userName, String dnsServiceInstanceName, long dnsServiceInstanceTypeCode, Properties dnsServiceInstanceProperties, IDNSResolverInstance resolverInstance ) throws DNSServiceCommonException
    {

        DNSServiceInstanceInfo info = new DNSServiceInstanceInfo();
        
        info.setDNSServiceInstanceID(null);
        info.setUserID(null);

        info.setDNSServiceInstanceName(dnsServiceInstanceName);
        info.setDNSServiceInstanceExplain(null);
        info.setDnsServiceTypeCode(dnsServiceInstanceTypeCode);

        // info.setDNSResolverInstanceID(null);
        info.setRecordStatus(LoulanDNSConstants.DB_CONST_VALUE_RECORD_STATUS_ACTIVE);

        ZonedDateTime currentDateTime = loulanDNSUtils.getCurrentZonedDateTime();
        info.setCreateDate( loulanDNSUtils.toDateTimeString(currentDateTime) );
        info.setUpdateDate( loulanDNSUtils.toDateTimeString(currentDateTime) );


        IDNSServiceInstance dnsServiceInstance = new DNSServiceInstanceImpl(info, resolverInstance);

        // ロガークラスを設定.
        ( (DNSServiceInstanceImpl)dnsServiceInstance ).setLoulanDNSLoggerFactory( this.loulanDNSLoggerFactory );

        return dnsServiceInstance;
    }


    /**
     * DNSサービスインスタンスを登録する.
     * 
     * @param dnsServiceInstanceID
     * @param dnsServiceInstance
     * @throws DNSServiceCommonException
     */
    protected void registDNSServiceInstanceCacheEntry(String userName, String dnsServiceInstanceName, IDNSServiceInstance dnsServiceInstance) throws DNSServiceCommonException
    {

        if ( getDNSServiceInstanceCacheEntry(userName, dnsServiceInstanceName) != null )
        {
            // 既に登録済みのインスタンスが存在する.
            String msg = String.format("Failed to regist DNSServiceInstance on list. Specifed instance is already registered. userName=%s, dnsServiceInstanceName=%s", userName, dnsServiceInstanceName );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        synchronized(this.dnsServiceInstanceCache)
        {
            String key = getDNSServiceInstanceCacheKey(userName, dnsServiceInstanceName);
            dnsServiceInstanceCache.put(key, dnsServiceInstance);
        }
        
    }




    /**
     * 登録済みのDNSサービスインスタンスを返す.
     * 存在しない場合はnullを返す.
     * 
     * @param dnsServiceInstanceID
     * @return
     * @throws DNSServiceCommonException
     */
    protected IDNSServiceInstance getDNSServiceInstanceCacheEntry(long dnsServiceInstanceID) throws DNSServiceCommonException
    {
        // DBからモデル情報クラスをロードする.
        DNSServiceInstanceInfo  dnsServiceInstanceInfo = loulanDNSLogicalModelService.getDNSServiceInstanceInfo(dnsServiceInstanceID);
        UserInfo userInfo = loulanDNSLogicalModelService.getUserInfo(dnsServiceInstanceInfo.getUserID());

        IDNSServiceInstance instance = getDNSServiceInstanceCacheEntry( userInfo.getUserName(), dnsServiceInstanceInfo.getDNSServiceInstanceName());
        return instance;
    }


    /**
     * キャッシュに登録済みのDNSサービスインスタンスを返す.
     * 存在しない場合はnullを返す.
     * 
     * @param userName
     * @param dnsServiceInstanceName
     * @return
     * @throws DNSServiceCommonException
     */
    protected IDNSServiceInstance getDNSServiceInstanceCacheEntry(String userName, String dnsServiceInstanceName) throws DNSServiceCommonException
    {
        IDNSServiceInstance instance = null;

        String cacheKey = getDNSServiceInstanceCacheKey(userName, dnsServiceInstanceName);

                
        synchronized(this.dnsServiceInstanceCache)
        {
            instance = this.dnsServiceInstanceCache.get(cacheKey);
        }

        return instance;
    }



    /**
     * DNSサービスインスタンスのキャッシュで使用するキー値を生成取得する.
     * 
     * @param userName
     * @param dnsServiceInstanceName
     * @return
     * @throws DNSServiceCommonException
     */
    protected String getDNSServiceInstanceCacheKey(String userName, String dnsServiceInstanceName) throws DNSServiceCommonException
    {
        String key = String.format("%s/%s", userName, dnsServiceInstanceName);
        return key;
    }


}

