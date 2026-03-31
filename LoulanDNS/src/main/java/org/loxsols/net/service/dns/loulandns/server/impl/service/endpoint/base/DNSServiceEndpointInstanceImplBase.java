package org.loxsols.net.service.dns.loulandns.server.impl.service.endpoint.base;


import java.util.*;
import java.lang.Thread;


import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.*;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.endpoint.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.IDNSMessageFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.factory.IDNSServiceInstanceFactory;

import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSQuestionMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSResponseMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.service.LoulanDNSLogicalModelService;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.resolver.*;
import org.loxsols.net.service.dns.loulandns.server.http.spring.service.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.*;


import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceInsufficientDNSRequestException;
import org.loxsols.net.service.dns.loulandns.server.common.InsufficientDNSMessageException;
import org.loxsols.net.service.dns.loulandns.server.common.constants.LoulanDNSConstants;
import org.loxsols.net.service.dns.loulandns.server.common.util.*;


/**
 * DNSサービスレイヤーの抽象基底クラス.
 * 本クラスを継承して、LoulanDNSの外部サービス層(Ex.DoH, UDP, DoT ...etc)の実装を行う.
 */
@Service
public abstract class DNSServiceEndpointInstanceImplBase implements IDNSServiceEndpointInstance, Runnable
{

    String userName;
    String dnsServiceInstanceName;

    Properties propeties = new Properties();

    // -----------
    // 以下はDBから動的に読み込んで設定するフィールド.
    DNSServiceInstanceInfo dnsServiceInstanceInfo;

    IDNSResolverInstance dnsResolverInstance;


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



    protected IDNSServiceInstanceFactory dnsServiceInstanceFactory;

    @Autowired
    @Qualifier("dnsServiceInstanceFactoryImpl")
    public void setDNSServiceInstanceFactory(IDNSServiceInstanceFactory instance)
    {
        this.dnsServiceInstanceFactory = instance;
    }


    protected IDNSMessageFactory dnsMessageFactory;

    @Autowired
    @Qualifier("dnsMessageFactoryImpl")
    public void setDNSMessageFactory(IDNSMessageFactory instance)
    {
        this.dnsMessageFactory = instance;
    }


    // 本DNSサービスエンドポイントに紐づく、DNSサービスエンドポイントの名前
    protected String dnsServiceEndpointName = null;


    // 本DNSサービスエンドポイントに紐づく、DNSサービスインスタンス.
    protected IDNSServiceInstance dnsServiceInstance;


    // DNSエンドポイントサービスを実行するスレッド.
    Thread dnsEndpointServiceTaskThread;


    // DNSサービスエンドポイントのタスクの実行状態(0:停止中, 101:実行中, 401:停止待機中)
    int dnsEndpointServiceTaskStatus = LoulanDNSConstants.CONST_TASK_STATUS_INVACTIVE_DNS_SERVICE_ENDPOINT;

    // DNSサービスエンドポイントのタスクの終了待ち状態で待機するタイムアウト時間
    int timeoutValueForSuspendForDNSEndpointServiceTask = 1000;
    
    // ----------------------------------

    LoulanDNSCommonUtils commonUtils = new LoulanDNSCommonUtils();



    /**
     * DNSサービスエンドポイントの名前を設定する.
     * 
     * @param bindEndpointName
     * @throws DNSServiceCommonException
     */
    public void setDNSServiceEndpointName(String bindEndpointName) throws DNSServiceCommonException
    {
        this.dnsServiceEndpointName = bindEndpointName;
    }


    /**
     * 本DNSサービスエンドポイントに紐づく、DNSサービスインスタンスを設定する.
     * 
     * @param bindDNSServiceInstance
     * @throws DNSServiceCommonException
     */
    public void setDNSServiceInstance(IDNSServiceInstance bindDNSServiceInstance) throws DNSServiceCommonException
    {
        this.dnsServiceInstance = bindDNSServiceInstance;
    }




    /**
     * DNSサービスエンドポイントのパラメーターを設定する.
     * 
     * @param properties
     * @throws DNSServiceCommonException
     */
    public void initDNSServiceEndpoint(Properties properties) throws DNSServiceCommonException
    {
        initBaseServiceEndpoint(properties);
    }


    /**
     * DNSサービスエンドポイントを開始する.
     */
    public void startDNSServiceEndpoint() throws DNSServiceCommonException
    {
        Thread dndEndpointServiceThread = new Thread(this);
        dndEndpointServiceThread.start();
    }


