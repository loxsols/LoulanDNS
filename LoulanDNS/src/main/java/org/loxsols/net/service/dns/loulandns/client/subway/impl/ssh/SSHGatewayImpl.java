package org.loxsols.net.service.dns.loulandns.client.subway.impl.ssh;


import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.HashMap;

import org.loxsols.net.service.dns.loulandns.client.*;
import org.loxsols.net.service.dns.loulandns.client.impl.simple.*;
import org.loxsols.net.service.dns.loulandns.client.common.DNSClientCommonException;
import org.loxsols.net.service.dns.loulandns.client.common.LoulanDNSClientConstants;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.model.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSQuestionMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSResponseMessage;
import org.loxsols.net.service.dns.loulandns.server.common.*;
import org.loxsols.net.service.dns.loulandns.client.subway.*;
import org.loxsols.net.service.dns.loulandns.client.subway.common.DNSSubwayCommonException;
import org.loxsols.net.service.dns.loulandns.client.subway.common.DNSSubwayConstants;
import org.loxsols.net.service.dns.loulandns.client.subway.common.DNSSubwayUtils;
import org.loxsols.net.service.dns.loulandns.client.subway.impl.ssh.module.SSHSessionInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;


import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.io.IoWriteFuture;
import org.apache.sshd.common.util.buffer.ByteArrayBuffer;
import org.apache.sshd.common.util.net.SshdSocketAddress;
import org.apache.sshd.core.CoreModuleProperties;

import java.util.concurrent.TimeUnit;
import java.util.Properties;
import java.util.List;
import java.util.ArrayList;

public class SSHGatewayImpl implements IDNSSubwayGateway
{

    SSHSessionInstance sshSessionInstance;
    List<SSHTunnelInstance> sshTunnelInstanceList = new ArrayList<SSHTunnelInstance>();

    public void setSSHSessionInstance(SSHSessionInstance instance)
    {
        this.sshSessionInstance = instance;
    }

    public SSHSessionInstance getSSHSessionInstance()
    {
        return sshSessionInstance;
    }

    public List<SSHTunnelInstance> getSSHTunnelInstanceList()
    {
        return sshTunnelInstanceList;
    }

    public void addSSHTunnelInstance(SSHTunnelInstance instance)
    {
        List<SSHTunnelInstance> list = getSSHTunnelInstanceList();
        list.add( instance );
    }



    public SSHGatewayImpl(SSHSessionInstance sshSessionInstance) throws DNSSubwayCommonException
    {
        setSSHSessionInstance(sshSessionInstance);
    }



    private String localAddress;

    private String targetHost;
    private int[] targetPortList;


    // 実際にbindして使用しているローカルのIPアドレス
    private String localBindAddress;
    

    DNSSubwayUtils dnsSubwayUtils = new DNSSubwayUtils();


    public String getSSHServerHost() throws DNSSubwayCommonException
    {
        String sshServerHost = getSSHSessionInstance().getSSHServerHost();
        return sshServerHost;
    }


    public int getSSHServerPort() throws DNSSubwayCommonException
    {
        int sshServerPort = getSSHSessionInstance().getSSHServerPort();
        return sshServerPort;
    }


    public String getSSHUser() throws DNSSubwayCommonException
    {
        String sshUser = getSSHSessionInstance().getSSHUser();
        return sshUser;
    }
    
    public String getSSHPassword() throws DNSSubwayCommonException
    {
        String sshPassword = getSSHSessionInstance().getSSHPassword();
        return sshPassword;
    }


    public String getLocalAddress() throws DNSSubwayCommonException
    {
        return localAddress;
    }

    public void setLocalAddress(String value) throws DNSSubwayCommonException
    {
        this.localAddress = value;
    }


    public String getTargetHost() throws DNSSubwayCommonException
    {
        return this.targetHost;
    }

    public void setTargetHost(String value) throws DNSSubwayCommonException
    {
        this.targetHost = value;
    }

    public int[] getTargetPortList() throws DNSSubwayCommonException
    {
        return this.targetPortList;
    }

    public void setTargetPortList(int[] list) throws DNSSubwayCommonException
    {
        this.targetPortList = list;
    }


