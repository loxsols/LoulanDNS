package org.loxsols.net.service.dns.loulandns.server.http.spring.service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.loxsols.net.service.dns.loulandns.server.http.spring.repository.*;

import org.loxsols.net.service.dns.loulandns.server.common.constants.LoulanDNSConstants;
import org.loxsols.net.service.dns.loulandns.server.common.LoulanDNSSystemServiceException;

import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;

import org.loxsols.net.service.dns.loulandns.server.http.spring.model.*;


import org.loxsols.net.service.dns.loulandns.server.logical.model.*;


// LoulanDNSの論理モデルサービスクラス.
// DBの各テーブルのレコードを論理的に紐づけて扱う.
@Service
@Transactional
public class LoulanDNSLogicalDBService
{

    @Autowired
    @Qualifier("loulanDNSDBServiceImpl")
    LoulanDNSDBService loulanDNSDBService;


    // DBからユーザー情報の論理モデルを取得する.
    public UserInfo getUserInfo(String userName) throws LoulanDNSSystemServiceException
    {

        User user = loulanDNSDBService.getUser(userName);
        if ( user == null )
        {
            return null;
        }

        long userID = user.getUserID();
        UserInfo userInfo = getUserInfo(userID);

        return userInfo;
    }

    // DBからユーザー情報の論理モデルを取得する.
    public UserInfo getUserInfo(long userID) throws LoulanDNSSystemServiceException
    {

        User user = loulanDNSDBService.getUser(userID);
        if ( user == null )
        {
            return null;
        }

        UserInfo userInfo = toUserInfo(user);

        List<UserGroupMapping> userGroupMappingList = loulanDNSDBService.getUserGroupMappingListByUserID( userInfo.getUserID() );

        List<UserGroup> userGroupList = new ArrayList<UserGroup>();
        for( UserGroupMapping userGroupMapping : userGroupMappingList)
        {
            UserGroup userGroup = loulanDNSDBService.getUserGroup( userGroupMapping.getUserGroupID() ) ;
            userGroupList.add( userGroup );
        }



        List<UserGroupInfo> userGroupInfoList = new ArrayList<UserGroupInfo>();
        for(UserGroup userGroup : userGroupList )
        {
            UserGroupInfo userGroupInfo = toUserGroupInfo(userGroup);

            UserGroupPrivilege userGroupPrivilege = loulanDNSDBService.getUserGroupPrivilege( userGroupInfo.getUserGroupID() );
            UserGroupPrivilegeInfo userGroupPrivilegeInfo = toUserGroupPrivilegeInfo( userGroupPrivilege );

            userGroupInfo.addUserGroupPrivilegeInfo( userGroupPrivilegeInfo );

            userGroupInfoList.add( userGroupInfo );
        }



        // DNSサービスインスタンスのリストを設定する.
        List<DNSServiceInstanceInfo> dnsServiceInstanceInfoList = new ArrayList<DNSServiceInstanceInfo>();
        List<DNSServiceInstance> dnsServiceInstanceList = loulanDNSDBService.getDNSServiceInstanceListByUserID( userInfo.getUserID() );
        for( DNSServiceInstance dnsServiceInstance : dnsServiceInstanceList)
        {
            DNSServiceInstanceInfo info = getDNSServiceInstanceInfo( dnsServiceInstance.getDNSServiceInstanceID() );
            dnsServiceInstanceInfoList.add( info );
        }
        userInfo.setDNSServiceInstanceInfoList( dnsServiceInstanceInfoList );


        // DNSリゾルバのリストを設定する.
        List<DNSResolverInstanceInfo> dnsResolverInfoList = new ArrayList<DNSResolverInstanceInfo>();
        List<DNSResolverInstance> dnsResolverInstanceList = loulanDNSDBService.getDNSResolverInstanceByUserID( userInfo.getUserID() );
        for( DNSResolverInstance dnsResolverInstance : dnsResolverInstanceList)
        {
            DNSResolverInstanceInfo info = getDNSResolverInstanceInfo( dnsResolverInstance.getDnsResolverInstanceID() );
            dnsResolverInfoList.add( info );
        }
        userInfo.setDnsResolverInstanceInfoList( dnsResolverInfoList );


        return userInfo;
    }


    // DBからユーザー情報の論理モデルの一覧を取得する.
    public List<UserInfo> getUserInfoList() throws LoulanDNSSystemServiceException
    {
        List<User> userList = loulanDNSDBService.getUserList();

        List<UserInfo> userInfoList = new ArrayList<UserInfo>();
        for( User user : userList )
        {
            UserInfo userInfo = getUserInfo(user.getUserID());
            if ( userInfo == null )
            {
                continue;
            }

            userInfoList.add( userInfo );
        }

        return userInfoList;
    }


    // DBからDNSサービスインスタンス情報を取得する.
    public DNSServiceInstanceInfo getDNSServiceInstanceInfo(long dnsServiceInstanceID) throws LoulanDNSSystemServiceException
    {
        DNSServiceInstance dnsServiceInstance = loulanDNSDBService.getDNSServiceInstance(dnsServiceInstanceID);

        if ( dnsServiceInstance == null )
        {
            // 指定されたIDのDNSServiceインスタンス情報は存在しない.
            return null;
        }

        DNSServiceInstanceInfo dnsServiceInstanceInfo = toDNSServiceInstanceInfo( dnsServiceInstance );


        // DNSサービスインスタンスのプロパティ情報を取得してDNSサービスインスタンス情報クラスに設定する.
        List<DNSServiceInstancePropertyInfo> propInfoList = getDNSServiceInstancePropertyInfoListByDNSServiceInstanceID( dnsServiceInstanceID );
        dnsServiceInstanceInfo.setDnsServiceInstanceProperties(propInfoList);
        


        // DNSリゾルバを設定する.
        DNSResolverInstanceInfo dnsResolverInfo = getDNSResolverInstanceInfo(  dnsServiceInstanceInfo.getDNSResolverInstanceID() );
        dnsServiceInstanceInfo.setDNSResolverInstanceInfo( dnsResolverInfo );


        // DNSエンドポイントのリストを設定する.
        List<DNSServiceEndpointInstanceInfo> dnsServiceEndpointInstanceInfoList = new ArrayList<DNSServiceEndpointInstanceInfo>();

        List<DNSServiceEndpointInstance> dnsServiceEndpointInstanceList = loulanDNSDBService.getDNSServiceEndpointInstanceListByDNSServiceInstanceID(dnsServiceInstanceID);
        for( DNSServiceEndpointInstance dnsServiceEndpointInstance : dnsServiceEndpointInstanceList )
        {
            DNSServiceEndpointInstanceInfo info = toDNSServiceEndpointInstanceInfo(dnsServiceEndpointInstance);
            dnsServiceEndpointInstanceInfoList.add( info );
        }
        dnsServiceInstanceInfo.setDNSServiceEndpointInstanceInfoList(dnsServiceEndpointInstanceInfoList);


        return dnsServiceInstanceInfo;
    }


    
    /**
     * DNSサービスインスタンスプロパティ情報を取得する.
     */
    public DNSServiceInstancePropertyInfo getDNSServiceInstancePropertyInfo(long dnsServiceInstancePropertyID) throws LoulanDNSSystemServiceException
    {

        DNSServiceInstanceProperties dnsServiceInstancePropeties = loulanDNSDBService.getDNSServiceInstanceProperties(dnsServiceInstancePropertyID);

        DNSServiceInstancePropertyInfo dnsServiceInstancePropertiesInfo = toDNSServiceInstancePropertyInfo( dnsServiceInstancePropeties );

        return dnsServiceInstancePropertiesInfo;
    }


    /**
     * DNSサービスインスタンスプロパティ情報を、プロパティが所属するDNSサービスインスタンスのIDをもとに取得する.
     */
    public List<DNSServiceInstancePropertyInfo> getDNSServiceInstancePropertyInfoListByDNSServiceInstanceID(long dnsServiceInstanceID) throws LoulanDNSSystemServiceException
    {
        List<DNSServiceInstancePropertyInfo> list = new ArrayList<DNSServiceInstancePropertyInfo>();

        // 指定されたDNSサービスインスタンスが存在するかをチェックする.
        DNSServiceInstance dnsServiceInstance = loulanDNSDBService.getDNSServiceInstance(dnsServiceInstanceID);
        if ( dnsServiceInstance == null )
        {
            // 指定されたDNSサービスインスタンスは存在しない.
            String msg = String.format("Specified DNSServiceInstance is not found. dnsServiceInstanceID=%d", dnsServiceInstanceID );
            LoulanDNSSystemServiceException exception = new LoulanDNSSystemServiceException(msg);
            throw exception;
        }

        List<DNSServiceInstanceProperties> properties = loulanDNSDBService.getDNSServiceInstancePropertiesListByDNSServiceInstanceID(dnsServiceInstanceID);
        for( DNSServiceInstanceProperties prop : properties )
        {
            DNSServiceInstancePropertyInfo propInfo = toDNSServiceInstancePropertyInfo(prop);
            list.add( propInfo );
        }

        return list;
    }



    // DBからDNSリゾルバ情報を取得する.
    public DNSResolverInstanceInfo getDNSResolverInstanceInfo(long dnsResolverInstanceID) throws LoulanDNSSystemServiceException
    {

        // DNSリゾルバ情報を取得する.
        DNSResolverInstance dnsResolverInstnace = loulanDNSDBService.getDNSResolverInstanceByDNSResolverInstanceID( dnsResolverInstanceID  );
        
        DNSResolverInstanceInfo dnsResolverInstanceInfo = toDNSResolverInstanceInfo(dnsResolverInstnace);

        // DNSリゾルバプロパティのリストを設定する.
        List<DNSResolverPropertiesInfo> dnsResolverPropertiesInfoList = new ArrayList<DNSResolverPropertiesInfo>();
        List<DNSResolverInstanceProperties> dndDnsResolverInstancePropertiesList = loulanDNSDBService.getDNSResolverInstancePropertiesListByDNSResolverInstanceID( dnsResolverInstanceInfo.getDNSResolverInstanceID() );
        for( DNSResolverInstanceProperties dnsResolverInstanceProperties : dndDnsResolverInstancePropertiesList )
        {
            DNSResolverPropertiesInfo dnsResolverPropertiesInfo = toDNSResolverPropertiesInfo( dnsResolverInstanceProperties );
            dnsResolverPropertiesInfoList.add( dnsResolverPropertiesInfo );
        }
        dnsResolverInstanceInfo.setDNSResolverPropertiesInfoList(dnsResolverPropertiesInfoList);

        return dnsResolverInstanceInfo;
    }


