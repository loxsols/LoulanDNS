package org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.common;

import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSResponseMessage;

/**
 * LoulanDNSのキャッシュサービスの共通例外クラス.
 * 
 * DNSCacheServiceCommonException
 */
public class DNSCacheServiceCommonException extends DNSServiceCommonException
{

    public DNSCacheServiceCommonException(String msg)
    {
        super(msg);
    }


    public DNSCacheServiceCommonException(String msg, Throwable cause)
    {
        super(msg, cause);
    }
    
}