package org.loxsols.net.service.dns.loulandns.client.impl.simple;

import org.loxsols.net.service.dns.loulandns.client.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.*;

import org.loxsols.net.service.dns.loulandns.client.common.DNSClientCommonException;
import org.loxsols.net.service.dns.loulandns.client.common.LoulanDNSClientConstants;
import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSDebugUtils;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSProtocolUtils;
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
public class SimpleTCPMessageTransporterImpl extends SimpleUDPMessageTransporterImpl implements IDNSMessageTransporter
{


    LoulanDNSProtocolUtils protocolUtils = new LoulanDNSProtocolUtils();


    // TCPのソケットチャネル.
    SocketChannel socketChannel = null;


    public SimpleTCPMessageTransporterImpl() throws DNSClientCommonException
    {
        super();
    }





    public void init(HashMap<String, String> properties) throws DNSClientCommonException
    {
        super.init(properties);
    }


    /**
     * DNSサーバーにTCPコネクションを張る.
     * 
     * @throws DNSClientCommonException
     */
    protected boolean openTCPConnection() throws DNSClientCommonException
    {
        InetAddress servAddress = getServerAddress();
        int serverPort = getServerPort();

        InetSocketAddress remoteAddress = new InetSocketAddress(servAddress, serverPort);

        try
        {
            this.socketChannel = SocketChannel.open();
            boolean ret = socketChannel.connect( remoteAddress );

            if ( ret == false )
            {
                closeTCPConnection();

                String msg = String.format("Failed to connect DNS TCP Server. serverAddress=%s, serverPort=%d", serverAddress.toString(), serverPort );
                DNSClientCommonException exception = new DNSClientCommonException(msg);
                throw exception;                
            }
        }
        catch(IOException cause)
        {
            String msg = String.format("Failed to open TCP connection for DNS message transporter. serverAddress=%s, serverPort=%d", serverAddress.toString(), serverPort);
            DNSClientCommonException exception = new DNSClientCommonException(msg, cause);
            throw exception; 
        }

        return true;

    }


    /**
     * DNSサーバーとのにTCPコネクションをクローズする.
     * 
     * @throws DNSClientCommonException
     */
    protected void closeTCPConnection() throws DNSClientCommonException
    {
        try
        {
            this.socketChannel.close();
        }
        catch(IOException cause)
        {
            String msg = String.format("Failed to close TCP connection for DNS message transporter. serverAddress=%s, serverPort=%d", getServerAddress(), getServerPort() );
            DNSClientCommonException exception = new DNSClientCommonException(msg, cause);
            throw exception; 
        }

        this.socketChannel = null;
    }


    /**
     * DNSサーバーとのTCPコネクションが開通済みの場合はtrueを返す.
     * 
     * @return
     */
    protected boolean isOpenTCPConnection()
    {
        if ( this.socketChannel == null )
        {
            return false;
        }

        if ( this.socketChannel.isOpen() == false )
        {
            return false;
        }

        if ( this.socketChannel.isConnected() == false )
        {
            return false;
        }

        return true;
    }




    public byte[] lookup(byte[] wiredQuestionMessage) throws DNSClientCommonException
    {

        byte[] wiredResponseMessage;
        try
        {


            // TCP#53のDNSパケットはUDP#53のDNSパケットに2バイトのパケット長フィールドを先頭に付加したもの.
            byte[] tcp53DNSPacket;
            try
            {
                tcp53DNSPacket = protocolUtils.toTCP53DNSPacketBytes(wiredQuestionMessage);
            }
            catch(DNSServiceCommonException cause)
            {
                String msg = String.format("Failed to create TCP#53 DNS Packet.");
                DNSClientCommonException exception = new DNSClientCommonException(msg);
                throw exception;
            }


            // socketChannel = SocketChannel.open();
            
            // // TCPサーバーへ接続
            // InetAddress serverAddress = getServerAddress();
            // int serverPort = getServerPort();
            // InetSocketAddress remoteAddress = new InetSocketAddress(serverAddress, serverPort);
            
            // boolean ret = socketChannel.connect( remoteAddress );
            // if ( ret == false )
            // {
            //     socketChannel.close();

            //     String msg = String.format("Failed to connect DNS TCP Server. serverAddress=%s, serverPort=%d", serverAddress.toString(), serverPort );
            //     DNSClientCommonException exception = new DNSClientCommonException(msg);
            //     throw exception;                
            // }


            if ( isOpenTCPConnection() == false )
            {
                boolean ret = openTCPConnection();
                if ( ret == false )
                {
                     String msg = String.format("Failed to open TCP connection to DNS server. serverAddress=%s, serverPort=%d", getServerAddress().toString(), getServerPort() );
                     DNSClientCommonException exception = new DNSClientCommonException(msg);
                    throw exception;            
                }
            }



            // TCPデータ送信
            ByteBuffer sendBuffer = ByteBuffer.wrap(tcp53DNSPacket);
            while (sendBuffer.hasRemaining())
            {
                socketChannel.write(sendBuffer);
            }

            // TCP#53 DNSパケットを受信
            ByteBuffer receiveBuffer = ByteBuffer.allocate( LoulanDNSClientConstants.TCP_CLIENT_MAX_RECEIVE_BUFFER_SIZE );
            long readSize = socketChannel.read(receiveBuffer);

            receiveBuffer.flip();
            
            // TCP#53 DNS受信パケットのlenフィールドの取得.
            byte[] tcp53HeaderField = new byte[2];
            receiveBuffer.get(tcp53HeaderField);

            // TCP#53 DNS受信パケットの先頭2byteはメッセージ長である.
            // メッセージ長フィールドを取得する.
            long len = protocolUtils.bytesToUInt16(tcp53HeaderField);

            if ( len < 0 || len > LoulanDNSClientConstants.TCP_CLIENT_MAX_RECEIVE_BUFFER_SIZE )
            {

                String msg = String.format("Failed to receive TCP DNS packet. Length field value is not valid : %d", len );
                DNSClientCommonException exception = new DNSClientCommonException(msg);
                throw exception;                
            }

            while( readSize < len + 2 )
            {
                readSize += socketChannel.read(receiveBuffer);
            }

            wiredResponseMessage = new byte[(int)len];
            receiveBuffer.get(wiredResponseMessage);

        }
        catch(IOException cause)
        {
            String msg = String.format("Failed to transport DNS message.");
            DNSClientCommonException exception = new DNSClientCommonException(msg, cause);
            throw exception; 
        }

        return wiredResponseMessage;
    }



}