    // DBからシステムプロパティ情報を取得する.
    public List<SystemPropertiesInfo> getSystemPropertiesInfoList() throws LoulanDNSSystemServiceException
    {
        List<SystemProperties> systemPropertiesList = loulanDNSDBService.getSystemPropertiesList();
        List<SystemPropertiesInfo> systemPropertiesInfoList = toSystemPropertiesInfoList(systemPropertiesList);
        return systemPropertiesInfoList;

    }


    // DBからシステムプロパティ情報のうち、プロパティキーに対応するデータのみを取得する.
    public List<SystemPropertiesInfo> getSystemPropertiesInfoListBySystemPropertyKey(String systemPropertyKey) throws LoulanDNSSystemServiceException
    {
        List<SystemProperties> systemPropertiesList = loulanDNSDBService.getSystemPropertiesListBySystemPropertyKey(systemPropertyKey);
        List<SystemPropertiesInfo> systemPropertiesInfoList = toSystemPropertiesInfoList(systemPropertiesList);
        return systemPropertiesInfoList;
    }



    /**
     * DBから共通ログ情報を取得し、併せて共通ログ情報のプロパティ情報のレコードも取得してセットする.
     * 
     * @param commonSystemLogID
     * @return
     * @throws LoulanDNSSystemServiceException
     */
    public CommonSystemLogInfo getCommonSystemLogInfoByID(long commonSystemLogID)throws LoulanDNSSystemServiceException
    {
        CommonSystemLogInfo commonSystemLogInfo;

        List<CommonSystemLog> commonSystemLogList = loulanDNSDBService.getCommonSystemLogListByID(commonSystemLogID);
        if ( commonSystemLogList.size() == 0 )
        {
            return null;
        }

        commonSystemLogInfo = toCommonSystemLogInfo( commonSystemLogList.get(0) );

        // プロパティ情報を取得して設定する.
        List<CommonSystemLogProperties> commonSystemLogPropertiesList = loulanDNSDBService.getCommonSystemLogPropertiesByCommonSystemLogID(commonSystemLogInfo.getCommonSystemLogID());
        List<CommonSystemLogPropertiesInfo> commonSystemLogPropertiesInfoList = toCommonSystemLogPropertiesInfoList(commonSystemLogPropertiesList);
        commonSystemLogInfo.setCommonSystemLogPropertiesInfoList(commonSystemLogPropertiesInfoList);

        return commonSystemLogInfo;
    }


    /**
     * DBから共通ログ情報のうち、指定したタグをもつレコードのみを取得する.
     * 
     * @param tag
     * @return
     * @throws LoulanDNSSystemServiceException
     */
    public List<CommonSystemLogInfo> getCommonSystemLogInfoListByTag(String tag) throws LoulanDNSSystemServiceException
    {
        List<CommonSystemLog> commonSystemLogList = loulanDNSDBService.getCommonSystemLogListByTag(tag);
        List<CommonSystemLogInfo> tmpInfoList = toCommonSystemLogInfoList(commonSystemLogList);

        // プロパティ情報も含めた論理クラスとして完成されるために再度DBからデータを取得しなおす.
        List<CommonSystemLogInfo> commonSystemLogInfoList = new ArrayList<CommonSystemLogInfo>();
        for(CommonSystemLogInfo tmpInfo : tmpInfoList)
        {
            CommonSystemLogInfo info = getCommonSystemLogInfoByID( tmpInfo.getCommonSystemLogID() );
            commonSystemLogInfoList.add(info);
        }

        return commonSystemLogInfoList;
    }

    /**
     * DBから共通ログ情報のうち、指定したタグを持ち、かつ指定したプロパティ情報が紐づいたレコードのみを取得する.
     * 
     * @param tag
     * @param propertyKey
     * @param propertyValue
     * @return
     * @throws LoulanDNSSystemServiceException
     */
    public List<CommonSystemLogInfo> getCommonSystemLogInfoListByTagAndProperty(String tag, String propertyKey, String propertyValue) throws LoulanDNSSystemServiceException
    {
        List<CommonSystemLog> commonSystemLogList = loulanDNSDBService.getCommonSystemLogListByTagAndProperty(tag, propertyKey, propertyValue);
        List<CommonSystemLogInfo> tmpInfoList = toCommonSystemLogInfoList(commonSystemLogList);

        // プロパティ情報も含めた論理クラスとして完成されるために再度DBからデータを取得しなおす.
        List<CommonSystemLogInfo> commonSystemLogInfoList = new ArrayList<CommonSystemLogInfo>();
        for(CommonSystemLogInfo tmpInfo : tmpInfoList)
        {
            CommonSystemLogInfo info = getCommonSystemLogInfoByID( tmpInfo.getCommonSystemLogID() );
            commonSystemLogInfoList.add(info);
        }

        return commonSystemLogInfoList;
    }


    public List<CommonSystemLogPropertiesInfo> getCommonSystemLogPropertiesInfoListByCommonSystemLogID(long commonSystemLogID) throws LoulanDNSSystemServiceException
    {
        List<CommonSystemLogProperties> commonSystemLogPropertiesList = loulanDNSDBService.getCommonSystemLogPropertiesByCommonSystemLogID(commonSystemLogID);
        List<CommonSystemLogPropertiesInfo> commonSystemLogPropertiesInfoList = toCommonSystemLogPropertiesInfoList(commonSystemLogPropertiesList);

        return commonSystemLogPropertiesInfoList;   
    }


    /**
     * CommonSystemLogテーブルとCommonSystemLogPropertiesテーブルを一括して新規追加/更新する.
     * 
     */
    public CommonSystemLogInfo saveCommonSystemLogInfo(CommonSystemLogInfo info) throws LoulanDNSSystemServiceException
    {
        CommonSystemLog model = toCommonSystemLog(info);

        CommonSystemLog savedModel;
        if (info.getCommonSystemLogID() == null )
        {
            // 新規追加を行う.
            savedModel = loulanDNSDBService.createCommonSystemLog(model);
        }
        else
        {
            // 更新処理を行う.
            savedModel = loulanDNSDBService.updateCommonSystemLog(model);
        }

        CommonSystemLogInfo savedInfo = toCommonSystemLogInfo(savedModel);
        
        // DB側で採番済みのログIDを取得する.
        long commonSystemLogID = savedInfo.getCommonSystemLogID();

        // プロパティの処理
        List<CommonSystemLogPropertiesInfo> propertiesList = info.getCommonSystemLogPropertiesInfoList();
        if ( propertiesList == null || propertiesList.isEmpty() )
        {
            // プロパティリストは空なのでこれ以上の処理はない.
            return savedInfo;
        }

        
        // プロパティ情報の登録.
        for( int i=0; i < savedInfo.getCommonSystemLogPropertiesInfoList().size(); i++ )
        {
            var propertiesInfo = savedInfo.getCommonSystemLogPropertiesInfoList().get(i);

            // DB側で採番済みのログIDをプロパティにもセットする.
            propertiesInfo.setCommonSystemLogID( commonSystemLogID );
            
            // DBにログプロパティを登録する.
            CommonSystemLogPropertiesInfo savedPropertiesInfo = saveCommonSystemLogPropertiesInfo(propertiesInfo);

            // 論理モデルにも反映する.
            savedInfo.getCommonSystemLogPropertiesInfoList().set(i, savedPropertiesInfo);
        }


        // 論理モデルに存在しないプロパティをDBから削除
        List<CommonSystemLogPropertiesInfo> dbRegisteredList = getCommonSystemLogPropertiesInfoListByCommonSystemLogID(commonSystemLogID);

        for(var r : dbRegisteredList)
        {
            boolean isModeled = false;
            for( var m : savedInfo.getCommonSystemLogPropertiesInfoList() )
            {
                if ( r.getCommonSystemLogPropertyID() == m.getCommonSystemLogPropertyID() )
                {
                    isModeled = true;
                    break;
                }
            }

            if ( isModeled == false)
            {
                // DBにのみ存在するプロパティ情報なので削除する.
                loulanDNSDBService.deleteCommonSystemLogProperties( r.getCommonSystemLogPropertyID() );
            }

        }


        return savedInfo;
    }



    /**
     * CommonSystemLogProperties情報を登録する.
     */
    public CommonSystemLogPropertiesInfo saveCommonSystemLogPropertiesInfo(CommonSystemLogPropertiesInfo info) throws LoulanDNSSystemServiceException
    {
        CommonSystemLogProperties model = toCommonSystemLogProperties(info);

        CommonSystemLogProperties saved;
        if ( info.getCommonSystemLogPropertyID() == null )
        {
            // 新規追加
            saved = loulanDNSDBService.createCommonSystemLogProperties(model);
        }
        else
        {
            // 更新
            saved = loulanDNSDBService.updateCommonSystemLogProperties(model);
        }

        CommonSystemLogPropertiesInfo savedInfo = toCommonSystemLogPropertiesInfo(saved);
        return savedInfo;
    }


