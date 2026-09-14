package org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.common;

import java.time.ZonedDateTime;
import java.util.Properties;

import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSResponseMessage;

/**
 * LoulanDNSの汎用キャッシュサービスのエントリ要素I/F
 * 
 * ILoulanDNSCommonCacheEntry
 */
public interface ILoulanDNSCommonCacheEntry
{

    /**
     * 本キャッシュエントリのキー値を返す.
     * 
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public String getKey() throws DNSCacheServiceCommonException;

    /**
     * 本キャッシュエントリの目的物(Ojbect型)を返す.
     * 
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public Object getObject() throws DNSCacheServiceCommonException;

    /**
     * 本キャッシュエントリのTTL値(設定時の値)を返す.
     * 
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public int getTTL() throws DNSCacheServiceCommonException;

    /**
     * 本キャッシュエントリのプロパティ値を返す.
     * 
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public Properties getOptions() throws DNSCacheServiceCommonException;

    /**
     * 本キャッシュエントリの作成日時をZonedDateTime型で返す.
     * 
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public ZonedDateTime getCreateZonedDateTime() throws DNSCacheServiceCommonException;

    /**
     * 本キャッシュエントリの作成日時をタイムゾーン付き時刻文字列(Ex."2024/06/11 12:34:56.123 JST")で返す.
     * 
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public String getCreateZonedDateTimeString()  throws DNSCacheServiceCommonException;


    /**
     * 本キャッシュエントリの更新日時をZonedDateTime型で返す.
     * 
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public ZonedDateTime getUpdateZonedDateTime() throws DNSCacheServiceCommonException;


    /**
     * 本キャッシュエントリの更新日時をタイムゾーン付き時刻文字列(Ex."2024/06/11 12:34:56.123 JST")で返す.
     * 
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public String getUpdateZonedDateTimeString() throws DNSCacheServiceCommonException;


    /**
     * TTLの残存時間(TTL設定値(sec) - 本キャッシュエントリの作成日時からの経過時間)を秒単位返す.
     * 
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public int getLeftTTLSeconds() throws DNSCacheServiceCommonException;
    
}