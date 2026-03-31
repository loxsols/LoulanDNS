package org.loxsols.net.service.dns.loulandns.app.spring.endpoint.general;


import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.spi.ExplicitBooleanOptionHandler;
import org.loxsols.net.service.dns.loulandns.client.common.DNSClientCommonException;
import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.common.constants.LoulanDNSConstants;
import org.loxsols.net.service.dns.loulandns.server.common.data.LoulanDNSOptionProperties;

import java.io.PrintStream;
import java.util.*;

/**
 * LoulanDNSEndpointServiceApplicationのコマンドラインパーサークラス
 * 
 */
public class LoulanDNSEndpointServiceApplicationCommandLineParser
{

    @Option(name = "-load", metaVar = "SystemType", required = true,  usage = "Load target system")
    public String systemType;

    @Option(name = "-su", metaVar = "ServiceUserName", required = true,  usage = "Service User Name")
    public String serviceUserName;

    @Option(name = "-sn", metaVar = "ServiceInstanceName", required = true,  usage = "Service Instance Name")
    public String serviceInstanceName;

    @Option(name = "-st", metaVar = "ServiceInstanceType", required = false,  usage = "Service Instance Type")
    public String serviceInstanceType;


    @Option(name = "-eu", metaVar = "EndpointUserName", required = true,  usage = "Endpoint User Name")
    public String endpointUserName;

    @Option(name = "-en", metaVar = "EndpointInstanceName", required = true,  usage = "Endpoint Instance Name")
    public String endpointInstanceName;

    @Option(name = "-et", metaVar = "EndpointInstanceType", required = false,  usage = "Endpoint Instance Type")
    public String endpointInstanceType;

    @Option(name = "-rn", metaVar = "ResolverInstanceName", required = false,  usage = "Resolver Instance Name")
    public String resolverInstanceName;

    @Option(name = "-rt", metaVar = "ResolverInstanceType", required = false,  usage = "Resolver Instance Type")
    public String resolverInstanceType;

    @Option(name = "-sargs", metaVar = "ServiceInstanceProperties", required = false,  usage = "Endpoint Instance Properties")
    public String serviceInstancePropertiesArguments;

    @Option(name = "-eargs", metaVar = "EndpointInstanceProperties", required = false,  usage = "Endpoint Instance Properties")
    public String endpointInstancePropertiesArguments;

    @Option(name = "-rargs", metaVar = "ResolverInstanceProperties", required = false,  usage = "Resolver Instance Properties")
    public String resolverInstancePropertiesArguments;



    public void printUsage(PrintStream ps) throws DNSServiceCommonException
    {
        String msg = "";
        
        msg += "Usage : \n";
        msg += String.format("-load database  -su <ServiceUserName> -sn <ServiceInstanceName> -en <EndpointInstanceName> \n");
        msg += String.format("-load temporary -su <ServiceUserName> -sn <ServiceInstanceName> -st <ServiceInstanceType> [-sargs <key1=value1;>...] -en <EndpointInstanceName> -et <EndpointType> [-eargs <key1=value1;...>] -rt <ResolverType>  [-rargs <key1=value1;...>] \n");
        
        msg += String.format("<ServiceInstanceType> : minimum \n");
        msg += String.format("<EndpointType> : udp | tcp | doh | dot \n");
        msg += String.format("<ResolverType> : udp | tcp | doh | dot \n");

        ps.println(msg);

    }


    /**
     * サービスインスタンスのデフォルトのオプションプロパティを設定する.
     * 
     * @return
     */
    protected LoulanDNSOptionProperties buildServiceInstanceDefaultOptionProperties() throws DNSServiceCommonException
    {

        LoulanDNSOptionProperties optionProperties = new LoulanDNSOptionProperties();

        // loulan.dns.user.name=admin
        optionProperties.putDefaultValue( LoulanDNSConstants.PROP_KEY_USER_NAME, "admin");

        // loulan.dns.service.instance.name=default
        optionProperties.putDefaultValue( LoulanDNSConstants.PROP_KEY_SERVICE_INSTANCE_NAME, "default");

        return optionProperties;
    }