    /**
     * CommonSystemLogProperties情報を登録する.
     */
    public void deleteCommonSystemLogPropertiesInfo(long CommonSystemLogPropertyID) throws LoulanDNSSystemServiceException
    {
        loulanDNSDBService.deleteCommonSystemLogProperties(CommonSystemLogPropertyID);
    }




    // DBにユーザー情報を設定する.(複数のテーブルを同時に更新する.)
    public UserInfo saveUserInfo(UserInfo userInfo) throws LoulanDNSSystemServiceException
    {
        Long userID = userInfo.getUserID();

        User user = toUser(userInfo);
        if ( isExistingUser(userID) == false )
        {
            // DB上に既存ユーザーが存在しないので新規ユーザーとして登録する.
            user = loulanDNSDBService.createUser(user);
            userID = user.getUserID();
        }
        else
        {
            // 既存ユーザーのレコードを更新する. 
            user = loulanDNSDBService.updateUser(user);
            userID = user.getUserID();
        }


        // 本ユーザーに紐づくグループグループマッピング情報を新規追加または更新する.
        List<UserGroupMappingInfo> userGroupMappingInfoList = userInfo.getUserGroupMappingInfoList();
        if ( userGroupMappingInfoList != null )
        {
            for( UserGroupMappingInfo userGroupMappingInfo : userGroupMappingInfoList )
            {
                long uid = user.getUserID();
                long gid = userGroupMappingInfo.getUserGroupID();

                UserGroupMapping userGroupMapping = toUserGroupMapping( userGroupMappingInfo );

                // 本ユーザーに紐づくグループ情報に変動がないかを確認する.
                if ( loulanDNSDBService.isBelongingToUserGroup(uid, gid) == false )
                {
                    // 既存のグループマッピング情報がDBに存在しないので新規追加する.
                    loulanDNSDBService.createUserGroupMapping(userGroupMapping);
                }
                else
                {
                    // 既存のグループマッピング情報がDBに存在するのでDBを更新する.
                    loulanDNSDBService.updateUserGroupMapping(userGroupMapping);
                }
            }
        }

        // DB上のユーザーマッピング情報のうち、指定されたモデルクラスに存在しないレコードを削除する.
        List<UserGroupMapping> userGroupMappingListOnDB =  loulanDNSDBService.getUserGroupMappingListByUserID(user.getUserID());
        if ( userGroupMappingListOnDB != null )
        {
            for( UserGroupMapping mappingOnDB : userGroupMappingListOnDB )
            {
                long gid = mappingOnDB.getUserGroupID();
                if( userInfo.isBelongingToUserGroup(gid) == false )
                {
                    // メモリ上のユーザー情報モデルクラスでは未所属のユーザーグループ情報がDB上に存在するので、DBから当該レコードを削除する.
                    loulanDNSDBService.deleteUserGroupMapping(userID, gid);
                }
            }
        }
        

        // DoHインスタンス情報の更新.
        List<DNSServiceInstanceInfo> dnsServiceInstanceList = userInfo.getDNSServiceInstanceInfoList();
        if ( dnsServiceInstanceList != null )
        {
            for( DNSServiceInstanceInfo dnsServiceInstanceInfo : dnsServiceInstanceList )
            {
                saveDNSServiceInstanceInfo( dnsServiceInstanceInfo );
            }
        }


        // DB上にのみ存在するDoHインスタンス情報を削除する.
        List<DNSServiceInstance> dnsServiceInstanceListOnDB = loulanDNSDBService.getDNSServiceInstanceListByUserID( userID );
        if ( dnsServiceInstanceListOnDB != null )
        {
            for( DNSServiceInstance dnsServiceInstanceOnDB : dnsServiceInstanceListOnDB )
            {
                long dnsServiceInstanceID = dnsServiceInstanceOnDB.getDNSServiceInstanceID();
                if ( userInfo.hasDNSServiceInstance( dnsServiceInstanceID ) == false )
                {
                    // このDoHインスタンス情報はDB上にのみ存在し、メモリ上のユーザー情報モデルクラスでは保持していない.
                    // 当該DoHインスタンス情報をDB上から削除する.
                    loulanDNSDBService.deleteDNSServiceInstance(dnsServiceInstanceID);
                }
            }
        }

        UserInfo savedUserInfo = getUserInfo(userID);
        return savedUserInfo;
    }


    // DBにDoHインスタンス情報を設定する.(複数のテーブルを同時に更新する.)
    public DNSServiceInstanceInfo saveDNSServiceInstanceInfo(DNSServiceInstanceInfo dnsServiceInstanceInfo) throws LoulanDNSSystemServiceException
    {

        // DNSResolverInstanceInfoオブジェクトをDBに書き込む.
        DNSResolverInstanceInfo dnsResolverInstanceInfo = dnsServiceInstanceInfo.getDnsResolverInstanceInfo();
        DNSResolverInstanceInfo  savedDNSResolverInfo = saveDNSResolverInstanceInfo(dnsResolverInstanceInfo);

        // DB更新済みのDNSResolveInfoオブジェクトからID値を取得する.
        long dnsResolverInstanceID = savedDNSResolverInfo.getDNSResolverInstanceID();
        dnsServiceInstanceInfo.setDNSResolverInstanceID( dnsResolverInstanceID );
        dnsServiceInstanceInfo.setDNSResolverInstanceInfo(savedDNSResolverInfo);


        DNSServiceInstance savedDNSServiceInstance = null;

        // 最初にDOH_INSTANCEテーブルを更新する.
        if ( loulanDNSDBService.getDNSServiceInstance( dnsServiceInstanceInfo.getDNSServiceInstanceID() ) == null)
        {
            // 指定されたIDのDoHインスタンス情報は存在しないので新規作成する.
            savedDNSServiceInstance = loulanDNSDBService.createDNSServiceInstance(  toDNSServiceInstance( dnsServiceInstanceInfo ) );
        }
        else
        {
            savedDNSServiceInstance = loulanDNSDBService.updateDoHServerInstance( toDNSServiceInstance( dnsServiceInstanceInfo ) );
        }

        // DB更新後にDNS_SERVICE_INSTANCEテーブルのID値を取得する.
        long dnsServiceInstanceID = savedDNSServiceInstance.getDNSServiceInstanceID();

        DNSServiceInstanceInfo savedDNSServiceInstanceInfo = getDNSServiceInstanceInfo(dnsServiceInstanceID);
        return savedDNSServiceInstanceInfo;
    }


    // DBにDNSリゾルバ情報を設定する.(複数のテーブルを同時に更新する.)
    public DNSResolverInstanceInfo saveDNSResolverInstanceInfo(DNSResolverInstanceInfo dnsResolverInstanceInfo) throws LoulanDNSSystemServiceException
    {
        // 最初にDNS_RESOLVER_INSTANCEテーブルを更新する.
        Long dnsResolverInstanceID = dnsResolverInstanceInfo.getDNSResolverInstanceID();

        DNSResolverInstance savedDNSResolverInstance = null;
        if ( getDNSResolverInstanceInfo( dnsResolverInstanceID ) == null )
        {
            // 新規作成.
            dnsResolverInstanceInfo.setDnsResolverInstanceID( null );

            String currentDate = LoulanDNSUtils.getCurrentDateTimeString();
            dnsResolverInstanceInfo.setUpdateDate(currentDate);
            dnsResolverInstanceInfo.setCreateDate(currentDate);

            savedDNSResolverInstance = loulanDNSDBService.createDNSResolverInstance( toDNSResolverInstance( dnsResolverInstanceInfo ) );
        }
        else
        {
            // 更新.
            String currentDate = LoulanDNSUtils.getCurrentDateTimeString();
            dnsResolverInstanceInfo.setUpdateDate(currentDate);

            savedDNSResolverInstance = loulanDNSDBService.updateDNSResolverInstance( toDNSResolverInstance( dnsResolverInstanceInfo ) );
        }

        // DB書き込み後のDNS_RESOLVERテーブルのID値を取得する.
        dnsResolverInstanceID = savedDNSResolverInstance.getDnsResolverInstanceID();


        DNSResolverInstanceInfo savedDNSResolverInstanceInfo = getDNSResolverInstanceInfo(dnsResolverInstanceID);

        // DNS_RESOLVER_PROPERTIESテーブルを更新する.
        for( DNSResolverPropertiesInfo dnsResolverPropertiesInfo : savedDNSResolverInstanceInfo.getDNSResolverPropertiesInfoList() )
        {
            // DDNS_RESOLVERテーブルのID値をDNS_RESOLVER_PROPETIESテーブルのモデルオブジェクトに設定する.
            dnsResolverPropertiesInfo.setDnsResolverID(dnsResolverInstanceID);

            // DNS_RESOLVER_PROPETIESテーブルを更新する.
            saveDNSResolverPropertiesInfo( dnsResolverPropertiesInfo );
        }


        // DB上にのみ存在するDNSリゾルバのプロパティ情報を削除する.
        List<DNSResolverInstanceProperties> dnsResolverPropertiesListOnDB = loulanDNSDBService.getDNSResolverInstancePropertiesListByDNSResolverInstanceID( dnsResolverInstanceID );
        for( DNSResolverInstanceProperties dnsResolverProperties : dnsResolverPropertiesListOnDB )
        {
            long propIDonDB = dnsResolverProperties.getDnsResolverInstancePropertyID();
            if ( dnsResolverInstanceInfo.hasProperties( propIDonDB ) == false )
            {
                // このDNSリゾルバのプロパティ情報はDB上にのみ存在し、メモリ上のユーザー情報モデルクラスでは保持していない.
                // 当該DNSリゾルバのプロパティ情報をDB上から削除する.
                loulanDNSDBService.deleteDNSResolverInstanceProperties( propIDonDB );
            }
        }


        return savedDNSResolverInstanceInfo;
    }


