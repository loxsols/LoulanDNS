package org.loxsols.net.service.dns.loulandns.server.common;


/**
 * 破損したDNSメッセージを処理した場合の例外クラス.
 */
public class MalformedDNSMessageException extends DNSProtocolException
{

    public MalformedDNSMessageException(String msg)
    {
        super(msg);
    }


    public MalformedDNSMessageException(String msg, Throwable cause)
    {
        super(msg, cause);
    }


}
