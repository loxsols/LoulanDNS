package org.loxsols.net.service.dns.loulandns.server.http.spring.common.exception;



import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;


import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;


@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class HttpStatus404Exception extends DNSServiceCommonException
{

    public HttpStatus404Exception(String msg)
    {
        super(msg);
    }


    public HttpStatus404Exception(String msg, Throwable cause)
    {
        super(msg, cause);
    }
    
}
