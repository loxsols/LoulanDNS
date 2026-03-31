package org.loxsols.net.service.dns.loulandns.server.impl.service.endpoint.udp;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;


import org.xbill.DNS.tools.*;
import org.xbill.DNS.ZoneTransferException;




import org.xbill.DNS.Address;
import org.xbill.DNS.CNAMERecord;
import org.xbill.DNS.Cache;
import org.xbill.DNS.Credibility;
import org.xbill.DNS.DClass;
import org.xbill.DNS.DNAMERecord;
import org.xbill.DNS.ExtendedFlags;
import org.xbill.DNS.Flags;
import org.xbill.DNS.Header;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Message;
import org.xbill.DNS.Name;
import org.xbill.DNS.NameTooLongException;
import org.xbill.DNS.OPTRecord;
import org.xbill.DNS.Opcode;
import org.xbill.DNS.RRset;
import org.xbill.DNS.Rcode;
import org.xbill.DNS.Record;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.Section;
import org.xbill.DNS.SetResponse;
import org.xbill.DNS.TSIG;
import org.xbill.DNS.TSIGRecord;
import org.xbill.DNS.Type;
import org.xbill.DNS.Zone;
import org.xbill.DNS.ZoneTransferException;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.SimpleResolver;


import org.loxsols.net.service.dns.loulandns.server.common.constants.*;
import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceInsufficientDNSRequestException;
import org.loxsols.net.service.dns.loulandns.server.common.InsufficientDNSMessageException;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSCommonUtils;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSProtocolUtils;
import org.loxsols.net.service.dns.loulandns.server.http.spring.model.DNSServiceEndpointInstance;
import org.loxsols.net.service.dns.loulandns.server.impl.service.endpoint.base.DNSServiceEndpointInstanceImplBase;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSQuestionMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSResponseMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.resolver.IDNSResolverInstance;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.factory.IDNSServiceInstanceFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.loxsols.net.service.dns.loulandns.server.logical.model.*;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.endpoint.*;

/**
 * UDPサービスエンドポイントの実装クラス.
 */
@ComponentScan
public class UDPServiceEndpointInstanceImpl extends DNSServiceEndpointInstanceImplBase implements IDNSServiceEndpointInstance, Runnable
{
  
  
  String udpServiceEndpointAddress;
  int udpServiceEndpointPort;


  LoulanDNSProtocolUtils protocolUtils = new LoulanDNSProtocolUtils();


    @Autowired
    @Qualifier("dnsServiceInstanceFactoryImpl")
    public void setDNSServiceInstanceFactory(IDNSServiceInstanceFactory instance)
    {
        super.setDNSServiceInstanceFactory(instance);
    }


    @Autowired
    @Qualifier("dnsMessageFactoryImpl")
    public void setDNSMessageFactory(IDNSMessageFactory instance)
    {
        super.setDNSMessageFactory(instance);
    }

  

    public UDPServiceEndpointInstanceImpl()
    {
      super();
    }
  
  LoulanDNSCommonUtils commonUtils = new LoulanDNSCommonUtils();

    // DNSサービスエンドポイントのパラメーターを設定する.
    public void initDNSServiceEndpoint(Properties properties) throws DNSServiceCommonException
    {
      
        super.initDNSServiceEndpoint(properties);
        
        initUDPServiceEndpoint(properties);
    }