    /**
     * エンドポイントインスタンスのデフォルトのオプションプロパティを設定する.
     * 
     * @return
     * @throws DNSServiceCommonException
     */
    protected LoulanDNSOptionProperties buildEndpointInstanceDefaultOptionProperties() throws DNSServiceCommonException
    {
        LoulanDNSOptionProperties optionProperties = new LoulanDNSOptionProperties();


        // loulan.dns.user.name=admin
        optionProperties.putDefaultValue( LoulanDNSConstants.PROP_KEY_USER_NAME, "admin");

        // loulan.dns.service.instance.name=default
        optionProperties.putDefaultValue( LoulanDNSConstants.PROP_KEY_SERVICE_INSTANCE_NAME, "default");

        // loulan.dns.service.endpoint.udp.address=0.0.0.0
        optionProperties.putDefaultValue( LoulanDNSConstants.PROP_KEY_SERVICE_ENDPOINT_UDP_BIND_ADDRESS, "0.0.0.0");

        // loulan.dns.service.endpoint.udp.port=50053
        optionProperties.putDefaultValue( LoulanDNSConstants.PROP_KEY_SERVICE_ENDPOINT_UDP_BIND_PORT, "50053");

        return optionProperties;
    }


    /**
     * リゾルバーインスタンスのデフォルトのオプションプロパティを設定する.
     * 
     * @return
     * @throws DNSServiceCommonException
     */
    protected LoulanDNSOptionProperties buildResolverInstanceDefaultOptionProperties() throws DNSServiceCommonException
    {

        LoulanDNSOptionProperties optionProperties = new LoulanDNSOptionProperties();

        // loulan.dns.resolver.outbound.server.host.primary=1.1.1.1
        optionProperties.putDefaultValue( LoulanDNSConstants.PROP_KEY_RESOLVER_OUTBOUND_SERVER_HOST_PRIMARY, "1.1.1.1");

        // loulan.dns.resolver.outbound.server.port.primary=53
        optionProperties.putDefaultValue( LoulanDNSConstants.PROP_KEY_RESOLVER_OUTBOUND_SERVER_PORT_PRIMARY, "53");


        return optionProperties;
    }
    


    public void parse(String[] args) throws DNSServiceCommonException
    {

        CmdLineParser parser = new CmdLineParser(this);
        try
        {
            parser.parseArgument(args);
        }
        catch (CmdLineException cause)
        {

            cause.printStackTrace();

            String msg = "Failed to parse GeneralEndpointServiceApplication command argments.";
            DNSClientCommonException exception = new DNSClientCommonException(msg, cause);

            throw exception;
        }

        check();

    }