    /**
     * DNSサービスエンドポイントを停止する.
     */
    public void stopDNSServiceEndpoint() throws DNSServiceCommonException
    {
        Thread thread = getDNSServiceEndpointTaskThread();

        if ( thread == null )
        {
            String msg = String.format("Failed to stop DNS Service Endpoint Task. Endpoint task thread is not found. userName=%s, serivceInstanceName=%s.", getUserName(), getDNSServiceInstanceName() );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        // タスクスレッドの停止を試みる.
        tryToSuspendForDNSServiceEndpointTask();

    }



    /**
     * 全てのサービスエンドポイントに共通する初期化処理を実装する.
     * 
     * @param properties
     * @throws DNSServiceCommonException
     */
    protected void initBaseServiceEndpoint(Properties properties) throws DNSServiceCommonException
    {
        // 本クラス内のメンバフィールドのPropertiesを初期化する.
        initProperties();

        // 本クラス内のメンバフィールドのPropertiesに値をコピーする.
        List<String> keys = commonUtils.getPropertiesKeys(properties);
        for( String key : keys )
        {
            String value = properties.getProperty(key);
            this.propeties.setProperty(key, value);
        }

        // ---------------------------------
        // 全てのサービスエンドポイントに共通するパラメータの登録処理を実行する.
        // ---------------------------------

        // サービスエンドポイントが所属するDNSサービスインスタンスのユーザー名
        String userName = properties.getProperty( LoulanDNSConstants.PROP_KEY_USER_NAME );

        if ( userName == null || userName.equals("") )
        {
            String msg = String.format("UserName is not specified. key=%s", LoulanDNSConstants.PROP_KEY_USER_NAME );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        setUserName(userName);

        // サービスエンドポイントが所属するDNSサービスインスタンスの名前.
        String dnsServiceInstanceName = properties.getProperty( LoulanDNSConstants.PROP_KEY_SERVICE_INSTANCE_NAME);


        if ( dnsServiceInstanceName == null || dnsServiceInstanceName.equals("") )
        {
            String msg = String.format( "DNSServiceInstanceName is not specified. key=%s", LoulanDNSConstants.PROP_KEY_SERVICE_INSTANCE_NAME);
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        setDNSServiceInstanceName(dnsServiceInstanceName);

        // DNSサービスインスタンスを登録する.
        initDNSServiceInstance( getUserName(), getDNSServiceInstanceName() );

    }


    /**
     * 本DNSサービスエンドポイントに紐づくDNSサービスインスタンスを登録する.
     * 
     * @param userName
     * @param dnsServiceInstanceName
     * @throws DNSServiceCommonException
     */
    protected void initDNSServiceInstance(String userName, String dnsServiceInstanceName) throws DNSServiceCommonException
    {
        IDNSServiceInstance serviceInstance = dnsServiceInstanceFactory.getOrCreateDNSServiceInstance(userName, dnsServiceInstanceName);
        this.setDNSServiceInstance(serviceInstance);
    }



    // DBから論理モデルを読み込んで本クラスに設定する.
    public void loadLogicalModel() throws DNSServiceCommonException
    {
        String userName = getUserName();
        String dnsServiceInstanceName = getDNSServiceInstanceName();

        
        UserInfo userInfo = loulanDNSLogicalModelService.getUserInfo(userName);
        DNSServiceInstanceInfo dnsServiceInstanceInfo = userInfo.getDNSServiceInstanceInfo( dnsServiceInstanceName );
    }

    

    public Properties getProperties() throws DNSServiceCommonException
    {
        return this.propeties;
    }

    public void setProerties(Properties properties) throws DNSServiceCommonException
    {
        this.propeties = properties;
    }

    public void initProperties() throws DNSServiceCommonException
    {
        // プロパティを初期化する.
        Properties properties = new Properties();
        this.setProerties(properties);
    }

    public void setUserName(String userName) throws DNSServiceCommonException
    {
        this.userName = userName;
    }

    public String getUserName() throws DNSServiceCommonException
    {
        return this.userName;
    }

    public void setDNSServiceInstanceName(String dnsServiceInstanceName) throws DNSServiceCommonException
    {
        this.dnsServiceInstanceName = dnsServiceInstanceName;
    }

    public String getDNSServiceInstanceName() throws DNSServiceCommonException
    {
        return this.dnsServiceInstanceName;
    }

    public DNSServiceInstanceInfo getDNSServiceInstanceInfo() throws DNSServiceCommonException
    {
        return dnsServiceInstanceInfo;
    }

    /**
     * 本DNSサービスエンドポイントに紐づく、DNSサービスインスタンスを返す.
     * @return DNSサービスインスタンス
     * @throws DNSServiceCommonException
     */
    protected IDNSServiceInstance getDNSServiceInstance() throws DNSServiceCommonException
    {
        return this.dnsServiceInstance;
    }



    /**
     * 本DNSサービスエンドポイントのタスクを処理するスレッドを設定する.
     * 
     * @param thread
     * @throws DNSServiceCommonException
     */
    protected void setDNSServiceEndpointTaskThread(Thread thread) throws DNSServiceCommonException
    {
        this.dnsEndpointServiceTaskThread = thread;
    }


    /**
     * 本DNSサービスエンドポイントのタスクを処理するスレッドを返す.
     * 
     * @return
     * @throws DNSServiceCommonException
     */
    protected Thread getDNSServiceEndpointTaskThread() throws DNSServiceCommonException
    {
        return this.dnsEndpointServiceTaskThread;
    }




    /**
     * DNS問い合わせメッセージを処理する.
     * 
     * @param dnsQuestionMessage
     * @return
     * @throws DNSServiceCommonException
     */
    protected IDNSResponseMessage serveDNSQuery(IDNSQuestionMessage dnsQuestionMessage) throws DNSServiceCommonException
    {
        IDNSServiceInstance dnsServiceInstance = getDNSServiceInstance();

        if ( dnsServiceInstance == null )
        {
            String msg = String.format("DNS Service Instance is not registered for DNS Service Endpoint. DNSServiceInstanceName=%s", getDNSServiceInstanceName() );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        IDNSResponseMessage dnsResponseMessage = dnsServiceInstance.serveDNSQuery(dnsQuestionMessage);
        return dnsResponseMessage;
    }



    /**
     * DNSパケットを解析する.
     * 
     * @param dnsPacketBytes
     * @return
     * @throws InsufficientDNSMessageException  長さが足りないDNSメッセージの場合.
     * @throws DNSServiceCommonException        上記以外の例外発生時.
     */
    protected IDNSMessage parseDNSPacketBytes(byte[] dnsMessageBytes) throws InsufficientDNSMessageException, DNSServiceCommonException
    {
        IDNSMessage dnsMessage = dnsMessageFactory.createDNSMesssage(dnsMessageBytes);
        return dnsMessage;
    }



    /**
     * スレッドのメインタスク.
     */
    public void run() 
    {
      try
      {
        doEndpointServiceTask();
      }
      catch(DNSServiceCommonException exception)
      {
        // TODO : 例外をロギングする処理が未実装.
        exception.printStackTrace();
      }

    }

    /**
     * サービスエンドポイントのメイン処理(スレッド内)
     * 
     * @throws DNSServiceCommonException
     */
    protected abstract void doEndpointServiceTask() throws DNSServiceCommonException;


    /**
     * サービスエンドポイントの処理タスクスレッドを外部から停止するためのメソッド.
     * 
     * @throws DNSServiceCommonException
     */
    protected void tryToSuspendForDNSServiceEndpointTask() throws DNSServiceCommonException
    {
        int status = getDNSEndpointServiceTaskStatus();
        if ( status != LoulanDNSConstants.CONST_TASK_STATUS_ACTIVE_DNS_SERVICE_ENDPOINT )
        {
            // DNSサービスエンドポイントのタスク状態が実行中ではない.
            String msg = String.format("Failed to suspend for DNS Service Endpoint task thread. DNS Service Endpoint task status is not active : %d", status);
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        // DNSサービスエンドポイントのタスク状態を、終了待ちに設定する.
        setDNSEndpointServiceTaskStatus( LoulanDNSConstants.CONST_TASK_STATUS_WAITING_FOR_SUSPEND_DNS_SERVICE_ENDPOINT );

        // 待機開始時刻を取得する.
        long waitingForSuspendStartTime = System.currentTimeMillis();

        // タスクの終了をポーリング待機する.
        while(true)
        {
            status = getDNSEndpointServiceTaskStatus();
            if ( status == LoulanDNSConstants.CONST_TASK_STATUS_INVACTIVE_DNS_SERVICE_ENDPOINT )
            {
                // タスクスレッドが終了した.
                break;
            }

            long currentTime = System.currentTimeMillis();;
            long spendTime = currentTime - waitingForSuspendStartTime;
            if ( spendTime > getTimeoutValueForSuspendForDNSEndpointServiceTask() )
            {
                // タスクの終了待ち時間がタイムアウトした.
                String msg = String.format("Failed to suspend for DNS Service Endpoint task thread. Because of timeout. UserName=%s, ServiceInstanceName=%s, Timeout setting value(ms)=%d, Task Status=%d", getUserName(), getDNSServiceInstanceName(), spendTime, status);
                DNSServiceCommonException exception = new DNSServiceCommonException(msg);
                throw exception;
            }
        }

    }


    /**
     * DNSサービスエンドポイントのタスク状態値を取得する.
     */
    public int getDNSEndpointServiceTaskStatus() throws DNSServiceCommonException
    {
        return this.dnsEndpointServiceTaskStatus;
    }

    
    /**
     * DNSサービスエンドポイントのタスク状態値を設定する.
     */
    public void setDNSEndpointServiceTaskStatus(int value) throws DNSServiceCommonException
    {
        this.dnsEndpointServiceTaskStatus = value;
    } 


    /**
     * DNSサービスエンドポイントのタスクの終了待ち処理におけるタイムアウト値(ミリ秒単位)を返す.
     * 
     * @return
     * @throws DNSServiceCommonException
     */
    public int getTimeoutValueForSuspendForDNSEndpointServiceTask() throws DNSServiceCommonException
    {
        int status;
        
        synchronized(this)
        {
            status = this.timeoutValueForSuspendForDNSEndpointServiceTask;
        }

        return status;
    }

    /**
     * DNSサービスエンドポイントのタスクの終了待ち処理におけるタイムアウト値(ミリ秒単位)を設定する.
     * 
     * @return
     * @throws DNSServiceCommonException
     */
    public void setTimeoutValueForSuspendForDNSEndpointServiceTask(int status) throws DNSServiceCommonException
    {
        synchronized(this)
        {
            this.timeoutValueForSuspendForDNSEndpointServiceTask = status;
        }

    }




}