    // ターゲットホストのポート番号のリストを文字列(","区切りテキスト)で指定する.
    public void setTargetPortList(String listString) throws DNSSubwayCommonException
    {
        int[] portList = dnsSubwayUtils.getPortListFromString(listString);
        setTargetPortList(portList);
    }

    
    // ポート転送するTCPポート番号のリストを取得する.
    public int[] getForwardTCPPortList() throws DNSSubwayCommonException
    {
        // TCPポート転送するポート番号のリストは、ターゲットホストのポート番号のリストをそのまま転用する.
        int[] forwardPortList = getTargetPortList().clone();
        return forwardPortList;
    }

    // ポート転送するTCPポート番号のリストを文字列で取得する.
    public String getForwardTCPPortListString() throws DNSSubwayCommonException
    {
        int[] portList = getForwardTCPPortList();
        String portListString = dnsSubwayUtils.getPortListString(portList);
        return portListString;
    }


    public void setLocalBindAddress(String address)
    {
        this.localBindAddress = address;
    }

    public String getLocalBindAddress()
    {
        return this.localBindAddress;
    }

    


    public void init(Properties properties) throws DNSSubwayCommonException
    {


        // ------ SSHトンネルの入り口のI/Fの設定処理.
        // ローカルアドレス
        String localAddress = properties.getProperty( DNSSubwayConstants.CONST_PROP_KEY_DNS_SUBWAY_GW_SSH_TUNNEL_LOCAL_ADDRESS );
        if ( localAddress == null || localAddress.isEmpty() )
        {
            String msg = String.format("Failed to init SSHGateway. LocalAddress is not specified. key=%s", DNSSubwayConstants.CONST_PROP_KEY_DNS_SUBWAY_GW_SSH_TUNNEL_LOCAL_ADDRESS );
            DNSSubwayCommonException exception = new DNSSubwayCommonException(msg);
            throw exception;
        }
        setLocalAddress(localAddress);

        // 2026/08/20 ローカルポートの設定処理はコメントアウトする.ターゲットホストのポート番号のリストをそのままフォワードポートとする.
        // // ローカルポート
        // String localPortString = properties.getProperty( DNSSubwayConstants.CONST_PROP_KEY_DNS_SUBWAY_GW_SSH_TUNNEL_LOCAL_PORT );
        // if ( localPortString == null || localPortString.isEmpty() )
        // {
        //     String msg = String.format("Failed to init SSHGateway. LocalPort is not specified. key=%s", DNSSubwayConstants.CONST_PROP_KEY_DNS_SUBWAY_GW_SSH_TUNNEL_LOCAL_PORT );
        //     DNSSubwayCommonException exception = new DNSSubwayCommonException(msg);
        //     throw exception;
        // }

        // try
        // {
        //     int localPort = Integer.parseInt(localPortString);
        //     setLocalPort(localPort);
        // }
        // catch(NumberFormatException cause)
        // {
        //     String msg = String.format("Failed to init SSHGateway, caused by Invalid local port value. key=%s, value=%s", DNSSubwayConstants.CONST_PROP_KEY_DNS_SUBWAY_GW_SSH_TUNNEL_LOCAL_PORT, localPortString );
        //     DNSSubwayCommonException exception = new DNSSubwayCommonException(msg);
        //     throw exception;
        // }

        // ----------


        // ------ SSHトンネルの出口のI/Fの設定処理.
        // ターゲットホスト (SSHトンネルの出口の先の向き先)
        String targetHost = properties.getProperty( DNSSubwayConstants.CONST_PROP_KEY_DNS_SUBWAY_GW_SSH_TUNNEL_TARGET_HOST );
        if ( targetHost == null || targetHost.isEmpty() )
        {
            String msg = String.format("Failed to init SSHGateway. TargetHost is not specified. key=%s", DNSSubwayConstants.CONST_PROP_KEY_DNS_SUBWAY_GW_SSH_TUNNEL_TARGET_HOST );
            DNSSubwayCommonException exception = new DNSSubwayCommonException(msg);
            throw exception;
        }
        setTargetHost(targetHost);


        // ターゲットポート (SSHトンネルの出口の先の向き先)
        String targetPortListString = properties.getProperty( DNSSubwayConstants.CONST_PROP_KEY_DNS_SUBWAY_GW_SSH_TUNNEL_TARGET_PORT );
        if ( targetPortListString == null || targetPortListString.isEmpty() )
        {
            String msg = String.format("Failed to init SSHGateway. TargetPort is not specified. key=%s", DNSSubwayConstants.CONST_PROP_KEY_DNS_SUBWAY_GW_SSH_TUNNEL_TARGET_PORT );
            DNSSubwayCommonException exception = new DNSSubwayCommonException(msg);
            throw exception;
        }
        // -------

        try
        {
            setTargetPortList(targetPortListString);
        }
        catch(DNSSubwayCommonException cause)
        {
            String msg = String.format("Failed to init SSHGateway, caused by Invalid target port value. key=%s, value=%s", DNSSubwayConstants.CONST_PROP_KEY_DNS_SUBWAY_GW_SSH_TUNNEL_TARGET_PORT, targetPortListString );
            DNSSubwayCommonException exception = new DNSSubwayCommonException(msg);
            throw exception;
        }


        
    }

