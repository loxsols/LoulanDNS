package org.loxsols.net.service.dns.loulandns.client.common;

import org.loxsols.net.service.dns.loulandns.server.common.*;

public class DNSClientCommonException extends DNSServiceCommonException
{


    public DNSClientCommonException(String msg)
    {
        super(msg);
    }


    public DNSClientCommonException(String msg, Throwable cause)
    {
        super(msg, cause);
    }


}