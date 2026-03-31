package org.loxsols.net.service.dns.loulandns.server.common;


// 破損したドメイン名を処理した場合の例外クラス.
public class MalformedDomainNameException extends DNSProtocolException
{

    public MalformedDomainNameException(String msg)
    {
        super(msg);
    }


    public MalformedDomainNameException(String msg, Throwable cause)
    {
        super(msg, cause);
    }


}