    public DNSResolverPropertiesInfo getDNSResolverPropertiesInfo(long dnsResolverInstancePropertiesID) throws LoulanDNSSystemServiceException
    {
        DNSResolverInstanceProperties dnsResolverInstanceProperties = loulanDNSDBService.getDNSResolverInstanceProperties(dnsResolverInstancePropertiesID);

        if (dnsResolverInstanceProperties == null )
        {
            return null;
        }

        DNSResolverPropertiesInfo dnsResolverPropertiesInfo = toDNSResolverPropertiesInfo( dnsResolverInstanceProperties );

        return dnsResolverPropertiesInfo;
    }


    // DBにDNSリゾルバプロパティ情報を設定する.
    public DNSResolverPropertiesInfo saveDNSResolverPropertiesInfo(DNSResolverPropertiesInfo dnsResolverPropertiesInfo) throws LoulanDNSSystemServiceException
    {
        Long dnsResolverPropertiesID = dnsResolverPropertiesInfo.getDNSResolverInstancePropertyID();

        DNSResolverInstanceProperties savedDNSResolverProperties = null;
        if ( getDNSResolverPropertiesInfo( dnsResolverPropertiesID ) == null )
        {
            // 新規作成.
            dnsResolverPropertiesInfo.setDNSResolverInstancePropertyID( null );

            String currentDate = LoulanDNSUtils.getCurrentDateTimeString();
            dnsResolverPropertiesInfo.setUpdateDate( currentDate );
            dnsResolverPropertiesInfo.setCreateDate( currentDate );
            
            savedDNSResolverProperties = loulanDNSDBService.createDNSResolverInstanceProperties( toDNSResolverInstanceProperties( dnsResolverPropertiesInfo ) );
        }
        else
        {
            // 更新.
            String currentDate = LoulanDNSUtils.getCurrentDateTimeString();
            dnsResolverPropertiesInfo.setUpdateDate( currentDate );

            savedDNSResolverProperties = loulanDNSDBService.updateDNSResolverInstanceProperties( toDNSResolverInstanceProperties( dnsResolverPropertiesInfo )  );
        }

        DNSResolverPropertiesInfo savedDNSResolverPropertiesInfo = toDNSResolverPropertiesInfo(savedDNSResolverProperties);
        return savedDNSResolverPropertiesInfo;
    }


    // ユーザーインスタンス情報をDBから削除する.(複数のテーブルを同時に更新する.)
    public void deleteUserInfo(long userID) throws LoulanDNSSystemServiceException
    {
        // DBから実際にレコードとして存在するユーザー情報を取得する.
        UserInfo userInfo = getUserInfo(userID);

        if ( userInfo == null )
        {
            // 指定されたIDのユーザーは存在しない.
            String msg = String.format("User is not found. UserID=%d", userID );
            LoulanDNSSystemServiceException execption = new LoulanDNSSystemServiceException( msg );
            throw execption;
        }

        // ユーザーグループマッピング情報の削除
        List<UserGroupMappingInfo> userGroupMappingInfoList = userInfo.getUserGroupMappingInfoList();
        if ( userGroupMappingInfoList != null )
        {
            for( UserGroupMappingInfo mappingInfo : userGroupMappingInfoList )
            {
                long uid = mappingInfo.getUserID();
                long gid = mappingInfo.getUserGroupID();
                loulanDNSDBService.deleteUserGroupMapping(uid, gid);
            }
        }


        // DNSリゾルバ情報の削除.
        List<DNSResolverInstanceInfo> dnsResolverInstanceInfoList = userInfo.getDnsResolverInstanceInfoList();
        if ( dnsResolverInstanceInfoList != null )
        {
            for( DNSResolverInstanceInfo dnsResolverInstanceInfo : dnsResolverInstanceInfoList )
            {
                deleteDNSResolverInstanceInfo(dnsResolverInstanceInfo);
            }
        }


        // DoHインスタンス情報の削除.
        List<DNSServiceInstanceInfo> dnsServiceInstanceInfoList = userInfo.getDNSServiceInstanceInfoList();
        if ( dnsServiceInstanceInfoList != null )
        {
            for( DNSServiceInstanceInfo dnsServiceInstanceInfo : dnsServiceInstanceInfoList )
            {
                deleteDNSServiceInstanceInfo( dnsServiceInstanceInfo );
            }
        }



        // ユーザー情報の削除.
        loulanDNSDBService.deleteUser(userID);

    }


    // DNSサービスインスタンス情報をDBから削除する.
    // ※ DNSサービスインスタンスに紐づくDNSリゾルバ情報は連動して削除しない.(DNSリゾルバ情報は複数のDNSサービスインスタンスから参照されるため.))
    public void deleteDNSServiceInstanceInfo(DNSServiceInstanceInfo dnsServiceInstanceInfo) throws LoulanDNSSystemServiceException
    {
        long dnsServiceInstanceID = dnsServiceInstanceInfo.getDNSServiceInstanceID();

        // DOH_INSTANCEテーブルからレコードを削除する.
        deleteDNSServiceInstanceInfo(dnsServiceInstanceID );
    }

    // DNSサービスインスタンス情報をDBから削除する.
    // ※ DNSサービスインスタンスに紐づくDNSリゾルバ情報は連動して削除しない.(DNSリゾルバ情報は複数のDNSサービスインスタンスから参照されるため.))
    public void deleteDNSServiceInstanceInfo(long dnsServiceInstanceID ) throws LoulanDNSSystemServiceException
    {
        if ( getDNSServiceInstanceInfo(dnsServiceInstanceID) == null )
        {
            // 指定されたDNSサービスインスタンスは存在しない.
            String msg = String.format("DNSServiceInstance is not found. dnsServiceInstanceID=%d", dnsServiceInstanceID );
            LoulanDNSSystemServiceException execption = new LoulanDNSSystemServiceException( msg );
            throw execption;
        }

        // DOH_INSTANCEテーブルからレコードを削除する.
        loulanDNSDBService.deleteDNSServiceInstance( dnsServiceInstanceID );
    }


    // DNSリゾルバ情報をDBから削除する.(複数のテーブルを同時に更新する.)
    public void deleteDNSResolverInstanceInfo(long dnsResolverInstanceID ) throws LoulanDNSSystemServiceException
    {
        DNSResolverInstanceInfo dnsResolverInstanceInfo = getDNSResolverInstanceInfo(dnsResolverInstanceID);

        if ( dnsResolverInstanceInfo == null )
        {
            // 指定されたDNSリゾルバは存在しない.
            String msg = String.format("DNSResolverInstance is not found. DNSResolverInstanceID=%d", dnsResolverInstanceID );
            LoulanDNSSystemServiceException execption = new LoulanDNSSystemServiceException( msg );
            throw execption;
        }

        deleteDNSResolverInstanceInfo(dnsResolverInstanceInfo);
    }


    // DNSリゾルバ情報をDBから削除する.(複数のテーブルを同時に更新する.)
    public void deleteDNSResolverInstanceInfo(DNSResolverInstanceInfo dnsResolverInstanceInfo) throws LoulanDNSSystemServiceException
    {

        // DNS_RESOLVER_PROPERTIESテーブルからレコードを削除する.
        for( DNSResolverPropertiesInfo dnsResolverPropertiesInfo : dnsResolverInstanceInfo.getDNSResolverPropertiesInfoList() )
        {
            loulanDNSDBService.deleteDNSResolverInstanceProperties( dnsResolverPropertiesInfo.getDNSResolverInstancePropertyID() );
        }

        // DNS_RESOLVERテーブルからレコードを削除する.
        loulanDNSDBService.deleteDNSResolverInstance( dnsResolverInstanceInfo.getDNSResolverInstanceID() );
    }



    // DBからDNSサービスエンドポイントのインスタンス情報を取得する.
    public DNSServiceEndpointInstanceInfo getDNSServiceEndpointInstanceInfo(long dnsServiceEndpointInstanceID) throws LoulanDNSSystemServiceException
    {
        DNSServiceEndpointInstance dnsServiceEndpointInstance = loulanDNSDBService.getDNSServiceEndpointInstance(dnsServiceEndpointInstanceID);

        if ( dnsServiceEndpointInstance == null )
        {
            // 指定されたIDのDNSサービスエンドポイントインスタンス情報は存在しない.
            return null;
        }

        DNSServiceEndpointInstanceInfo dnsServiceEndpointInstanceInfo = toDNSServiceEndpointInstanceInfo( dnsServiceEndpointInstance );


        // DNSサービスエンドポイントのインスタンスのプロパティ情報を取得してDNSサービスエンドポイントのインスタンス情報クラスに設定する.
        List<DNSServiceEndpointInstancePropertyInfo> propInfoList = getDNSServiceEndpointInstancePropertyInfoListByDNSServiceEndpointInstanceID( dnsServiceEndpointInstanceID );
        dnsServiceEndpointInstanceInfo.setDnsServiceEndpointInstanceProperties(propInfoList);
        

        return dnsServiceEndpointInstanceInfo;
    }


    /**
     * DNSサービスエンドポイントのインスタンス情報を新規作成、または更新する.
     * 
     * @param dnsServiceEndpointInstanceInfo
     * @return
     * @throws LoulanDNSSystemServiceException
     */
    public DNSServiceEndpointInstanceInfo saveDNSServiceEndpointInstanceInfo(DNSServiceEndpointInstanceInfo dnsServiceEndpointInstanceInfo) throws LoulanDNSSystemServiceException
    {

        // 子レコードのDNSサービスエンドポイントのインスタンスのプロパティ情報を更新する.
        for( DNSServiceEndpointInstancePropertyInfo childItem : dnsServiceEndpointInstanceInfo.getDNSServiceEndpointInstanceProperties() )
        {
            saveDNSServiceEndpointInstancePropertyInfo(childItem);
        }

        // DNSサービスエンドポイントのインスタンス情報を登録する.
        DNSServiceEndpointInstance model =  toDNSServiceEndpointInstance( dnsServiceEndpointInstanceInfo );

        DNSServiceEndpointInstance savedModel = null;
        if ( model.getDnsServiceEndpointInstanceID() == null )
        {
            // 新規登録.
            savedModel = loulanDNSDBService.createDNSServiceEndpointInstance(model);
        }
        else
        {
            // 更新.
            savedModel = loulanDNSDBService.updateDNSServiceEndpointInstance(model);
        }


        long id = savedModel.getDnsServiceEndpointInstanceID();
        DNSServiceEndpointInstanceInfo savedInfo = getDNSServiceEndpointInstanceInfo(id);
        
        return savedInfo;
    }




