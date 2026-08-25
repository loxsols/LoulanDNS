package org.loxsols.net.service.dns.loulandns.client.subway.common;


import org.loxsols.net.service.dns.loulandns.client.subway.*;

public interface IDNSSubwayGatewayDescriptor
{
    public void setDNSSubwayDescriptorName(String value) throws DNSSubwayCommonException;
    public String getDNSSubwayDescriptorName() throws DNSSubwayCommonException;

    public void setTargetDName(String value) throws DNSSubwayCommonException;
    public String getTargetDName() throws DNSSubwayCommonException;

    public void setTargetProtocolType(String value) throws DNSSubwayCommonException;
    public String getTargetProtocolType() throws DNSSubwayCommonException;

    public void setTargetPortList(int[] portList) throws DNSSubwayCommonException;
    public int[] getTargetPortList() throws DNSSubwayCommonException;


    public void setGatewayAddress(String value) throws DNSSubwayCommonException;
    public String getGatewayAddress() throws DNSSubwayCommonException;
    
    
    public void setGatewayPortList(int[] value) throws DNSSubwayCommonException;
    public int[] gatGatewayPortList() throws DNSSubwayCommonException;

    public void setGatewayInstance(IDNSSubwayGateway value) throws DNSSubwayCommonException;
    public IDNSSubwayGateway getGatewayInstance() throws DNSSubwayCommonException;

}