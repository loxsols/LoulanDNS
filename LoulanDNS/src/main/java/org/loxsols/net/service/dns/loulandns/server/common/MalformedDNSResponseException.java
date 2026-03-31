package org.loxsols.net.service.dns.loulandns.server.common;


// 破損したDNSリクエストを処理した場合の例外クラス.
public class MalformedDNSResponseException extends MalformedDNSMessageException
{

    public MalformedDNSResponseException(String msg)
    {
        super(msg);
    }


    public MalformedDNSResponseException(String msg, Throwable cause)
    {
        super(msg, cause);
    }


}
