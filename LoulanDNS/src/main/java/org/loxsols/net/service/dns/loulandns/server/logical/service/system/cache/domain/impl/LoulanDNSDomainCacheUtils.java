package org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.domain.impl;

import java.time.ZonedDateTime;
import java.util.Properties;

import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSQuestionMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSResponseMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.IDNSResourceRecord;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.IDNSQueryPart;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.common.DNSCacheServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.domain.ILoulanDNSDomainCacheEntry;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.common.*;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.common.impl.base.*;

import io.netty.handler.codec.dns.DnsMessage;



/**
 * LoulanDNSのドメインキャッシュサービスのユーティリティクラス.
 * 
 * LoulanDNSDomainCacheUtils
 */
public class LoulanDNSDomainCacheUtils
{

    /**
     * 
     * 
     * @param domainName
     * @param queryType
     * @param queryClass
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public static String buildDomainCacheKey(String domainName, int queryType, int queryClass) throws DNSCacheServiceCommonException
    {
        String domainCacheKey = String.format("%s/%d/%d", domainName, queryType, queryClass );
        return domainCacheKey;
    }


    /**
     * DNSレスポンスメッセージが適当なRRを保持している場合はtrueを返す.
     * DNSレスポンスメッセージにRRが含まれないケース(問い合わせに対応するドメインが存在しない場合など)ではfalseを返す.
     * 
     * @param responseMessage
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public static boolean hasValidDNSResponseRR(IDNSResponseMessage responseMessage) throws DNSCacheServiceCommonException
    {
        try
        {
            if ( responseMessage.getDNSHeaderSection().getANCOUNT() == 0 )
            {
                // ヘッダセクションのANカウントが0
                return false;
            }

            if ( responseMessage.getDNSAnswerSection().getDNSRRCount() == 0 )
            {
                // アンサーセクションのRRカウントが0
                return false;
            }

            IDNSResourceRecord[] rrArray = responseMessage.getDNSAnswerSection().getDNSResourceRecords();
            if ( rrArray.length == 0 )
            {
                // リソースレコード(RR)の個数が0個.(通常はここには入らない.)
                return false;
            }
        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to check Valid DNSResponse RR.");
            DNSCacheServiceCommonException exception = new DNSCacheServiceCommonException(msg, cause);
            throw exception;
        }

        return true;
    }


    /**
     * DNSレスポンスメッセージのTTL値を取得する.
     * DNSレスポンスメッセージ中に複数のリソースレコード(RR)が存在する場合、その中で最も小さいTTL値を返す.
     * 
     * @param responseMessage
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public static Integer getDNSResponseMessageMinimumTTL(IDNSResponseMessage responseMessage) throws DNSCacheServiceCommonException
    {
        if ( hasValidDNSResponseRR(responseMessage) == false )
        {
            // 指定されたDNSレスポンスメッセージは有効なリソースレコードを持たないため、nullを返す.
            return null;
        }

        int minTTL = Integer.MAX_VALUE;
        try
        {
            // RRを全て舐めて、最小TTLを算出する.
            IDNSResourceRecord[] rrArray = responseMessage.getDNSAnswerSection().getDNSResourceRecords();
            for( IDNSResourceRecord rr : rrArray )
            {
                int rrTTL = rr.getResourceTTL();
                if ( rrTTL < minTTL )
                {
                    // このRRのTTLで最小値を更新する.
                    minTTL = rrTTL;
                }
            }
        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to get DNSResponse RR TTL.");
            DNSCacheServiceCommonException exception = new DNSCacheServiceCommonException(msg, cause);
            throw exception;
        }

        return minTTL;
    }


    public static String getDNSMessageQName(IDNSMessage dnsMessage) throws DNSCacheServiceCommonException
    {
        String qName;
        int qType, qClass;
        try
        {
            IDNSQueryPart query = dnsMessage.getDNSQuestionSection().getDNSQueries()[0];
            qName = query.getDNSQueryName();
            qType = query.getDNSQueryType();
            qClass = query.getDNSQueryClass();

        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to get DNS Message Question parameters.");
            DNSCacheServiceCommonException exception = new DNSCacheServiceCommonException(msg, cause);
            throw exception;
        }
        return qName;
    }

    public static int getDNSMessageQType(IDNSMessage dnsMessage) throws DNSCacheServiceCommonException
    {
        String qName;
        int qType, qClass;
        try
        {
            IDNSQueryPart query = dnsMessage.getDNSQuestionSection().getDNSQueries()[0];
            qName = query.getDNSQueryName();
            qType = query.getDNSQueryType();
            qClass = query.getDNSQueryClass();

        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to get DNS Message Question parameters.");
            DNSCacheServiceCommonException exception = new DNSCacheServiceCommonException(msg, cause);
            throw exception;
        }
        return qType;
    }

    public static int getDNSMessageQClass(IDNSMessage dnsMessage) throws DNSCacheServiceCommonException
    {
        String qName;
        int qType, qClass;
        try
        {
            IDNSQueryPart query = dnsMessage.getDNSQuestionSection().getDNSQueries()[0];
            qName = query.getDNSQueryName();
            qType = query.getDNSQueryType();
            qClass = query.getDNSQueryClass();

        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to get DNS Message Question parameters.");
            DNSCacheServiceCommonException exception = new DNSCacheServiceCommonException(msg, cause);
            throw exception;
        }
        return qClass;
    }


    /**
     * DNSレスポンスメッセージのキャッシュデータから、レスポンスメッセージの電文を生成する.
     * 具体的には、ヘッダー部のID値をリクエスト時のIDに書き換える.
     * TODO : DNSクッキーには非対応.
     * 
     * @param cachedMessage
     * @param questionMessage
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public static IDNSResponseMessage createDNSResponseMessageFromDomainCacheEntry(ILoulanDNSDomainCacheEntry cacheEntry, IDNSQuestionMessage questionMessage) throws DNSCacheServiceCommonException
    {
         IDNSResponseMessage responseMessage;
         
         
         try
         {
            responseMessage = (IDNSResponseMessage)cacheEntry.getDNSresponseMessage().cloneDNSMessage();

            int id = questionMessage.getDNSHeaderSection().getID();
            responseMessage.getDNSHeaderSection().setID(id);

            // TODO : DNSクッキーの処理は未実装.

            int leftTTL = cacheEntry.getLeftTTLSeconds();

            // 2026/09/11 : NODATAレスポンス対応(getDNSAnswerSection()メソッドはnullを返すことがあるのでヘッダセクションのANCountを参照してから処理する)
            if ( responseMessage.getDNSHeaderSection().getANCOUNT() > 0 )
            {
                // RRのTTLを更新する.
                for( var rr : responseMessage.getDNSAnswerSection().getDNSResourceRecords() )
                {
                    if ( rr.getResourceTTL() > leftTTL )
                    {
                        // RRのTTL値が本ドメインキャッシュ全体に適用されたTTL値を超える場合は、ドメインキャッシュのTTL値を優先する.
                        rr.setResourceTTL(leftTTL);
                    }
                }
            }
        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to create DNSResponse Message from DomainCache contents. domainCacheKey=%s", cacheEntry.getDomainCacheKey() );
            DNSCacheServiceCommonException exception = new DNSCacheServiceCommonException(msg, cause);
            throw exception;
        }

         return responseMessage;
    }



    

}