    /**
     * DBからDNSサービスエンドポイントのインスタンス情報を削除する.
     * なお、連鎖的に下位レコード(プロパティ情報など)も削除する.
     * 
     * @param dnsServiceEndpointInstanceInfo
     * @throws LoulanDNSSystemServiceException
     */
    public void deleteDNSServiceEndpointInstanceInfo(DNSServiceEndpointInstanceInfo dnsServiceEndpointInstanceInfo) throws LoulanDNSSystemServiceException
    {

        // 子レコードのDNSサービスエンドポイントのインスタンスのプロパティ情報を削除する.
        for( DNSServiceEndpointInstancePropertyInfo childItem : dnsServiceEndpointInstanceInfo.getDNSServiceEndpointInstanceProperties() )
        {
            deleteDNSServiceEndpointInstancePropertyInfo(childItem);
        }

        // DNSサービスエンドポイントのインスタンス情報を削除する.
        long id = dnsServiceEndpointInstanceInfo.getDNSServiceEndpointInstanceID();
        loulanDNSDBService.deleteDNSServiceEndpointInstance( id );

        return ;
    }


    /**
     * DNSサービスエンドポイントのIDから、それに紐づくDNSサービスエンドポイントプロパティのリストを返す.
     * 
     * @param dnsServiceEndpointInstanceID
     * @return
     */
    public List<DNSServiceEndpointInstancePropertyInfo> getDNSServiceEndpointInstancePropertyInfoListByDNSServiceEndpointInstanceID( long dnsServiceEndpointInstanceID ) throws LoulanDNSSystemServiceException
    {
        List<DNSServiceEndpointInstancePropertyInfo> itemInfoList = new ArrayList<DNSServiceEndpointInstancePropertyInfo>();

        List<DNSServiceEndpointInstanceProperties> list = loulanDNSDBService.getDNSServiceEndpointInstancePropertiesByDNSServiceEndpointInstanceID(dnsServiceEndpointInstanceID);

        for( DNSServiceEndpointInstanceProperties item : list )
        {
            // 情報クラスはシンプルなgetメソッドを再度呼び出してDBから取得する.
            // (情報クラス独自のセット処理を将来実装するかもしれないため.)
            DNSServiceEndpointInstancePropertyInfo itemInfo = getDNSServiceEndpointInstancePropertyInfo( item.getDnsServiceEndpointInstancePropertyID() );
            itemInfoList.add( itemInfo );
        }

        return itemInfoList;
    }

    /**
     * DNSサービスエンドポイントインスタンスのプロパティ情報を返す.
     * 
     * @param dnsServiceEndpointInstancePropertyID
     * @return
     * @throws LoulanDNSSystemServiceException
     */
    public DNSServiceEndpointInstancePropertyInfo getDNSServiceEndpointInstancePropertyInfo(long dnsServiceEndpointInstancePropertyID ) throws LoulanDNSSystemServiceException
    {
        DNSServiceEndpointInstanceProperties model = loulanDNSDBService.getDNSServiceEndpointInstanceProperties(dnsServiceEndpointInstancePropertyID);
        DNSServiceEndpointInstancePropertyInfo info = toDNSServiceEndpointInstancePropertyInfo(model);

        // 本クラスは特に追加で情報クラスに設定すべきデータはない.
        return info;
    }


    /**
     * DNSサービスエンドポイントインスタンスのプロパティレコードを新規登録、または更新する.
     * 
     * @param dnsServiceEndpointInstancePropertyInfo
     * @return
     * @throws LoulanDNSSystemServiceException
     */
    public DNSServiceEndpointInstancePropertyInfo saveDNSServiceEndpointInstancePropertyInfo( DNSServiceEndpointInstancePropertyInfo dnsServiceEndpointInstancePropertyInfo ) throws LoulanDNSSystemServiceException
    {

        DNSServiceEndpointInstancePropertyInfo svaedInfo;

        if ( dnsServiceEndpointInstancePropertyInfo.getDNSServiceEndpointInstancePropertyID() == null )
        {
            // IDがnullなので新規登録する.
            DNSServiceEndpointInstanceProperties model = toDNSServiceEndpointInstanceProperties(dnsServiceEndpointInstancePropertyInfo);
            DNSServiceEndpointInstanceProperties createdModel = loulanDNSDBService.createDNSServiceEndpointInstanceProperties(model);

            svaedInfo = toDNSServiceEndpointInstancePropertyInfo(createdModel);
        }
        else
        {
            // IDが設定済みなので更新する.
            DNSServiceEndpointInstanceProperties model = toDNSServiceEndpointInstanceProperties(dnsServiceEndpointInstancePropertyInfo);
            DNSServiceEndpointInstanceProperties updatedModel = loulanDNSDBService.updateDNSServiceEndpointInstanceProperties(model);

            svaedInfo = toDNSServiceEndpointInstancePropertyInfo(updatedModel);
        }

        return svaedInfo;
    }


    /**
     * DNSサービスエンドポイントインスタンスのプロパティレコードを削除する.
     * 
     * @param dnsServiceEndpointInstancePropertyInfo
     * @throws LoulanDNSSystemServiceException
     */
    public void deleteDNSServiceEndpointInstancePropertyInfo(DNSServiceEndpointInstancePropertyInfo dnsServiceEndpointInstancePropertyInfo) throws LoulanDNSSystemServiceException
    {
        long id = dnsServiceEndpointInstancePropertyInfo.getDNSServiceEndpointInstancePropertyID();
        loulanDNSDBService.deleteDNSResolverInstanceProperties(id);
        return;
    }








    // 指定したユーザーがDB上に存在するか判定する.
    public boolean isExistingUser(Long userID) throws LoulanDNSSystemServiceException
    {

        if ( userID == null )
        {
            return false;
        }
        
        User user = loulanDNSDBService.getUser(userID);
        if ( user == null )
        {
            return false;
        }

        return true;
    }



    private UserInfo toUserInfo(User user)
    {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserID( user.getUserID() );
        userInfo.setUserName( user.getUserName() );
        userInfo.setUserPassword( user.getUserPassword() );
        userInfo.setRecordStatus( user.getRecordStatus() );
        userInfo.setMemo( user.getMemo() );

        userInfo.setCreateDate( user.getCreateDate() );
        userInfo.setUpdateDate( user.getUpdateDate() );     
     
        return userInfo;
    }

    private UserGroupInfo toUserGroupInfo(UserGroup userGroup)
    {

        UserGroupInfo  userGroupInfo = new UserGroupInfo();

        userGroupInfo.setUserGroupID( userGroup.getUserGroupID() );
        userGroupInfo.setUserGroupName( userGroup.getUserGroupName() );
        userGroupInfo.setRecordStatus( userGroup.getRecordStatus() );
        userGroupInfo.setMemo( userGroup.getMemo() );
        userGroupInfo.setCreateDate( userGroup.getCreateDate() );
        userGroupInfo.setUpdateDate( userGroup.getUpdateDate() );

        return userGroupInfo;
    }

    private UserGroupPrivilegeInfo toUserGroupPrivilegeInfo(UserGroupPrivilege userGroupPrivilege)
    {
        UserGroupPrivilegeInfo  userGroupPrivilegeInfo = new UserGroupPrivilegeInfo();

        userGroupPrivilegeInfo.setUserGroupPrivilegeID( userGroupPrivilege.getUserGroupPrivilegeID() );
        userGroupPrivilegeInfo.setUserGroupID( userGroupPrivilege.getUserGroupID() );
        userGroupPrivilegeInfo.setDnsInfoMaskValue( userGroupPrivilege.getDnsInfoMaskValue() );
        userGroupPrivilegeInfo.setUserInfoMaskValue( userGroupPrivilege.getUserInfoMaskValue() );    
        userGroupPrivilegeInfo.setSystemInfoMaskValue( userGroupPrivilege.getSystemInfoMaskValue() );    

        userGroupPrivilegeInfo.setRecordStatus( userGroupPrivilege.getRecordStatus() );
        userGroupPrivilegeInfo.setMemo( userGroupPrivilege.getMemo() );
        userGroupPrivilegeInfo.setCreateDate( userGroupPrivilege.getCreateDate() );
        userGroupPrivilegeInfo.setUpdateDate( userGroupPrivilege.getUpdateDate() );

        return userGroupPrivilegeInfo;

    }

    private List<UserGroupPrivilegeInfo> toUserGroupPrivilegeInfoList( List<UserGroupPrivilege> userGroupPrivilegeList )
    {
        List<UserGroupPrivilegeInfo> list = new ArrayList<UserGroupPrivilegeInfo>();
        for( UserGroupPrivilege privilege : userGroupPrivilegeList )
        {
            UserGroupPrivilegeInfo info = toUserGroupPrivilegeInfo( privilege );
            list.add( info );
        }

        return list;
    }


