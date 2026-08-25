package org.loxsols.net.service.dns.loulandns.client.subway.impl.ssh.module;


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
import org.loxsols.net.service.dns.loulandns.client.subway.impl.ssh.SSHGatewayImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.integration.IntegrationProperties.RSocket.Client;
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
import java.util.Properties;


/**
 * Apache MINAのSSH I/Fをカプセル化するためのインスタンスクラス.
 * 
 * 
 * SSHSessionInstance
 */
public class SSHSessionInstance
{

    private SshClient sshClient;
    private ClientSession sshSession;

    private String sshServerHost;
    int sshServerPort;

    private String sshUser;
    private String sshPassword;

    private Properties options;



    public String getSSHUser() throws DNSSubwayCommonException
    {
        return this.sshUser;
    }

    public void setSSHUser(String value) throws DNSSubwayCommonException
    {
        this.sshUser = value;
    }

    public String getSSHPassword() throws DNSSubwayCommonException
    {
        return this.sshPassword;
    }

    public void setSSHPassword(String value) throws DNSSubwayCommonException
    {
        this.sshPassword = value;
    }

    public String getSSHServerHost() throws DNSSubwayCommonException
    {
        return sshServerHost;
    }

    public void setSSHServerHost(String value) throws DNSSubwayCommonException
    {
        this.sshServerHost = value;
    }

    public int getSSHServerPort() throws DNSSubwayCommonException
    {
        return sshServerPort;
    }

    public void setSSHServerPort(int value) throws DNSSubwayCommonException
    {
        this.sshServerPort = value;
    }

    public Properties getOptions() throws DNSSubwayCommonException
    {
        return this.options;
    }

    public void setOptions(Properties value) throws DNSSubwayCommonException
    {
        this.options = value;
    }

    public SshClient getSSHClient()throws DNSSubwayCommonException
    {
        return sshClient;
    }


    public ClientSession getSSHSession()throws DNSSubwayCommonException
    {
        return sshSession;
    }


    public SSHSessionInstance(String sshServerHost, int sshServerPort, String sshUser, String sshPassword, Properties options) throws DNSSubwayCommonException
    {
        init(sshServerHost, sshServerPort, sshUser, sshPassword, options);
    }



    public void init(String sshServerHost, int sshServerPort, String sshUser, String sshPassword, Properties options) throws DNSSubwayCommonException
    {

        setSSHServerHost(sshServerHost);
        setSSHServerPort(sshServerPort);

        setSSHUser(sshUser);
        setSSHPassword(sshPassword);

        setOptions(options);
    }


    public void open() throws DNSSubwayCommonException
    {

        sshClient = SshClient.setUpDefaultClient();
        sshClient.start();

        try
        {

            // 以下のエラー対策で、NIOのタイムアウト時間を60秒に設定する.
            // java.lang.NullPointerException: Cannot invoke "org.apache.sshd.common.io.IoOutputStream.writeBuffer(org.apache.sshd.common.util.buffer.Buffer)" because the return value of "org.apache.sshd.common.forward.TcpipClientChannel.getAsyncIn()" is null
            sshClient.getProperties().put(CoreModuleProperties.NIO2_MIN_WRITE_TIMEOUT.getName(), 60000); // 60秒


            // セッションの接続と認証
            sshSession = sshClient.connect( getSSHUser(), getSSHServerHost(), getSSHServerPort() )
                            .verify(10, TimeUnit.SECONDS).getSession();
            sshSession.addPasswordIdentity( getSSHPassword() );
            sshSession.auth().verify(10, TimeUnit.SECONDS);

        }
        catch(IOException cause)
        {
            String msg = String.format("Failed to open SSH session. sshServerHost=%s, sshServerPort=%d, sshUserName=%s", getSSHServerHost(), getSSHServerPort(), getSSHUser() );
            DNSSubwayCommonException exception = new DNSSubwayCommonException(msg, cause);
            throw exception;
        }
    }


    /**
     * SSHセッションを閉塞する.
     * 
     * @throws DNSSubwayCommonException
     */
    public void close() throws DNSSubwayCommonException
    {
        try
        {
            sshSession.close();
        }
        catch(IOException cause)
        {
            String msg = String.format("Failed to close SSH Session : %s", getSSHSesssionInfoString() );
            DNSSubwayCommonException exception = new DNSSubwayCommonException(msg, cause);
            throw exception;
        }
    }

    public boolean isOpen() throws DNSSubwayCommonException
    {
        if ( getSSHSession() == null )
        {
            return false;
        }

        boolean ret = getSSHSession().isOpen();
        return ret;
    }

    public boolean equals(String sshServerHost, int sshServerPort, String sshUser) throws DNSSubwayCommonException
    {
        if ( getSSHServerHost().equals(sshServerHost) == false )
        {
            return false;
        }

        if ( getSSHServerPort() != sshServerPort )
        {
            return false;
        }

        if ( getSSHUser().equals(sshUser) == false )
        {
            return false;
        }

        return true;
    }



    /**
     * SSHセッションを識別するためのキーワード文字列を生成する.
     * "<SSHサーバーホスト>:<SSHサーバーポート>/<SSHユーザー名>"
     * @return
     * @throws DNSSubwayCommonException
     */
    public String getSSHSesssionInfoString() throws DNSSubwayCommonException
    {
        String str = String.format("%s:%d/%s,", getSSHServerHost(), getSSHServerPort(), getSSHUser() );
        return str;
    }

    public void startLocalPortForwarding(InetSocketAddress localSocketAddress, InetSocketAddress remoteSocketAddress) throws DNSSubwayCommonException
    {
        if ( isOpen() == false )
        {
            String msg = String.format("Failed to start SSH Local Port forwarding. SSH session is not opened. SSHSessionInstance=%s", this.getSSHSesssionInfoString());
            DNSSubwayCommonException exception = new DNSSubwayCommonException(msg);
            throw exception;
        }

        try
        {
            SshdSocketAddress localAddress = new SshdSocketAddress(localSocketAddress);
            SshdSocketAddress remoteAddress = new SshdSocketAddress(remoteSocketAddress);

            getSSHSession().startLocalPortForwarding(localAddress, remoteAddress);
        }
        catch(IOException cause)
        {
            String msg = String.format("Failed to start SSH Local port forwarding. SSHSessionInstance=%s, LocalAddress=%s, RemoteAddress=%s", this.getSSHSesssionInfoString(), localSocketAddress.toString(), remoteSocketAddress.toString() );
            DNSSubwayCommonException exception = new DNSSubwayCommonException(msg, cause);
            throw exception;
        }

    }

    public void stopLocalPortForwarding(InetSocketAddress localSocketAddress) throws DNSSubwayCommonException
    {
        SshdSocketAddress localAddress = new SshdSocketAddress(localSocketAddress);

        try
        {
            getSSHSession().stopLocalPortForwarding(localAddress);
        }
        catch(IOException cause)
        {
            String msg = String.format("Failed to stop SSH Local port forwarding. SSHSessionInstance=%s, LocalAddress=%s.", this.getSSHSesssionInfoString(), localSocketAddress.toString() );
            DNSSubwayCommonException exception = new DNSSubwayCommonException(msg, cause);
            throw exception;
        }

    }


}