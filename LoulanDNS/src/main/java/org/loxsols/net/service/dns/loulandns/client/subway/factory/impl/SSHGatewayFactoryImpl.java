package org.loxsols.net.service.dns.loulandns.client.subway.factory.impl;


import static org.mockito.Mockito.mockingDetails;

import java.net.InetAddress;
import java.util.HashMap;

import org.apache.sshd.client.session.ClientSession;
import org.loxsols.net.service.dns.loulandns.client.*;
import org.loxsols.net.service.dns.loulandns.client.impl.simple.*;
import org.loxsols.net.service.dns.loulandns.client.subway.common.DNSSubwayCommonException;
import org.loxsols.net.service.dns.loulandns.client.subway.common.DNSSubwayConstants;
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

import org.loxsols.net.service.dns.loulandns.client.subway.*;
import org.loxsols.net.service.dns.loulandns.client.subway.factory.IDNSSubwayGatewayFactory;
import org.loxsols.net.service.dns.loulandns.client.subway.impl.ssh.SSHGatewayImpl;
import org.loxsols.net.service.dns.loulandns.client.subway.impl.ssh.module.SSHSessionInstance;

import java.util.Properties;
import java.util.List;
import java.util.ArrayList;


/**
 * DNSSubwayのSSHのGWインスタンスを生成して返す.
 * 
 * SSHGatewayFactoryImpl
 */
public class SSHGatewayFactoryImpl implements IDNSSubwayGatewayFactory
{


    List<SSHSessionInstance> sshSessionInstanceList = new ArrayList<SSHSessionInstance>();


    /**
     * DNSSubwayのGWインスタンスを新規に生成して返す.
     * 
     * @return
     * @throws DNSSubwayCommonException
     */
    public IDNSSubwayGateway createDNSSubwayGatewayInstance(Properties properties) throws DNSSubwayCommonException
    {

        SSHSessionInstance sshSessionInstance = getOrcreateSSHSessionInstance(properties);     

        IDNSSubwayGateway gwImpl = new SSHGatewayImpl(sshSessionInstance);
        gwImpl.init(properties);

        return gwImpl;
    }

    /**
     * DNSSubwayのGWインスタンスを廃棄する.
     * 
     * @param instance
     * @throws DNSSubwayCommonException
     */
    public void destroyDNSSubwayGatewayInstance(IDNSSubwayGateway instance) throws DNSSubwayCommonException
    {
        instance.close();        
    }