    /**
     * UDPサービスエンドポイントのパラメータを設定する.
     * @param properties
     * @throws DNSServiceCommonException
     */
    protected void initUDPServiceEndpoint(Properties properties) throws DNSServiceCommonException
    {

      // 本エンドポイントの設定には以下のパラメータを使用する.
      //  ----------------------------------------------
      //  以下のパラメータはベースクラスで既に読み取り済み.
      //    - loulan.dns.user.name
      //    - loulan.dns.service.instance.name
      //  ----------------------------------------------
      //
      //  -----------------------------------------------
      //  UDPエンドポイント独自のパラメータ
      //    - loulan.dns.service.endpoint.udp.address    :     bindするIPアドレスの文字列(IPv4/IPv6).オプション.省略時は0.0.0.0が適用される.
      //    - loulan.dns.service.endpoint.udp.port       :     bindするポート番号の文字列.必須.省略時は例外をスロー.
      //  -----------------------------------------------

      String address = properties.getProperty( LoulanDNSConstants.PROP_KEY_SERVICE_ENDPOINT_UDP_BIND_ADDRESS );
      
      if ( address == null || address.equals("") )
      {
          String msg = String.format("UDP Service Endpoint Address is not specified. key=%s", LoulanDNSConstants.PROP_KEY_SERVICE_ENDPOINT_UDP_BIND_ADDRESS );
          DNSServiceCommonException exception = new DNSServiceCommonException(msg);
          throw exception;
      }

      setUDPServiceEndpointAddress(address);

      String portString = properties.getProperty( LoulanDNSConstants.PROP_KEY_SERVICE_ENDPOINT_UDP_BIND_PORT );

      if ( portString == null || portString.equals("") )
      {
          String msg = String.format("UDP Service Endpoint is not specified. key=%s", LoulanDNSConstants.PROP_KEY_SERVICE_ENDPOINT_UDP_BIND_PORT );
          DNSServiceCommonException exception = new DNSServiceCommonException(msg);
          throw exception;
      }

      int port = Integer.parseInt(portString);
      setUDPServiceEndpointPort(port);

    }


    
    /**
     * サービスエンドポイントのメイン処理(スレッド内)
     * 
     * @throws DNSServiceCommonException
     */
    protected void doEndpointServiceTask() throws DNSServiceCommonException
    {
      doUDPEndpointServiceTask();
    }

    

    /**
     * UDPサービスエンドポイントのメイン処理(スレッド内)
     * 
     * @throws DNSServiceCommonException
     */
    protected void doUDPEndpointServiceTask() throws DNSServiceCommonException
    {
      SocketAddress socketAddress = getUDPServiceSocketAddress();

      DatagramSocket sock;
      
      try
      {
        sock = new DatagramSocket(socketAddress);
      }
      catch(IOException cause)
      {
        String msg = String.format("Failed to Start DNS UDP Service Endpoint. Failed to create UDP socket. userName=%s, serviceInstanceName=%s, address=%s, port=%d.", getUserName(), getDNSServiceInstanceName(), getUDPServiceSocketAddress(), getUDPServiceEndpointPort() );
        DNSServiceCommonException exception = new DNSServiceCommonException(msg, cause);
        throw exception;
      }

      // UDPのDNSパケットの最大長は512バイト.
      byte[] recvBytes = new byte[DNSProtocolConstants.MAX_DNS_UDP_PACKET_SIZE];
      int totalReceivedSize = 0;
      

      // メインループ.
      while(true)
      {

        int taskStatus = getDNSEndpointServiceTaskStatus();
        if (  taskStatus == LoulanDNSConstants.CONST_TASK_STATUS_WAITING_FOR_SUSPEND_DNS_SERVICE_ENDPOINT )
        {
          // タスク状態が終了待ち状態になっているため、本タスクを終了する.

          // ソケットをクリアする.
          sock.disconnect();
          sock.close();

          // タスク状態を停止状態に設定する.
          setDNSEndpointServiceTaskStatus( LoulanDNSConstants.CONST_TASK_STATUS_INVACTIVE_DNS_SERVICE_ENDPOINT );

          return;
        }


        byte[] recvBuffer = new byte[ DNSProtocolConstants.MAX_DNS_UDP_PACKET_SIZE ];
        DatagramPacket recvPacket = new DatagramPacket(recvBuffer, recvBuffer.length);
        recvPacket.setLength( recvBuffer.length  - totalReceivedSize );

        try
        {
          // 最大512バイトまで受信する.
          sock.receive(recvPacket);
        }
        catch(IOException cause)
        {

          sock.disconnect();
          sock.close();

          String msg = String.format("Failed to receive DNS Query Message on DNS UDP Service Endpoint. userName=%s, serviceInstanceName=%s, address=%s, port=%d.", getUserName(), getDNSServiceInstanceName(), getUDPServiceSocketAddress(), getUDPServiceEndpointPort() );
          DNSServiceCommonException exception = new DNSServiceCommonException(msg, cause);
          throw exception;
        }


        byte[] tmpRecvBytes = recvPacket.getData();
        int recvSize = recvPacket.getLength();

        byte[] dnsMessageBytes = new byte[ DNSProtocolConstants.MAX_DNS_UDP_PACKET_SIZE ];
        System.arraycopy( recvBytes, 0, dnsMessageBytes, 0, totalReceivedSize );
        System.arraycopy( tmpRecvBytes, 0, dnsMessageBytes, totalReceivedSize, recvSize );

        totalReceivedSize += recvSize;

        IDNSMessage dnsMessage;
        try
        {
          dnsMessage = parseDNSPacketBytes(dnsMessageBytes);
        }
        catch(DNSServiceCommonException exception)
        {

            if ( exception instanceof InsufficientDNSMessageException )
            {
              // 正常系 : 受信したDNSリクエストパケットが不十分なので追加受信する.
              continue;
            }

            // DNSメッセージ解析時に想定外のエラーが発生した.
            // 例外を上位層にスローして処理を停止する.
            // TODO : ログに例外を吐き出しつつ処理自体は続行するのが望ましい.
            sock.disconnect();
            sock.close();

            throw exception;
        }

        if ( protocolUtils.isDNSQuestionMessage(dnsMessage) == false )
        {
          // 受信したDNSメッセージはDNS問い合わせメッセージではない.
          sock.close();

          String msg = String.format("Not DNS Question Message. DNS message is %s.", dnsMessage );
          DNSServiceCommonException exception = new DNSServiceCommonException(msg);
          throw exception;
        }

        IDNSQuestionMessage dnsQuestionMessage;
        if ( dnsMessage instanceof IDNSQuestionMessage )
        {
          dnsQuestionMessage = (IDNSQuestionMessage)dnsMessage;
        }
        else
        {
          // DNSメッセージをIDNSQuestionMessage型に変換する.
          dnsQuestionMessage = dnsMessageFactory.createQuestionDNSMesssage( dnsMessage.getDNSMessageBytes() );
        }

        // DNSメッセージの終端まで受信したのでカウンタを0に設定する.
        totalReceivedSize = 0;

        // DNSサービスインスタンス経由でDNS問い合わせを実行する.
        IDNSResponseMessage dnsResponseMessage = serveDNSQuery(dnsQuestionMessage);

        // DNSレスポンスを返却する.
        byte[] dnsResponseBytes = dnsResponseMessage.getDNSMessageBytes();
        DatagramPacket responsePacket = new DatagramPacket( dnsResponseBytes, dnsResponseBytes.length );

        InetAddress clientAddress = recvPacket.getAddress();
        responsePacket.setAddress(clientAddress);

        int clientPort = recvPacket.getPort();
        responsePacket.setPort(clientPort);

        try
        {
          sock.send( responsePacket );
        }
        catch(IOException cause)
        {
          
          sock.disconnect();
          sock.close();

          String msg = String.format("Failed to send DNS Response Message on DNS UDP Service Endpoint. userName=%s, serviceInstanceName=%s, address=%s, port=%d.", getUserName(), getDNSServiceInstanceName(), getUDPServiceSocketAddress(), getUDPServiceEndpointPort() );
          DNSServiceCommonException exception = new DNSServiceCommonException(msg, cause);
          throw exception;
        }

      }


    }








