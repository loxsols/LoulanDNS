package org.loxsols.net.service.dns.loulandns.client.subway.common;

import org.loxsols.net.service.dns.loulandns.client.common.DNSClientCommonException;

public class DNSSubwayCommonException extends DNSClientCommonException
{

    public DNSSubwayCommonException(String msg, Throwable cause)
    {
        super(msg, cause);
    }

    public DNSSubwayCommonException(String msg)
    {
        super(msg);
    }

}