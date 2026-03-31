package org.loxsols.net.service.dns.loulandns.server.impl.service.endpoint.factory;



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
import org.loxsols.net.service.dns.loulandns.server.logical.model.*;
import org.loxsols.net.service.dns.loulandns.server.http.spring.model.DNSServiceEndpointInstance;
import org.loxsols.net.service.dns.loulandns.server.http.spring.service.LoulanDNSDBService;
import org.loxsols.net.service.dns.loulandns.server.http.spring.service.LoulanDNSLogicalDBService;
import org.loxsols.net.service.dns.loulandns.server.impl.service.endpoint.base.DNSServiceEndpointInstanceImplBase;
import org.loxsols.net.service.dns.loulandns.server.impl.service.endpoint.udp.UDPServiceEndpointInstanceImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSQuestionMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSResponseMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.service.LoulanDNSLogicalModelService;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.resolver.IDNSResolverInstance;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.factory.IDNSServiceInstanceFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.loxsols.net.service.dns.loulandns.server.logical.model.*;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.IDNSServiceInstance;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.endpoint.*;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.endpoint.factory.*;

import org.loxsols.net.service.dns.loulandns.server.impl.service.endpoint.factory.base.*;
import org.loxsols.net.service.dns.loulandns.server.impl.service.endpoint.factory.simple.*;


/**
 * シンプルなDNSサービスエンドポイントのファクトリクラス.
 * SpringのDI機能を使用しない.
 * ただし、DBにもアクセスできない.
 */
public class DNSServiceEndpointInstanceFactoryImpl extends SimpleDNSServiceEndpointInstanceFactoryImpl implements IDNSServiceEndpointInstanceFactory
{

    // DNSサービスエンドポイントのID値と、DNSサービスエンドポイントインスタンスのリポジトリキー情報の対応関係をオンメモリで管理するマップ.
    protected HashMap<Long, String> dnsServiceEndpointInstanceIDToRepositoryKeyMap = new HashMap<Long,String>();


    // ----------------------------------
    // DIでクラスを自動設定する.
    @Autowired
    @Qualifier("loulanDNSDBServiceImpl")
    public LoulanDNSDBService loulanDNSDBService;

    @Autowired
    @Qualifier("loulanDNSLogicalDBServiceImpl")
    public LoulanDNSLogicalDBService loulanDNSLogicalDBService;

    @Autowired
    @Qualifier("loulanDNSLogicalModelServiceImpl")
    public LoulanDNSLogicalModelService loulanDNSLogicalModelService;

    @Autowired
    @Qualifier("dnsServiceInstanceFactoryImpl")
    public IDNSServiceInstanceFactory dnsServiceInstanceFactory;


    LoulanDNSUtils loulanDNSUtils = new LoulanDNSUtils();


