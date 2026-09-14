package org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.domain;

import java.time.ZonedDateTime;
import java.util.Properties;

import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSResponseMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.common.DNSCacheServiceCommonException;

import io.netty.handler.codec.dns.DnsMessage;

/**
 * LoulanDNSのドメインキャッシュサービスのエントリ要素I/F
 * 
 * ILoulanDNSDomainCacheEntry
 */
public interface ILoulanDNSDomainCacheEntry
{


    /**
     * 本ドメインキャッシュのキー値を返す.
     * 
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public String getDomainCacheKey() throws DNSCacheServiceCommonException;
    

    /**
     * 本ドメインキャッシュエントリのドメイン名を返す.
     * 
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public String getDomainName() throws DNSCacheServiceCommonException;

    /**
     * 本ドメインキャッシュエントリの問い合わせタイプ(Ex.A, AAAA, ANY...etc)を返す.
     * 
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public Integer getQueryType() throws DNSCacheServiceCommonException;


    /**
     * 本ドメインキャッシュエントリの問い合わせクラス(IN, CS, ...etc)を返す.
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public Integer getQueryClass() throws DNSCacheServiceCommonException;


    /**
     * 本ドメインキャッシュエントリの目的物たるDNSレスポンスメッセージを返す.
     * 
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public IDNSResponseMessage getDNSresponseMessage() throws DNSCacheServiceCommonException;




    /**
     * 本ドメインキャッシュエントリのTTL値(設定時の値)を返す.
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
    public String getCreateZonedDateTimeString() throws DNSCacheServiceCommonException;


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