    /**
     * SSHセッションを取得または新規作成する.
     * 
     * @param properties
     * @return
     * @throws DNSSubwayCommonException
     */
    public SSHSessionInstance getOrcreateSSHSessionInstance(Properties properties) throws DNSSubwayCommonException
    {

        // サーバーホスト
        String serverHost = properties.getProperty( DNSSubwayConstants.CONST_PROP_KEY_DNS_SUBWAY_GW_SSH_TUNNEL_SERVER_HOST );
        if ( serverHost == null || serverHost.isEmpty() )
        {
            String msg = String.format("Failed to init SSHGateway. ServerHost is not specified. key=%s", DNSSubwayConstants.CONST_PROP_KEY_DNS_SUBWAY_GW_SSH_TUNNEL_SERVER_HOST );
            DNSSubwayCommonException exception = new DNSSubwayCommonException(msg);
            throw exception;
        }

        
        // サーバーポート
        String serverPortString = properties.getProperty( DNSSubwayConstants.CONST_PROP_KEY_DNS_SUBWAY_GW_SSH_TUNNEL_SERVER_PORT );
        if ( serverPortString == null || serverPortString.isEmpty() )
        {
            String msg = String.format("Failed to init SSHGateway. ServerPort is not specified. key=%s", DNSSubwayConstants.CONST_PROP_KEY_DNS_SUBWAY_GW_SSH_TUNNEL_SERVER_PORT );
            DNSSubwayCommonException exception = new DNSSubwayCommonException(msg);
            throw exception;
        }

        int serverPort;
        try
        {
            serverPort = Integer.parseInt(serverPortString);
        }
        catch(NumberFormatException cause)
        {
            String msg = String.format("Failed to init SSHGateway, caused by Invalid server port value. key=%s, value=%s", DNSSubwayConstants.CONST_PROP_KEY_DNS_SUBWAY_GW_SSH_TUNNEL_SERVER_PORT, serverPortString );
            DNSSubwayCommonException exception = new DNSSubwayCommonException(msg);
            throw exception;
        }



        String sshUser = properties.getProperty( DNSSubwayConstants.CONST_PROP_KEY_DNS_SUBWAY_GW_SSH_TUNNEL_SERVER_USER );
        if ( sshUser == null || sshUser.isEmpty() )
        {
            String msg = String.format("Failed to init SSHGateway. SSH user is not specified. key=%s", DNSSubwayConstants.CONST_PROP_KEY_DNS_SUBWAY_GW_SSH_TUNNEL_SERVER_USER );
            DNSSubwayCommonException exception = new DNSSubwayCommonException(msg);
            throw exception;
        }

        String sshPassword = properties.getProperty( DNSSubwayConstants.CONST_PROP_KEY_DNS_SUBWAY_GW_SSH_TUNNEL_SERVER_PASSWORD );
        if ( sshPassword == null || sshPassword.isEmpty() )
        {
            String msg = String.format("Failed to init SSHGateway. SSH password is not specified. key=%s", DNSSubwayConstants.CONST_PROP_KEY_DNS_SUBWAY_GW_SSH_TUNNEL_SERVER_PASSWORD );
            DNSSubwayCommonException exception = new DNSSubwayCommonException(msg);
            throw exception;
        }


        // SSHトンネルの構築時にSSHセッションを共有するか否かのフラグ.
        String sshSessionSharedFlagString = properties.getProperty( DNSSubwayConstants.CONST_PROP_KEY_DNS_SUBWAY_GW_SSH_TUNNEL_MODE_SESSION_SHARED );
        if ( sshSessionSharedFlagString == null || sshSessionSharedFlagString.isEmpty() )
        {
            String msg = String.format("Failed to init SSHGateway. SSH session mode shared flag is not specified. key=%s", DNSSubwayConstants.CONST_PROP_KEY_DNS_SUBWAY_GW_SSH_TUNNEL_MODE_SESSION_SHARED );
            DNSSubwayCommonException exception = new DNSSubwayCommonException(msg);
            throw exception;
        }
        boolean sshSessionSharedFlag = Boolean.parseBoolean( sshSessionSharedFlagString );

        SSHSessionInstance sshInstance;
        if ( sshSessionSharedFlag )
        {
            // SSHセッションを共有するフラグが立っているから、プール済みのSSHセッションインスタンスを検索して一致したらそれを再利用する.
            sshInstance = getSSHSessionInstance(serverHost, serverPort, sshUser);
            if ( sshInstance != null )
            {
                return sshInstance;
            }
        }


        // インスタンスを新規作成する.
        sshInstance = createSSHSessionInstance(serverHost, serverPort, sshUser, sshPassword, properties);

        // 新規作成したインスタンスをプールに登録する.
        sshSessionInstanceList.add( sshInstance );

        return sshInstance;
    }


    public SSHSessionInstance getSSHSessionInstance(String serverHost, int serverPort, String sshUser) throws DNSSubwayCommonException
    {
        for( SSHSessionInstance sshSessionInstance : sshSessionInstanceList )
        {
            if ( sshSessionInstance.equals(serverHost, serverPort, sshUser) )
            {
                return sshSessionInstance;
            }
        }

        return null;
    }

    public SSHSessionInstance createSSHSessionInstance(String serverHost, int serverPort, String sshUser, String sshPassword, Properties options) throws DNSSubwayCommonException
    {
        SSHSessionInstance sshSessionInstance = new SSHSessionInstance(serverHost, serverPort, sshUser, sshPassword, options);
        this.sshSessionInstanceList.add( sshSessionInstance );
        return sshSessionInstance;

    }


}