    public void open() throws DNSSubwayCommonException
    {
        SSHSessionInstance sshSessionInstance = getSSHSessionInstance();
        if ( sshSessionInstance == null )
        {
            String msg = String.format("Failed to open SSH Gateway. SSH session instance is null.");
            DNSSubwayCommonException exception = new DNSSubwayCommonException(msg);
            throw exception; 
        }

        if ( sshSessionInstance.isOpen() == false )
        {
            // SSHセッションがまだオープン済みでないのでオープンする.
            sshSessionInstance.open();
        }


        // GWの入り口アドレス(ローカルIPアドレス)はサブネット指定された文字列(Ex. 127.0.0.0/24)などから実際のアドレス文字列(Ex. 127.0.0.128)を生成して使用する.
        String localBindAddress = getEnableLocalSocketAddress();
        setLocalBindAddress(localBindAddress);

        // Apache MINAのSSHセッションは、単一のセッション内で複数のSSHトンネルを構築できる.
        for( int forwardPort : getForwardTCPPortList() )
        {
            // ローカルポートもリモートポートもフォワードするポート番号のリストをそのまま使用する.
            // (G/Wの入り口のポート番号と出口のそれが一致するようにする.)
                
            int localBindPort = forwardPort;

            String remoteAddress = getTargetHost();
            int remotePort = forwardPort;
            SSHTunnelInstance sshTunnelInstance = new SSHTunnelInstance( sshSessionInstance, localBindAddress, localBindPort, remoteAddress, remotePort  );
            
            // SSHトンネルを構築する.
            // ローカルポートフォワーディングの開始（入り口の構築）
            // ローカルの localPort への通信が、SSH先を経由して targetHost:targetPort へ転送される
            sshTunnelInstance.open();

            addSSHTunnelInstance(sshTunnelInstance);
        }

    }

    
    public void close() throws DNSSubwayCommonException
    {

        for( SSHTunnelInstance instance : getSSHTunnelInstanceList() )
        {
            // SSHトンネル(ローカルポートフォワード)を全て閉塞する.
            instance.close();
        }


        // SSHセッションもクローズする.
        SSHSessionInstance sshSessionInstance = getSSHSessionInstance();
        sshSessionInstance.close();
    }

    public int write(byte[] bytes) throws DNSSubwayCommonException
    {
        // 一応、SSHセッションに強引に横から書き込む機能を実装する.
        // SSHトンネルなので、本来はTCPでローカルポートに接続して書き込んでもらうのが正しい.

        String msg = String.format("Not Implemented.");
        DNSSubwayCommonException exception = new DNSSubwayCommonException(msg);
        throw exception;
    }


    public byte[] read(int size) throws DNSSubwayCommonException
    {
        // 未実装 : そもそもSSHトンネルなので、パケットを横取りすると意味がない.
        String msg = String.format("Not Implemented.");
        DNSSubwayCommonException exception = new DNSSubwayCommonException(msg);
        throw exception;
    }



    /**
     * 実際にbindしているGWの入り口IPアドレスを返す.
     * 
     */
    public String getGatewayAddress() throws DNSSubwayCommonException
    {
        String gwAddress = getLocalBindAddress();
        if ( gwAddress == null || gwAddress.isEmpty() )
        {
            String msg = String.format("Failed to get G/W Address. The local bind address is not set. target-host=%s, target-port=%s", getTargetHost(), dnsSubwayUtils.getPortListString( getTargetPortList() ) );
            DNSSubwayCommonException exception = new DNSSubwayCommonException(msg);
            throw exception;
        }
        
        return gwAddress;
    }


