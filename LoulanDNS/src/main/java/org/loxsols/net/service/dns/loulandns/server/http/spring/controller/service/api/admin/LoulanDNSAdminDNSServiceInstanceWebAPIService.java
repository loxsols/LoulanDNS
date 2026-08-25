package org.loxsols.net.service.dns.loulandns.server.http.spring.controller.service.api.admin;


import static org.mockito.ArgumentMatchers.nullable;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.springframework.stereotype.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xbill.DNS.Zone;
import org.xbill.DNS.tools.update;
import org.loxsols.net.service.dns.loulandns.server.common.constants.LoulanDNSConstants;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;
import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.common.LoulanDNSSystemServiceException;
import org.loxsols.net.service.dns.loulandns.server.http.spring.model.*;
import org.loxsols.net.service.dns.loulandns.server.http.spring.service.LoulanDNSDBService;
import org.loxsols.net.service.dns.loulandns.server.http.spring.service.LoulanDNSLogicalDBService;
import org.loxsols.net.service.dns.loulandns.server.logical.model.factory.*;
import org.loxsols.net.service.dns.loulandns.server.logical.service.LoulanDNSLogicalModelService;

import org.loxsols.net.service.dns.loulandns.server.logical.model.*;

import org.loxsols.net.service.dns.loulandns.server.http.spring.common.exception.HttpStatus404Exception;


// LoulanDNSのDNSサービスインスタンス管理用WebAPIサービスクラス
@RestController
@RequestMapping("/admin/api/dns/service")
public class LoulanDNSAdminDNSServiceInstanceWebAPIService
{

    // 次の値と同等の文字列を設定する. ※ 固定値でないとコンパイルが通らないため.
    // "LoulanDNSConstatns.DB_CONST_VALUE_RECORD_STATUS_AC  TIVE"
    final static String PRM_STR_DB_CONST_VALUE_RECORD_STATUS_ACTIVE = "101";
    // "LoulanDNSConstatns.DB_CONST_VALUE_RECORD_STATUS_INACTIVE"
    final static String PRM_STR_DB_CONST_VALUE_RECORD_STATUS_INACTIVE = "401";


    @Autowired
    @Qualifier("loulanDNSDBServiceImpl")
    LoulanDNSDBService loulanDNSDBService;

    @Autowired
    @Qualifier("loulanDNSLogicalDBServiceImpl")
    LoulanDNSLogicalDBService loulanDNSLogicalDBService;

    @Autowired
    @Qualifier("loulanDNSLogicalModelServiceImpl")
    LoulanDNSLogicalModelService loulanDNSLogicalModelService;


    DNSServiceInstanceInfoFactory dnsServiceInstanceInfoFactory = new DNSServiceInstanceInfoFactory();
    DNSServiceInstancePropertyInfoFactory dnsServiceInstancePropertyInfoFactory = new DNSServiceInstancePropertyInfoFactory();

    DNSResolverInstanceInfoFactory dnsResolverInstanceInfoFactory = new DNSResolverInstanceInfoFactory();
    DNSResolverInstancePropertyInfoFactory dnsResolverInstancePropertyInfoFactory = new DNSResolverInstancePropertyInfoFactory();
    
    DNSServiceEndpointInstanceInfoFactory dnsServiceEndpointInstanceInfoFactory = new DNSServiceEndpointInstanceInfoFactory();
    DNSServiceEndpointInstancePropertyInfoFactory dnsServiceEndpointInstancePropertyInfoFactory = new DNSServiceEndpointInstancePropertyInfoFactory();

    public LoulanDNSAdminDNSServiceInstanceWebAPIService()
    {

    }

    // DNSサービスインスタンス情報の一覧取得
    @GetMapping("/list/dns-service-instance")
    public DNSServiceInstanceInfo[] listDNSServiceInstance(@RequestParam(name = "UserName", required = false) String userName) throws DNSServiceCommonException
    {
        List<DNSServiceInstance> dnsServiceInstanceList = null;
        
        if ( userName != null )
        {
            UserInfo userInfo = loulanDNSLogicalModelService.getUserInfo(userName);
            if(userInfo == null )
            {
                return null;
            }

            dnsServiceInstanceList = loulanDNSDBService.getDNSServiceInstanceListByUserID( userInfo.getUserID() );
        }
        else
        {
            dnsServiceInstanceList = loulanDNSDBService.getDNSServiceInstanceList();
        }

        DNSServiceInstanceInfo[] dnsServiceInstanceInfoArray = new DNSServiceInstanceInfo[dnsServiceInstanceList.size()];
        for( int i=0; i < dnsServiceInstanceList.size(); i++ )
        {
            DNSServiceInstance instance = dnsServiceInstanceList.get(i);
            dnsServiceInstanceInfoArray[i] = loulanDNSLogicalModelService.getDNSServiceInstanceInfo(instance.getDNSServiceInstanceID());
        }

        return dnsServiceInstanceInfoArray;
    }


    // DNSサービスインスタンス情報取得
    @GetMapping("/get/dns-service-instance/{dnsServiceInstanceID}")
    public DNSServiceInstanceInfo getDNSServiceInstance(@PathVariable Long dnsServiceInstanceID) throws DNSServiceCommonException
    {
        DNSServiceInstanceInfo dnsServiceInstanceInfo = loulanDNSLogicalModelService.getDNSServiceInstanceInfo(dnsServiceInstanceID);
        return dnsServiceInstanceInfo;
    }

    // DNSサービスインスタンス情報取得
    @GetMapping("/get/dns-service-instance")
    public DNSServiceInstanceInfo getDNSServiceInstance(  @RequestParam(name = "UserName", required = true) String userName,
                                                @RequestParam(name = "DNSServiceInstanceName", required = true) String dnsServiceInstanceName ) throws DNSServiceCommonException
    {

        DNSServiceInstanceInfo[] dnsServiceInstanceArray = listDNSServiceInstance(userName);

        if ( dnsServiceInstanceArray == null )
        {
            return null;
        }
        
        for( DNSServiceInstanceInfo dnsServiceInstance : dnsServiceInstanceArray )
        {

            if ( dnsServiceInstance.getDNSServiceInstanceName().equals(dnsServiceInstanceName) )
            {
                return dnsServiceInstance;
            }
        }

        return null;
    }


