package org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.common.impl.base;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Properties;
import java.time.Duration;

import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSResponseMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.common.DNSCacheServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.common.ILoulanDNSCommonCacheEntry;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.common.ILoulanDNSCommonCacheService;
import org.loxsols.net.service.dns.loulandns.util.DNSUtils;

/**
 * LoulanDNSの汎用キャッシュサービスのエントリ要素の基本実装クラス.
 * 
 * LoulanDNSCommonCacheEntryImpl
 */
public class LoulanDNSCommonCacheEntryBaseImpl implements ILoulanDNSCommonCacheEntry
{

    String key;
    Object object;
    int ttl;
    Properties options;
    ZonedDateTime createDateTime;
    ZonedDateTime updateDateTime;


    /**
     * コンストラクタ.
     * 
     * @param key
     * @param object
     * @param ttl
     * @param properties
     * @throws DNSCacheServiceCommonException
     */
    public LoulanDNSCommonCacheEntryBaseImpl(String key, Object object, int ttl, Properties options) throws DNSCacheServiceCommonException
    {
        init(key, object, ttl, options);
    }


    /**
     * コンストラクタ.(外部非公開)
     * 
     * @param key
     * @param object
     * @param ttl
     * @param properties
     * @throws DNSCacheServiceCommonException
     */
    protected LoulanDNSCommonCacheEntryBaseImpl() throws DNSCacheServiceCommonException
    {
        String tmpKey = "tmp";
        Object tmpObj = "tmpObject";
        int tmpTTL = 3600;
        Properties tmpOptions = new Properties();

        init(tmpKey, tmpObj, tmpTTL, tmpOptions);
    }


    public void init(String key, Object object, int ttl, Properties options) throws DNSCacheServiceCommonException
    {

        setKey(key);
        setObject(object);
        setOptions(options);

        // 本キャッシュエントリの生成時刻と更新時刻を現在時刻に設定する.
        ZonedDateTime current = LoulanDNSUtils.getCurrentZonedDateTime();
        setCreateZonedDateTime(current);
        setUpdateZonedDateTime(current);

    }



    /**
     * 本キャッシュエントリのキー値を返す.
     * 
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public String getKey() throws DNSCacheServiceCommonException
    {
        return this.key;
    }

    public void setKey(String value) throws DNSCacheServiceCommonException
    {
        this.key = value;
    }

    /**
     * 本キャッシュエントリの目的物(Ojbect型)を返す.
     * 
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public Object getObject() throws DNSCacheServiceCommonException
    {
        return this.object;
    }

    public void setObject(Object value) throws DNSCacheServiceCommonException
    {
        this.object = value;
    }

    /**
     * 本キャッシュエントリのTTL値(設定時の値)を返す.
     * 
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public int getTTL() throws DNSCacheServiceCommonException
    {
        return this.ttl;
    }

    public void setTTL(int value) throws DNSCacheServiceCommonException
    {
        this.ttl = value;
    }



    /**
     * 本キャッシュエントリのプロパティ値を返す.
     * 
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public Properties getOptions() throws DNSCacheServiceCommonException
    {
        return this.options;
    }

    public void setOptions(Properties value) throws DNSCacheServiceCommonException
    {
        this.options = value;
    }



    /**
     * 本キャッシュエントリの作成日時を設定する.
     * 
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public void setCreateZonedDateTime(ZonedDateTime createDateTime) throws DNSCacheServiceCommonException
    {
        this.createDateTime = createDateTime;
    }


    /**
     * 本キャッシュエントリの作成日時をZonedDateTime型で返す.
     * 
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public ZonedDateTime getCreateZonedDateTime() throws DNSCacheServiceCommonException
    {
        return this.createDateTime;
    }

    /**
     * 本キャッシュエントリの作成日時をタイムゾーン付き時刻文字列(Ex."2024/06/11 12:34:56.123 JST")で返す.
     * 
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public String getCreateZonedDateTimeString() throws DNSCacheServiceCommonException
    {
        ZonedDateTime dateTime = getCreateZonedDateTime();
        String dateTimeString = LoulanDNSUtils.toDateTimeString(dateTime);
        return dateTimeString;
    }



    /**
     * 本キャッシュエントリの更新日時を設定する.
     * 
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public void setUpdateZonedDateTime(ZonedDateTime updateDateTime) throws DNSCacheServiceCommonException
    {
        this.updateDateTime = updateDateTime;
    }


    /**
     * 本キャッシュエントリの更新日時をZonedDateTime型で返す.
     * 
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public ZonedDateTime getUpdateZonedDateTime() throws DNSCacheServiceCommonException
    {
        return this.updateDateTime;
    }


    /**
     * 本キャッシュエントリの更新日時をタイムゾーン付き時刻文字列(Ex."2024/06/11 12:34:56.123 JST")で返す.
     * 
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public String getUpdateZonedDateTimeString() throws DNSCacheServiceCommonException
    {
        ZonedDateTime dateTime = getUpdateZonedDateTime();
        String dateTimeString = LoulanDNSUtils.toDateTimeString(dateTime);
        return dateTimeString;
    }




    /**
     * TTLの残存時間(TTL設定値(sec) - 本キャッシュエントリの作成日時からの経過時間)を秒単位返す.
     * 
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public int getLeftTTLSeconds() throws DNSCacheServiceCommonException
    {
        int leftTTL;
        if ( hasUnlimitedTTL() )
        {
            // TTLが無限の場合はint型の最大値を返す.
            leftTTL = Integer.MAX_VALUE;
        }
        else
        {
            // TTLが有限の場合は、現在時刻からTTL満了時刻までの残り時間を秒単位で返す.

            ZonedDateTime expireDateTime = getExpireZonedDateTime();
            ZonedDateTime nowDateTime = ZonedDateTime.now();
            
            // 差分を秒単位で取得
            Duration duration = Duration.between(nowDateTime, expireDateTime);
            leftTTL = (int)duration.getSeconds();
        }

        return leftTTL;
    }


    /**
     * 本キャッシュエントリの失効日時をZonedDateTime型で返す.
     * 
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public ZonedDateTime getExpireZonedDateTime() throws DNSCacheServiceCommonException
    {
        ZonedDateTime expireDateTime;

        if ( hasUnlimitedTTL() == false )
        {
            // TTL値が有限時間の場合はキャッシュ満了予定時刻を計算して返す.
            ZonedDateTime createDateTime = getCreateZonedDateTime();
            int ttl = getTTL();
        
            expireDateTime = createDateTime.plusSeconds(ttl);
        }
        else
        {
            // TTL値が無限時間の場合は(実質)無限時間を返す.
            expireDateTime = ZonedDateTime.of(LocalDateTime.MAX, ZoneId.systemDefault() );
        }

        return expireDateTime;
    }


    /**
     * 失効まで無限に時間がある場合はtrueを返す.
     * 
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public boolean hasUnlimitedTTL() throws DNSCacheServiceCommonException
    {
        int ttl = getTTL();

        if ( ttl < 0 )
        {
            // TTL値が負数の場合は無限時間とみなす.
            return true;
        }

        if ( ttl == Integer.MAX_VALUE )
        {
            // TTL値がint型の最大値の場合も無限時間とみなす.
            return true;
        }

        return true;
    }


    
}