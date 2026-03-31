package org.loxsols.net.service.dns.loulandns.client.impl.simple;

import org.loxsols.net.service.dns.loulandns.client.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.*;

import org.loxsols.net.service.dns.loulandns.client.common.DNSClientCommonException;
import org.loxsols.net.service.dns.loulandns.client.common.LoulanDNSClientConstants;
import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSDebugUtils;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.SimpleDNSMessageFactoryImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.model.SimpleDNSProtocolModelInstanceFactoryImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.IDNSMessageFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.model.IDNSProtocolModelInstanceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.xbill.DNS.NioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.io.*;


import java.io.EOFException;
import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.ComponentScan;


@ComponentScan
public class SimpleUDPMessageTransporterImpl implements IDNSMessageTransporter
{

    IDNSProtocolModelInstanceFactory dnsProtocolModelInstanceFactory;
    IDNSMessageFactory dnsMessageFactory;

    InetAddress serverAddress;
    int serverPort;


    public SimpleUDPMessageTransporterImpl() throws DNSClientCommonException
    {
        IDNSProtocolModelInstanceFactory dnsProtocolModelInstanceFactory;
        IDNSMessageFactory dnsMessageFactory;

        try
        {
            dnsProtocolModelInstanceFactory = new SimpleDNSProtocolModelInstanceFactoryImpl();
        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to build IDNSProtocolModelInstanceFactory object.");
            DNSClientCommonException exception = new DNSClientCommonException(msg, cause);
            throw exception;
        }

        try
        {
            dnsMessageFactory = new SimpleDNSMessageFactoryImpl();
        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to build IDNSMessageFactory object.");
            DNSClientCommonException exception = new DNSClientCommonException(msg, cause);
            throw exception;
        }

        setDNSProtocolModelInstanceFactory( dnsProtocolModelInstanceFactory );
        setDNSMessageFacotry( dnsMessageFactory );
    }


    public void setDNSProtocolModelInstanceFactory(IDNSProtocolModelInstanceFactory instance)
    {
        this.dnsProtocolModelInstanceFactory = instance;
    }

    public void setDNSMessageFacotry(IDNSMessageFactory instance)
    {
        this.dnsMessageFactory = instance;
    }

    public void setServerAddress(InetAddress address)
    {
        this.serverAddress = address;
    }

    public InetAddress getServerAddress()
    {
        return this.serverAddress;
    }

    public void setServerPort(int port)
    {
        this.serverPort = port;
    }

    public int getServerPort()
    {
        return this.serverPort;
    }


    public void init(HashMap<String, String> properties) throws DNSClientCommonException
    {
        String serverAddressValue    =  properties.get(LoulanDNSClientConstants.PROP_KEY_DNS_SERVER_ADDRESS);
        String serverPortValue       =  properties.get(LoulanDNSClientConstants.PROP_KEY_DNS_SERVER_PORT);

        if ( serverAddressValue == null || serverAddressValue.equals(""))
        {
            String msg = String.format("DNS Server address is not specified.");
            DNSClientCommonException exception = new DNSClientCommonException(msg);
            throw exception;
        }

        if ( serverPortValue == null || serverPortValue.equals(""))
        {
            String msg = String.format("DNS Server port is not specified.");
            DNSClientCommonException exception = new DNSClientCommonException(msg);
            throw exception;
        }

        try
        {
            serverAddress = InetAddress.getByName(serverAddressValue);
        }
        catch(UnknownHostException cause)
        {
            String msg = String.format("Failed to parse DNS Server address. value=%s", serverPortValue );
            DNSClientCommonException exception = new DNSClientCommonException(msg, cause);
            throw exception; 
        }


        try
        {
            serverPort = Integer.parseInt(serverPortValue);
        }
        catch(NumberFormatException cause)
        {
            String msg = String.format("Failed to parse DNS Server port. value=%s", serverPortValue );
            DNSClientCommonException exception = new DNSClientCommonException(msg, cause);
            throw exception; 
        }

    }

