package org.loxsols.net.service.dns.loulandns.server.common;


// DNSサービスの汎用的な例外クラス.
public class DNSServiceCommonException extends Exception
{

    public DNSServiceCommonException(String msg)
    {
        super(msg);
    }


    public DNSServiceCommonException(String msg, Throwable cause)
    {
        super(msg, cause);
    }


}
