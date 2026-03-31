package org.loxsols.net.service.dns.loulandns.server.common;

import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;


// DNSプロトコルの規定に起因する例外
public class DNSProtocolException extends DNSServiceCommonException
{

    public DNSProtocolException(String msg)
    {
        super(msg);
    }


    public DNSProtocolException(String msg, Throwable cause)
    {
        super(msg, cause);
    }



}