    /**
     * DNSサービスエンドポイントインスタンスのファクトリメソッド.
     * オブジェクトが既にメモリ上に実体化されている場合はそれを返す.
     * オブジェクトが実体化されていない場合は新規に作成する.
     * 
     * 本クラスはSpringのDBアクセス機能を使用して、指定されたDNSサービスエンドポイントIDのDBレコードの登録情報をインスタンス化する.
     */
    public IDNSServiceEndpointInstance getOrCreateDNSServiceEndpointInstance(long dnsServiceEndpointInstanceID) throws DNSServiceCommonException
    {
        
        IDNSServiceEndpointInstance dnsServiceEndpointInstance = getDNSServiceEndpointInstanceRepository(dnsServiceEndpointInstanceID);

        if ( dnsServiceEndpointInstance != null )
        {
            // DNSサービスエンドポイントインスタンスは既にキャッシュに載っているのでそれを返す.
            return dnsServiceEndpointInstance;
        }

        DNSServiceEndpointInstanceInfo dnsServiceEndpointInstanceInfo = getDNSServiceEndpointInstanceInfo(dnsServiceEndpointInstanceID);

        long dnsServiceInstanceID = dnsServiceEndpointInstanceInfo.getDNSServiceInstanceID();
        DNSServiceInstanceInfo  dnsServiceInstanceInfo = getDNSServiceInstanceInfo( dnsServiceInstanceID );


        long endpointTypeCode = dnsServiceEndpointInstanceInfo.getDNSServiceEndpointTypeCode();
        Properties properties = dnsServiceEndpointInstanceInfo.getProperties();

        String serviceName = dnsServiceInstanceInfo.getDNSServiceInstanceName();
        String endpointName = dnsServiceEndpointInstanceInfo.getDNSServiceEndpointInstanceName();

        // エンドポイントが接続するDNSサービスインスタンスを取得する.
        IDNSServiceInstance serviceInstance = dnsServiceInstanceFactory.getOrCreateDNSServiceInstance(dnsServiceInstanceID);
        if ( serviceInstance == null )
        {
            String msg = String.format("Failed to create DNSServiceEndpointInstance, caused by DNSServiceInstance is not generated.");
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        String endpointTypeName = loulanDNSUtils.endpointTypeCodeToEndpointTypeName(endpointTypeCode);
        if ( endpointTypeName == null )
        {
            String msg = String.format("Failed to create DNSServiceEndpointInstance, caused by Unknown DNSServiceEndpointTypeCode (%d). endpointTypeCode=%d, endpointTypeName=%s", endpointTypeCode, endpointTypeCode, endpointTypeName );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }
    
        // DNSサービスエンドポイントのインスタンスを取得する.
        dnsServiceEndpointInstance = createNewDNSServiceEndpointInstance(endpointTypeName, properties, endpointName, serviceInstance);

        return dnsServiceEndpointInstance;
    }



    public IDNSServiceEndpointInstance getOrCreateDNSServiceEndpointInstance(String userName, String serviceInstanceName, String endpointInstanceName) throws DNSServiceCommonException
    {
        IDNSServiceEndpointInstance dnsServiceEndpointInstance;

        DNSServiceEndpointInstanceInfo endpointInstanceInfo = 
                    loulanDNSLogicalModelService.getDNSServiceEndpointInstanceInfo(userName, serviceInstanceName, endpointInstanceName);

        if ( endpointInstanceInfo == null )
        {
            String msg = String.format("Failed to create DNSServiceEndpointInstance, caused by DNSServiceEndpointInstance is not found. userName=%s, serviceInstanceName=%s, endpointInstanceName=%s",
                                             userName, serviceInstanceName, endpointInstanceName );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        dnsServiceEndpointInstance = getOrCreateDNSServiceEndpointInstance(endpointInstanceInfo.getDNSServiceEndpointInstanceID());
        return dnsServiceEndpointInstance;
    }

    


    /**
     * DNSエンドポイントインスタンスをリポジトリから取得する.
     * 
     * @param serviceName
     * @param endpointName
     * @return
     * @throws DNSServiceCommonException
     */
    protected IDNSServiceEndpointInstance getDNSServiceEndpointInstanceRepository(long dnsServiceEndpointInstanceID) throws DNSServiceCommonException
    {
        IDNSServiceEndpointInstance dnsServiceEndpointInstance = null;

        String serviceName, endpointName;

        // DNSサービスエンドポイントのID値から、リポジトリのキー値("<サービス名>/<エンドポイント名>")を取得する
        String key = getDNSServiceEndpointInstanceIDToRepogitoryKey(dnsServiceEndpointInstanceID);
        if ( key == null )
        {
            // リポジトリにキー値が未登録のため、DBからサービスインスタンス名とエンドポイント名を取得してキー値を新規に生成する.

            // DBからDNSサービスエンドポイントのインスタンスの情報を取得する.
            DNSServiceEndpointInstanceInfo dnsServiceEndpointInstanceInfo = getDNSServiceEndpointInstanceInfo(dnsServiceEndpointInstanceID);
            long dnsServiceInstanceID = dnsServiceEndpointInstanceInfo.getDNSServiceInstanceID();

            // DBからDNSサービスインスタンスの情報を取得する.
            DNSServiceInstanceInfo  dnsServiceInstanceInfo = getDNSServiceInstanceInfo( dnsServiceInstanceID );

            // リポジトリに既存のインスタンスがないかを検索する.
            serviceName = dnsServiceInstanceInfo.getDNSServiceInstanceName();
            endpointName = dnsServiceEndpointInstanceInfo.getDNSServiceEndpointInstanceName();

            // DNSサービスエンドポイントのID値と、リポジトリキーの対応関係を設定する.
            registDNSServiceEndpointInstanceIDToRepogitoryKey(dnsServiceEndpointInstanceID, serviceName, endpointName);

            // 再度、リポジトリのキー値の取得を試みる.
            key = getDNSServiceEndpointInstanceIDToRepogitoryKey(dnsServiceEndpointInstanceID);
        }

        // 基底クラスのリポジトリから、DNSサービスエンドポイントインスタンスの取得を試みる.
        dnsServiceEndpointInstance = getDNSServiceEndpointInstanceRepository(key);

        return dnsServiceEndpointInstance;
    }



    /**
     * DNSサービスエンドポイント情報のID値と、リポジトリキー値(DNSサービスインスタンス名、DNSサービスエンドポイント名を繋げた文字列)の対応関係を登録する.
     * 
     * @param dnsServiceEndpointInstanceID
     * @param serviceName
     * @param endpointName
     * @throws DNSServiceCommonException
     */
    protected void registDNSServiceEndpointInstanceIDToRepogitoryKey(long dnsServiceEndpointInstanceID, String serviceName, String endpointName) throws DNSServiceCommonException
    {

        String key = buildDNSServiceEndpointInstanceRepositoryKey(serviceName, endpointName);

        HashMap<Long, String> map = getDNSServiceEndpointInstanceIDToRepositoryKeyMap();
        map.put(dnsServiceEndpointInstanceID, key);

        return;
    }


    /**
     * DNSサービスエンドポイント情報のID値と、リポジトリキー値(DNSサービスインスタンス名、DNSサービスエンドポイント名を繋げた文字列)の対応関係の登録を解除する.
     * 
     * @param dnsServiceEndpointInstanceID
     * @throws DNSServiceCommonException
     */
    protected void clearDNSServiceEndpointInstanceIDToRepogitoryKey(long dnsServiceEndpointInstanceID) throws DNSServiceCommonException
    {
        HashMap<Long, String> map = getDNSServiceEndpointInstanceIDToRepositoryKeyMap();
        map.remove(dnsServiceEndpointInstanceID);

        return;
    }


    /**
     * DNSサービスエンドポイント情報のID値と、リポジトリキー値(DNSサービスインスタンス名、DNSサービスエンドポイント名を繋げた文字列)の対応関係のマップからエントリを取得する.
     * 
     * @param dnsServiceEndpointInstanceID
     * @return
     * @throws DNSServiceCommonException
     */
    protected String getDNSServiceEndpointInstanceIDToRepogitoryKey(long dnsServiceEndpointInstanceID) throws DNSServiceCommonException
    {
        HashMap<Long, String> map = getDNSServiceEndpointInstanceIDToRepositoryKeyMap();
        String key = map.get(dnsServiceEndpointInstanceID);

        return key;
    }

    /**
     * DNSサービスエンドポイント情報のID値と、リポジトリキー値(DNSサービスインスタンス名、DNSサービスエンドポイント名を繋げた文字列)の対応関係を管理するマップを返す.
     * 
     * @return
     * @throws DNSServiceCommonException
     */
    protected HashMap<Long, String> getDNSServiceEndpointInstanceIDToRepositoryKeyMap() throws DNSServiceCommonException
    {
        return this.dnsServiceEndpointInstanceIDToRepositoryKeyMap;
    }





    protected DNSServiceEndpointInstanceInfo getDNSServiceEndpointInstanceInfo(long dnsServiceEndpointInstanceID) throws DNSServiceCommonException
    {
        DNSServiceEndpointInstanceInfo info = loulanDNSLogicalDBService.getDNSServiceEndpointInstanceInfo(dnsServiceEndpointInstanceID);
        return info;
    }

    /**
     * DNSサービスインスタンスの論理モデルクラスを返す.
     * 
     * @param dnsServiceEndpointInstanceID
     * @return
     * @throws DNSServiceCommonException
     */
    protected DNSServiceInstanceInfo getDNSServiceInstanceInfo(long dnsServiceInstanceID) throws DNSServiceCommonException
    {
        DNSServiceInstanceInfo info = loulanDNSLogicalDBService.getDNSServiceInstanceInfo(dnsServiceInstanceID);
        return info;
    }





}