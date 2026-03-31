
package org.loxsols.net.service.dns.loulandns.server.logical.service.dns.resolver.impl;

import java.util.*;

import org.loxsols.net.service.dns.loulandns.client.IDNSLookupClient;
import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.*;

import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.resolver.IDNSResolverInstance;

/**
 * DNSリゾルバインスタンスの実体クラス.
 */
public abstract class DNSResolverInstanceBaseImpl implements IDNSResolverInstance
{

    IDNSLookupClient dnsLookupClient;

    public void setDNSLookupClient(IDNSLookupClient client) throws DNSServiceCommonException
    {
        this.dnsLookupClient = client;
    }

    public IDNSLookupClient getDNSLookupClient() throws DNSServiceCommonException
    {
        return this.dnsLookupClient;
    }


    /**
     * コンストラクタ
     * 
     * @throws DNSServiceCommonException
     */
    public DNSResolverInstanceBaseImpl(Properties properties) throws DNSServiceCommonException
    {
        init(properties);
    }




    // DNSクエリ(クエリ部)を処理してDNSレスポンスメッセージを返す.
    public IDNSResponseMessage resolve(IDNSQueryPart dnsQueryPart) throws DNSServiceCommonException
    {
        IDNSLookupClient client = getDNSLookupClient();
        IDNSResponseMessage responseMessage = client.resolve(dnsQueryPart.getDNSQueryName(), dnsQueryPart.getDNSQueryType(), dnsQueryPart.getDNSQueryClass());
        return responseMessage;
    }

    // DNS問い合わせセクション(複数のDNSクエリ部からなるセクション)を処理してDNSレスポンスメッセージを返す.
    public IDNSResponseMessage resolve(IDNSQuestionSection dnsQueetionSection) throws DNSServiceCommonException
    {
        int sizeOfQueries = dnsQueetionSection.getDNSQueries().length;
        if ( sizeOfQueries != 1 )
        {
            String msg = String.format("Size of DNS Queries is not 1. Query count=%d.", sizeOfQueries);
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        IDNSQueryPart dnsQueryPart = dnsQueetionSection.getDNSQueries()[0];
        IDNSResponseMessage responseMessage = resolve(dnsQueryPart);

        return responseMessage;
    }

    // DNS問い合わせメッセージを処理してDNSレスポンスメッセージを返す.
    public IDNSResponseMessage resolve(IDNSQuestionMessage dnsQuestionMessage) throws DNSServiceCommonException
    {
        IDNSLookupClient client = getDNSLookupClient();

        if ( client == null )
        {
            String msg = String.format("Failed to resolve DNS question message. DNS Resolver Client object is not set.");
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        IDNSResponseMessage responseMessage = client.resolve(dnsQuestionMessage);

        return responseMessage;
    }


}