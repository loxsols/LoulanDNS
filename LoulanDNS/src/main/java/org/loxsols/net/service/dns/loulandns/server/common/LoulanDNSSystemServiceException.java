package org.loxsols.net.service.dns.loulandns.server.common;


// LoulanDNSのシステムサービス層の例外クラス
public class LoulanDNSSystemServiceException extends DNSServiceCommonException
{

    public LoulanDNSSystemServiceException(String msg)
    {
        super(msg);
    }


    public LoulanDNSSystemServiceException(String msg, Throwable cause)
    {
        super(msg, cause);
    }


}
