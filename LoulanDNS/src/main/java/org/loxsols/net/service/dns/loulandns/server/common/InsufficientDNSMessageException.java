package org.loxsols.net.service.dns.loulandns.server.common;


/**
 * 長さが不十分なDNSメッセージを処理した場合の例外クラス.
 */
public class InsufficientDNSMessageException extends MalformedDNSRequestException
{

    public InsufficientDNSMessageException(String msg)
    {
        super(msg);
    }


    public InsufficientDNSMessageException(String msg, Throwable cause)
    {
        super(msg, cause);
    }


}