    private void check() throws DNSServiceCommonException
    {
        if ( systemType == null || systemType.equals("") )
        {
            String msg = String.format("Failed to parse command line parameters. SystemType is not specified.");
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        if ( systemType.equals("database") == false &&  systemType.equals("temporary") == false  )
        {
            String msg = String.format("Failed to parse command line parameters. Unknown SystemType : %s", systemType );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        if ( endpointInstanceName == null || endpointInstanceName.equals("") )
        {
            String msg = String.format("Failed to parse command line parameters. SystemType is not specified.");
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        if ( systemType.equals("temporary") )
        {
            if ( endpointInstanceType == null || endpointInstanceType.equals("") )
            {
                String msg = String.format("Failed to parse command line parameters. EndpointType is not specified.");
                DNSServiceCommonException exception = new DNSServiceCommonException(msg);
                throw exception;
            }

            if ( endpointInstanceType.equals("udp") == false && endpointInstanceType.equals("tcp") == false && endpointInstanceType.equals("doh") == false && endpointInstanceType.equals("dot") )
            {
                String msg = String.format("Failed to parse command line parameters. Unknown EndpointType : %s", endpointInstanceType );
                DNSServiceCommonException exception = new DNSServiceCommonException(msg);
                throw exception;
            }


            if ( resolverInstanceType == null || resolverInstanceType.equals("") )
            {
                String msg = String.format("Failed to parse command line parameters. ResolverType is not specified.");
                DNSServiceCommonException exception = new DNSServiceCommonException(msg);
                throw exception;
            }

            if ( resolverInstanceType.equals("udp") == false && resolverInstanceType.equals("tcp") == false && resolverInstanceType.equals("doh") == false && resolverInstanceType.equals("dot") )
            {
                String msg = String.format("Failed to parse command line parameters. Unknown ResolverType : %s", resolverInstanceType );
                DNSServiceCommonException exception = new DNSServiceCommonException(msg);
                throw exception;
            }

        }

    }


    public String getSystemType() throws DNSServiceCommonException
    {
        return systemType;
    }

    public String getServiceUserName()  throws DNSServiceCommonException
    {
        return serviceUserName;
    }

    public String getServiceInstanceName()  throws DNSServiceCommonException
    {
        return serviceInstanceName;
    }

    public String getServiceInstanceType() throws DNSServiceCommonException
    {
        return serviceInstanceType;
    }

    public String getEndpointUserName()  throws DNSServiceCommonException
    {
        return endpointUserName;
    }


    public String getEndpointInstanceName()  throws DNSServiceCommonException
    {
        return endpointInstanceName;
    }

    public String getEndpointInstanceType()  throws DNSServiceCommonException
    {
        return endpointInstanceType;
    }


    public String getResolverInstanceName()  throws DNSServiceCommonException
    {
        return resolverInstanceName;
    }

    public String getResolverInstanceType()  throws DNSServiceCommonException
    {
        return resolverInstanceType;
    }

    public Properties getServiceInstanceProperties() throws DNSServiceCommonException
    {

        LoulanDNSOptionProperties optionProperties = buildServiceInstanceDefaultOptionProperties();

        Properties userProperties = stringToProperties(serviceInstancePropertiesArguments);
        optionProperties.put(userProperties);

        Properties properties = optionProperties.getProperties();
        return properties;
    }
    

    public Properties getEndpointInstanceProperties() throws DNSServiceCommonException
    {

        LoulanDNSOptionProperties optionProperties = buildEndpointInstanceDefaultOptionProperties();

        Properties userProperties = stringToProperties(endpointInstancePropertiesArguments);
        optionProperties.put(userProperties);

        Properties properties = optionProperties.getProperties();
        return properties;
    }

    public Properties getResolverInstanceProperties() throws DNSServiceCommonException
    {
        LoulanDNSOptionProperties optionProperties = buildResolverInstanceDefaultOptionProperties();

        Properties userProperties = stringToProperties(resolverInstancePropertiesArguments);
        optionProperties.put(userProperties);

        Properties properties = optionProperties.getProperties();
        return properties;

    }


    private Properties stringToProperties(String propertiesString) throws DNSServiceCommonException
    {
        Properties properties = new Properties();

        if ( propertiesString == null || propertiesString.equals("") )
        {
            return properties;
        }

        String[] array = propertiesString.split(";");

        for( String pair : array )
        {
            if ( pair.split("=").length <= 1 )
            {
                // このエントリはkey=valueの形式になっていないため、読み飛ばす.
                continue;
            }

            String key = pair.split("=")[0];
            String val = pair.split("=")[1];

            properties.setProperty(key, val);
        }

        return properties;
    }


    public boolean isSystemTypeDatabase() throws DNSServiceCommonException
    {
        if ( getSystemType().equals("database") )
        {
            return true;
        }

        return false;
    }

    public boolean isSystemTypeTemporary() throws DNSServiceCommonException
    {
        if ( getSystemType().equals("temporary") )
        {
            return true;
        }

        return false;
    }



}