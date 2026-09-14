package org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.domain;

import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSResponseMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.common.DNSCacheServiceCommonException;

/**
 * LoulanDNSのドメインキャッシュサービスの共通例外クラス.
 * 
 * DNSDomainCacheServiceException
 */
public class DNSDomainCacheServiceException extends DNSCacheServiceCommonException
{

    public DNSDomainCacheServiceException(String msg)
    {
        super(msg);
    }


    public DNSDomainCacheServiceException(String msg, Throwable cause)
    {
        super(msg, cause);
    }
    
}