    /**
     * GWがポート転送している(またはこれからする)ポート番号のリストを返す.
     * 
     */
    public int[] getGatewayPortList() throws DNSSubwayCommonException
    {
         int[] gwPortList = getForwardTCPPortList();
         return gwPortList;
    }


    /**
     * 指定されたIPアドレス(サブネットワーク形式:"xxx.xxx.xxx.xxx/yy")とポート番号の組み合わせから、使用可能なローカルのIPアドレス文字列を返す.
     * 
     * @return
     * @throws DNSSubwayCommonException
     */
    protected String getEnableLocalSocketAddress() throws DNSSubwayCommonException
    {
        String localAddress = getLocalAddress();

        String ipAddressString = null;
        if ( dnsSubwayUtils.isIPSubnetFormatString(localAddress) )
        {
            // サブネットワーク形式("xxx.xxx.xxx.xxx/yy")なので、候補アドレスの一覧からbind可能なアドレスを探す.
            List<String> addressList = dnsSubwayUtils.getIPAddressListFromIPSubnetFormat(localAddress);
            for( String addr : addressList )
            {
                int[] portList = getForwardTCPPortList();
                if ( dnsSubwayUtils.isEnableToBindTCPAddress(addr, portList) )
                {
                    ipAddressString = addr;
                    break;
                }
            }
        }
        else
        {
            ipAddressString = localAddress;
        }

        if ( ipAddressString == null )
        {
            // bindできそうなアドレスが存在しなかった.
            String msg = String.format("Failed to bind SocketAddress. localAddress=%s, forwardPortList=%s", localAddress, getForwardTCPPortListString());
            DNSSubwayCommonException exception = new DNSSubwayCommonException(msg);
            throw exception;
        }

        return ipAddressString;
    }




    /**
     * SSHトンネルを管理するためのインスタンスクラス.(インナークラス).
     * 
     * SSHTunnelInstance
     */
    class SSHTunnelInstance
    {

        SSHSessionInstance sshSessionInstance;

        String localAddress;
        int localPort;

        String remoteAddress;
        int remotePort;

        SshdSocketAddress localSSHSocketAddress;
        SshdSocketAddress remoteSSHSocketAddress;


        public void setSSHSessionInstance(SSHSessionInstance instance)
        {
            this.sshSessionInstance = instance;
        }

        public SSHSessionInstance getSSHSessionInstance()
        {
            return this.sshSessionInstance;
        }

        public void setLocalAddress(String value)
        {
            this.localAddress = value;
        }

        public String getLocalAddress()
        {
            return this.localAddress;
        }

        public void setLocalPort(int value)
        {
            this.localPort = value;
        }

        public int getLocalPort()
        {
            return this.localPort;
        }

        public void setRemoteAddress(String value)
        {
            this.remoteAddress = value;
        }

        public String getRemoteAddress()
        {
            return this.remoteAddress;
        }

        public void setRemotePort(int value)
        {
            this.remotePort = value;
        }

        public int getRemotePort()
        {
            return this.remotePort;
        }


        public InetSocketAddress getLocalSocketAddress()
        {
            InetSocketAddress localSocketAddress = new InetSocketAddress( this.getLocalAddress(), this.getLocalPort() );
            return localSocketAddress;
        }

        public InetSocketAddress getRemoteSocketAddress()
        {
            InetSocketAddress localSocketAddress = new InetSocketAddress( this.getRemoteAddress(), this.getRemotePort() );
            return localSocketAddress;
        }

        public SshdSocketAddress getLocalSSHSocketAddress()
        {
            InetSocketAddress localSocketAddress = getLocalSocketAddress();
            SshdSocketAddress localSSHSocketAddress = new SshdSocketAddress(localSocketAddress);
            return localSSHSocketAddress;
        }

        public SshdSocketAddress getRemoteSSHSocketAddress()
        {
            InetSocketAddress remoteSocketAddress = getRemoteSocketAddress();
            SshdSocketAddress remoteSSHSocketAddress = new SshdSocketAddress(remoteSocketAddress);
            return remoteSSHSocketAddress;
        }

