package org.loxsols.net.service.dns.loulandns.server.impl.service.endpoint.factory.base;



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
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;
import org.loxsols.net.service.dns.loulandns.server.http.spring.model.DNSServiceEndpointInstance;
import org.loxsols.net.service.dns.loulandns.server.impl.service.endpoint.base.DNSServiceEndpointInstanceImplBase;
import org.loxsols.net.service.dns.loulandns.server.impl.service.endpoint.udp.UDPServiceEndpointInstanceImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSQuestionMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSResponseMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.resolver.IDNSResolverInstance;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.factory.IDNSServiceInstanceFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.loxsols.net.service.dns.loulandns.server.logical.model.*;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.IDNSServiceInstance;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.endpoint.*;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.endpoint.factory.*;


public abstract class DNSServiceEndpointInstanceFactoryImplBase implements IDNSServiceEndpointInstanceFactory
{

    //　生成済みのエンドポイントインスタンスを格納するリポジトリ
    protected HashMap<String, IDNSServiceEndpointInstance> endpointInstanceRepository = new HashMap<String, IDNSServiceEndpointInstance>();


    LoulanDNSUtils loulanDNSUtils = new LoulanDNSUtils();


    IDNSMessageFactory dnsMessageFactoryImpl;

    @Autowired
    @Qualifier("dnsMessageFactoryImpl")
    public void setDNSMessageFactory(IDNSMessageFactory instance)
    {
        this.dnsMessageFactoryImpl = instance;
    }


    IDNSServiceInstanceFactory dnsServiceInstanceFactoryImpl;

    @Autowired
    @Qualifier("dnsServiceInstanceFactoryImpl")
    public void setDNSServiceInstanceFactory(IDNSServiceInstanceFactory instance)
    {
        this.dnsServiceInstanceFactoryImpl = instance;
    }

  
    /**
     * DNSサービスエンドポイントインスタンスのファクトリメソッド.
     * DBにエンドポイント定義がなくても、本メソッドの引数だけでエンドポイントインスタンスを生成する.
     * 
     * @param serviceEndpointTypeCode       エンドポイントタイプコード
     * @param properties                    エンドポイント生成時のプロパティ
     * @param bindServiceEndpointName       エンドポイントに紐づけるエンドポイント名(サービスインスタンスにおいて一意な名前)
     * @param bindServiceInstance           エンドポイントに紐づけるDNSサービスインスタンス
     * @return
     * @throws DNSServiceCommonException
     */
    public IDNSServiceEndpointInstance getOrCreateDNSServiceEndpointInstance(long serviceEndpointTypeCode, Properties properties, String bindServiceEndpointName,  IDNSServiceInstance bindServiceInstance) throws DNSServiceCommonException
    {
        IDNSServiceEndpointInstance endpointInstance = null;

        String serviceEndpointTypeName = loulanDNSUtils.endpointTypeCodeToEndpointTypeName(serviceEndpointTypeCode);
        endpointInstance = getOrCreateDNSServiceEndpointInstance(serviceEndpointTypeName, properties, bindServiceEndpointName, bindServiceInstance);

        return endpointInstance;
    }


    /**
     * DNSサービスエンドポイントインスタンスのファクトリメソッド.
     * DBにエンドポイント定義がなくても、本メソッドの引数だけでエンドポイントインスタンスを生成する.
     * 
     * @param serviceEndpointTypName       エンドポイントタイプ名
     * @param properties                    エンドポイント生成時のプロパティ
     * @param bindServiceEndpointName       エンドポイントに紐づけるエンドポイント名(サービスインスタンスにおいて一意な名前)
     * @param bindServiceInstance           エンドポイントに紐づけるDNSサービスインスタンス
     * @return
     * @throws DNSServiceCommonException
     */
    public IDNSServiceEndpointInstance getOrCreateDNSServiceEndpointInstance(String serviceEndpointTypeName, Properties properties, String bindServiceEndpointName,  IDNSServiceInstance bindServiceInstance) throws DNSServiceCommonException
    {
        IDNSServiceEndpointInstance endpointInstance = null;

        String serviceName = bindServiceInstance.getDNSServiceInstanceName();

        // エンドポイントインスタンスが既にリポジトリに登録されている場合はそれを返す.
        endpointInstance = getDNSServiceEndpointInstanceRepository( serviceName, bindServiceEndpointName );
        if ( endpointInstance != null )
        {
            return endpointInstance;
        }

        // エンドポイントインスタンスを新規に作成する.
        endpointInstance = createNewDNSServiceEndpointInstance(serviceEndpointTypeName, properties, bindServiceEndpointName, bindServiceInstance);
        return endpointInstance;
    }


