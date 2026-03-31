package org.loxsols.net.service.dns.loulandns.server.http.spring.test;

import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.common.LoulanDNSSystemServiceException;

/**
 * Spring系機能のテスト実行時の例外クラス.
 * 
 */
public class LoulanDNDSpringTestException extends DNSServiceCommonException
{

    
    public LoulanDNDSpringTestException(String msg)
    {
        super(msg);
    }


    public LoulanDNDSpringTestException(String msg, Throwable cause)
    {
        super(msg, cause);
    }

}