    public IDNSResponseMessage lookup(IDNSQuestionMessage questionMessage) throws DNSClientCommonException
    {

        byte[] questionBytes;
        try
        {
            questionBytes = questionMessage.getDNSMessageBytes();
        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to build DNS Question Message Bytes." );
            DNSClientCommonException exception = new DNSClientCommonException(msg, cause);
            throw exception; 
        }


        try
        {
            // デバッグ用のブロック. ブロックごとけしてもかまわない.
            LoulanDNSDebugUtils.printDebug( this.getClass(), "lookup", "qname=" + questionMessage.getDNSQuestionSection().getDNSQueries()[0].getDNSQueryName() );
            LoulanDNSDebugUtils.printDebug( this.getClass(), "lookup", "qtype=" + questionMessage.getDNSQuestionSection().getDNSQueries()[0].getDNSQueryType() );
            LoulanDNSDebugUtils.printDebug( this.getClass(), "lookup", "qclass=" + questionMessage.getDNSQuestionSection().getDNSQueries()[0].getDNSQueryClass() );

            LoulanDNSDebugUtils.printDebug( this.getClass(), "lookup", String.format("questionBytes.length=%d ", questionBytes.length ) );
            LoulanDNSDebugUtils.printHexString(getClass(), "lookup", questionBytes);

        }
        catch(DNSServiceCommonException cause)
        {
            // デバッグ用なので例外は標準出力して無視する.
            cause.printStackTrace();
        }


        byte[] responseBytes = lookup(questionBytes);

        IDNSResponseMessage responseMessage;
        try
        {
            // responseMessage = dnsMessageFactory.createDNSMesssage(responseBytes);
            responseMessage = dnsMessageFactory.createResponseDNSMesssage(responseBytes);
        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to build DNS Response Message Bytes." );
            DNSClientCommonException exception = new DNSClientCommonException(msg, cause);
            throw exception; 
        }


        // ------ For Debug ------------
        try
        {
            // デバッグ用のブロック. ブロックごとけしてもかまわない.
            LoulanDNSDebugUtils.printDebug( this.getClass(), "lookup", "ANCOUNT=" + responseMessage.getDNSHeaderSection().getANCOUNT() );
            LoulanDNSDebugUtils.printDebug( this.getClass(), "lookup", "answerSection=" + responseMessage.getDNSAnswerSection()  );
         
            LoulanDNSDebugUtils.printDebug( this.getClass(), "lookup", "responseBytes.length=" + responseBytes.length );  
            LoulanDNSDebugUtils.printHexString(getClass(), "lookup", responseBytes );

        }
        catch(DNSServiceCommonException cause)
        {
            // デバッグ用なので例外は標準出力して無視する.
            cause.printStackTrace();
        }



        return responseMessage;
    }

    public byte[] lookup(byte[] wiredQuestionMessage) throws DNSClientCommonException
    {
        ByteBuffer sendBuffer = ByteBuffer.wrap(wiredQuestionMessage);

        byte[] wiredResponseMessage;
        try
        {
            DatagramChannel channel = DatagramChannel.open();

            InetSocketAddress remoteAddress = new InetSocketAddress(serverAddress, serverPort);
            channel.connect( remoteAddress );

            // channel.send(sendBuffer, target);
            channel.write(sendBuffer);


            System.out.println("[DEBUG] UDPMessageTransporterImpl.lookup() receive start.");

            ByteBuffer receiveBuffer = ByteBuffer.allocate(LoulanDNSClientConstants.UDP_CLIENT_MAX_RECEIVE_BUFFER_SIZE);
            // channel.receive(receiveBuffer);
            channel.read(receiveBuffer);

            System.out.println("[DEBUG] UDPMessageTransporterImpl.lookup() receive end.");

            receiveBuffer.flip();
            
            wiredResponseMessage = new byte[receiveBuffer.limit()];
            receiveBuffer.get(wiredResponseMessage);

            channel.close();
        }
        catch(IOException cause)
        {
            String msg = String.format("Failed to transport DNS message.");
            DNSClientCommonException exception = new DNSClientCommonException(msg, cause);
            throw exception; 
        }

        return wiredResponseMessage;
    }



    /*
    // OSSのdnsjavaプロジェクトから引用した非同期のUDP通信コード.
    // TODO : 試験評価用コードのため試験後はどうするか決める必要がある. OSSのライセンス調査など.
    CompletableFuture<byte[]> sendrecv(InetSocketAddress local, InetSocketAddress remote, byte[] sendData, byte[] data, Duration timeout)
    {
      CompletableFuture<byte[]> f = new CompletableFuture();

      try {
         Selector selector = selector();
         long endTime = System.nanoTime() + timeout.toNanos();
         ChannelState channel = (ChannelState)channelMap.computeIfAbsent(new ChannelKey(local, remote), (key) -> {
            log.debug("Opening async channel for l={}/r={}", local, remote);
            SocketChannel c = null;

            try {
               c = SocketChannel.open();
               c.configureBlocking(false);
               if (local != null) {
                  c.bind(local);
               }

               c.connect(remote);
               return new ChannelState(c);
            } catch (IOException var8) {
               if (c != null) {
                  try {
                     c.close();
                  } catch (IOException var7) {
                  }
               }

               f.completeExceptionally(var8);
               return null;
            }
         });
         if (channel != null) {
            log.trace("Creating transaction for id {} ({}/{})", new Object[]{query.getHeader().getID(), query.getQuestion().getName(), Type.string(query.getQuestion().getType())});
            Transaction t = new Transaction(query, data, endTime, channel.channel, f);
            channel.pendingTransactions.add(t);
            registrationQueue.add(channel);
            selector.wakeup();
         }
      } catch (IOException var11) {
         f.completeExceptionally(var11);
      }

      return f;
   }


    Selector selector() throws IOException
    {
        Selector selector;
      if (selector == null) {
         Class var0 = NioClient.class;
         synchronized(NioClient.class) {
            if (selector == null) {
               selector = Selector.open();
               log.debug("Starting dnsjava NIO selector thread");
               run = true;
               selectorThread = new Thread(NioClient::runSelector);
               selectorThread.setDaemon(true);
               selectorThread.setName("dnsjava NIO selector");
               selectorThread.start();
               closeThread = new Thread(() -> {
                  close(true);
               });
               closeThread.setName("dnsjava NIO shutdown hook");
               Runtime.getRuntime().addShutdownHook(closeThread);
            }
         }
      }

      return selector;
   }
   */


}
