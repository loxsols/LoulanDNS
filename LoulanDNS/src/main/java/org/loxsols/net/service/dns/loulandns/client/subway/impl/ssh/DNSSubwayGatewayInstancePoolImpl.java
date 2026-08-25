package org.loxsols.net.service.dns.loulandns.client.subway.impl.ssh;


import java.net.InetAddress;
import java.util.HashMap;

import org.loxsols.net.service.dns.loulandns.client.*;
import org.loxsols.net.service.dns.loulandns.client.impl.simple.*;
import org.loxsols.net.service.dns.loulandns.client.subway.factory.IDNSSubwayGatewayFactory;
import org.loxsols.net.service.dns.loulandns.client.subway.factory.IDNSSubwayGatewayInstancePoolFactory;
import org.loxsols.net.service.dns.loulandns.client.subway.factory.impl.SSHGatewayFactoryImpl;
import org.loxsols.net.service.dns.loulandns.client.common.DNSClientCommonException;
import org.loxsols.net.service.dns.loulandns.client.common.LoulanDNSClientConstants;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.model.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSQuestionMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSResponseMessage;
import org.loxsols.net.service.dns.loulandns.server.common.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;


import org.loxsols.net.service.dns.loulandns.client.subway.common.*;
import org.loxsols.net.service.dns.loulandns.client.subway.common.impl.DNSSubwayGatewayInstanceDescriptorImpl;
import org.loxsols.net.service.dns.loulandns.client.subway.*;

import java.util.Properties;
import java.util.Map;
import java.util.HashMap;


/**
 * DNSSubwayGatewayのインスタンスのプールI/F.
 * 動的に構築したゲートウェイを管理するためのプールI/F.
 * 
 * DNSSubwayGatewayInstancePoolImpl
 */
public class DNSSubwayGatewayInstancePoolImpl implements IDNSSubwayGatewayInstancePool
{

    Properties properties;

    IDNSSubwayGatewayFactory sshGatewayFactory;

    Map<String, IDNSSubwayGatewayDescriptor> gwDescriptorTable = new HashMap<String, IDNSSubwayGatewayDescriptor>();


    DNSSubwayUtils dnsSubwayUtils = new DNSSubwayUtils();


    void setProperties(Properties properties)
    {
        this.properties = properties;
    }



    @Autowired
    @Qualifier("sshGatewayFactoryImpl")
    public void setSSHGatewayFactory(IDNSSubwayGatewayFactory instance) throws DNSSubwayCommonException
    {
        this.sshGatewayFactory = instance;
    }

    public IDNSSubwayGatewayFactory getSSHGatewayFactory() throws DNSSubwayCommonException
    {
        return this.sshGatewayFactory;
    }


    public void init(Properties properties) throws DNSSubwayCommonException
    {
        setProperties(properties);

        if ( getSSHGatewayFactory() == null )
        {
            // SSH GWファクトリを設定する.
            IDNSSubwayGatewayFactory sshGatewayFactory = new SSHGatewayFactoryImpl();
            setSSHGatewayFactory(sshGatewayFactory);
        }


    }

    public IDNSSubwayGatewayDescriptor getOrCreateDNSSubwayDescriptor(String subwayLineType, String dname, String protocolType, int[] portList, Properties options) throws DNSSubwayCommonException
    {
        IDNSSubwayGatewayDescriptor gwDescriptor;
        if ( isExists(subwayLineType, dname, protocolType, portList, options) )
        {
            gwDescriptor = getDNSSubwayDescriptor(subwayLineType, dname, protocolType, portList, options);
        }
        else
        {
            gwDescriptor = createDNSSubwayDescriptor(subwayLineType, dname, protocolType, portList, options);
        }
        
        return gwDescriptor;
    }

