package org.loxsols.net.service.dns.loulandns.client.subway.common.exception;

import org.loxsols.net.service.dns.loulandns.client.common.DNSClientCommonException;
import org.loxsols.net.service.dns.loulandns.client.subway.common.DNSSubwayCommonException;

/**
 * IPアドレス空間のサブネット表記(CIDR形式 : Ex."128.0.0.0/8")に適合しない場合の例外.
 * 
 * InvalidIPSubnetFormatException
 */
public class InvalidIPSubnetFormatException extends DNSSubwayCommonException
{

    String ipSubnetFormatString = null;

    public void setIPSubnetFormatString(String value)
    {
        this.ipSubnetFormatString = value;
    }

    public String getIPSubnetFormatString()
    {
        return this.ipSubnetFormatString;
    }

    public InvalidIPSubnetFormatException(String msg, Throwable cause, String ipSubnetFormatString)
    {
        super(msg, cause);
        setIPSubnetFormatString(ipSubnetFormatString);
    }

    public InvalidIPSubnetFormatException(String msg, String ipSubnetFormatString)
    {
        super(msg);
        setIPSubnetFormatString(ipSubnetFormatString);
    }

}