    public void setUDPServiceEndpointAddress(String address) throws DNSServiceCommonException
    {
      if ( commonUtils.isValidIPAddressString(address) == false )
      {
        // 指定された文字列はIPアドレス形式として不適.
        String msg = String.format("Specified address is not valid for UDPService Endpoint Address. address=%s", address );
        DNSServiceCommonException exception = new DNSServiceCommonException(msg);
        throw exception;
      }

      this.udpServiceEndpointAddress = address;
    }

    public String getUDPServiceEndpointAddress() throws DNSServiceCommonException
    {
      return this.udpServiceEndpointAddress;
    }

    public void setUDPServiceEndpointPort(int port) throws DNSServiceCommonException
    {
      if (port < 0 || port > 65535 )
      {
        // UDPポート番号として不適.
        String msg = String.format("Specified port number is not valid for UDPService Endpoint Port. address=%d", port );
        DNSServiceCommonException exception = new DNSServiceCommonException(msg);
        throw exception;
      }

      this.udpServiceEndpointPort = port;
    }

    public int getUDPServiceEndpointPort() throws DNSServiceCommonException
    {
      return this.udpServiceEndpointPort;
    }


    public SocketAddress getUDPServiceSocketAddress() throws DNSServiceCommonException
    {
      String address = getUDPServiceEndpointAddress();
      int port = getUDPServiceEndpointPort();

      SocketAddress socketAddress = new InetSocketAddress(address, port);

      return socketAddress;
    }




}