    public IDNSSubwayGatewayDescriptor getDNSSubwayDescriptor(String subwayLineType, String dname, String protocolType, int[] portList, Properties options) throws DNSSubwayCommonException
    {
        String key = dnsSubwayUtils.buildGatewayDescriptorKey(subwayLineType, dname, protocolType, portList, options);
        
        if ( isExists(subwayLineType, dname, protocolType, portList, options) == false )
        {
            String msg = String.format("Failed to get DNSSubwayDescriptor : %s", key);
            DNSSubwayCommonException exception = new DNSSubwayCommonException(msg);
            throw exception;            
        }

        IDNSSubwayGatewayDescriptor gwDescriptor = gwDescriptorTable.get(key);
        return gwDescriptor;
    }

    public boolean isExists(String subwayLineType, String dname, String protocolType, int[] portList, Properties options) throws DNSSubwayCommonException
    {
        String descriptorName = dnsSubwayUtils.buildGatewayDescriptorKey(subwayLineType, dname, protocolType, portList, options);

        boolean flg = gwDescriptorTable.containsKey(descriptorName);
        return flg;
    }


    public IDNSSubwayGatewayDescriptor createDNSSubwayDescriptor(String subwayLineType, String dname, String protocolType, int[] portList, Properties options) throws DNSSubwayCommonException
    {
        IDNSSubwayGateway gwInstance = createDNSSubwayGatewayInstance(subwayLineType, dname, protocolType, portList, options);

        if ( gwInstance == null )
        {
            String msg = String.format("Failed to create DNSSubwayGateway Instance.");
            DNSSubwayCommonException exception = new DNSSubwayCommonException(msg);
            throw exception;        
        }

        String descriptorName = dnsSubwayUtils.buildGatewayDescriptorKey(subwayLineType, dname, protocolType, portList, options);
        String gwAddress = gwInstance.getGatewayAddress();
        int[] gwPortList = gwInstance.getGatewayPortList();

        IDNSSubwayGatewayDescriptor gwDescriptor = new DNSSubwayGatewayInstanceDescriptorImpl();
        gwDescriptor.setDNSSubwayDescriptorName( descriptorName);
        gwDescriptor.setTargetDName(dname);
        gwDescriptor.setTargetPortList(portList);
        gwDescriptor.setTargetProtocolType(protocolType);
        gwDescriptor.setGatewayAddress( gwAddress );
        gwDescriptor.setGatewayPortList( gwPortList );

        gwDescriptor.setGatewayInstance(gwInstance);


        gwDescriptorTable.put( descriptorName, gwDescriptor);
        
        return gwDescriptor;
    }



    public void destroy(IDNSSubwayGatewayDescriptor descriptor) throws DNSSubwayCommonException
    {
        String key = descriptor.getDNSSubwayDescriptorName();

        if ( gwDescriptorTable.containsKey(key) == false )
        {
            String msg = String.format("Failed to desctory DNSSubwayGateway Descriptor : %s", descriptor.getDNSSubwayDescriptorName() );
            DNSSubwayCommonException exception = new DNSSubwayCommonException(msg);
            throw exception;       
        }

        IDNSSubwayGateway gwInstance = descriptor.getGatewayInstance();
        gwInstance.close();
    
        gwDescriptorTable.remove( key );

    }



    protected IDNSSubwayGateway createDNSSubwayGatewayInstance(String subwayLineType, String dname, String protocolType, int[] portList, Properties options) throws DNSSubwayCommonException
    {
        
        IDNSSubwayGatewayFactory gwFactory;
        
        if ( subwayLineType.equals( DNSSubwayConstants.COSNT_DNS_SUBWAY_LINE_TYPE_SSH_TUNNEL ) )
        {
            gwFactory = getSSHGatewayFactory();
        }
        else
        {
            String msg = String.format("Failed to create DNSSubwayGateway Instance, caused by Invalid subwayLineType=%s .", subwayLineType);
            DNSSubwayCommonException exception = new DNSSubwayCommonException(msg);
            throw exception;            
        }

        IDNSSubwayGateway gwInstance = gwFactory.createDNSSubwayGatewayInstance( options );


        // ここでゲートウェイをopen済みにする.
        gwInstance.open();

        return gwInstance;
    }

}