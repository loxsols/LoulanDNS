package org.loxsols.net.service.dns.loulandns.client.subway.common.impl;


import org.loxsols.net.service.dns.loulandns.client.subway.*;
import org.loxsols.net.service.dns.loulandns.client.subway.common.DNSSubwayCommonException;
import org.loxsols.net.service.dns.loulandns.client.subway.common.IDNSSubwayGatewayDescriptor;

public class DNSSubwayGatewayInstanceDescriptorImpl implements IDNSSubwayGatewayDescriptor
{

    public String dnsSubwayDescriptorName;

    public String targetDName;
    public String targetProtocolType;
    public int[] targetPortList;

    public String gatewayAddress;
    public int[] gatewayPortList;

    public IDNSSubwayGateway gatewayInstance;

    public void setDNSSubwayDescriptorName(String value) throws DNSSubwayCommonException
    {
        this.dnsSubwayDescriptorName = value;
    }

    public String getDNSSubwayDescriptorName() throws DNSSubwayCommonException
    {
        return dnsSubwayDescriptorName;
    }


    public void setTargetDName(String value) throws DNSSubwayCommonException
    {
        this.targetDName = value;
    }

    public String getTargetDName() throws DNSSubwayCommonException
    {
        return this.targetDName;
    }


    public void setTargetProtocolType(String value) throws DNSSubwayCommonException
    {
        this.targetProtocolType = value;
    }

    public String getTargetProtocolType() throws DNSSubwayCommonException
    {
        return this.targetProtocolType;
    }
    

    
    public void setTargetPortList(int[] value) throws DNSSubwayCommonException
    {
        this.targetPortList = value;
    }

    public int[] getTargetPortList() throws DNSSubwayCommonException
    {
        return this.targetPortList;
    }


    public void setGatewayAddress(String value) throws DNSSubwayCommonException
    {
        this.gatewayAddress = value;    
    }

    public String getGatewayAddress() throws DNSSubwayCommonException
    {
        return this.gatewayAddress;
    }



    public void setGatewayPortList(int[] portList) throws DNSSubwayCommonException
    {
        this.gatewayPortList = portList;
    }

    public int[] gatGatewayPortList() throws DNSSubwayCommonException
    {
        return this.gatewayPortList;
    }


    public void setGatewayInstance(IDNSSubwayGateway value) throws DNSSubwayCommonException
    {
        this.gatewayInstance = value;
    }
    
    public IDNSSubwayGateway getGatewayInstance() throws DNSSubwayCommonException
    {
        return this.getGatewayInstance();
    }


}