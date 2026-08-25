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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
import org.loxsols.net.service.dns.loulandns.server.common.constants.messages.LoulanDNSMessageConstants;
import org.loxsols.net.service.dns.loulandns.server.common.DNSProtocolErrorRCodeException;
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
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.log.logger.ILoulanDNSLogger;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.log.logger.factory.ILoulanDNSLoggerFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.DNSQuestionMessageImpl;
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


    
    @Autowired
    @Qualifier("loulanDNSLoggerFactoryImpl")
    public void setLoulanDNSLoggerFactory(ILoulanDNSLoggerFactory instance)
    {
      super.setLoulanDNSLoggerFactory(instance);
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

      ILoulanDNSLogger logger = getLogger();
      
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

          // INFO-100101 : "Endpoint Service Task is going to suspend status.";
          logger.info(LoulanDNSMessageConstants.INFO_100101, LoulanDNSMessageConstants.INFO_100101_MSG);

          // ソケットをクリアする.
          sock.disconnect();
          sock.close();

          // タスク状態を停止状態に設定する.
          setDNSEndpointServiceTaskStatus( LoulanDNSConstants.CONST_TASK_STATUS_INVACTIVE_DNS_SERVICE_ENDPOINT );

          // INFO-100102 : "Endpoint Service Task is suspended.";
          logger.info(LoulanDNSMessageConstants.INFO_100102, LoulanDNSMessageConstants.INFO_100102_MSG);

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

          String msg = String.format("Failed to receive DNS Query Message on DNS UDP Service Endpoint. userName=%s, serviceInstanceName=%s, address=%s, port=%d.", getUserName(), getDNSServiceInstanceName(), getUDPServiceSocketAddress(), getUDPServiceEndpointPort() );
          DNSServiceCommonException exception = new DNSServiceCommonException(msg, cause);

          // TODO :　当面は例外を標準出力する.
          exception.printStackTrace();

          // ERROR-800101 : "I/O error.";
          logger.error(LoulanDNSMessageConstants.ERROR_800101, LoulanDNSMessageConstants.ERROR_800101_MSG, exception);

          if ( getDNSEndpointServiceTaskIgnoreError() )
          {
            // IgnoreErrorフラグが設定されている場合はエラーを無視して続行する.
            continue;
          }


          // IgnoreErrorフラグがセットされていない場合は、ソケットを閉じてEndpointサービスタスクを終了する.

          // INFO-100198 : "Endpoint Service Task is going to accidentaly STOP.";
          logger.info(LoulanDNSMessageConstants.INFO_100198, LoulanDNSMessageConstants.INFO_100198_MSG);

          // ソケットをクリアする.
          sock.disconnect();
          sock.close();

          // タスク状態を停止状態に設定する.
          setDNSEndpointServiceTaskStatus( LoulanDNSConstants.CONST_TASK_STATUS_INVACTIVE_DNS_SERVICE_ENDPOINT );

          // INFO-100199 : "Endpoint Service Task is accidentaly STOPPED.";
          logger.info(LoulanDNSMessageConstants.INFO_100199, LoulanDNSMessageConstants.INFO_100199_MSG);

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
        catch(InsufficientDNSMessageException exception)
        {
          // 受信したDNSリクエストパケットが不十分なサイズで処理できない.
          // TCPでは追加受信すべきだが、UDPでは追加受信せずにエラーを返却すべき.

          // TODO : 当面は標準出力でも例外を出力する.
          exception.printStackTrace();

          // ERROR-100101 : "Invalid DNS Question Messagge.";
          logger.error(LoulanDNSMessageConstants.ERROR_100101, LoulanDNSMessageConstants.ERROR_100101_MSG, exception);


          if ( getDNSEndpointServiceTaskIgnoreError() )
          {
            // IgnoreErrorフラグが設定されている場合はエラーを無視して続行する.
            continue;
          }


          // 例外を上位層にスローして処理を停止する.

          // IgnoreErrorフラグがセットされていない場合は、ソケットを閉じてEndpointサービスタスクを終了する.
          // INFO-100198 : "Endpoint Service Task is going to accidentaly STOP.";
          logger.info(LoulanDNSMessageConstants.INFO_100198, LoulanDNSMessageConstants.INFO_100198_MSG);

          // ソケットをクリアする.
          sock.disconnect();
          sock.close();

          // タスク状態を停止状態に設定する.
          setDNSEndpointServiceTaskStatus( LoulanDNSConstants.CONST_TASK_STATUS_INVACTIVE_DNS_SERVICE_ENDPOINT );

          // INFO-100199 : "Endpoint Service Task is accidentaly STOPPED.";
          logger.info(LoulanDNSMessageConstants.INFO_100199, LoulanDNSMessageConstants.INFO_100199_MSG);

          throw exception;

        }
        catch(DNSServiceCommonException exception)
        {

          // DNSメッセージ解析時に想定外のエラーが発生した.

          // TODO : 当面は標準出力でも例外を出力する.
          exception.printStackTrace();

          // ERROR-100101 : "Invalid DNS Question Messagge.";
          logger.error(LoulanDNSMessageConstants.ERROR_100101, LoulanDNSMessageConstants.ERROR_100101_MSG, exception);

          if ( getDNSEndpointServiceTaskIgnoreError() )
          {
            // IgnoreErrorフラグが設定されている場合はエラーを無視して続行する.
            continue;
          }

          // 例外を上位層にスローして処理を停止する.

          // IgnoreErrorフラグがセットされていない場合は、ソケットを閉じてEndpointサービスタスクを終了する.
          // INFO-100198 : "Endpoint Service Task is going to accidentaly STOP.";
          logger.info(LoulanDNSMessageConstants.INFO_100198, LoulanDNSMessageConstants.INFO_100198_MSG);

          // ソケットをクリアする.
          sock.disconnect();
          sock.close();

          // タスク状態を停止状態に設定する.
          setDNSEndpointServiceTaskStatus( LoulanDNSConstants.CONST_TASK_STATUS_INVACTIVE_DNS_SERVICE_ENDPOINT );

          // INFO-100199 : "Endpoint Service Task is accidentaly STOPPED.";
          logger.info(LoulanDNSMessageConstants.INFO_100199, LoulanDNSMessageConstants.INFO_100199_MSG);

          throw exception;
        }

        if ( protocolUtils.isDNSQuestionMessage(dnsMessage) == false )
        {
          // 受信したDNSメッセージはDNS問い合わせメッセージではない.

          String msg = String.format("Not DNS Question Message. DNS message is %s.", dnsMessage );
          DNSServiceCommonException exception = new DNSServiceCommonException(msg);

          // TODO : 当面は標準出力にも例外を出力しておく.
          exception.printStackTrace();

          // ERROR-100201 : Invalid DNS Response Messagge.
          logger.error(LoulanDNSMessageConstants.ERROR_100201, LoulanDNSMessageConstants.ERROR_100201_MSG, exception);

          if ( getDNSEndpointServiceTaskIgnoreError() )
          {
            // IgnoreErrorフラグが設定されている場合はエラーを無視して続行する.
            continue;
          }

          // 例外を上位層にスローして処理を停止する.

          // IgnoreErrorフラグがセットされていない場合は、ソケットを閉じてEndpointサービスタスクを終了する.
          // INFO-100198 : "Endpoint Service Task is going to accidentaly STOP.";
          logger.info(LoulanDNSMessageConstants.INFO_100198, LoulanDNSMessageConstants.INFO_100198_MSG);

          // ソケットをクリアする.
          sock.disconnect();
          sock.close();

          // タスク状態を停止状態に設定する.
          setDNSEndpointServiceTaskStatus( LoulanDNSConstants.CONST_TASK_STATUS_INVACTIVE_DNS_SERVICE_ENDPOINT );

          // INFO-100199 : "Endpoint Service Task is accidentaly STOPPED.";
          logger.info(LoulanDNSMessageConstants.INFO_100199, LoulanDNSMessageConstants.INFO_100199_MSG);

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
        // 以下の処理はスレッド内で並列実行する.

        UDPServiceThreadTask serviceThreadTask = new UDPServiceThreadTask(sock, recvPacket, dnsQuestionMessage, logger);
        Thread thread = new Thread(serviceThreadTask);
        thread.start();

        // TODO : スレッド内で発生したエラーのうち、致命的なものを補足して上位層にスローする処理が未実装.


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



    class UDPServiceThreadTask implements Runnable
    {

      DatagramSocket sock;
      DatagramPacket recvPacket;

      IDNSQuestionMessage dnsQuestionMessage;
      ILoulanDNSLogger logger;

      DNSServiceCommonException failedTaskException = null;

      public UDPServiceThreadTask(DatagramSocket sock, DatagramPacket recvPacket, IDNSQuestionMessage dnsQuestionMessage, ILoulanDNSLogger logger)
      {
        this.sock = sock;
        this.recvPacket = recvPacket;
        this.dnsQuestionMessage = dnsQuestionMessage;
        this.logger = logger;
      }

      public boolean isFailed()
      {
        if ( failedTaskException == null )
        {
          return false;
        }

        return true;
      }

      public DNSServiceCommonException getFialedTaskException()
      {
        return this.failedTaskException;
      }

      public void run()
      {

        try
        {

            // DNSサービスインスタンス経由でDNS問い合わせを実行する.
            IDNSResponseMessage dnsResponseMessage;
            
            try
            {
              dnsResponseMessage = serveDNSQuery(dnsQuestionMessage);
            }
            catch(DNSProtocolErrorRCodeException cause)
            {
              // DNSエラーメッセージを返却すべき例外事象が発生した.
              // 最上位層であるEndpointサービスにおいて例外を処理しつつ、DNSクライアントにはRCODEが0以外(ERROR)のDNSレスポンスを返却する.
              
              // TODO : 当面は例外を標準出力例外にも出力する.
              cause.printStackTrace();

              // WARN-200101 : "DNS protocol error is happend."
              logger.warn(LoulanDNSMessageConstants.WARN_200101, LoulanDNSMessageConstants.WARN_200101_MSG, cause);

              
              // クライアントにはRCODEが0以外(エラー)のDNSレスポンスメッセージを返却する.
              dnsResponseMessage = cause.createDNSErrorResponseMessage();

            }

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

              String msg = String.format("Failed to send DNS Response Message on DNS UDP Service Endpoint. userName=%s, serviceInstanceName=%s, address=%s, port=%d.", getUserName(), getDNSServiceInstanceName(), getUDPServiceSocketAddress(), getUDPServiceEndpointPort() );
              DNSServiceCommonException exception = new DNSServiceCommonException(msg, cause);

              // TODO :　当面は例外を標準出力する.
              exception.printStackTrace();

              // ERROR-800101 : "I/O error.";
              logger.error(LoulanDNSMessageConstants.ERROR_800101, LoulanDNSMessageConstants.ERROR_800101_MSG, exception);


              if ( getDNSEndpointServiceTaskIgnoreError() )
              {
                // IgnoreErrorフラグが設定されている場合はエラーを無視して続行する.
                return;
              }

              // IgnoreErrorフラグがセットされていない場合は、ソケットを閉じてEndpointサービスタスクを終了する.
              // INFO-100198 : "Endpoint Service Task is going to accidentaly STOP.";
              logger.info(LoulanDNSMessageConstants.INFO_100198, LoulanDNSMessageConstants.INFO_100198_MSG);

              // ソケットをクリアする.
              sock.disconnect();
              sock.close();

              // タスク状態を停止状態に設定する.
              setDNSEndpointServiceTaskStatus( LoulanDNSConstants.CONST_TASK_STATUS_INVACTIVE_DNS_SERVICE_ENDPOINT );

              // INFO-100199 : "Endpoint Service Task is accidentaly STOPPED.";
              logger.info(LoulanDNSMessageConstants.INFO_100199, LoulanDNSMessageConstants.INFO_100199_MSG);

              throw exception;
            }

          }
          catch(DNSServiceCommonException cause)
          {
            this.failedTaskException = cause;
          }

      }

    }


}