    private DNSServiceInstanceInfo toDNSServiceInstanceInfo(DNSServiceInstance dnsServiceInstance)
    {
        DNSServiceInstanceInfo dnsServiceInstanceInfo = new DNSServiceInstanceInfo();

        dnsServiceInstanceInfo.setDNSServiceInstanceID( dnsServiceInstance.getDNSServiceInstanceID() );
        dnsServiceInstanceInfo.setUserID( dnsServiceInstance.getUserID() );
        dnsServiceInstanceInfo.setDNSServiceInstanceName( dnsServiceInstance.getDNSServiceInstanceName() );
        dnsServiceInstanceInfo.setDNSResolverInstanceID( dnsServiceInstance.getDNSResolverInstanceID() );
        dnsServiceInstanceInfo.setDNSServiceInstanceExplain( dnsServiceInstance.getDNSServiceInstanceExplain() );
        dnsServiceInstanceInfo.setRecordStatus( dnsServiceInstance.getRecordStatus() );
        dnsServiceInstanceInfo.setMemo( dnsServiceInstance.getMemo() );
        dnsServiceInstanceInfo.setCreateDate( dnsServiceInstance.getCreateDate() );
        dnsServiceInstanceInfo.setUpdateDate( dnsServiceInstance.getUpdateDate() );

        return dnsServiceInstanceInfo;
    }


    private DNSResolverInstanceInfo toDNSResolverInstanceInfo(DNSResolverInstance dnsResolverInstance)
    {

        DNSResolverInstanceInfo dnsResolverInfo = new DNSResolverInstanceInfo();

        dnsResolverInfo.setDnsResolverInstanceID( dnsResolverInstance.getDnsResolverInstanceID() );
        dnsResolverInfo.setUserID( dnsResolverInstance.getUserID() );
        dnsResolverInfo.setDnsResolverInstanceName( dnsResolverInstance.getDnsResolverInstanceName() );
        dnsResolverInfo.setDnsResolverInstanceExplain( dnsResolverInstance.getDnsResolverInstanceExplain() );
        dnsResolverInfo.setDnsResolverTypeCode( dnsResolverInstance.getDnsResolverTypeCode() );
        dnsResolverInfo.setRecordStatus( dnsResolverInstance.getRecordStatus() );
        dnsResolverInfo.setMemo( dnsResolverInstance.getMemo() );
        dnsResolverInfo.setCreateDate( dnsResolverInstance.getCreateDate() );
        dnsResolverInfo.setUpdateDate( dnsResolverInstance.getUpdateDate() );

        return dnsResolverInfo;
    }


    private DNSResolverPropertiesInfo toDNSResolverPropertiesInfo(DNSResolverInstanceProperties dnsResolverInstanceProperties)
    {

        DNSResolverPropertiesInfo dnsResolverPropertiesInfo = new DNSResolverPropertiesInfo();

        dnsResolverPropertiesInfo.setDNSResolverInstancePropertyID( dnsResolverInstanceProperties.getDnsResolverInstancePropertyID() );
        dnsResolverPropertiesInfo.setDnsResolverID( dnsResolverInstanceProperties.getDnsResolverInstanceID() );
        dnsResolverPropertiesInfo.setDnsResolverPropertyKey( dnsResolverInstanceProperties.getDnsResolverInstancePropertyKey() );
        dnsResolverPropertiesInfo.setDnsResolverPropertyValue( dnsResolverInstanceProperties.getDnsResolverInstancePropertyValue() );
        dnsResolverPropertiesInfo.setDnsResolverPropertyExplain( dnsResolverInstanceProperties.getDnsResolverInstancePropertyExplain() );
        dnsResolverPropertiesInfo.setRecordStatus( dnsResolverInstanceProperties.getRecordStatus() );
        dnsResolverPropertiesInfo.setMemo( dnsResolverInstanceProperties.getMemo() );
        dnsResolverPropertiesInfo.setCreateDate( dnsResolverInstanceProperties.getCreateDate() );
        dnsResolverPropertiesInfo.setUpdateDate( dnsResolverInstanceProperties.getUpdateDate() );

        return dnsResolverPropertiesInfo;
    }


    private SystemPropertiesInfo toSystemPropertiesInfo(SystemProperties systemProperties)
    {
        SystemPropertiesInfo systemPropertiesInfo = new SystemPropertiesInfo();

        systemPropertiesInfo.setSystemPropertyID( systemProperties.getSystemPropertyID() );
        systemPropertiesInfo.setSystemPropertyKey( systemProperties.getSystemPropertyKey() );
        systemPropertiesInfo.setSystemPropertyValue( systemProperties.getSystemPropertyValue() );
        systemPropertiesInfo.setSystemPropertyExplain( systemProperties.getSystemPropertyExplain() );
        systemPropertiesInfo.setRecordStatus( systemProperties.getRecordStatus() );
        systemPropertiesInfo.setMemo( systemProperties.getMemo() );
        systemPropertiesInfo.setCreateDate( systemProperties.getCreateDate() );
        systemPropertiesInfo.setUpdateDate( systemProperties.getUpdateDate() );

        return systemPropertiesInfo;
    }


    private List<SystemPropertiesInfo> toSystemPropertiesInfoList(List<SystemProperties> systemPropertiesList)
    {
        List<SystemPropertiesInfo> systemPropertiesInfoList = new ArrayList<SystemPropertiesInfo>();

        for( SystemProperties systemProperties : systemPropertiesList )
        {
            SystemPropertiesInfo systemPropertiesInfo = toSystemPropertiesInfo(systemProperties);
            systemPropertiesInfoList.add( systemPropertiesInfo );
        }

        return systemPropertiesInfoList;
    } 


    private CommonSystemLogInfo toCommonSystemLogInfo(CommonSystemLog commonSystemLog)
    {
        CommonSystemLogInfo commonSystemLogInfo = new CommonSystemLogInfo();

        commonSystemLogInfo.setCommonSystemLogID(commonSystemLog.getCommonSystemLogID());

        commonSystemLogInfo.setCommonSystemLogTag(commonSystemLog.getCommonSystemLogTag());
        commonSystemLogInfo.setCommonSystemLogRecord( commonSystemLog.getCommonSystemLogRecord() );
        commonSystemLogInfo.setCommonSystemLogOccurDate( commonSystemLog.getCommonSystemLogOccurDate() );

        commonSystemLogInfo.setCommonSystemLogOption1( commonSystemLog.getCommonSystemLogOption1() );
        commonSystemLogInfo.setCommonSystemLogOption2( commonSystemLog.getCommonSystemLogOption2() );
        commonSystemLogInfo.setCommonSystemLogOption3( commonSystemLog.getCommonSystemLogOption3() );            
        
        commonSystemLogInfo.setRecordStatus( commonSystemLog.getRecordStatus() );
        commonSystemLogInfo.setMemo( commonSystemLog.getMemo() );
        commonSystemLogInfo.setCreateDate( commonSystemLog.getCreateDate() );
        commonSystemLogInfo.setUpdateDate( commonSystemLog.getUpdateDate() );

        return commonSystemLogInfo;
    }

    private List<CommonSystemLogInfo> toCommonSystemLogInfoList(List<CommonSystemLog> commonSystemLogList)
    {
        List<CommonSystemLogInfo> commonSystemLogInfoList = new ArrayList<CommonSystemLogInfo>();

        for( CommonSystemLog m : commonSystemLogList )
        {
            CommonSystemLogInfo info = toCommonSystemLogInfo(m);
            commonSystemLogInfoList.add(info);
        }

        return commonSystemLogInfoList;
    }


    private CommonSystemLogPropertiesInfo toCommonSystemLogPropertiesInfo(CommonSystemLogProperties commonSystemLogProperties )
    {
        CommonSystemLogPropertiesInfo commonSystemLogPropertiesInfo = new CommonSystemLogPropertiesInfo();

        commonSystemLogPropertiesInfo.setCommonSystemLogPropertyID( commonSystemLogProperties.getCommonSystemLogPropertyID() );
        commonSystemLogPropertiesInfo.setCommonSystemLogID( commonSystemLogProperties.getCommonSystemLogID() );

        commonSystemLogPropertiesInfo.setCommonSystemLogPropertyKey( commonSystemLogProperties.getCommonSystemLogPropertyKey() );
        commonSystemLogPropertiesInfo.setCommonSystemLogPropertyValue( commonSystemLogProperties.getCommonSystemLogPropertyValue() );

        commonSystemLogPropertiesInfo.setCommonSystemLogOption1( commonSystemLogProperties.getCommonSystemLogPropertyOption1() );
        commonSystemLogPropertiesInfo.setCommonSystemLogOption2( commonSystemLogProperties.getCommonSystemLogPropertyOption2() );
        commonSystemLogPropertiesInfo.setCommonSystemLogOption3( commonSystemLogProperties.getCommonSystemLogPropertyOption3() );                

        commonSystemLogPropertiesInfo.setRecordStatus( commonSystemLogProperties.getRecordStatus() );
        commonSystemLogPropertiesInfo.setMemo( commonSystemLogProperties.getMemo() );
        commonSystemLogPropertiesInfo.setCreateDate( commonSystemLogProperties.getCreateDate() );
        commonSystemLogPropertiesInfo.setUpdateDate( commonSystemLogProperties.getUpdateDate() );

        return commonSystemLogPropertiesInfo;
    }


    private List<CommonSystemLogPropertiesInfo> toCommonSystemLogPropertiesInfoList(List<CommonSystemLogProperties> commonSystemLogPropertiesList )
    {
        List<CommonSystemLogPropertiesInfo> commonSystemLogPropertiesInfoList = new ArrayList<CommonSystemLogPropertiesInfo>();

        for( CommonSystemLogProperties m : commonSystemLogPropertiesList )
        {
            CommonSystemLogPropertiesInfo info = toCommonSystemLogPropertiesInfo(m);
            commonSystemLogPropertiesInfoList.add( info );
        }

        return commonSystemLogPropertiesInfoList;
    }




    private User toUser(UserInfo userInfo)
    {
        User user = new User();

        user.setUserID( userInfo.getUserID() );
        user.setUserName( userInfo.getUserName() );
        user.setUserPassword( userInfo.getUserPassword() );
        user.setRecordStatus( userInfo.getRecordStatus() );
        user.setMemo( userInfo.getMemo() );

        user.setCreateDate( LoulanDNSUtils.toDateTimeString( userInfo.getCreateDate() ) );
        user.setUpdateDate( LoulanDNSUtils.toDateTimeString( userInfo.getUpdateDate() ) );     
     
        return user;
    }