    /**
     * テンポラリDNSサービスエンドポイントインスタンスのファクトリメソッド.
     * DBにエンドポイント定義がなくても、本メソッドの引数だけでエンドポイントインスタンスを生成する.
     * 
     * @param endpointUserName
     * @param endpointTypeName
     * @param properties
     * @param endpointInstanceName
     * @param bindServiceInstance
     * @return
     * @throws DNSServiceCommonException
     */
    public IDNSServiceEndpointInstance getOrCreateTemporaryDNSServiceEndpointInstance(String endpointUserName, String endpointTypeName, Properties properties, String endpointInstanceName,  IDNSServiceInstance bindServiceInstance) throws DNSServiceCommonException
    {
        IDNSServiceEndpointInstance endpointInstance = null;

        String serviceName = bindServiceInstance.getDNSServiceInstanceName();

        // エンドポイントインスタンスが既にリポジトリに登録されている場合はそれを返す.
        endpointInstance = getDNSServiceEndpointInstanceRepository( serviceName, endpointInstanceName );
        if ( endpointInstance != null )
        {
            return endpointInstance;
        }

        endpointInstance = createNewDNSServiceEndpointInstance( endpointTypeName, properties, endpointInstanceName, bindServiceInstance );
        return endpointInstance;
    }



    /**
     * DNSサービスエンドポイントインスタンスのファクトリメソッド.
     * DBにエンドポイント定義がなくても、本メソッドの引数だけでエンドポイントインスタンスを生成する.
     * 新規作成する.
     * 
     * @param serviceEndpointTypeName       エンドポイントタイプ名
     * @param properties                    エンドポイント生成時のプロパティ
     * @param bindServiceEndpointName       エンドポイントに紐づけるエンドポイント名(サービスインスタンスにおいて一意な名前)
     * @param bindServiceInstance           エンドポイントに紐づけるDNSサービスインスタンス
     * @return
     * @throws DNSServiceCommonException
     */
    protected IDNSServiceEndpointInstance createNewDNSServiceEndpointInstance(String serviceEndpointTypeName, Properties properties, String bindServiceEndpointName,  IDNSServiceInstance bindServiceInstance) throws DNSServiceCommonException
    {

        IDNSServiceEndpointInstance endpointInstance = null;

        if ( serviceEndpointTypeName == null || serviceEndpointTypeName.equals("") )
        {
            // エンドポインタイプ名が指定されていない.
            String msg = String.format("Failed to create New DNSServiceEndpointInstance, caused by Endpoint Type Name is not specified. serviceEndpointTypeName=%s", serviceEndpointTypeName);
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        String serviceName = bindServiceInstance.getDNSServiceInstanceName();
        if ( isRegisteredDNSServiceEndpointInstanceRepository(serviceName, bindServiceEndpointName ) == true )
        {
            // 既に該当するエンドポイントインスタンスが登録済み.
            String msg = String.format("Failed to create New DNSServiceEndpointInstance, caused by Specified Endpoint Instance is already registered. serviceName=%s, bindServiceEndpointName=%s", serviceName, bindServiceEndpointName);
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        
        if ( serviceEndpointTypeName.toUpperCase().equals( LoulanDNSConstants.CONST_DNS_SERVICE_ENDPOINT_TYPE_NAME_UDP) )
        {
            // UDPエンドポイント.
            UDPServiceEndpointInstanceImpl instance = new UDPServiceEndpointInstanceImpl();
            instance.setDNSMessageFactory(dnsMessageFactoryImpl);
            instance.setDNSServiceInstanceFactory(dnsServiceInstanceFactoryImpl);

            endpointInstance = instance;
        }
        else
        {
            // 未定義or未実装のエンドポイント名
            String msg = String.format("Failed to create New DNSServiceEndpointInstance, caused by Specified Endpoint Type Name is not Implemented. serviceEndpointTypeName=%s", serviceEndpointTypeName);
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        // エンドポイントのプロパティを設定する.
        endpointInstance.initDNSServiceEndpoint(properties);

        // エンドポイントに紐づけるエンドポイント名を設定する.
        endpointInstance.setDNSServiceEndpointName(bindServiceEndpointName);

        // エンドポイントに紐づけるDNSサービスインスタンスを設定する.
        endpointInstance.setDNSServiceInstance(bindServiceInstance);

        // エンドポイントのインスタンスをリポジトリに登録する.
        registDNSServiceEndpointInstanceRepository(serviceName, bindServiceEndpointName, endpointInstance);

        return endpointInstance;
    }



    /**
     * DNSエンドポイントインスタンスのリポジトリで使用するキー値を生成する.
     * 
     * @param serviceName
     * @param endpointName
     * @return
     * @throws DNSServiceCommonException
     */
    protected String buildDNSServiceEndpointInstanceRepositoryKey(String serviceName, String endpointName) throws DNSServiceCommonException
    {

        if ( serviceName.contains("\\/") )
        {
            String msg = String.format("Invalid DNS ServiceName. serviceName=%s", serviceName );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        if ( endpointName.contains("\\/") )
        {
            String msg = String.format("Invalid DNS EndpointName. endpointName=%s", endpointName );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        String key = String.format("%s/%s", serviceName, endpointName);
        return key;
    }


    /**
     * DNSエンドポイントインスタンスのリポジトリで使用するキー値から、DNSサービス名を取得する.
     * 
     * @param key
     * @return
     * @throws DNSServiceCommonException
     */
    protected String getServiceNameFromDNSServiceEndpointInstanceRepositoryKey(String key) throws DNSServiceCommonException
    {
        if( key.contains("\\/") == false )
        {
            String msg = String.format("Invalid DNSServiceEndpointInstanceRepositoryKey. key=%s", key );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        if ( key.split("\\/").length > 2 )
        {
            String msg = String.format("Invalid DNSServiceEndpointInstanceRepositoryKey. key=%s", key );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        String serviceName = key.split("\\/")[0];
        return serviceName;
    }


    /**
     * DNSエンドポイントインスタンスのリポジトリで使用するキー値から、DNSエンドポイント名を取得する.
     * 
     * @param key
     * @return
     * @throws DNSServiceCommonException
     */
    protected String getEndpointNameFromDNSServiceEndpointInstanceRepositoryKey(String key) throws DNSServiceCommonException
    {
        if( key.contains("\\/") == false )
        {
            String msg = String.format("Invalid DNSServiceEndpointInstanceRepositoryKey. key=%s", key );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        if ( key.split("\\/").length > 2 )
        {
            String msg = String.format("Invalid DNSServiceEndpointInstanceRepositoryKey. key=%s", key );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        String serviceName = key.split("\\/")[1];
        return serviceName;
    }


    
    /**
     * DNSエンドポイントインスタンスをリポジトリに登録する.
     * 
     * @param serviceName
     * @param endpointName
     * @param endpointInstance
     * @throws DNSServiceCommonException
     */
    protected void registDNSServiceEndpointInstanceRepository(String serviceName, String endpointName, IDNSServiceEndpointInstance endpointInstance) throws DNSServiceCommonException
    {
        IDNSServiceEndpointInstance registered = getDNSServiceEndpointInstanceRepository(serviceName, endpointName);
        if ( registered != null )
        {
            // 既に登録済み
            String msg = String.format("Failed to regist DNSServiceEndpointInstance to repository, caused by Duplicate endpoint instance. serviceName=%s, endpointName=%s", serviceName, endpointName);
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;   
        }

        String key = buildDNSServiceEndpointInstanceRepositoryKey(serviceName, endpointName);
        endpointInstanceRepository.put(key, endpointInstance);
    }


    /**
     * DNSエンドポイントインスタンスをリポジトリから取得する.
     * 
     * @param serviceName
     * @param endpointName
     * @return
     * @throws DNSServiceCommonException
     */
    protected IDNSServiceEndpointInstance getDNSServiceEndpointInstanceRepository(String serviceName, String endpointName) throws DNSServiceCommonException
    {
        String key = buildDNSServiceEndpointInstanceRepositoryKey(serviceName, endpointName);

        IDNSServiceEndpointInstance endpointInstance = getDNSServiceEndpointInstanceRepository(key);
        return endpointInstance;
    }


    /**
     * DNSエンドポイントインスタンスをリポジトリから取得する.
     * 
     * @param key
     * @return
     * @throws DNSServiceCommonException
     */
    protected IDNSServiceEndpointInstance getDNSServiceEndpointInstanceRepository(String key) throws DNSServiceCommonException
    {
        IDNSServiceEndpointInstance endpointInstance = endpointInstanceRepository.get(key);
        return endpointInstance;
    }


    /**
     * DNSエンドポイントインスタンスをリポジトリから解放する.
     * 
     * @param serviceName
     * @param endpointName
     * @return
     * @throws DNSServiceCommonException
     */
    protected void clearDNSServiceEndpointInstanceRepository(String serviceName, String endpointName) throws DNSServiceCommonException
    {
        String key = buildDNSServiceEndpointInstanceRepositoryKey(serviceName, endpointName);
        endpointInstanceRepository.remove(key);
    }

    /**
     * DNSエンドポイントインスタンスがリポジトリに登録されているかを判定する.
     * 
     * @param serviceName
     * @param endpointName
     * @return
     * @throws DNSServiceCommonException
     */
    protected boolean isRegisteredDNSServiceEndpointInstanceRepository(String serviceName, String endpointName) throws DNSServiceCommonException
    {
        if ( getDNSServiceEndpointInstanceRepository(serviceName, endpointName) != null )
        {
            return true;
        }

        return false;
    }




}