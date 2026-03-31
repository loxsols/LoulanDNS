package org.loxsols.net.service.dns.loulandns.server.common;


/**
 * LoulanDNSの共通ユーティリティ処理に関連する例外
 */
public class LoulanCommonUtilityException extends DNSServiceCommonException
{

    public LoulanCommonUtilityException(String msg)
    {
        super(msg);
    }


    public LoulanCommonUtilityException(String msg, Throwable cause)
    {
        super(msg, cause);
    }


}
