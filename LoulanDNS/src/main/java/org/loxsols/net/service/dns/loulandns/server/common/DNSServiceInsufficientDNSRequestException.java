package org.loxsols.net.service.dns.loulandns.server.common;


// 不完全なDNSリクエストを処理した場合の例外クラス.
public class DNSServiceInsufficientDNSRequestException extends InsufficientDNSMessageException
{

    public DNSServiceInsufficientDNSRequestException(String msg)
    {
        super(msg);
    }


    public DNSServiceInsufficientDNSRequestException(String msg, Throwable cause)
    {
        super(msg, cause);
    }


}