        public void init(SSHSessionInstance sshSessionInstance, String localAddress, int localPort, String remoteAddress, int remotePort )
        {
            this.setSSHSessionInstance(sshSessionInstance);

            this.setLocalAddress(localAddress);
            this.setLocalPort(localPort);
            
            this.setRemoteAddress(remoteAddress);
            this.setRemotePort(remotePort);
        }

        public SSHTunnelInstance(SSHSessionInstance sshSessionInstance, String localAddress, int localPort, String remoteAddress, int remotePort )
        {
            init(sshSessionInstance, localAddress, localPort, remoteAddress, remotePort);
        }


        /**
         * SSHトンネル(ローカルポートフォワード)を開通する.
         * 
         * @throws DNSSubwayCommonException
         */
        public void open() throws DNSSubwayCommonException
        {
            SSHSessionInstance sshSessionInstance = getSSHSessionInstance();
            sshSessionInstance.startLocalPortForwarding(getLocalSocketAddress(), getRemoteSocketAddress());
        }


        /**
         * SSHトンネル(ローカルポートフォワード)を閉塞する.
         * 
         * @return
         * @throws DNSSubwayCommonException
         */
        public void close() throws DNSSubwayCommonException
        {
            SSHSessionInstance sshSessionInstance = getSSHSessionInstance();
            sshSessionInstance.stopLocalPortForwarding(getLocalSocketAddress());
        }

    }


    /**
     * デバッグ用のメイン関数.
     * Usage : <local-port>:<remote-host><remote-port>  <user>/<password>@<server-host>:<server-port>
     * @param args
    */
    public static void main(String[] args)
    {
        if ( args.length < 2)
        {
            System.out.println("Usage : <local-port>:<remote-host>:<remote-port>  <user>/<password>@<server-host>:<server-port>");
            return ;
        }

        System.out.println("args[0]=" + args[0]);
        System.out.println("args[0]=" + args[1]);

        String localAddress = "0.0.0.0";
        String localPort = args[0].split(":")[0];
        String remoteHost = args[0].split(":")[1];
        String remotePort = args[0].split(":")[2];

        String user = args[1].split("@")[0].split("/")[0];
        String password = args[1].split("@")[0].split("/")[1];

        String serverHost = args[1].split("@")[1].split(":")[0];
        String serverPort = args[1].split("@")[1].split(":")[1];


        Properties properties = new Properties();

        properties.put( DNSSubwayConstants.CONST_PROP_KEY_DNS_SUBWAY_GW_SSH_TUNNEL_LOCAL_ADDRESS, localAddress );
        properties.put( DNSSubwayConstants.CONST_PROP_KEY_DNS_SUBWAY_GW_SSH_TUNNEL_LOCAL_PORT, localPort );

        properties.put( DNSSubwayConstants.CONST_PROP_KEY_DNS_SUBWAY_GW_SSH_TUNNEL_TARGET_HOST, remoteHost );
        properties.put( DNSSubwayConstants.CONST_PROP_KEY_DNS_SUBWAY_GW_SSH_TUNNEL_TARGET_PORT, remotePort );

        properties.put( DNSSubwayConstants.CONST_PROP_KEY_DNS_SUBWAY_GW_SSH_TUNNEL_SERVER_USER, user );
        properties.put( DNSSubwayConstants.CONST_PROP_KEY_DNS_SUBWAY_GW_SSH_TUNNEL_SERVER_PASSWORD, password );

        properties.put( DNSSubwayConstants.CONST_PROP_KEY_DNS_SUBWAY_GW_SSH_TUNNEL_SERVER_HOST, serverHost );
        properties.put( DNSSubwayConstants.CONST_PROP_KEY_DNS_SUBWAY_GW_SSH_TUNNEL_SERVER_PORT, serverPort );

        try
        {
            SSHSessionInstance sshSessionInstance = new SSHSessionInstance(serverHost, Integer.parseInt(serverPort), user, password, properties);

            SSHGatewayImpl sshGW = new SSHGatewayImpl(sshSessionInstance);
            sshGW.init(properties);

            sshGW.open();

            Thread.sleep(Long.MAX_VALUE);
        }
        catch(DNSSubwayCommonException | InterruptedException exception)
        {
            exception.printStackTrace();
        }

    }
    

}