    private UserGroup toUserGroup(UserGroupInfo userGroupInfo)
    {

        UserGroup userGroup = new UserGroup();

        userGroup.setUserGroupID( userGroupInfo.getUserGroupID() );
        userGroup.setUserGroupName( userGroupInfo.getUserGroupName() );
        userGroup.setRecordStatus( userGroupInfo.getRecordStatus() );
        userGroup.setMemo( userGroupInfo.getMemo() );
        userGroup.setCreateDate( LoulanDNSUtils.toDateTimeString( userGroupInfo.getCreateDate() ) );
        userGroup.setUpdateDate( LoulanDNSUtils.toDateTimeString( userGroupInfo.getUpdateDate() ) );

        return userGroup;
    }


    private UserGroupPrivilege toUserGroupPrivilege(UserGroupPrivilegeInfo userGroupPrivilegeInfo)
    {
        UserGroupPrivilege  userGroupPrivilege = new UserGroupPrivilege();

        userGroupPrivilege.setUserGroupPrivilegeID( userGroupPrivilegeInfo.getUserGroupPrivilegeID() );
        userGroupPrivilege.setUserGroupID( userGroupPrivilegeInfo.getUserGroupID() );
        userGroupPrivilege.setDnsInfoMaskValue( userGroupPrivilegeInfo.getDnsInfoMaskValue() );
        userGroupPrivilege.setUserInfoMaskValue( userGroupPrivilegeInfo.getUserInfoMaskValue() );    
        userGroupPrivilege.setSystemInfoMaskValue( userGroupPrivilegeInfo.getSystemInfoMaskValue() );    

        userGroupPrivilege.setRecordStatus( userGroupPrivilegeInfo.getRecordStatus() );
        userGroupPrivilege.setMemo( userGroupPrivilegeInfo.getMemo() );
        userGroupPrivilege.setCreateDate( LoulanDNSUtils.toDateTimeString( userGroupPrivilegeInfo.getCreateDate() ) );
        userGroupPrivilege.setUpdateDate( LoulanDNSUtils.toDateTimeString( userGroupPrivilegeInfo.getUpdateDate() ) );

        return userGroupPrivilege;

    }


    private DNSServiceInstance toDNSServiceInstance(DNSServiceInstanceInfo dnsServiceInstanceInfo)
    {
        DNSServiceInstance dnsServiceInstance = new DNSServiceInstance();

        dnsServiceInstance.setDNSServiceInstanceID( dnsServiceInstanceInfo.getDNSServiceInstanceID() );
        dnsServiceInstance.setUserID( dnsServiceInstanceInfo.getUserID() );
        dnsServiceInstance.setDNSServiceInstanceName( dnsServiceInstanceInfo.getDNSServiceInstanceName() );
        dnsServiceInstance.setDNSResolverInstanceID( dnsServiceInstanceInfo.getDNSResolverInstanceID() );
        dnsServiceInstance.setDNSServiceInstanceExplain( dnsServiceInstanceInfo.getDNSServiceInstanceExplain() );
        dnsServiceInstance.setRecordStatus( dnsServiceInstanceInfo.getRecordStatus() );
        dnsServiceInstance.setMemo( dnsServiceInstanceInfo.getMemo() );
        dnsServiceInstance.setCreateDate( LoulanDNSUtils.toDateTimeString( dnsServiceInstanceInfo.getCreateDate() ) );
        dnsServiceInstance.setUpdateDate( LoulanDNSUtils.toDateTimeString( dnsServiceInstanceInfo.getUpdateDate() ) );

        return dnsServiceInstance;
    }
    

    private DNSResolverInstance toDNSResolverInstance(DNSResolverInstanceInfo dnsResolverInstanceInfo)
    {

        DNSResolverInstance dnsResolverInstance = new DNSResolverInstance();

        dnsResolverInstance.setDnsResolverInstanceID( dnsResolverInstanceInfo.getDNSResolverInstanceID() );
        dnsResolverInstance.setUserID( dnsResolverInstanceInfo.getUserID() );
        dnsResolverInstance.setDnsResolverInstanceName( dnsResolverInstanceInfo.getDNSResolverInstanceName() );
        dnsResolverInstance.setDnsResolverInstanceExplain( dnsResolverInstanceInfo.getDNSResolverInstanceExplain() );
        dnsResolverInstance.setDnsResolverTypeCode( dnsResolverInstanceInfo.getDnsResolverTypeCode() );
        dnsResolverInstance.setRecordStatus( dnsResolverInstanceInfo.getRecordStatus() );
        dnsResolverInstance.setMemo( dnsResolverInstanceInfo.getMemo() );
        dnsResolverInstance.setCreateDate( LoulanDNSUtils.toDateTimeString( dnsResolverInstanceInfo.getCreateDate() ) );
        dnsResolverInstance.setUpdateDate( LoulanDNSUtils.toDateTimeString( dnsResolverInstanceInfo.getUpdateDate() ) );

        return dnsResolverInstance;
    }



    public DNSServiceInstancePropertyInfo toDNSServiceInstancePropertyInfo(DNSServiceInstanceProperties dnsServiceInstanceProperties)
    {
        DNSServiceInstancePropertyInfo info = new DNSServiceInstancePropertyInfo();

        info.setDnsServiceInstancePropertyID( dnsServiceInstanceProperties.getDnsServiceInstancePropertyID() );
        info.setDnsServiceInstanceID( dnsServiceInstanceProperties.getDnsServiceInstanceID() );
        info.setDnsServiceInstancePropertyKey( dnsServiceInstanceProperties.getDnsServiceInstancePropertyKey() );
        info.setDnsServiceInstancePropertyValue( dnsServiceInstanceProperties.getDnsServiceInstancePropertyValue() );
        info.setDnsServiceInstancePropertyExplain( dnsServiceInstanceProperties.getDnsServiceInstancePropertyExplain()  );
 
        info.setRecordStatus( dnsServiceInstanceProperties.getRecordStatus() );
        info.setMemo( dnsServiceInstanceProperties.getMemo() );
        info.setCreateDate( dnsServiceInstanceProperties.getCreateDate() );
        info.setUpdateDate( dnsServiceInstanceProperties.getUpdateDate() );
     
        return info;
    }


    private DNSResolverInstanceProperties toDNSResolverInstanceProperties(DNSResolverPropertiesInfo dnsResolverPropertiesInfo)
    {

        DNSResolverInstanceProperties dnsResolverInstanceProperties = new DNSResolverInstanceProperties();

        dnsResolverInstanceProperties.setDnsResolverInstancePropertyID( dnsResolverPropertiesInfo.getDNSResolverPropertyID() );
        dnsResolverInstanceProperties.setDnsResolverInstanceID( dnsResolverPropertiesInfo.getDnsResolverID() );
        dnsResolverInstanceProperties.setDnsResolverInstancePropertyKey( dnsResolverPropertiesInfo.getDnsResolverPropertyKey() );
        dnsResolverInstanceProperties.setDnsResolverInstancePropertyValue( dnsResolverPropertiesInfo.getDnsResolverPropertyValue() );
        dnsResolverInstanceProperties.setDnsResolverInstancePropertyExplain( dnsResolverPropertiesInfo.getDnsResolverPropertyExplain() );
        dnsResolverInstanceProperties.setRecordStatus( dnsResolverPropertiesInfo.getRecordStatus() );
        dnsResolverInstanceProperties.setMemo( dnsResolverPropertiesInfo.getMemo() );
        dnsResolverInstanceProperties.setCreateDate( LoulanDNSUtils.toDateTimeString( dnsResolverPropertiesInfo.getCreateDate() )  );
        dnsResolverInstanceProperties.setUpdateDate( LoulanDNSUtils.toDateTimeString( dnsResolverPropertiesInfo.getUpdateDate() ) );

        return dnsResolverInstanceProperties;
    }


    private DNSServiceEndpointInstanceInfo toDNSServiceEndpointInstanceInfo(DNSServiceEndpointInstance dnsServiceEndpointInstance)
    {
        DNSServiceEndpointInstanceInfo dnsServiceEndpointInstanceInfo = new DNSServiceEndpointInstanceInfo();

        dnsServiceEndpointInstanceInfo.setDNSServiceEndpointInstanceID( dnsServiceEndpointInstance.getDnsServiceEndpointInstanceID() );
        dnsServiceEndpointInstanceInfo.setDNSServiceInstanceID( dnsServiceEndpointInstance.getDnsServiceInstanceID() );
        dnsServiceEndpointInstanceInfo.setDNSServiceEndpointInstanceName( dnsServiceEndpointInstance.getDnsServiceEndpointInstanceName() );
        dnsServiceEndpointInstanceInfo.setDNSServiceEndpointInstanceExplain( dnsServiceEndpointInstance.getDnsServiceEndpointInstanceExplain() );
        dnsServiceEndpointInstanceInfo.setDNSServiceEndpointTypeCode( dnsServiceEndpointInstance.getDnsServiceEndpointTypeCode() );
        dnsServiceEndpointInstanceInfo.setRecordStatus( dnsServiceEndpointInstance.getRecordStatus() );
        dnsServiceEndpointInstanceInfo.setCreateDate( dnsServiceEndpointInstance.getCreateDate() );
        dnsServiceEndpointInstanceInfo.setUpdateDate( dnsServiceEndpointInstance.getUpdateDate() );

        return dnsServiceEndpointInstanceInfo;
    }