    // DNSサービスインスタンス情報作成
    @PutMapping("/create/dns-service-instance")
    public DNSServiceInstanceInfo createDNSServiceInstance( @RequestParam(name = "UserName", required = true) String userName,
                                                            @RequestParam(name = "DNSServiceInstanceName", required = true) String dnsServiceInstanceName,
                                                            @RequestParam(name = "DNSServiceInstanceExplain", required = false ) String dnsServiceInstanceExplain,
                                                            @RequestParam(name = "DNSServiceTypeCode", required = true ) Long dnsServiceTypeCode,
                                                            @RequestParam(name = "DNSResolverInstanceID", required = true ) Long dnsResolverInstanceID,
                                                            @RequestParam(name = "RecordStatus", required = true ) Long recordStatus,
                                                            @RequestParam(name = "Memo", required = false) String memo) throws DNSServiceCommonException
    {


        // ユーザー名からユーザーIDを取得.
        User user = loulanDNSDBService.getUser( userName );
        Long userID = user.getUserID();


        // ------------------------------------
        // ここからDNSServiceInstanceのファクトリクラスからオブジェクトを新規生成してsaveする.
        // ----
        
        // DNSServiceInstanceInfo論理モデルオブジェクトの生成.
        DNSServiceInstanceInfo tmpDNSServiceInstanceInfo = dnsServiceInstanceInfoFactory.createDNSServiceInstanceInfoObject(null, userID, dnsServiceInstanceName, dnsServiceInstanceExplain, dnsServiceTypeCode, dnsResolverInstanceID, recordStatus, memo );
        

        // DBからDNSリゾルバインスタンスの論理モデルを取得して、サービスインスタンスに設定.
        DNSResolverInstanceInfo tmpDnsResolverInstanceInfo = loulanDNSLogicalModelService.getDNSResolverInstanceInfo(dnsResolverInstanceID);
        if ( tmpDnsResolverInstanceInfo == null )
        {
            // DNSリゾルバインスタンスがDB上に存在しない.
            String msg = String.format("Specifed DNSResolverInstance is not found. DNSResolverInstanceID=%d", dnsResolverInstanceID );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        tmpDNSServiceInstanceInfo.setDNSResolverInstanceInfo(tmpDnsResolverInstanceInfo);

        // DBを更新.
        DNSServiceInstanceInfo savedDNSServiceInstanceInfo = loulanDNSLogicalModelService.saveDNSServiceInstanceInfo(tmpDNSServiceInstanceInfo);
        // -----

        return savedDNSServiceInstanceInfo;
    }



    // DNSサービスインスタンス情報更新
    @PutMapping("/update/dns-service-instance/{dnsServiceInstanceID}")
    public DNSServiceInstanceInfo updateDNSServiceInstance( @PathVariable Long dnsServiceInstanceID, 
                                                        @RequestParam(name = "UserName", required = true) String userName,
                                                        @RequestParam(name = "DNSServiceInstanceName", required = false) String dnsServiceInstanceName,
                                                        @RequestParam(name = "DNSServiceInstanceExplain", required = false) String dnsServiceInstanceExplain,
                                                        @RequestParam(name = "DNSServiceTypeCode", required = false) Long dnsServiceTypeCode,
                                                        @RequestParam(name = "DNSResolverInstanceID", required = false) Long dnsResolverInstanceID,
                                                        @RequestParam(name = "RecordStatus", required = false) Long recordStatus,
                                                        @RequestParam(name = "Memo", required = false) String memo) throws DNSServiceCommonException
    {

        if ( dnsServiceInstanceID == null )
        {
            // DNSサービスインスタンスを特定するための情報が指定されていない.
            String msg = String.format("Unsatisfied parameters for search DNSServiceInstance. DNSServiceInstanceID=%d, UserName=%s, DNSServiceInstanceName=%s", dnsServiceInstanceID, userName, dnsServiceInstanceName);
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        // 既存レコードをDBから取得.
        DNSServiceInstanceInfo existDNSServiceInstance = getDNSServiceInstance(dnsServiceInstanceID);

        // 既存のDNSサービスインスタンスのレコードが存在しない場合は例外をスロー.
        if  ( existDNSServiceInstance == null )
        {
            String msg = String.format("Specifed DNSServiceInstance is not found. DNSServiceInstanceID=%d, UserName=%s, DNSServiceInstanceName=%s", dnsServiceInstanceID, userName, dnsServiceInstanceName);
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }


        Long userID;
        if ( userName == null || userName.equals("") )
        {
            User user = loulanDNSDBService.getUser( existDNSServiceInstance.getUserID() );
            userID = user.getUserID();
        }
        else
        {
            User user = loulanDNSDBService.getUser( userName );
            userID = user.getUserID();
        }

        if ( dnsServiceInstanceName == null || dnsServiceInstanceName.equals("") )
        {
            dnsServiceInstanceName = existDNSServiceInstance.getDNSServiceInstanceName();
        }

        if ( dnsServiceInstanceExplain == null || dnsServiceInstanceExplain.equals("") )
        {
            dnsServiceInstanceExplain = existDNSServiceInstance.getDNSServiceInstanceExplain();
        }

        if ( dnsServiceTypeCode == null )
        {
            dnsServiceTypeCode = existDNSServiceInstance.getDnsServiceTypeCode();
        }

        if ( dnsResolverInstanceID == null )
        {
            dnsResolverInstanceID = existDNSServiceInstance.getDNSResolverInstanceID();
        }


        if (memo == null)
        {
            // 本メソッドの引数がnullの場合は既存レコードの情報を利用する.
            memo = existDNSServiceInstance.getMemo();
        }

        if ( recordStatus == null)
        {
            // 本メソッドの引数がnullの場合は既存レコードの情報を利用する.
            recordStatus = existDNSServiceInstance.getRecordStatus();
        }

        // ------------------------------------
        // ここからDNSServiceInstanceのファクトリクラスからオブジェクトを新規生成してsaveする.
        // ----
        
        // DNSServiceInstanceInfo論理モデルオブジェクトの生成.
        DNSServiceInstanceInfo tmpDNSServiceInstanceInfo = dnsServiceInstanceInfoFactory.createDNSServiceInstanceInfoObject(dnsServiceInstanceID, userID, dnsServiceInstanceName, dnsServiceInstanceExplain, dnsServiceTypeCode, dnsResolverInstanceID, recordStatus, memo, existDNSServiceInstance.getCreateDate(), existDNSServiceInstance.getUpdateDate() );
        

        // DBからDNSリゾルバインスタンスの論理モデルを取得して、サービスインスタンスに設定.
        DNSResolverInstanceInfo tmpDnsResolverInstanceInfo = loulanDNSLogicalModelService.getDNSResolverInstanceInfo(dnsResolverInstanceID);
        if ( tmpDnsResolverInstanceInfo == null )
        {
            // DNSリゾルバインスタンスがDB上に存在しない.
            String msg = String.format("Specifed DNSResolverInstance is not found. DNSResolverInstanceID=%d", dnsResolverInstanceID );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        tmpDNSServiceInstanceInfo.setDNSResolverInstanceInfo(tmpDnsResolverInstanceInfo);

        // DBを更新.
        DNSServiceInstanceInfo savedDNSServiceInstanceInfo = loulanDNSLogicalModelService.saveDNSServiceInstanceInfo(tmpDNSServiceInstanceInfo);
        // -----

        return savedDNSServiceInstanceInfo;
    }


    // DNSサービスインスタンス情報更新
    @PutMapping("/update/dns-service-instance")
    public DNSServiceInstanceInfo updateDNSServiceInstance( @RequestParam(name = "UserName", required = true) String userName,
                                                        @RequestParam(name = "DNSServiceInstanceName", required = true) String dnsServiceInstanceName,
                                                        @RequestParam(name = "DNSServiceInstanceExplain", required = false) String dnsServiceInstanceExplain,
                                                        @RequestParam(name = "DNSServiceTypeCode", required = false) Long dnsServiceTypeCode,
                                                        @RequestParam(name = "DNSResolverInstanceID", required = false) Long dnsResolverInstanceID,
                                                        @RequestParam(name = "RecordStatus", required = false) Long recordStatus,
                                                        @RequestParam(name = "Memo", required = false) String memo) throws DNSServiceCommonException
    {

        if (userName == null || dnsServiceInstanceName == null )
        {
            // DNSサービスインスタンスを特定するための情報が指定されていない.
            String msg = String.format("Unsatisfied parameters for search DNSServiceInstance. UserName=%s, DNSServiceInstanceName=%s", userName, dnsServiceInstanceName);
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        // ユーザー名とインスタンス名で既存レコードを引く.
        DNSServiceInstanceInfo existDNSServiceInstance = getDNSServiceInstance(userName, dnsServiceInstanceName);
        

        // 既存のDNSサービスインスタンスのレコードが存在しない場合は例外をスロー.
        if  ( existDNSServiceInstance == null )
        {
            String msg = String.format("Specifed DNSServiceInstance is not found. UDNSServiceInstanceID=%d, UserName=%s, DNSServiceInstanceName=%s", dnsResolverInstanceID, userName, dnsServiceInstanceName);
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }


        // DNSサービスインスタンスのID値を既存レコードの値で強制取得する(新規採番が起きないようにガードする).
        long dnsServiceInstanceID = existDNSServiceInstance.getDNSServiceInstanceID();

        // 実際の更新処理は別メソッド(ID値を引数に取るメソッド)で行う.
        DNSServiceInstanceInfo updatedDNSServiceInstanceInfo = updateDNSServiceInstance(dnsServiceInstanceID, userName, dnsServiceInstanceName, dnsServiceInstanceExplain, dnsServiceTypeCode, dnsResolverInstanceID, recordStatus, memo );
        return updatedDNSServiceInstanceInfo;
    }


    // DNSサービスインスタンス情報削除
    @DeleteMapping("/delete/dns-service-instance/{dnsServiceInstanceID}")
    public void deleteDNSServiceInstance( @PathVariable Long dnsServiceInstanceID ) throws DNSServiceCommonException
    {

        if ( dnsServiceInstanceID == null )
        {
            String msg = String.format("DNSServiceInstanceID is not specifed. dnsServiceInstanceID=%d", dnsServiceInstanceID );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }


        DNSServiceInstanceInfo dnsServiceInstanceInfo = loulanDNSLogicalModelService.getDNSServiceInstanceInfo(dnsServiceInstanceID);
        if ( dnsServiceInstanceInfo == null )
        {
            String msg = String.format("Specified DNSServiceInstance is not exists. dnsServiceInstanceID=%d", dnsServiceInstanceID );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }


        // DBから対象レコードを削除する.
        // なお、論理モデルクラスの削除メソッドを呼ぶと子要素も含めて削除することになるため、DBサービスの削除メソッドをあえて読んでいる.
        loulanDNSDBService.deleteDNSServiceInstance(dnsServiceInstanceID);

        return;
    }


    // DNSサービスインスタンス情報削除
    @DeleteMapping("/delete/dns-service-instance")
    public void deleteDNSServiceInstance( @RequestParam(name = "UserName", required = true) String userName,
                                          @RequestParam(name = "DNSServiceInstanceName", required = true) String dnsServiceInstanceName ) throws DNSServiceCommonException
    {

        DNSServiceInstanceInfo dnsServiceInstanceInfo = 
            loulanDNSLogicalModelService.getDNSServiceInstanceInfo(userName, dnsServiceInstanceName);

        if ( dnsServiceInstanceInfo == null )
        {
            // 指定されたDNSサービスインスタンスはDB上に存在しない.
            String msg = String.format("Specified DNSServiceInstance is not exists. userName=%s, dnsServiceInstanceName=%s", userName, dnsServiceInstanceName );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        // DNSサービスインスタンス情報の削除をID値を基に行う.
        deleteDNSServiceInstance( dnsServiceInstanceInfo.getDNSServiceInstanceID() );

    }


    // DNSサービスインスタンスプロパティ情報の一覧取得
    @GetMapping("/get/dns-service-instance-properties")
    public List<DNSServiceInstancePropertyInfo> getDNSServiceInstanceProperties(
                                                            @RequestParam(name = "UserName", required = true) String userName,
                                                            @RequestParam(name = "DNSServiceInstanceName", required = true) String dnsServiceInstanceName ) throws DNSServiceCommonException
    {
        // DBからDNSサービスインスタンスを取得する.
        DNSServiceInstanceInfo dnsServiceInstanceInfo = getDNSServiceInstance(userName, dnsServiceInstanceName);

        if ( dnsServiceInstanceInfo == null )
        {
            // 指定されたDNSサービスインスタンスはDB上に存在しない.
            String msg = String.format("Specified DNSServiceInstance is not exists. userName=%s, dnsServiceInstanceName=%s", userName, dnsServiceInstanceName );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }


        System.out.println("[DEBUG]getDNSServiceInstanceProperties() : dnsServiceInstanceInfo.getDNSServiceInstanceName()=" + dnsServiceInstanceInfo.getDNSServiceInstanceName() + ", dnsServiceInstanceInfo.getDNSServiceInstanceProperties().size()=" +  dnsServiceInstanceInfo.getDNSServiceInstanceProperties().size() );


        List<DNSServiceInstancePropertyInfo> dnsServiceInstanceProperties = dnsServiceInstanceInfo.getDNSServiceInstanceProperties();
        return dnsServiceInstanceProperties;
    }

    // DNSサービスインスタンスプロパティ情報取得
    @GetMapping("/get/dns-service-instance-property/{UserName}/{DNSServiceInstanceName}/{DNSServiceInstancePropertyKey}")
    public DNSServiceInstancePropertyInfo getDNSServiceInstanceProperty(
                                                            @PathVariable(name = "UserName") String userName,
                                                            @PathVariable(name = "DNSServiceInstanceName") String dnsServiceInstanceName,
                                                            @PathVariable(name = "DNSServiceInstancePropertyKey") String dnsServiceInstancePropertyKey ) throws DNSServiceCommonException
    {

        List<DNSServiceInstancePropertyInfo> dnsServiceInstanceProperties = getDNSServiceInstanceProperties(userName, dnsServiceInstanceName);

        DNSServiceInstancePropertyInfo dnsServiceInstancePropertyInfo = null;
        for( DNSServiceInstancePropertyInfo item : dnsServiceInstanceProperties )
        {
            if ( item.getDnsServiceInstancePropertyKey().equals(dnsServiceInstancePropertyKey) )
            {
                // プロパティキーが一致するエントリが見つかった.
                dnsServiceInstancePropertyInfo = item;
                break;
            }
        }

        return dnsServiceInstancePropertyInfo;
    }

    // DNSサービスインスタンスプロパティ情報取得
    @GetMapping("/get/dns-service-instance-property/{dnsServiceInstancePropertyID}")
    public DNSServiceInstancePropertyInfo getDNSServiceInstanceProperty( @PathVariable Long dnsServiceInstancePropertyID ) throws DNSServiceCommonException
    {
        // DBからレコードを取得する.
        DNSServiceInstanceProperties existRecord = loulanDNSDBService.getDNSServiceInstanceProperties( dnsServiceInstancePropertyID );
        if ( existRecord == null )
        {
            return null;
        }

        // DBから取得したレコードの情報を基に、親エンティティのID値と、プロパティキーを取得する.
        Long dnsServiceInstanceID = existRecord.getDnsServiceInstanceID();
        String key = existRecord.getDnsServiceInstancePropertyKey();

        // 親エンティティのID値と、プロパティキーをもとに再度、論理モデルクラスの問い合わせを行う.
        DNSServiceInstanceInfo  dnsServiceInstanceInfo  = getDNSServiceInstance(dnsServiceInstanceID);
        DNSServiceInstancePropertyInfo  dnsServiceInstancePropertyInfo  = dnsServiceInstanceInfo.getDNSServiceInstanceProperty(key);

        return dnsServiceInstancePropertyInfo;
    }



    // DNSサービスインスタンスプロパティ情報 新規作成
    @PutMapping("/create/dns-service-instance-property")
    public DNSServiceInstancePropertyInfo createDNSServiceInstanceProperty( @RequestParam(name = "UserName", required = true) String userName,
                                                            @RequestParam(name = "DNSServiceInstanceName", required = true) String dnsServiceInstanceName,
                                                            @RequestParam(name = "DNSServiceInstancePropertyKey", required = true) String dnsServiceInstancePropertyKey,  
                                                            @RequestParam(name = "DNSServiceInstancePropertyValue", required = true) String dnsServiceInstancePropertyValue,  
                                                            @RequestParam(name = "DNSServiceInstancePropertyExplain", required = true) String dnsServiceInstancePropertyExplain,  
                                                            @RequestParam(name = "RecordStatus", required = true ) Long recordStatus,
                                                            @RequestParam(name = "Memo", required = false) String memo) throws DNSServiceCommonException
    {

        // DB上に既存のプロパティ情報のエントリがあるかをチェックする.
        DNSServiceInstancePropertyInfo checkDNSServiceInstancePropertyInfo = getDNSServiceInstanceProperty(userName, dnsServiceInstanceName, dnsServiceInstancePropertyKey );
        if ( checkDNSServiceInstancePropertyInfo != null )
        {
            // DB上には既に指定されたプロパティキーと同じプロパティ情報のエントリが存在する.
            // 新規追加は出来ないので例外をスローする.
            String msg = String.format("Failed to create DNSServiceInstanceProperties record. Specified DNSServiceInstanceProperty is already exists. userName=%s, dnsServiceInstanceName=%s, dnsServiceInstancePropertyKey=%s", userName, dnsServiceInstanceName, dnsServiceInstancePropertyKey );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        // DBからDNSサービスインスタンスを取得する.
        DNSServiceInstanceInfo dnsServiceInstanceInfo = getDNSServiceInstance(userName, dnsServiceInstanceName);

        if ( dnsServiceInstanceInfo == null )
        {
            // 指定されたサービスインスタンスはDB上に存在しない.
            String msg = String.format("Specified DNSServiceInstance is not exists. userName=%s, dnsServiceInstanceName=%s", userName, dnsServiceInstanceName );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }


        Long dnsServiceInstanceID = dnsServiceInstanceInfo.getDNSServiceInstanceID();
        DNSServiceInstancePropertyInfo dnsServiceInstancePropertyInfo = dnsServiceInstancePropertyInfoFactory.createDNSServiceInstancePropertiesInfoObject(dnsServiceInstanceID, dnsServiceInstancePropertyKey, dnsServiceInstancePropertyValue, dnsServiceInstancePropertyExplain, recordStatus, memo );

        // 既存のプロパティのリストに新規追加エントリを挿入する.
        List<DNSServiceInstancePropertyInfo> properties = dnsServiceInstanceInfo.getDNSServiceInstanceProperties();
        properties.add(dnsServiceInstancePropertyInfo);
        dnsServiceInstanceInfo.setDnsServiceInstanceProperties(properties);

        // 親エントリであるDNSサービスインスタンスごと論理的に更新する.
        DNSServiceInstanceInfo savedDNSServiceInstanceInfo = loulanDNSLogicalModelService.saveDNSServiceInstanceInfo(dnsServiceInstanceInfo);
        DNSServiceInstancePropertyInfo savedDNSServiceInstancePropertyInfo = savedDNSServiceInstanceInfo.getDNSServiceInstanceProperty( dnsServiceInstancePropertyKey );

        return savedDNSServiceInstancePropertyInfo;
    }



    // DNSサービスインスタンスプロパティ情報 更新
    @PutMapping("/update/dns-service-instance-property/{dnsServiceInstancePropertyID}")
    public DNSServiceInstancePropertyInfo updateDNSServiceInstanceProperty( @PathVariable Long dnsServiceInstancePropertyID,
                                                            @RequestParam(name = "DNSServiceInstancePropertyKey", required = false) String dnsServiceInstancePropertyKey,  
                                                            @RequestParam(name = "DNSServiceInstancePropertyValue", required = false) String dnsServiceInstancePropertyValue,  
                                                            @RequestParam(name = "DNSServiceInstancePropertyExplain", required = false) String dnsServiceInstancePropertyExplain,  
                                                            @RequestParam(name = "RecordStatus", required = false ) Long recordStatus,
                                                            @RequestParam(name = "Memo", required = false) String memo) throws DNSServiceCommonException
    {

        DNSServiceInstancePropertyInfo  dnsServiceInstancePropertyInfo = loulanDNSLogicalDBService.getDNSServiceInstancePropertyInfo(dnsServiceInstancePropertyID);
        if ( dnsServiceInstancePropertyInfo == null )
        {
            // DB上には指定されたプロパティエントリは存在しない.
            String msg = String.format("Failed to update DNSServiceInstanceProperties record. Specified DNSServiceInstanceProperty is NOT exists. dnsServiceInstancePropertyID=%d", dnsServiceInstancePropertyID );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        if ( dnsServiceInstancePropertyKey != null )
        {
            dnsServiceInstancePropertyInfo.setDnsServiceInstancePropertyKey(dnsServiceInstancePropertyKey);
        }

        if ( dnsServiceInstancePropertyValue != null )
        {
            dnsServiceInstancePropertyInfo.setDnsServiceInstancePropertyValue(dnsServiceInstancePropertyValue);
        }

        if ( dnsServiceInstancePropertyExplain != null )
        {
            dnsServiceInstancePropertyInfo.setDnsServiceInstancePropertyExplain(dnsServiceInstancePropertyExplain);
        }

        if ( recordStatus != null )
        {
            dnsServiceInstancePropertyInfo.setRecordStatus(recordStatus);
        }

        if ( memo != null )
        {
            dnsServiceInstancePropertyInfo.setMemo(memo);
        }

        ZonedDateTime zonedUpdateDateTime = LoulanDNSUtils.getCurrentZonedDateTime();
        String updateTime = LoulanDNSUtils.toDateTimeString(zonedUpdateDateTime);
        dnsServiceInstancePropertyInfo.setUpdateDate(updateTime);

        DNSServiceInstancePropertyInfo savedDNSServiceInstancePropertyInfo = loulanDNSLogicalDBService.saveDNSServiceInstancePropertyInfo(dnsServiceInstancePropertyInfo);
        return savedDNSServiceInstancePropertyInfo;
    }


    // DNSサービスインスタンスプロパティ情報 更新
    @PutMapping("/update/dns-service-instance-property")
    public DNSServiceInstancePropertyInfo updateDNSServiceInstanceProperty( @RequestParam(name = "UserName", required = true) String userName,
                                                            @RequestParam(name = "DNSServiceInstanceName", required = true) String dnsServiceInstanceName,
                                                            @RequestParam(name = "DNSServiceInstancePropertyKey", required = false) String dnsServiceInstancePropertyKey,  
                                                            @RequestParam(name = "DNSServiceInstancePropertyValue", required = false) String dnsServiceInstancePropertyValue,  
                                                            @RequestParam(name = "DNSServiceInstancePropertyExplain", required = false) String dnsServiceInstancePropertyExplain,  
                                                            @RequestParam(name = "RecordStatus", required = false ) Long recordStatus,
                                                            @RequestParam(name = "Memo", required = false) String memo) throws DNSServiceCommonException
    {

        // DB上に既存のプロパティ情報のエントリがあるかをチェックする.
        DNSServiceInstancePropertyInfo checkDNSServiceInstancePropertyInfo = getDNSServiceInstanceProperty(userName, dnsServiceInstanceName, dnsServiceInstancePropertyKey );
        if ( checkDNSServiceInstancePropertyInfo == null )
        {
            // DB上には指定されたレコードは存在しない.
            // 更新処理はできないので例外をスローする.
            String msg = String.format("Failed to update DNSServiceInstanceProperties record. Specified DNSServiceInstanceProperty is NOT exists. userName=%s, dnsServiceInstanceName=%s, dnsServiceInstancePropertyKey=%s", userName, dnsServiceInstanceName, dnsServiceInstancePropertyKey );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        // 更新処理を行うべきレコードのID値を取得したので、本ID値を主キーとして更新処理を行う.
        Long dnsServiceInstancePropertyID = checkDNSServiceInstancePropertyInfo.getDnsServiceInstancePropertyID();
        DNSServiceInstancePropertyInfo savedDNSServiceInstancePropertyInfo = updateDNSServiceInstanceProperty(dnsServiceInstancePropertyID, dnsServiceInstancePropertyKey, dnsServiceInstancePropertyValue, dnsServiceInstancePropertyExplain, recordStatus, memo);

        return savedDNSServiceInstancePropertyInfo;
    }



    // DNSサービスインスタンスプロパティ情報 削除
    @DeleteMapping("/delete/dns-service-instance-property/{dnsServiceInstancePropertyID}")
    public void deleteDNSServiceInstanceProperty( @PathVariable Long dnsServiceInstancePropertyID ) throws DNSServiceCommonException
    {
        DNSServiceInstancePropertyInfo info = getDNSServiceInstanceProperty(dnsServiceInstancePropertyID);
        if ( info == null )
        {
            // 指定されたプロパティレコードはDB上に存在しない.
            String msg = String.format("Specified DNSServiceInstanceProperty is not exists. dnsServiceInstancePropertyID=%d", dnsServiceInstancePropertyID  );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        loulanDNSDBService.deleteDNSServiceInstanceProperties(dnsServiceInstancePropertyID);
    }

    // DNSサービスインスタンスプロパティ情報 削除
    @DeleteMapping("/delete/dns-service-instance-property")
    public void deleteDNSServiceInstanceProperty(   @RequestParam(name = "UserName", required = true) String userName,
                                                    @RequestParam(name = "DNSServiceInstanceName", required = true) String dnsServiceInstanceName,
                                                    @RequestParam(name = "DNSServiceInstancePropertyKey", required = false) String dnsServiceInstancePropertyKey ) throws  DNSServiceCommonException
    {

        DNSServiceInstancePropertyInfo info = getDNSServiceInstanceProperty(userName, dnsServiceInstanceName, dnsServiceInstancePropertyKey);
        if ( info == null )
        {
            // 指定されたプロパティレコードはDB上に存在しない.
            String msg = String.format("Specified DNSServiceInstanceProperty is not exists. userName=%s, dnsServiceInstanceName=%s, dnsServiceInstancePropertyKey=%s", userName, dnsServiceInstanceName, dnsServiceInstancePropertyKey );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        // レコードのID値を基に削除処理を行う.
        Long dnsServiceInstancePropertyID = info.getDnsServiceInstancePropertyID();
        deleteDNSServiceInstanceProperty(dnsServiceInstancePropertyID);
    }



    // DNSサービスインスタンスプロパティ情報 簡易設定
    @PutMapping("/put/dns-service-instance-property/{UserName}/{DNSServiceInstanceName}/{key}")
    public DNSServiceInstancePropertyInfo putDNSServiceInstanceProperty( @PathVariable(name = "UserName") String userName,
                                                            @PathVariable(name = "DNSServiceInstanceName") String dnsServiceInstanceName,
                                                            @PathVariable(name = "key") String key,
                                                            @RequestParam(name = "value", required=true) String value ) throws DNSServiceCommonException
    {

        DNSServiceInstancePropertyInfo savedInfo;


        System.out.println( String.format("[DEBUG] putDNSServiceInstanceProperty() : UserName=%s, DNSServiceInstanceName=%s, key=%s, value=%s", userName, dnsServiceInstanceName, key, value) );

        DNSServiceInstancePropertyInfo  existInfo = getDNSServiceInstanceProperty(userName, dnsServiceInstanceName, key);
        if ( existInfo == null )
        {
            // 既存のプロパティレコードが存在しないので、新規作成する.
            savedInfo = createDNSServiceInstanceProperty(userName, dnsServiceInstanceName, key, value, "", (long)LoulanDNSConstants.DB_CONST_VALUE_RECORD_STATUS_ACTIVE, null);
        }
        else
        {
            // 既存レコードの更新処理を行う.
            existInfo.dnsServiceInstancePropertyKey = key;
            existInfo.dnsServiceInstancePropertyValue = value;

            savedInfo = updateDNSServiceInstanceProperty(userName, dnsServiceInstanceName, key, value, existInfo.getDnsServiceInstancePropertyExplain(), existInfo.recordStatus, existInfo.getMemo() );
        }

        return savedInfo;
    }





       


    /**
     * DNSリゾルバインスタンス情報の一覧取得
     * 
     */
    @GetMapping("/list/dns-resolver-instance")
    public DNSResolverInstanceInfo[] listDNSResolverInstance( @RequestParam(name = "UserName", required = true) String userName) throws DNSServiceCommonException
    {
        // ユーザー名からユーザーIDを取得.
        User user = loulanDNSDBService.getUser( userName );
        if ( user == null )
        {
            // 指定されたユーザーはDB上に存在しない.
            String msg = String.format("Specified User is not exists. userName=%s", userName  );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        List<DNSResolverInstanceInfo> list = loulanDNSLogicalModelService.getDNSResolverInstanceInfoListByUserName(userName);
        DNSResolverInstanceInfo[] array = new DNSResolverInstanceInfo[ list.size() ];
        for( int i=0; i < array.length; i++)
        {
            array[i] = list.get(i);
        }

        return array;
    }


    // DNSリゾルバインスタンス情報取得
    @GetMapping("/get/dns-resolver-instance")
    public DNSResolverInstanceInfo getDNSResolverInstance( @RequestParam(name = "UserName", required = true) String userName,
                                                            @RequestParam(name = "DNSResolverInstanceName", required = true) String dnsResolverInstanceName ) throws DNSServiceCommonException
    {
        // ユーザー名からユーザーIDを取得.
        User user = loulanDNSDBService.getUser( userName );
        if ( user == null )
        {
            // 指定されたユーザーはDB上に存在しない.
            String msg = String.format("Specified User is not exists. userName=%s", userName  );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        Long userID = user.getUserID();
        DNSResolverInstance  dnsResolverInstance  = loulanDNSDBService.getDNSResolverInstanceByName(userID, dnsResolverInstanceName);
        if ( dnsResolverInstance == null )
        {
            // 指定されたDNSリゾルバインスタンスはDB上に存在しない.
            String msg = String.format("Specified DNSResolverInstance is not exists. userName=%s, dnsResolverInstanceName=%s", userName, dnsResolverInstanceName );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        Long dnsResolverInstanceID = dnsResolverInstance.getDnsResolverInstanceID();
        DNSResolverInstanceInfo dnsResolverInstanceInfo = loulanDNSLogicalModelService.getDNSResolverInstanceInfo(dnsResolverInstanceID);

        return dnsResolverInstanceInfo;
    }


    // DNSリゾルバインスタンス情報作成
    @PutMapping("/create/dns-resolver-instance")
    public DNSResolverInstanceInfo createDNSResolverInstance( @RequestParam(name = "UserName", required = true) String userName,
                                                            @RequestParam(name = "DNSResolverInstanceName", required = true) String dnsResolverInstanceName,
                                                            @RequestParam(name = "DNSResolverInstanceExplain", required = false ) String dnsResolverInstanceExplain,
                                                            @RequestParam(name = "DNSResolverTypeCode", required = true ) Long dnsResolverTypeCode,
                                                            @RequestParam(name = "RecordStatus", required = true ) Long recordStatus,
                                                            @RequestParam(name = "Memo", required = false) String memo) throws DNSServiceCommonException
    {

        // ユーザー名からユーザーIDを取得.
        User user = loulanDNSDBService.getUser( userName );
        Long userID = user.getUserID();

        // DNSResolverInstanceInfo論理モデルオブジェクトの生成.
        DNSResolverInstanceInfo tmpDNSResolverInstanceInfo = dnsResolverInstanceInfoFactory.createDNSResolverInstanceInfoObject(null, userID, dnsResolverInstanceName, dnsResolverInstanceExplain, dnsResolverTypeCode, recordStatus, memo );

        // 新規作成なので、あえて論理モデルのsaveメソッドを呼んでいる.(新規追加では子要素の入れ替え等が生じないから.)
        DNSResolverInstanceInfo savedDNSResolverInstanceInfo = loulanDNSLogicalModelService.saveDNSResolverInstanceInfo(tmpDNSResolverInstanceInfo);
        return savedDNSResolverInstanceInfo;
    }


    // DNSリゾルバインスタンス情報更新
    @PutMapping("/update/dns-resolver-instance/{DNSResolverInstanceID}")
    public DNSResolverInstanceInfo updateDNSResolverInstance( @PathVariable(name = "DNSResolverInstanceID", required = true) Long dnsResolverInstanceID,
                                                            @RequestParam(name = "UserName", required = false) String userName,
                                                            @RequestParam(name = "DNSResolverInstanceName", required = false) String dnsResolverInstanceName,
                                                            @RequestParam(name = "DNSResolverInstanceExplain", required = false ) String dnsResolverInstanceExplain,
                                                            @RequestParam(name = "DNSResolverTypeCode", required = false ) Long dnsResolverTypeCode,
                                                            @RequestParam(name = "RecordStatus", required = false ) Long recordStatus,
                                                            @RequestParam(name = "Memo", required = false) String memo) throws DNSServiceCommonException
    {
        DNSResolverInstanceInfo dnsResolverInstanceInfo = loulanDNSLogicalModelService.getDNSResolverInstanceInfo(dnsResolverInstanceID);

        if ( userName != null )
        {
            // ユーザー名からユーザーIDを取得.
            User user = loulanDNSDBService.getUser( userName );
            Long userID = user.getUserID();
            dnsResolverInstanceInfo.setUserID(userID);
        }

        if ( dnsResolverInstanceName != null )
        {
            dnsResolverInstanceInfo.setDnsResolverInstanceName(dnsResolverInstanceName);
        }

        if ( dnsResolverInstanceExplain != null )
        {
            dnsResolverInstanceInfo.setDnsResolverInstanceExplain(dnsResolverInstanceExplain);
        }

        if ( dnsResolverTypeCode != null )
        {
            dnsResolverInstanceInfo.setDnsResolverTypeCode(dnsResolverTypeCode);
        }

        if ( recordStatus != null )
        {
            dnsResolverInstanceInfo.setRecordStatus(recordStatus);
        }

        if ( memo != null )
        {
            dnsResolverInstanceInfo.setMemo(memo);
        }

        ZonedDateTime updateZonedDateTime = ZonedDateTime.now();
        dnsResolverInstanceInfo.setCreateDate(updateZonedDateTime);

        // DB上のデータを更新する. (無関係の子要素の更新処理も走るが、既存レコードをDBから取得してから更新しているため、問題はない.)
        DNSResolverInstanceInfo savedDNSResolverInstanceInfo = loulanDNSLogicalModelService.saveDNSResolverInstanceInfo(dnsResolverInstanceInfo);
        return savedDNSResolverInstanceInfo;
    }


    // DNSリゾルバインスタンス情報更新
    @PutMapping("/update/dns-resolver-instance")
    public DNSResolverInstanceInfo updateDNSResolverInstance( @RequestParam(name = "UserName", required = true) String userName,
                                                            @RequestParam(name = "DNSResolverInstanceName", required = true) String dnsResolverInstanceName,
                                                            @RequestParam(name = "DNSResolverInstanceExplain", required = false ) String dnsResolverInstanceExplain,
                                                            @RequestParam(name = "DNSResolverTypeCode", required = false ) Long dnsResolverTypeCode,
                                                            @RequestParam(name = "RecordStatus", required = false ) Long recordStatus,
                                                            @RequestParam(name = "Memo", required = false) String memo) throws DNSServiceCommonException
    {

        // DBから既存レコードを取得する.
        DNSResolverInstanceInfo dnsResolverInstanceInfo = getDNSResolverInstance(userName, dnsResolverInstanceName);
        Long dnsResolverInstanceID = dnsResolverInstanceInfo.getDNSResolverInstanceID();

        // ID値をベースに書き込む.
        DNSResolverInstanceInfo savedDNSResolverInstanceInfo = updateDNSResolverInstance(dnsResolverInstanceID, userName, dnsResolverInstanceName, dnsResolverInstanceExplain, dnsResolverTypeCode, recordStatus, memo);
        return savedDNSResolverInstanceInfo;
    }


    // DNSリゾルバインスタンス削除
    @DeleteMapping("/delete/dns-resolver-instance")
    public void deleteDNSResolverInstance(  @RequestParam(name = "UserName", required = true) String userName,
                                            @RequestParam(name = "DNSResolverInstanceName", required = true) String dnsResolverInstanceName ) throws DNSServiceCommonException
    {
        DNSResolverInstanceInfo  dnsResolverInstanceInfo  = getDNSResolverInstance(userName, dnsResolverInstanceName);
        
        if ( dnsResolverInstanceInfo == null )
        {
            // 指定されたDNSリゾルバインスタンスはDB上に存在しない.
            String msg = String.format("Specified DNSResolverInstance is not exists. userName=%s, dnsResolverInstanceName=%s", userName, dnsResolverInstanceName );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        // DBからレコードを削除する.
        loulanDNSLogicalModelService.deleteDNSResolverInstanceInfo(dnsResolverInstanceInfo);
    }
                                                            

                        
    

     // DNSリゾルバインスタンスプロパティ情報の一覧取得
    @GetMapping("/get/dns-resolver-instance-properties")
    public List<DNSResolverInstancePropertyInfo> getDNSResolverInstanceProperties(
                                                            @RequestParam(name = "UserName", required = true) String userName,
                                                            @RequestParam(name = "DNSResolverInstanceName", required = true) String dnsResolverInstanceName ) throws DNSServiceCommonException
    {
        // DBからDNSリゾルバインスタンスを取得する.
        DNSResolverInstanceInfo dnsResolverInstanceInfo = getDNSResolverInstance(userName, dnsResolverInstanceName);

        if ( dnsResolverInstanceInfo == null )
        {
            // 指定されたDNSリゾルバインスタンスはDB上に存在しない.
            String msg = String.format("Specified DNSResolverInstance is not exists. userName=%s, dnsResolverInstanceName=%s", userName, dnsResolverInstanceName );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }


        List<DNSResolverInstancePropertyInfo> dnsResolverInstanceProperties = dnsResolverInstanceInfo.getDNSResolverPropertiesInfoList();
        return dnsResolverInstanceProperties;
    }

    // DNSリゾルバインスタンスプロパティ情報取得
    @GetMapping("/get/dns-resolver-instance-property/{UserName}/{DNSResolverInstanceName}/{DNSResolverInstancePropertyKey}")
    public DNSResolverInstancePropertyInfo getDNSResolverInstanceProperty(
                                                            @PathVariable(name = "UserName") String userName,
                                                            @PathVariable(name = "DNSResolverInstanceName") String dnsResolverInstanceName,
                                                            @PathVariable(name = "DNSResolverInstancePropertyKey") String dnsResolverInstancePropertyKey ) throws DNSServiceCommonException
    {

        List<DNSResolverInstancePropertyInfo> dnsResolverInstanceProperties = getDNSResolverInstanceProperties(userName, dnsResolverInstanceName);

        DNSResolverInstancePropertyInfo dnsResolverInstancePropertyInfo = null;
        for( DNSResolverInstancePropertyInfo item : dnsResolverInstanceProperties )
        {
            if ( item.getDNSResolverInstancePropertyKey().equals(dnsResolverInstancePropertyKey) )
            {
                // プロパティキーが一致するエントリが見つかった.
                dnsResolverInstancePropertyInfo = item;
                break;
            }
        }

        return dnsResolverInstancePropertyInfo;
    }

    // DNSサービスインスタンスプロパティ情報取得
    @GetMapping("/get/dns-resolver-instance-property/{dnsResolverInstancePropertyID}")
    public DNSResolverInstancePropertyInfo getDNSResolverInstanceProperty( @PathVariable Long dnsResolverInstancePropertyID ) throws DNSServiceCommonException
    {
        // DBからレコードを取得する.
        DNSResolverInstanceProperties existRecord = loulanDNSDBService.getDNSResolverInstanceProperties( dnsResolverInstancePropertyID );
        if ( existRecord == null )
        {
            return null;
        }

        // DBから取得したレコードの情報を基に、親エンティティのID値と、プロパティキーを取得する.
        Long dnsResolverInstanceID = existRecord.getDnsResolverInstanceID();
        String key = existRecord.getDnsResolverInstancePropertyKey();

        // 親エンティティのID値と、プロパティキーをもとに再度、論理モデルクラスの問い合わせを行う.
        DNSResolverInstanceInfo  dnsResolverInstanceInfo  = loulanDNSLogicalModelService.getDNSResolverInstanceInfo(dnsResolverInstanceID);
        DNSResolverInstancePropertyInfo  dnsResolverInstancePropertyInfo  = dnsResolverInstanceInfo.getDNSPropertiesInfo(key);

        return dnsResolverInstancePropertyInfo;
    }


   // DNSリゾルバインスタンスプロパティ情報 新規作成
    @PutMapping("/create/dns-resolver-instance-property")
    public DNSResolverInstancePropertyInfo createDNSResolverInstanceProperty( @RequestParam(name = "UserName", required = true) String userName,
                                                            @RequestParam(name = "DNSResolverInstanceName", required = true) String dnsResolverInstanceName,
                                                            @RequestParam(name = "DNSResolverInstancePropertyKey", required = true) String dnsResolverInstancePropertyKey,  
                                                            @RequestParam(name = "DNSResolverInstancePropertyValue", required = true) String dnsResolverInstancePropertyValue,  
                                                            @RequestParam(name = "DNSResolverInstancePropertyExplain", required = true) String dnsResolverInstancePropertyExplain,  
                                                            @RequestParam(name = "RecordStatus", required = true ) Long recordStatus,
                                                            @RequestParam(name = "Memo", required = false) String memo) throws DNSServiceCommonException
    {

        System.out.println( String.format("[DEBUG] createDNSResolverInstanceProperty() : UserName=%s, DNSResolverInstanceName=%s, DNSResolverInstancePropertyKey=%s, DNSResolverInstancePropertyValue=%s", userName, dnsResolverInstanceName, dnsResolverInstancePropertyKey, dnsResolverInstancePropertyValue ) );


        // DB上に既存のプロパティ情報のエントリがあるかをチェックする.
        DNSResolverInstancePropertyInfo checkDNSResolverInstancePropertyInfo = getDNSResolverInstanceProperty(userName, dnsResolverInstanceName, dnsResolverInstancePropertyKey );
        if ( checkDNSResolverInstancePropertyInfo != null )
        {
            // DB上には既に指定されたプロパティキーと同じプロパティ情報のエントリが存在する.
            // 新規追加は出来ないので例外をスローする.
            String msg = String.format("Failed to create DNSResolverInstanceProperties record. Specified DNSResolverInstanceProperty is already exists. userName=%s, dnsResolverInstanceName=%s, dnsResolverInstancePropertyKey=%s", userName, dnsResolverInstanceName, dnsResolverInstancePropertyKey );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        // DBからDNSリゾルバインスタンスを取得する.
        DNSResolverInstanceInfo dnsResolverInstanceInfo = getDNSResolverInstance(userName, dnsResolverInstanceName);

        if ( dnsResolverInstanceInfo == null )
        {
            // 指定されたサービスインスタンスはDB上に存在しない.
            String msg = String.format("Specified DNSResolverInstance is not exists. userName=%s, dnsResolverInstanceName=%s", userName, dnsResolverInstanceName );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }


        Long dnsResolverInstanceID = dnsResolverInstanceInfo.getDNSResolverInstanceID();
        DNSResolverInstancePropertyInfo dnsResolverInstancePropertyInfo = dnsResolverInstancePropertyInfoFactory.createDNSResolverInstancePropertiesInfoObject(dnsResolverInstanceID, dnsResolverInstancePropertyKey, dnsResolverInstancePropertyValue, dnsResolverInstancePropertyExplain, recordStatus, memo );

        // 既存のプロパティのリストに新規追加エントリを挿入する.
        List<DNSResolverInstancePropertyInfo> properties = dnsResolverInstanceInfo.getDNSResolverPropertiesInfoList();
        properties.add(dnsResolverInstancePropertyInfo);
        dnsResolverInstanceInfo.setDNSResolverPropertiesInfoList(properties);

        System.out.println( String.format("[DEBUG] createDNSResolverInstanceProperty() : dnsResolverInstanceInfo.getDNSResolverPropertiesInfoList().size()=%d, properties.size()=%d", dnsResolverInstanceInfo.getDNSResolverPropertiesInfoList().size(), properties.size() )  );


        // 親エントリであるDNSサービスインスタンスごと論理的に更新する.
        DNSResolverInstanceInfo savedDNSResolverInstanceInfo = loulanDNSLogicalModelService.saveDNSResolverInstanceInfo(dnsResolverInstanceInfo);
        DNSResolverInstancePropertyInfo savedDNSResolverInstancePropertyInfo = savedDNSResolverInstanceInfo.getDNSPropertiesInfo(dnsResolverInstancePropertyKey);

        System.out.println( String.format("[DEBUG] createDNSResolverInstanceProperty() : savedDNSResolverInstancePropertyInfo.size()=%d", savedDNSResolverInstanceInfo.getDNSResolverPropertiesInfoList().size() )  );


        return savedDNSResolverInstancePropertyInfo;
    }




    // DNSリゾルバインスタンスプロパティ情報 更新
    @PutMapping("/update/dns-resolver-instance-property/{dnsResolverInstancePropertyID}")
    public DNSResolverInstancePropertyInfo updateDNSResolverInstanceProperty( @PathVariable Long dnsResolverInstancePropertyID,
                                                            @RequestParam(name = "DNSResolverInstancePropertyKey", required = false) String dnsResolverInstancePropertyKey,  
                                                            @RequestParam(name = "DNSResolverInstancePropertyValue", required = false) String dnsResolverInstancePropertyValue,  
                                                            @RequestParam(name = "DNSResolverInstancePropertyExplain", required = false) String dnsResolverInstancePropertyExplain,  
                                                            @RequestParam(name = "RecordStatus", required = false ) Long recordStatus,
                                                            @RequestParam(name = "Memo", required = false) String memo) throws DNSServiceCommonException
    {

        DNSResolverInstancePropertyInfo  dnsResolverInstancePropertyInfo = loulanDNSLogicalDBService.getDNSResolverPropertiesInfo(dnsResolverInstancePropertyID);
        if ( dnsResolverInstancePropertyInfo == null )
        {
            // DB上には指定されたプロパティエントリは存在しない.
            String msg = String.format("Failed to update DNSResolverInstanceProperties record. Specified DNSResolverInstanceProperty is NOT exists. dnsResolverInstancePropertyID=%d", dnsResolverInstancePropertyID );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        if ( dnsResolverInstancePropertyKey != null )
        {
            dnsResolverInstancePropertyInfo.setDnsResolverPropertyKey(dnsResolverInstancePropertyKey);
        }

        if ( dnsResolverInstancePropertyValue != null )
        {
            dnsResolverInstancePropertyInfo.setDnsResolverPropertyValue(dnsResolverInstancePropertyValue);
        }

        if ( dnsResolverInstancePropertyExplain != null )
        {
            dnsResolverInstancePropertyInfo.setDnsResolverPropertyExplain(dnsResolverInstancePropertyExplain);
        }

        if ( recordStatus != null )
        {
            dnsResolverInstancePropertyInfo.setRecordStatus(recordStatus);
        }

        if ( memo != null )
        {
            dnsResolverInstancePropertyInfo.setMemo(memo);
        }

        ZonedDateTime zonedUpdateDateTime = LoulanDNSUtils.getCurrentZonedDateTime();
        String updateTime = LoulanDNSUtils.toDateTimeString(zonedUpdateDateTime);
        dnsResolverInstancePropertyInfo.setUpdateDate(updateTime);

        DNSResolverInstancePropertyInfo savedDNSResolverInstancePropertyInfo = loulanDNSLogicalDBService.saveDNSResolverPropertiesInfo(dnsResolverInstancePropertyInfo);
        return savedDNSResolverInstancePropertyInfo;
    }


    // DNSリゾルバインスタンスプロパティ情報 更新
    @PutMapping("/update/dns-resolver-instance-property")
    public DNSResolverInstancePropertyInfo updateDNSResolverInstanceProperty( @RequestParam(name = "UserName", required = true) String userName,
                                                            @RequestParam(name = "DNSResolverInstanceName", required = true) String dnsResolverInstanceName,
                                                            @RequestParam(name = "DNSResolverInstancePropertyKey", required = false) String dnsResolverInstancePropertyKey,  
                                                            @RequestParam(name = "DNSResolverInstancePropertyValue", required = false) String dnsResolverInstancePropertyValue,  
                                                            @RequestParam(name = "DNSResolverInstancePropertyExplain", required = false) String dnsResolverInstancePropertyExplain,  
                                                            @RequestParam(name = "RecordStatus", required = false ) Long recordStatus,
                                                            @RequestParam(name = "Memo", required = false) String memo) throws DNSServiceCommonException
    {

        // DB上に既存のプロパティ情報のエントリがあるかをチェックする.
        DNSResolverInstancePropertyInfo checkDNSResolverInstancePropertyInfo = getDNSResolverInstanceProperty(userName, dnsResolverInstanceName, dnsResolverInstancePropertyKey );
        if ( checkDNSResolverInstancePropertyInfo == null )
        {
            // DB上には指定されたレコードは存在しない.
            // 更新処理はできないので例外をスローする.
            String msg = String.format("Failed to update DNSResolverInstanceProperties record. Specified DNSResolverInstanceProperty is NOT exists. userName=%s, dnsResolverInstanceName=%s, dnsResolverInstancePropertyKey=%s", userName, dnsResolverInstanceName, dnsResolverInstancePropertyKey );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        // 更新処理を行うべきレコードのID値を取得したので、本ID値を主キーとして更新処理を行う.
        Long dnsResolverInstancePropertyID = checkDNSResolverInstancePropertyInfo.getDNSResolverInstancePropertyID();
        DNSResolverInstancePropertyInfo savedDNSResolverInstancePropertyInfo = updateDNSResolverInstanceProperty(dnsResolverInstancePropertyID, dnsResolverInstancePropertyKey, dnsResolverInstancePropertyValue, dnsResolverInstancePropertyExplain, recordStatus, memo);

        return savedDNSResolverInstancePropertyInfo;
    }


    // DNSリゾルバインスタンスプロパティ情報 削除
    @DeleteMapping("/delete/dns-resolver-instance-property/{dnsResolverInstancePropertyID}")
    public void deleteDNSResolverInstanceProperty( @PathVariable Long dnsResolverInstancePropertyID ) throws DNSServiceCommonException
    {
        DNSResolverInstancePropertyInfo info = getDNSResolverInstanceProperty(dnsResolverInstancePropertyID);
        if ( info == null )
        {
            // 指定されたプロパティレコードはDB上に存在しない.
            String msg = String.format("Specified DNSResolverInstanceProperty is not exists. dnsResolverInstancePropertyID=%d", dnsResolverInstancePropertyID  );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        loulanDNSDBService.deleteDNSResolverInstanceProperties(dnsResolverInstancePropertyID);
    }


    // DNSサービスインスタンスプロパティ情報 削除
    @DeleteMapping("/delete/dns-resolver-instance-property")
    public void deleteDNSResolverInstanceProperty(   @RequestParam(name = "UserName", required = true) String userName,
                                                    @RequestParam(name = "DNSResolverInstanceName", required = true) String dnsResolverInstanceName,
                                                    @RequestParam(name = "DNSResolverInstancePropertyKey", required = false) String dnsResolverInstancePropertyKey ) throws  DNSServiceCommonException
    {

        DNSResolverInstancePropertyInfo info = getDNSResolverInstanceProperty(userName, dnsResolverInstanceName, dnsResolverInstancePropertyKey );
        if ( info == null )
        {
            // 指定されたプロパティレコードはDB上に存在しない.
            String msg = String.format("Specified DNSResolverInstanceProperty is not exists. userName=%s, dnsResolverInstanceName=%s, dnsResolverInstancePropertyKey=%s", userName, dnsResolverInstanceName, dnsResolverInstancePropertyKey );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        // レコードのID値を基に削除処理を行う.
        Long dnsResolverInstancePropertyID = info.getDNSResolverInstancePropertyID();
        deleteDNSResolverInstanceProperty(dnsResolverInstancePropertyID);
    }



    // DNSリゾルバインスタンスプロパティ情報 簡易設定
    @PutMapping("/put/dns-resolver-instance-property/{UserName}/{DNSResolverInstanceName}/{key}")
    public DNSResolverInstancePropertyInfo putDNSResolverInstanceProperty( @PathVariable(name = "UserName") String userName,
                                                            @PathVariable(name = "DNSResolverInstanceName") String dnsResolverInstanceName,
                                                            @PathVariable(name = "key") String key,
                                                            @RequestParam(name = "value", required=true) String value ) throws DNSServiceCommonException
    {

        System.out.println( String.format("[DEBUG] putDNSResolverInstanceProperty() : UserName=%s, DNSResolverInstanceName=%s, key=%s, value=%s", userName, dnsResolverInstanceName, key, value) );


        DNSResolverInstancePropertyInfo savedInfo;

        DNSResolverInstancePropertyInfo  existInfo = getDNSResolverInstanceProperty(userName, dnsResolverInstanceName, key);
        if ( existInfo == null )
        {
            // 既存のプロパティレコードが存在しないので、新規作成する.
            savedInfo = createDNSResolverInstanceProperty(userName, dnsResolverInstanceName, key, value, "", (long)LoulanDNSConstants.DB_CONST_VALUE_RECORD_STATUS_ACTIVE, null);
        }
        else
        {
            // 既存レコードの更新処理を行う.
            existInfo.setDnsResolverPropertyKey(key);
            existInfo.setDnsResolverPropertyValue(value);

            savedInfo = updateDNSResolverInstanceProperty(userName, dnsResolverInstanceName, key, value, existInfo.dnsResolverPropertyExplain, existInfo.recordStatus, existInfo.getMemo() );
        }

        return savedInfo;
    }


    // DNSサービスインスタンス情報の一覧取得
    @GetMapping("/list/dns-service-endpoint-instance")
    public DNSServiceEndpointInstanceInfo[] listDNSServiceEndpointInstance(@RequestParam(name = "UserName", required = false) String userName) throws DNSServiceCommonException
    {
        List<DNSServiceEndpointInstanceInfo> endpointInstanceInfoList = new ArrayList<DNSServiceEndpointInstanceInfo>();

        DNSServiceInstanceInfo[] dnsServiceInstanceInfoArray = listDNSServiceInstance(userName);
        for( DNSServiceInstanceInfo serviceInfo : dnsServiceInstanceInfoArray )
        {
            endpointInstanceInfoList.addAll( serviceInfo.getDNSServiceEndpointInstanceInfoList() );
        }

        DNSServiceEndpointInstanceInfo[] dnsServiceEndpointInstanceInfoArray = new DNSServiceEndpointInstanceInfo[ endpointInstanceInfoList.size() ];
        for( int i = 0; i < endpointInstanceInfoList.size(); i++ )
        {
            DNSServiceEndpointInstanceInfo info = endpointInstanceInfoList.get(i);
            dnsServiceEndpointInstanceInfoArray[i] = info;
        }

        return dnsServiceEndpointInstanceInfoArray;
    }



    // DNSサービスエンドポイントインスタンス情報取得
    @GetMapping("/get/dns-service-endpoint-instance/{dnsServiceEndpointInstanceID}")
    public DNSServiceEndpointInstanceInfo getDNSServiceEndpointInstance(@PathVariable Long dnsServiceEndpoointInstanceID) throws DNSServiceCommonException
    {
        DNSServiceEndpointInstanceInfo dnsServiceEndpointInstanceInfo = loulanDNSLogicalModelService.getDNSServiceEndpointInstanceInfo(dnsServiceEndpoointInstanceID);
        return dnsServiceEndpointInstanceInfo;
    }

    // DNSサービスエンドポイントインスタンス情報取得
    @GetMapping("/get/dns-service-endpoint-instance")
    public DNSServiceEndpointInstanceInfo getDNSServiceEndpointInstance(  @RequestParam(name = "UserName", required = true) String userName,
                                                @RequestParam(name = "DNSServiceEndpointInstanceName", required = true) String dnsServiceEndpointInstanceName ) throws DNSServiceCommonException
    {

        DNSServiceEndpointInstanceInfo[] dnsServiceEndpointInstanceArray = listDNSServiceEndpointInstance(userName);

        if ( dnsServiceEndpointInstanceArray == null )
        {
            return null;
        }
        
        for( DNSServiceEndpointInstanceInfo dnsServiceEndpointInstance : dnsServiceEndpointInstanceArray )
        {

            if ( dnsServiceEndpointInstance.getDNSServiceEndpointInstanceName().equals(dnsServiceEndpointInstanceName) )
            {
                return dnsServiceEndpointInstance;
            }
        }

        return null;
    }


    // DNSサービスエンドポイントインスタンス情報作成
    @PutMapping("/create/dns-service-endpoint-instance")
    public DNSServiceEndpointInstanceInfo createDNSServiceEndpointInstanceInfo( @RequestParam(name = "UserName", required = true) String userName,
                                                            @RequestParam(name = "DNSServiceEndpointInstanceName", required = true) String dnsServiceEndpointInstanceName,
                                                            @RequestParam(name = "DNSServiceEndpointInstanceExplain", required = true) String dnsServiceEndpointInstanceExplain,
                                                            @RequestParam(name = "DNSServiceEndpointTypeCode", required = true) Long dnsServiceEndpointTypeCode,
                                                            @RequestParam(name = "DNSServiceInstanceName", required = true) String dnsServiceInstanceName,
                                                            @RequestParam(name = "RecordStatus", required = true ) Long recordStatus,
                                                            @RequestParam(name = "Memo", required = false) String memo) throws DNSServiceCommonException
    {
        // DNSサービスインスタンスを取得
        DNSServiceInstanceInfo dnsServiceInstanceInfo = getDNSServiceInstance(userName, dnsServiceInstanceName);
        if ( dnsServiceInstanceInfo == null )
        {
            // 指定されたDNSサービスインスタンスは存在しない.
            String msg = String.format("Specified DNSServiceInstance is not exists. userName=%s, dnsServiceInstanceName=%s", userName, dnsServiceInstanceName);
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        // 既存のDNSサービスエンドポイントが存在しないかをチェック
        DNSServiceEndpointInstanceInfo existsEndpointInstanceInfo = getDNSServiceEndpointInstance(userName, dnsServiceEndpointInstanceName);
        if ( existsEndpointInstanceInfo != null )
        {
            String msg = String.format("Specified DNSServiceEndpointInstanc is already exists. userName=%s, dnsServiceEndpointInstanceName=%s", userName, dnsServiceEndpointInstanceName);
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        // DNSServiceEndpointInstanceInfo論理モデルオブジェクトの生成.
        DNSServiceEndpointInstanceInfo tmpDNSServiceEndpointInstanceInfo = dnsServiceEndpointInstanceInfoFactory.createDNSServiceEndpointInstanceInfoObject( null, dnsServiceInstanceInfo.getDNSServiceInstanceID(), dnsServiceEndpointInstanceName, dnsServiceEndpointInstanceExplain, dnsServiceEndpointTypeCode, recordStatus, memo);

        // 新規作成なので、あえて論理モデルのsaveメソッドを呼んでいる.(新規追加では子要素の入れ替え等が生じないから.)
        DNSServiceEndpointInstanceInfo savedDNSServiceEndpointInstanceInfo = loulanDNSLogicalModelService.saveDNSServiceEndpointInstanceInfo( tmpDNSServiceEndpointInstanceInfo );
        return savedDNSServiceEndpointInstanceInfo;
    }



    // DNSサービスインスタンス情報更新
    @PutMapping("/update/dns-service-endpoint-instance/{dnsServiceEndpointInstanceID}")
    public DNSServiceEndpointInstanceInfo updateDNSServiceEndpointInstance( @PathVariable Long dnsServiceEndpointInstanceID, 
                                                            @RequestParam(name = "DNSServiceEndpointInstanceName", required = false) String dnsServiceEndpointInstanceName,
                                                            @RequestParam(name = "DNSServiceEndpointInstanceExplain", required = false) String dnsServiceEndpointInstanceExplain,
                                                            @RequestParam(name = "DNSServiceEndpointTypeCode", required = false) Long dnsServiceEndpointTypeCode,
                                                            @RequestParam(name = "DNSServiceInstanceID", required = false) Long dnsServiceInstanceID,
                                                            @RequestParam(name = "RecordStatus", required = false ) Long recordStatus,
                                                            @RequestParam(name = "Memo", required = false) String memo) throws DNSServiceCommonException
    {

        if ( dnsServiceEndpointInstanceID == null )
        {
            // DNSサービスエンドポイントインスタンスを特定するための情報が指定されていない.
            String msg = String.format("Unsatisfied parameters for search DNSServiceEndpointInstanceInfo. DNSServiceEndpointInstanceID=%d, DNSServiceInstanceID=%d, DNSServiceInstanceName=%s", dnsServiceEndpointInstanceID, dnsServiceInstanceID, dnsServiceEndpointInstanceName );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        // 既存レコードをDBから取得.
        DNSServiceEndpointInstanceInfo existDNSServiceEndpointInstanceInfo = getDNSServiceEndpointInstance( dnsServiceEndpointInstanceID );

        // 既存のDNSエンドポイントサービスインスタンスのレコードが存在しない場合は例外をスロー.
        if  ( existDNSServiceEndpointInstanceInfo == null )
        {
            String msg = String.format("Specifed DNSServiceEndpointInstance is not found. DNSServiceEndpointInstanceID=%d, DNSServiceInstanceID=%d, DNSServiceInstanceName=%s", dnsServiceEndpointInstanceID, dnsServiceInstanceID, dnsServiceEndpointInstanceName );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        if ( dnsServiceEndpointInstanceName == null || dnsServiceEndpointInstanceName.equals("") )
        {
            dnsServiceEndpointInstanceName = existDNSServiceEndpointInstanceInfo.getDNSServiceEndpointInstanceName();
        }

        if ( dnsServiceEndpointInstanceExplain == null || dnsServiceEndpointInstanceExplain.equals("") )
        {
            dnsServiceEndpointInstanceExplain = existDNSServiceEndpointInstanceInfo.getDNSServiceEndpointInstanceExplain();
        }

        if ( dnsServiceEndpointTypeCode == null )
        {
            dnsServiceEndpointTypeCode = existDNSServiceEndpointInstanceInfo.getDNSServiceEndpointTypeCode();
        }

        if ( dnsServiceInstanceID == null )
        {
            dnsServiceInstanceID = existDNSServiceEndpointInstanceInfo.getDNSServiceInstanceID();
        }

        if ( recordStatus == null )
        {
            recordStatus = existDNSServiceEndpointInstanceInfo.getRecordStatus();             
        }

        if ( memo == null )
        {
            memo = existDNSServiceEndpointInstanceInfo.getMemo();
        }
        
        // ------------------------------------
        // ここからDNSServiceEndpointInstanceのファクトリクラスからオブジェクトを新規生成してsaveする.
        // ----
        
        // DNSServiceEndpointInstanceInfo論理モデルオブジェクトの生成.
        DNSServiceEndpointInstanceInfo tmpDNSServiceEndpointInstanceInfo = dnsServiceEndpointInstanceInfoFactory.createDNSServiceEndpointInstanceInfoObject( dnsServiceEndpointInstanceID, dnsServiceInstanceID, dnsServiceEndpointInstanceName, dnsServiceEndpointInstanceExplain, dnsServiceEndpointTypeCode, recordStatus, memo);

        // DBを更新.
        DNSServiceEndpointInstanceInfo savedDNSServiceEndpointInstanceInfo = loulanDNSLogicalDBService.saveDNSServiceEndpointInstanceInfo(tmpDNSServiceEndpointInstanceInfo);

        // -----

        return savedDNSServiceEndpointInstanceInfo;
    }


    // DNSサービスエンドポイントインスタンス情報更新
    @PutMapping("/update/dns-service-endpoint-instance")
    public DNSServiceEndpointInstanceInfo updateDNSServiceEndpointInstance( @RequestParam(name = "UserName", required = true) String userName,
                                                            @RequestParam(name = "DNSServiceEndpointInstanceName", required = false) String dnsServiceEndpointInstanceName,
                                                            @RequestParam(name = "DNSServiceEndpointInstanceExplain", required = false) String dnsServiceEndpointInstanceExplain,
                                                            @RequestParam(name = "DNSServiceEndpointTypeCode", required = false) Long dnsServiceEndpointTypeCode,
                                                            @RequestParam(name = "DNSServiceInstanceID", required = false) Long dnsServiceInstanceID,
                                                            @RequestParam(name = "RecordStatus", required = false ) Long recordStatus,
                                                            @RequestParam(name = "Memo", required = false) String memo) throws DNSServiceCommonException
    {

        if (userName == null || dnsServiceEndpointInstanceName == null )
        {
            // DNSサービスエンドポイントインスタンスを特定するための情報が指定されていない.
            String msg = String.format("Unsatisfied parameters for search DNSServiceInstance. UserName=%s, DNSServiceEndpointInstanceName=%s", userName, dnsServiceEndpointInstanceName );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        // ユーザー名とインスタンス名で既存レコードを引く.
        DNSServiceEndpointInstanceInfo existDNSServiceEndpointInstanceInfo = getDNSServiceEndpointInstance(userName, dnsServiceEndpointInstanceName);
        
        // 既存のDNSサービスエンドポイントインスタンスのレコードが存在しない場合は例外をスロー.
        if  ( existDNSServiceEndpointInstanceInfo == null )
        {
            String msg = String.format("Specifed DNSServiceEndpointInstance is not found.UserName=%s, DNSServiceEndpointInstanceName=%s", userName, dnsServiceEndpointInstanceName );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        long dnsServiceEndpointInstanceID = existDNSServiceEndpointInstanceInfo.getDNSServiceEndpointInstanceID();

        // 実際の更新処理は別メソッド(ID値を引数に取るメソッド)で行う.
        DNSServiceEndpointInstanceInfo updatedDNSServiceEndpointInstanceInfo = updateDNSServiceEndpointInstance( dnsServiceEndpointInstanceID, dnsServiceEndpointInstanceName, dnsServiceEndpointInstanceExplain, dnsServiceEndpointTypeCode, dnsServiceInstanceID, recordStatus, memo );

        return updatedDNSServiceEndpointInstanceInfo;
    }


    // DNSサービスエンドポイントインスタンス情報削除
    @DeleteMapping("/delete/dns-service-endpoint-instance/{dnsServiceEndpointInstanceID}")
    public void deleteDNSServiceEndpointInstance( @PathVariable Long dnsServiceEndpointInstanceID ) throws DNSServiceCommonException
    {

        if ( dnsServiceEndpointInstanceID == null )
        {
            String msg = String.format("DNSServiceEndpointInstanceID is not specifed. dnsServiceEndpointInstanceID=%d", dnsServiceEndpointInstanceID );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }


        DNSServiceEndpointInstanceInfo dnsServiceEndpointInstanceInfo = loulanDNSLogicalModelService.getDNSServiceEndpointInstanceInfo(dnsServiceEndpointInstanceID);
        if ( dnsServiceEndpointInstanceInfo == null )
        {
            String msg = String.format("Specified DNSServiceInstance is not exists. dnsServiceEndpointInstanceID=%d", dnsServiceEndpointInstanceID );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }


        // DBから対象レコードを削除する.
        // なお、論理モデルクラスの削除メソッドを呼ぶと子要素も含めて削除することになるため、DBサービスの削除メソッドをあえて読んでいる.
        loulanDNSDBService.deleteDNSServiceEndpointInstance(dnsServiceEndpointInstanceID);

        return;
    }


    // DNSサービスエンドポイントインスタンス情報削除
    @DeleteMapping("/delete/dns-service-endpoint-instance")
    public void deleteDNSServiceEndpointInstance( @RequestParam(name = "UserName", required = true) String userName,
                                          @RequestParam(name = "DNSServiceEndpointInstanceName", required = true) String dnsServiceEndpointInstanceName ) throws DNSServiceCommonException
    {

        DNSServiceEndpointInstanceInfo dnsServiceEndpointInstanceInfo = getDNSServiceEndpointInstance(userName, dnsServiceEndpointInstanceName);

        if ( dnsServiceEndpointInstanceInfo == null )
        {
            // 指定されたDNSサービスインスタンスはDB上に存在しない.
            String msg = String.format("Specified DNSServiceEndpointInstanceInfo is not exists. userName=%s, dnsServiceEndpointInstanceName=%s", userName, dnsServiceEndpointInstanceName );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        // DNSサービスインスタンス情報の削除をID値を基に行う.
        deleteDNSServiceEndpointInstance( dnsServiceEndpointInstanceInfo.getDNSServiceEndpointInstanceID() );

    }




     // DNSサービスエンドポイントインスタンスプロパティ情報の一覧取得
    @GetMapping("/get/dns-service-endpoint-instance-properties")
    public List<DNSServiceEndpointInstancePropertyInfo> getDNSServiceEndpointInstanceProperties(
                                                            @RequestParam(name = "UserName", required = true) String userName,
                                                            @RequestParam(name = "DNSServiceEndpointInstanceName", required = true) String dnsServiceEndpointInstanceName ) throws DNSServiceCommonException
    {
        // DBからDNSサービスエンドポイントインスタンスを取得する.
        DNSServiceEndpointInstanceInfo dnsServiceEndpointInstanceInfo = getDNSServiceEndpointInstance(userName, dnsServiceEndpointInstanceName);

        if ( dnsServiceEndpointInstanceInfo == null )
        {
            // 指定されたDNSサービスエンドポイントインスタンスはDB上に存在しない.
            String msg = String.format("Specified dnsServiceEndpointInstance is not exists. userName=%s, dnsServiceEndpointInstanceName=%s", userName, dnsServiceEndpointInstanceName );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        // DNSサービスエンドポイントIDをもとに、エンドポイントプロパティの一覧を得る.
        List<DNSServiceEndpointInstancePropertyInfo> properties = dnsServiceEndpointInstanceInfo.getDNSServiceEndpointInstanceProperties();

        return properties;
    }

    // DNSサービスエンドポイントインスタンス プロパティ情報取得
    @GetMapping("/get/dns-service-endpoint-instance-property/{UserName}/{DNSServiceEndpointInstanceName}/{DNSServiceEndpointInstancePropertyKey}")
    public DNSServiceEndpointInstancePropertyInfo getDNSServiceEndpointInstanceProperty(
                                                            @PathVariable(name = "UserName") String userName,
                                                            @PathVariable(name = "DNSServiceEndpointInstanceName") String dnsServiceEndpointInstanceName,
                                                            @PathVariable(name = "DNSServiceEndpointInstancePropertyKey") String dnsServiceEndpointInstancePropertyKey ) throws DNSServiceCommonException
    {

        List<DNSServiceEndpointInstancePropertyInfo> dnsServiceEndpointInstanceProperties 
                            = getDNSServiceEndpointInstanceProperties(userName, dnsServiceEndpointInstanceName);

        DNSServiceEndpointInstancePropertyInfo dnsServiceEndpointInstancePropertyInfo = null;
        for( DNSServiceEndpointInstancePropertyInfo item : dnsServiceEndpointInstanceProperties )
        {
            if ( item.getDNSServiceEndpointInstancePropertyKey().equals(dnsServiceEndpointInstancePropertyKey) )
            {
                // プロパティキーが一致するエントリが見つかった.
                dnsServiceEndpointInstancePropertyInfo = item;
                break;
            }
        }

        return dnsServiceEndpointInstancePropertyInfo;
    }


    // DNSサービスエンドポイントインスタンスプロパティ情報 取得
    @GetMapping("/get/dns-service-endpoint-instance-property/{dnsServiceEndpointInstancePropertyID}")
    public DNSServiceEndpointInstancePropertyInfo getDNSServiceEndpointInstanceProperty( @PathVariable Long dnsServiceEndpointInstancePropertyID ) throws DNSServiceCommonException
    {
        // DBからレコードを取得する.
        DNSServiceEndpointInstanceProperties existRecord = loulanDNSDBService.getDNSServiceEndpointInstanceProperties( dnsServiceEndpointInstancePropertyID );
        if ( existRecord == null )
        {
            return null;
        }

        // DBから取得したレコードの情報を基に、親エンティティのID値と、プロパティキーを取得する.
        Long dnsServiceEndpointInstanceID = existRecord.getDnsServiceEndpointInstanceID();
        String key = existRecord.getDnsServiceEndpointInstancePropertyKey();

        // 親エンティティを取得.
        DNSServiceEndpointInstanceInfo dnsServiceEndpointInstanceInfo = getDNSServiceEndpointInstance(dnsServiceEndpointInstanceID);

        if ( dnsServiceEndpointInstanceInfo == null )
        {
            // 親エンティティがDB上に存在しない.
            String msg = String.format("Specified DNSServiceEndpointInstance is not exists. dnsServiceEndpointInstanceID=%d. Related by dnsServiceEndpointInstancePropertyID=%d", dnsServiceEndpointInstanceID, dnsServiceEndpointInstancePropertyID );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        // 親エンティティからプロパティのエントリを取得する.
        DNSServiceEndpointInstancePropertyInfo dnsServiceEndpointInstancePropertyInfo 
            = dnsServiceEndpointInstanceInfo.getDNSServiceEndpointInstanceProperty(key);

        return dnsServiceEndpointInstancePropertyInfo;
    }


   // DNSサービスエンドポイントインスタンスプロパティ情報 新規作成
    @PutMapping("/create/dns-service-endpoint-instance-property")
    public DNSServiceEndpointInstancePropertyInfo createDNSServiceEndpointInstanceProperty( @RequestParam(name = "UserName", required = true) String userName,
                                                            @RequestParam(name = "DNSServiceEndpointInstanceName", required = true) String dnsServiceEndpointInstanceName,
                                                            @RequestParam(name = "DNSServiceEndpointInstancePropertyKey", required = true) String dnsServiceEndpointInstancePropertyKey,  
                                                            @RequestParam(name = "DNSServiceEndpointInstancePropertyValue", required = true) String dnsServiceEndpointInstancePropertyValue,  
                                                            @RequestParam(name = "DNSServiceEndpointInstancePropertyExplain", required = true) String dnsServiceEndpointInstancePropertyExplain,  
                                                            @RequestParam(name = "RecordStatus", required = true ) Long recordStatus,
                                                            @RequestParam(name = "Memo", required = false) String memo) throws DNSServiceCommonException
    {
        // DB上に既存のプロパティ情報のエントリがあるかをチェックする.
        DNSServiceEndpointInstancePropertyInfo checkDNSServiceEndpointInstancePropertyInfo
                                                    = getDNSServiceEndpointInstanceProperty(userName, dnsServiceEndpointInstanceName, dnsServiceEndpointInstancePropertyKey);

        if ( checkDNSServiceEndpointInstancePropertyInfo != null )
        {
            // DB上には既に指定されたプロパティキーと同じプロパティ情報のエントリが存在する.
            // 新規追加は出来ないので例外をスローする.
            String msg = String.format("Failed to create DNSServiceEndpointInstanceProperties record. Specified DNSServiceEndpointInstanceProperty is already exists. userName=%s, dnsServiceEndpointInstanceName=%s, dnsServiceEndpointInstancePropertyKey=%s", userName, dnsServiceEndpointInstanceName, dnsServiceEndpointInstancePropertyKey );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        // DBからDNSサービスエンドポイントインスタンスを取得する.
        DNSServiceEndpointInstanceInfo dnsServiceEndpointInstanceInfo = getDNSServiceEndpointInstance(userName, dnsServiceEndpointInstanceName);

        if ( dnsServiceEndpointInstanceInfo == null )
        {
            // 指定されたサービスインスタンスはDB上に存在しない.
            String msg = String.format("Specified DNSServiceEndpointInstance is not exists. userName=%s, dnsServiceEndpointInstanceName=%s", userName, dnsServiceEndpointInstanceName );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        Long dnsServiceEndpointInstanceID = dnsServiceEndpointInstanceInfo.getDNSServiceEndpointInstanceID();
        DNSServiceEndpointInstancePropertyInfo dnsServiceEndpointInstancePropertyInfo 
            = dnsServiceEndpointInstancePropertyInfoFactory.createDNSServiceEndpointInstancePropertyInfoObject(dnsServiceEndpointInstanceID, dnsServiceEndpointInstancePropertyKey, dnsServiceEndpointInstancePropertyValue, dnsServiceEndpointInstancePropertyExplain, recordStatus, memo );

        // 既存のプロパティのリストに新規追加エントリを挿入する.
        List<DNSServiceEndpointInstancePropertyInfo> properties = dnsServiceEndpointInstanceInfo.getDNSServiceEndpointInstanceProperties();
        properties.add(dnsServiceEndpointInstancePropertyInfo);
        dnsServiceEndpointInstanceInfo.setDnsServiceEndpointInstanceProperties(properties);


        // 親エントリであるDNSサービスインスタンスごと論理的に更新する.
        DNSServiceEndpointInstanceInfo savedDNSServiceEndpointInstanceInfo = loulanDNSLogicalModelService.saveDNSServiceEndpointInstanceInfo(dnsServiceEndpointInstanceInfo);
        DNSServiceEndpointInstancePropertyInfo savedDNSServiceEndpointInstancePropertyInfo = savedDNSServiceEndpointInstanceInfo.getDNSServiceEndpointInstanceProperty(dnsServiceEndpointInstancePropertyKey);

        return savedDNSServiceEndpointInstancePropertyInfo;
    }


    // DNSサービスエンドポイントインスタンスプロパティ情報 更新
    @PutMapping("/update/dns-service-endpoint-instance-property/{dnsServiceEndpointInstancePropertyID}")
    public DNSServiceEndpointInstancePropertyInfo updateDNSServiceEndpointInstanceProperty( @PathVariable Long dnsServiceEndpointInstancePropertyID,
                                                            @RequestParam(name = "DNSServiceEndpointInstancePropertyKey", required = false) String dnsServiceEndpointInstancePropertyKey,  
                                                            @RequestParam(name = "DNSServiceEndpointInstancePropertyValue", required = false) String dnsServiceEndpointInstancePropertyValue,  
                                                            @RequestParam(name = "DNSServiceEndpointInstancePropertyExplain", required = false) String dnsServiceEndpointInstancePropertyExplain,  
                                                            @RequestParam(name = "RecordStatus", required = false ) Long recordStatus,
                                                            @RequestParam(name = "Memo", required = false) String memo) throws DNSServiceCommonException
    {

        DNSServiceEndpointInstancePropertyInfo  dnsServiceEndpointInstancePropertyInfo = getDNSServiceEndpointInstanceProperty(dnsServiceEndpointInstancePropertyID);
        if ( dnsServiceEndpointInstancePropertyInfo == null )
        {
            // DB上には指定されたプロパティエントリは存在しない.
            String msg = String.format("Failed to update DNSServiceEndpointInstanceProperties record. Specified DNSServiceInstanceProperty is NOT exists. dnsServiceEndpointInstancePropertyID=%d", dnsServiceEndpointInstancePropertyID );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        if ( dnsServiceEndpointInstancePropertyKey != null )
        {
            dnsServiceEndpointInstancePropertyInfo.setDNSServiceEndpointInstancePropertyKey( dnsServiceEndpointInstancePropertyKey );
        }

        if ( dnsServiceEndpointInstancePropertyValue != null )
        {
            dnsServiceEndpointInstancePropertyInfo.setDNSServiceEndpointInstancePropertyValue( dnsServiceEndpointInstancePropertyValue );
        }

        if ( dnsServiceEndpointInstancePropertyExplain != null )
        {
            dnsServiceEndpointInstancePropertyInfo.setDNSServiceEndpointInstancePropertyExplain( dnsServiceEndpointInstancePropertyExplain );
        }

        if ( recordStatus != null )
        {
            dnsServiceEndpointInstancePropertyInfo.setRecordStatus(recordStatus);
        }

        if ( memo != null )
        {
            dnsServiceEndpointInstancePropertyInfo.setMemo(memo);
        }

        ZonedDateTime zonedUpdateDateTime = LoulanDNSUtils.getCurrentZonedDateTime();
        String updateTime = LoulanDNSUtils.toDateTimeString(zonedUpdateDateTime);
        dnsServiceEndpointInstancePropertyInfo.setUpdateDate(updateTime);

        DNSServiceEndpointInstancePropertyInfo savedDNSServiceEndpointInstancePropertyInfo = loulanDNSLogicalDBService.saveDNSServiceEndpointInstancePropertyInfo(dnsServiceEndpointInstancePropertyInfo);
        return savedDNSServiceEndpointInstancePropertyInfo;
    }


    // DNSサービスエンドポイントインスタンスプロパティ情報 更新
    @PutMapping("/update/dns-service-endpoint-instance-property")
    public DNSServiceEndpointInstancePropertyInfo updateDNSServiceEndpointInstanceProperty( @RequestParam(name = "UserName", required = true) String userName,
                                                            @RequestParam(name = "DNSServiceEndpointInstanceName", required = true) String dnsServiceEndpointInstanceName,
                                                            @RequestParam(name = "DNSServiceEndpointInstancePropertyKey", required = false) String dnsServiceEndpointInstancePropertyKey,  
                                                            @RequestParam(name = "DNSServiceEndpointInstancePropertyValue", required = false) String dnsServiceEndpointInstancePropertyValue,  
                                                            @RequestParam(name = "DNSServiceEndpointInstancePropertyExplain", required = false) String dnsServiceEndpointInstancePropertyExplain,  
                                                            @RequestParam(name = "RecordStatus", required = false ) Long recordStatus,
                                                            @RequestParam(name = "Memo", required = false) String memo) throws DNSServiceCommonException
    {

        // DB上に既存のプロパティ情報のエントリがあるかをチェックする.
        DNSServiceEndpointInstancePropertyInfo checkDNSServiceEndpointInstancePropertyInfo = getDNSServiceEndpointInstanceProperty(userName, dnsServiceEndpointInstanceName, dnsServiceEndpointInstancePropertyKey);
        if ( checkDNSServiceEndpointInstancePropertyInfo == null )
        {
            // DB上には指定されたレコードは存在しない.
            // 更新処理はできないので例外をスローする.
            String msg = String.format("Failed to update DNSServiceEndpointInstanceProperties record. Specified DNSServiceEndpointInstanceProperty is NOT exists. userName=%s, dnsServiceEndpointInstanceName=%s, dnsServiceEndpointInstancePropertyKey=%s", userName, dnsServiceEndpointInstanceName, dnsServiceEndpointInstancePropertyKey );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        // 更新処理を行うべきレコードのID値を取得したので、本ID値を主キーとして更新処理を行う.
        Long dnsServiceEndpointInstancePropertyID = checkDNSServiceEndpointInstancePropertyInfo.getDNSServiceEndpointInstancePropertyID();
        DNSServiceEndpointInstancePropertyInfo savedDNSServiceEndpointInstancePropertyInfo = updateDNSServiceEndpointInstanceProperty(dnsServiceEndpointInstancePropertyID, dnsServiceEndpointInstancePropertyKey, dnsServiceEndpointInstancePropertyValue, dnsServiceEndpointInstancePropertyExplain, recordStatus, memo);

        return savedDNSServiceEndpointInstancePropertyInfo;
    }



    // DNSサービスエンドポイントインスタンスプロパティ情報 削除
    @DeleteMapping("/delete/dns-service-endpoint-instance-property/{dnsServiceEndpointInstancePropertyID}")
    public void deleteDNSServiceEndpointInstanceProperty( @PathVariable Long dnsServiceEndpointInstancePropertyID ) throws DNSServiceCommonException
    {
        DNSServiceEndpointInstancePropertyInfo info = getDNSServiceEndpointInstanceProperty(dnsServiceEndpointInstancePropertyID);
        if ( info == null )
        {
            // 指定されたプロパティレコードはDB上に存在しない.
            String msg = String.format("Specified DNSServiceEndpointInstanceProperty is not exists. dnsServiceEndpointInstancePropertyID=%d", dnsServiceEndpointInstancePropertyID  );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        loulanDNSDBService.deleteDNSServiceEndpointInstanceProperties(dnsServiceEndpointInstancePropertyID);
    }


    // DNSサービスエンドポイントインスタンスプロパティ情報 削除
    @DeleteMapping("/delete/dns-service-endpoint-instance-property")
    public void deleteDNSServiceEndpointInstanceProperty(   @RequestParam(name = "UserName", required = true) String userName,
                                                    @RequestParam(name = "DNSServiceEndpointInstanceName", required = true) String dnsServiceEndpointInstanceName,
                                                    @RequestParam(name = "DNSServiceEndpointInstancePropertyKey", required = false) String dnsServiceEndpointInstancePropertyKey ) throws  DNSServiceCommonException
    {

        DNSServiceEndpointInstancePropertyInfo info = getDNSServiceEndpointInstanceProperty(userName, dnsServiceEndpointInstanceName, dnsServiceEndpointInstancePropertyKey );
        if ( info == null )
        {
            // 指定されたプロパティレコードはDB上に存在しない.
            String msg = String.format("Specified DNSServiceEndpointInstancePropertyInfo is not exists. userName=%s, dnsServiceEndpointInstanceName=%s, dnsServiceEndpointInstancePropertyKey=%s", userName, dnsServiceEndpointInstanceName, dnsServiceEndpointInstancePropertyKey );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        // レコードのID値を基に削除処理を行う.
        Long dnsServiceEndpointInstancePropertyID = info.getDNSServiceEndpointInstancePropertyID();
        deleteDNSServiceEndpointInstanceProperty(dnsServiceEndpointInstancePropertyID);
    }


    // DNSサービスエンドポイントインスタンスプロパティ情報 簡易設定
    @PutMapping("/put/dns-service-endpoint-instance-property/{UserName}/{DNSServiceEndpointInstanceName}/{key}")
    public DNSServiceEndpointInstancePropertyInfo putDNSServiceEndpointInstanceProperty( @PathVariable(name = "UserName") String userName,
                                                            @PathVariable(name = "DNSServiceEndpointInstanceName") String dnsServiceEndpointInstanceName,
                                                            @PathVariable(name = "key") String key,
                                                            @RequestParam(name = "value", required=true) String value ) throws DNSServiceCommonException
    {

        DNSServiceEndpointInstancePropertyInfo savedInfo;

        DNSServiceEndpointInstancePropertyInfo  existInfo = getDNSServiceEndpointInstanceProperty(userName, dnsServiceEndpointInstanceName, key);
        if ( existInfo == null )
        {
            // 既存のプロパティレコードが存在しないので、新規作成する.
            savedInfo = createDNSServiceEndpointInstanceProperty(userName, dnsServiceEndpointInstanceName, key, value, "", (long)LoulanDNSConstants.DB_CONST_VALUE_RECORD_STATUS_ACTIVE, null);
        }
        else
        {
            // 既存レコードの更新処理を行う.
            existInfo.setDNSServiceEndpointInstancePropertyKey( key );
            existInfo.setDNSServiceEndpointInstancePropertyValue( value );

            savedInfo = updateDNSServiceEndpointInstanceProperty(userName, dnsServiceEndpointInstanceName, key, value, existInfo.getDNSServiceEndpointInstancePropertyExplain(), existInfo.recordStatus, existInfo.getMemo() );
        }

        return savedInfo;
    }


}