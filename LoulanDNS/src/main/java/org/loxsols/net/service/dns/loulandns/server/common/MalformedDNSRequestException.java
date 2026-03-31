package org.loxsols.net.service.dns.loulandns.server.common;


// 破損したDNSリクエストを処理した場合の例外クラス.
public class MalformedDNSRequestException extends MalformedDNSMessageException
{

    public MalformedDNSRequestException(String msg)
    {
        super(msg);
    }


    public MalformedDNSRequestException(String msg, Throwable cause)
    {
        super(msg, cause);
    }


}