    private DNSServiceEndpointInstance toDNSServiceEndpointInstance(DNSServiceEndpointInstanceInfo dnsServiceEndpointInstanceInfo )
    {

        DNSServiceEndpointInstance dnsServiceEndpointInstance = new DNSServiceEndpointInstance();

        dnsServiceEndpointInstance.setDNSServiceEndpointInstanceID( dnsServiceEndpointInstanceInfo.getDNSServiceEndpointInstanceID() );
        dnsServiceEndpointInstance.setDnsServiceInstanceID( dnsServiceEndpointInstanceInfo.getDNSServiceInstanceID() );
        dnsServiceEndpointInstance.setDnsServiceEndpointInstanceName( dnsServiceEndpointInstanceInfo.getDNSServiceEndpointInstanceName() );
        dnsServiceEndpointInstance.setDnsServiceEndpointInstanceExplain( dnsServiceEndpointInstanceInfo.getDNSServiceEndpointInstanceExplain() );
        dnsServiceEndpointInstance.setDnsServiceEndpointTypeCode( dnsServiceEndpointInstanceInfo.getDNSServiceEndpointTypeCode() );
        dnsServiceEndpointInstance.setRecordStatus( dnsServiceEndpointInstanceInfo.getRecordStatus() );
        dnsServiceEndpointInstance.setCreateDate( LoulanDNSUtils.toDateTimeString(dnsServiceEndpointInstanceInfo.getCreateDate()) );
        dnsServiceEndpointInstance.setUpdateDate( LoulanDNSUtils.toDateTimeString(dnsServiceEndpointInstanceInfo.getUpdateDate()) );

        return dnsServiceEndpointInstance;
    }


    private DNSServiceEndpointInstancePropertyInfo toDNSServiceEndpointInstancePropertyInfo(DNSServiceEndpointInstanceProperties dnsServiceEndpointInstanceProperties)
    {
        DNSServiceEndpointInstancePropertyInfo info = new DNSServiceEndpointInstancePropertyInfo();

        info.setDNSServiceEndpointInstancePropertyID( dnsServiceEndpointInstanceProperties.getDnsServiceEndpointInstancePropertyID() );
        info.setDNSServiceEndpointInstanceID( dnsServiceEndpointInstanceProperties.getDnsServiceEndpointInstanceID() );

        info.setDNSServiceEndpointInstancePropertyKey( dnsServiceEndpointInstanceProperties.getDnsServiceEndpointInstancePropertyKey() );
        info.setDNSServiceEndpointInstancePropertyValue( dnsServiceEndpointInstanceProperties.getDnsServiceEndpointInstancePropertyValue() );
        info.setDNSServiceEndpointInstancePropertyExplain( dnsServiceEndpointInstanceProperties.getDnsServiceEndpointInstancePropertyExplain()  );
 
        info.setRecordStatus( dnsServiceEndpointInstanceProperties.getRecordStatus() );
        info.setMemo( dnsServiceEndpointInstanceProperties.getMemo() );
        info.setCreateDate( dnsServiceEndpointInstanceProperties.getCreateDate() );
        info.setUpdateDate( dnsServiceEndpointInstanceProperties.getUpdateDate() );
     
        return info;
    }


    private DNSServiceEndpointInstanceProperties toDNSServiceEndpointInstanceProperties(DNSServiceEndpointInstancePropertyInfo info)
    {

        DNSServiceEndpointInstanceProperties dnsServiceEndpointInstanceProperties = new DNSServiceEndpointInstanceProperties();

        dnsServiceEndpointInstanceProperties.setDnsServiceEndpointInstancePropertyID( info.getDNSServiceEndpointInstancePropertyID() );
        dnsServiceEndpointInstanceProperties.setDnsServiceEndpointInstanceID( info.getDNSServiceEndpointInstanceID() );
        dnsServiceEndpointInstanceProperties.setDnsServiceEndpointInstancePropertyKey( info.getDNSServiceEndpointInstancePropertyKey() );
        dnsServiceEndpointInstanceProperties.setDnsServiceEndpointInstancePropertyValue( info.getDNSServiceEndpointInstancePropertyValue() );
        dnsServiceEndpointInstanceProperties.setDnsServiceEndpointInstancePropertyExplain( info.getDNSServiceEndpointInstancePropertyExplain() );
        dnsServiceEndpointInstanceProperties.setRecordStatus( info.getRecordStatus() );
        dnsServiceEndpointInstanceProperties.setMemo( info.getMemo() );
        dnsServiceEndpointInstanceProperties.setCreateDate( LoulanDNSUtils.toDateTimeString( info.getCreateDate() )  );
        dnsServiceEndpointInstanceProperties.setUpdateDate( LoulanDNSUtils.toDateTimeString( info.getUpdateDate() ) );

        return dnsServiceEndpointInstanceProperties;
    }



    private UserGroupMapping toUserGroupMapping(UserGroupMappingInfo userGroupMappingInfo)
    {
        UserGroupMapping userGroupMapping = new UserGroupMapping();

        userGroupMapping.setUserID( userGroupMappingInfo.getUserID() );
        userGroupMapping.setUserGroupID( userGroupMappingInfo.getUserGroupID() );
        userGroupMapping.setRecordStatus( userGroupMappingInfo.getRecordStatus() );
        userGroupMapping.setMemo( userGroupMappingInfo.getMemo() );
        userGroupMapping.setCreateDate( LoulanDNSUtils.toDateTimeString(userGroupMappingInfo.getCreateDate() ) );
        userGroupMapping.setUpdateDate( LoulanDNSUtils.toDateTimeString( userGroupMappingInfo.getUpdateDate() ) );
        
        return userGroupMapping;
    }

    
    private SystemProperties toSystemProperties(SystemPropertiesInfo systemPropertiesInfo)
    {
        SystemProperties systemProperties = new SystemProperties();

        systemProperties.setSystemPropertyID( systemPropertiesInfo.getSystemPropertyID() );
        systemProperties.setSystemPropertyKey( systemPropertiesInfo.getSystemPropertyKey() );
        systemProperties.setSystemPropertyValue( systemPropertiesInfo.getSystemPropertyValue() );
        systemProperties.setSystemPropertyExplain( systemPropertiesInfo.getSystemPropertyExplain() );
        systemProperties.setRecordStatus( systemPropertiesInfo.getRecordStatus() );
        systemProperties.setMemo( systemPropertiesInfo.getMemo() );
        systemProperties.setCreateDate( LoulanDNSUtils.toDateTimeString( systemPropertiesInfo.getCreateDate() ) );
        systemProperties.setUpdateDate( LoulanDNSUtils.toDateTimeString( systemPropertiesInfo.getUpdateDate() ) );

        return systemProperties;
    }

    private CommonSystemLog toCommonSystemLog(CommonSystemLogInfo commonSystemLogInfo)
    {
        CommonSystemLog commonSystemLog = new CommonSystemLog();

        commonSystemLog.setCommonSystemLogID( commonSystemLogInfo.getCommonSystemLogID() );
        
        commonSystemLog.setCommonSystemLogTag( commonSystemLogInfo.getCommonSystemLogTag() );
        commonSystemLog.setCommonSystemLogRecord( commonSystemLogInfo.getCommonSystemLogRecord() );
        commonSystemLog.setCommonSystemLogOccurDate( LoulanDNSUtils.toDateTimeString( commonSystemLogInfo.getCommonSystemLogOccurDate() ) );

        commonSystemLog.setCommonSystemLogOption1( commonSystemLogInfo.getCommonSystemLogOption1() );
        commonSystemLog.setCommonSystemLogOption2( commonSystemLogInfo.getCommonSystemLogOption2() );
        commonSystemLog.setCommonSystemLogOption3( commonSystemLogInfo.getCommonSystemLogOption3() );                

        commonSystemLog.setRecordStatus( commonSystemLogInfo.getRecordStatus() );
        commonSystemLog.setMemo( commonSystemLogInfo.getMemo() );
        commonSystemLog.setCreateDate( LoulanDNSUtils.toDateTimeString( commonSystemLogInfo.getCreateDate() ) );
        commonSystemLog.setUpdateDate( LoulanDNSUtils.toDateTimeString( commonSystemLogInfo.getUpdateDate() ) );

        return commonSystemLog;
    }

    private CommonSystemLogProperties toCommonSystemLogProperties(CommonSystemLogPropertiesInfo commonSystemLogPropertiesInfo)
    {
        CommonSystemLogProperties commonSystemLogProperties = new CommonSystemLogProperties();

        commonSystemLogProperties.setCommonSystemLogPropertyID( commonSystemLogPropertiesInfo.getCommonSystemLogPropertyID() );
        commonSystemLogProperties.setCommonSystemLogID( commonSystemLogPropertiesInfo.getCommonSystemLogID() );

        commonSystemLogProperties.setCommonSystemLogPropertyKey(commonSystemLogPropertiesInfo.getCommonSystemLogPropertyKey());
        commonSystemLogProperties.setCommonSystemLogPropertyValue( commonSystemLogPropertiesInfo.getCommonSystemLogPropertyValue() );

        commonSystemLogProperties.setCommonSystemLogPropertyOption1( commonSystemLogPropertiesInfo.getCommonSystemLogOption1() );
        commonSystemLogProperties.setCommonSystemLogPropertyOption2( commonSystemLogPropertiesInfo.getCommonSystemLogOption2() );
        commonSystemLogProperties.setCommonSystemLogPropertyOption3( commonSystemLogPropertiesInfo.getCommonSystemLogOption3() );

        commonSystemLogProperties.setRecordStatus( commonSystemLogPropertiesInfo.getRecordStatus() );
        commonSystemLogProperties.setMemo( commonSystemLogPropertiesInfo.getMemo() );
        commonSystemLogProperties.setCreateDate( LoulanDNSUtils.toDateTimeString( commonSystemLogPropertiesInfo.getCreateDate() ) );
        commonSystemLogProperties.setUpdateDate( LoulanDNSUtils.toDateTimeString( commonSystemLogPropertiesInfo.getUpdateDate() ) );

        return commonSystemLogProperties;
    }


}