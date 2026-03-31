package org.loxsols.net.service.dns.loulandns.client.command.lookup.simple;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Date;
import java.text.SimpleDateFormat;

import lombok.Data;


import org.loxsols.net.service.dns.loulandns.client.impl.*;
import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.common.constants.LoulanDNSConstants;
import org.loxsols.net.service.dns.loulandns.server.http.spring.service.LoulanDNSDBService;
import org.loxsols.net.service.dns.loulandns.server.http.spring.service.LoulanDNSLogicalDBService;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.DNSMessageFactoryImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.section.DNSAnswerSectionFactoryImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.section.DNSHeaderSectionFactoryImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.section.DNSQuestionSectionFactoryImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.model.DNSProtocolModelInstanceFactoryImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.IDNSMessageFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.section.IDNSAnswerSectionFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.section.IDNSHeaderSectionFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.section.IDNSQuestionSectionFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.model.IDNSProtocolModelInstanceFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSResponseMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.IDNSQuestionSection;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.IDNSResourceRecord;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.IDNSQueryPart;
import org.loxsols.net.service.dns.loulandns.server.logical.service.LoulanDNSLogicalModelService;
import org.loxsols.net.service.dns.loulandns.util.DNSUtils;
import org.loxsols.net.service.dns.loulandns.server.common.util.*;
import org.loxsols.net.service.dns.loulandns.client.common.*;

import org.loxsols.net.service.dns.loulandns.client.*;
import org.loxsols.net.service.dns.loulandns.client.command.lookup.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties.Simple;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.xbill.DNS.ZoneTransferException;

import com.android.internal.org.bouncycastle.util.Arrays;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;


// シンプルなdigコマンドのアプリケーションのステータスクラス.
public class SimpleDNSLookupCommandJobStatus
{

    public String commandVersion = "0.0.1";

    public InetAddress serverAddress;
    public int serverPort;

    public String[] commandArguments;
    HashMap<String, String> commandProperties;

    public IDNSResponseMessage dnsResponseMessage;

    public Date queryStartDate;
    public Date queryEndDate;

    DNSUtils dnsUtils = new DNSUtils();

    public void setServerAddress(InetAddress value)
    {
        serverAddress = value;
    }

    public void setServerPort(int value)
    {
        serverPort = value;
    }

    public void setCommandArguments(String[] value)
    {
        commandArguments = value;
    }

    public void setCommandProperties(HashMap<String, String> value)
    {
        commandProperties = value;
    }

    public void setQueryStartDate(Date value)
    {
        queryStartDate = value;
    }

    public void setQueryEndDate(Date value)
    {
        queryEndDate = value;
    }


    public void setDNSResponseMessage(IDNSResponseMessage value)
    {
        dnsResponseMessage = value;
    }

    public SimpleDNSLookupCommandJobStatus(InetAddress serverAddress, int serverPort, String[] args, HashMap<String, String> commandProperties, IDNSResponseMessage response, Date queryStartDate, Date queryEndDate) throws DNSClientCommonException
    {

        setServerAddress(serverAddress);
        setServerPort(serverPort);

        setCommandArguments(args);
        setCommandProperties(commandProperties);

        setDNSResponseMessage(response);

        setQueryStartDate(queryStartDate);
        setQueryEndDate(queryEndDate);

    }


    // コマンドのバージョンを返す.
    public String getCommandVersion() throws DNSClientCommonException
    {
        return commandVersion;
    }

    public String[] getCommandArguments() throws DNSClientCommonException
    {
        return commandArguments;
    }

    public InetAddress getServerAddress() throws DNSClientCommonException
    {
        return serverAddress;
    }

    // コマンド実行時のコマンドラインの文字列を返す.
    // 例) "@1.1.1.1 -p 53 www.google.co.jp"
    public String getCommandLine() throws DNSClientCommonException
    {
        String commandLine = "";
        for( String arg : getCommandArguments() )
        {
            commandLine += arg + " ";
        }

        return commandLine;
    }

    // コマンド実行時のグローバルオプションの一覧を返す.
    // 例) "+cmd"
    public String getGlobalOptions() throws DNSClientCommonException
    {
        String globalOptions = "TODO";

        return globalOptions;
    }


    // DNSサーバーのIPアドレスの文字列を返す.
    public String getServerIPAddressString() throws DNSClientCommonException
    {
        String ipString;
        
        try
        {
            ipString = dnsUtils.toIPAddressString( getServerAddress() );
        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to convert DNS Server address to String. serverAddress=%s", getServerAddress().toString() );
            DNSClientCommonException exception = new DNSClientCommonException(msg, cause);
            throw exception;
        }

        return ipString;
    }


    // DNSサーバーのホスト名またはIPアドレスを返す.
    public String getServerHostString() throws DNSClientCommonException
    {
        String host = getServerAddress().toString().split("/")[0];

        if ( host.isEmpty() )
        {
            host = getServerIPAddressString();
        }

        return host;
    }

    public IDNSResponseMessage getDNSResponseMessage() throws DNSClientCommonException
    {
        return dnsResponseMessage;
    }


    
    public Date getQueryStartDate() throws DNSClientCommonException
    {
        return queryStartDate;
    }

    public Date getQueryEndDate() throws DNSClientCommonException
    {
        return queryEndDate;
    }

    public int getServerPort() throws DNSClientCommonException
    {
        return serverPort;
    }

}