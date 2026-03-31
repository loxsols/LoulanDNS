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
import org.loxsols.net.service.dns.loulandns.server.logical.model.DNSResolverInstanceInfo;
import org.loxsols.net.service.dns.loulandns.server.logical.model.DNSServiceInstancePropertyInfo;
import org.loxsols.net.service.dns.loulandns.server.common.constants.LoulanDNSConstants;
import org.loxsols.net.service.dns.loulandns.server.common.LoulanDNSSystemServiceException;

import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;

import org.loxsols.net.service.dns.loulandns.server.http.spring.model.*;
import org.loxsols.net.service.dns.loulandns.server.http.spring.common.util.LoulanDNSSpringUtils;


@Service
@Transactional
public class LoulanDNSDBService 
{

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserGroupRepository userGroupRepository;

    @Autowired
    UserGroupMappingRepository userGroupMappingRepository;

    @Autowired
    UserGroupPrivilegeRepository userGroupPrivilegeRepository;

    @Autowired
    DNSServiceInstanceRepository dnsServiceInstanceRepository;

    @Autowired
    DNSServiceInstancePropertiesRepository dnsServiceInstancePropertiesRepository;

    @Autowired
    DNSResolverInstanceRepository dnsResolverInstanceRepository;

    @Autowired
    DNSResolverInstancePropertiesRepository dnsResolverInstancePropertiesRepository;

    @Autowired
    DNSServiceEndpointInstanceRepository dnsServiceEndpointInstanceRepository;

    @Autowired
    DNSServiceEndpointInstancePropertiesRepository dnsServiceEndpointInstancePropertiesRepository;
    
    @Autowired
    SystemPropertiesRepository systemPropertiesRepository;

    @Autowired
    CommonSystemLogRepository commonSystemLogRepository;

    @Autowired
    CommonSystemLogPropertiesRepository commonSystemLogPropertiesRepository;



    // ユーザー情報を取得する.
    public User getUser(long userID) throws LoulanDNSSystemServiceException
    {
        List<User> userList = userRepository.findByUserID(userID);

        if (userList.isEmpty()) {
            return null;
        }

        User user = userList.get(0);
        return user;
    }



    // ユーザー情報を取得する.
    public User getUser(String userName) throws LoulanDNSSystemServiceException
    {
        List<User> userList = userRepository.findByUserName(userName);

        if (userList.isEmpty()) {
            return null;
        }

        User user = userList.get(0);
        return user;
    }


    // ユーザーの一覧を取得する.
    public List<User> getUserList() throws LoulanDNSSystemServiceException
    {
        List<User> userList = userRepository.findAll();
        return userList;
    }

    // ユーザーを新規作成する.
    public User createUser(String userName, String userPassword, int recordStatus, String memo) throws LoulanDNSSystemServiceException
    {

        // ユーザー情報を新規作成する.
        User newUser = LoulanDNSSpringUtils.createUserObject(userName, userPassword, recordStatus, memo);

        // IDは新規採番するのでnullに設定する.
        newUser.setUserID(null);
        
        // ユーザー情報を新規作成する.
        User savedUser = createUser(newUser);
        return savedUser;
    }

    // ユーザーを新規作成する.
    public User createUser(User user) throws LoulanDNSSystemServiceException
    {
        String userName = user.getUserName();
        if ( isExistingUser( userName ) == true )
        {
            // 指定されたユーザーは既に存在する.
            String msg = String.format("Specified user is already exist. userName=%s", userName );
            LoulanDNSSystemServiceException execption = new LoulanDNSSystemServiceException( msg );
            throw execption;
        }

        // IDは新規採番するのでnullに設定する.
        user.setUserID(null);
        
        // ユーザー情報を新規作成する.
        userRepository.save(user);
        
        // DBから新規作成したユーザー情報を取得して呼び出し元に返す.
        User savedUser = getUser(userName);
        return savedUser;

    }




    // ユーザー情報を更新する.
    public User updateUser(long userID, String userName, String userPassword, int recordStatus, String memo ) throws LoulanDNSSystemServiceException
    {

        // 既存のユーザー情報をDBから取得する.
        User user = getUser(userID);

        if ( user == null )
        {
            // 指定されたIDのユーザーは存在しない.
            String msg = String.format("User is not found. UserID=%d", userID );
            LoulanDNSSystemServiceException execption = new LoulanDNSSystemServiceException( msg );
            throw execption;
        }


        // モデルクラスを設定.
        String updateDate = LoulanDNSUtils.getCurrentDateTimeString();
        user.setUserID( userID );
        user.setUserName( userName );
        user.setUserPassword( userPassword );
        user.setRecordStatus( recordStatus );
        user.setMemo( memo );
        user.setUpdateDate( updateDate );

        // DB更新.
        User savedUser = updateUser(user);
        return savedUser;
    }
    

    // ユーザー情報を更新する.
    public User updateUser(User user) throws LoulanDNSSystemServiceException
    {
        long userID = user.getUserID();

        if ( isExistingUser( user.getUserID() ) == false )
        {
            // 指定されたIDのユーザーは存在しない.
            String msg = String.format("User is not found. UserID=%d", userID );
            LoulanDNSSystemServiceException execption = new LoulanDNSSystemServiceException( msg );
            throw execption;
        }

        // 更新時刻を設定する.
        String updateDate = LoulanDNSUtils.getCurrentDateTimeString();
        user.setUpdateDate(updateDate);
        
        // ユーザー情報を更新する.
        userRepository.save(user);
        
        User savedUser = getUser( userID );
        return savedUser;
    }




    // ユーザー情報を削除する.
    public void deleteUser(long userID ) throws LoulanDNSSystemServiceException
    {
        User user = getUser(userID);
        if ( user == null )
        {
            // 指定されたIDのユーザーは存在しない.
            String msg = String.format("User is not found. UserID=%d", userID );
            LoulanDNSSystemServiceException execption = new LoulanDNSSystemServiceException( msg );
            throw execption;
        }

        userRepository.delete(user);
    }


    // ユーザーグループマッピング情報を削除する.
    public void deleteUserGroupMapping(long uid, long gid ) throws LoulanDNSSystemServiceException
    {
        UserGroupMapping userGroupMapping = getUserGroupMapping(uid, gid);
        if ( userGroupMapping == null )
        {
            // 指定されたIDのユーザーグループマッピング情報は存在しない.
            String msg = String.format("UserGroupMapping is not found. UserID=%d, UserGroupID=%d", uid, gid );
            LoulanDNSSystemServiceException execption = new LoulanDNSSystemServiceException( msg );
            throw execption;
        }

        userGroupMappingRepository.delete(userGroupMapping);
    }



    // 既存ユーザーがいるか確認する.
    public boolean isExistingUser(String userName) throws LoulanDNSSystemServiceException
    {
        User user = getUser(userName);
        if (user != null) {
            return true;
        }

        return false;
    }

    // 既存ユーザーがいるか確認する.
    public boolean isExistingUser(long uid) throws LoulanDNSSystemServiceException
    {
        User user = getUser(uid);
        if (user != null) {
            return true;
        }

        return false;
    }



    // ユーザーグループ情報を取得する.
    public UserGroup getUserGroup(long userGroupID) throws LoulanDNSSystemServiceException
    {
        List<UserGroup> userGroupList = userGroupRepository.findByUserGroupID(userGroupID);

        if (userGroupList.isEmpty())
        {
            return null;
        }

        UserGroup userGroup = userGroupList.get(0);
        return userGroup;
    }



    // ユーザー情報を取得する.
    public UserGroup getUserGroup(String userGroupName) throws LoulanDNSSystemServiceException
    {
        List<UserGroup> userGroupList = userGroupRepository.findByUserGroupName(userGroupName);

        if (userGroupList.isEmpty())
        {
            return null;
        }

        UserGroup userGroup = userGroupList.get(0);
        return userGroup;
    }

    // ユーザーグループを新規作成する.
    public UserGroup createUserGroup(String userGroupName, int recordStatus, String memo) throws LoulanDNSSystemServiceException
    {

        if ( isExistingUserGroup( userGroupName ) == true )
        {
            // 指定されたユーザーグループは既に存在する.
            String msg = String.format("Specified user group is already exist. userGroupName=%s", userGroupName );
            LoulanDNSSystemServiceException execption = new LoulanDNSSystemServiceException( msg );
            throw execption;
        }


        // ユーザーグループ情報を新規作成する.
        UserGroup newUserGroup = LoulanDNSSpringUtils.createUserGroupObject(userGroupName, recordStatus, memo);

        // IDは新規採番するのでnullに設定する.
        newUserGroup.setUserGroupID(null);

        userGroupRepository.save(newUserGroup);
        
        // DBから新規作成したユーザー情報を取得して呼び出し元に返す.
        UserGroup savedUserGroup = getUserGroup(userGroupName);
        return savedUserGroup;
    }

    // ユーザーグループ情報を更新する.
    public UserGroup updateUserGroup(long userGroupID, String userGroupName, int recordStatus, String memo ) throws LoulanDNSSystemServiceException
    {

        UserGroup userGroup = getUserGroup(userGroupID);
        if ( userGroup == null )
        {
            // 指定されたIDのユーザーグループは存在しない.
            String msg = String.format("UserGroup is not found. UserGroupID=%d", userGroupID );
            LoulanDNSSystemServiceException execption = new LoulanDNSSystemServiceException( msg );
            throw execption;
        }


        // ユーザーグループ情報を更新する.
        String updateDate = LoulanDNSUtils.getCurrentDateTimeString();
        UserGroup updateUserGroup = LoulanDNSSpringUtils.createUserGroupObject(userGroup.getUserGroupID(), userGroupName, recordStatus, memo, userGroup.getCreateDate(), updateDate );
        userGroupRepository.save(updateUserGroup);
        
        UserGroup savedUserGroup = getUserGroup(userGroupName);
        return savedUserGroup;
    }

    // ユーザーグループ情報を削除する.
    public void deleteUserGroup(long userGroupID ) throws LoulanDNSSystemServiceException
    {
        UserGroup userGroup = getUserGroup(userGroupID);
        if ( userGroup == null )
        {
            // 指定されたIDのユーザーグループは存在しない.
            String msg = String.format("UserGroup is not found. UserGroupID=%d", userGroupID );
            LoulanDNSSystemServiceException execption = new LoulanDNSSystemServiceException( msg );
            throw execption;
        }

        userGroupRepository.delete(userGroup);
    }


    // 既存ユーザーグループがあるか確認する.
    public boolean isExistingUserGroup(String userGroupName) throws LoulanDNSSystemServiceException
    {
        UserGroup userGroup = getUserGroup(userGroupName);
        if (userGroup != null)
        {
            return true;
        }
    
        return false;
    }

    // 既存ユーザーグループがあるか確認する.
    public boolean isExistingUserGroup(long gid) throws LoulanDNSSystemServiceException
    {
        UserGroup userGroup = getUserGroup(gid);
        if (userGroup != null)
        {
            return true;
        }
    
        return false;
    }



    // ユーザーグループマッピング情報(USER_GROUP_MAPPINGテーブル)を取得する.
    public List<UserGroupMapping> getUserGroupMappingListByUserID(long userID) throws LoulanDNSSystemServiceException
    {
        List<UserGroupMapping> userGroupMappingList = userGroupMappingRepository.findByUserID(userID);

        return userGroupMappingList;
    }


    // ユーザーグループマッピング情報(USER_GROUP_MAPPINGテーブル)を取得する.
    public List<UserGroupMapping> getUserGroupMappingListByUserGroupdID(long userGroupID) throws LoulanDNSSystemServiceException
    {
        List<UserGroupMapping> userGroupMappingList = userGroupMappingRepository.findByUserGroupID(userGroupID);
    
        return userGroupMappingList;
    }

    // ユーザーグループマッピング情報(USER_GROUP_MAPPINGテーブル)を取得する.
    public UserGroupMapping getUserGroupMapping(long uid, long gid) throws LoulanDNSSystemServiceException
    {
        List<UserGroupMapping> userGroupMappingList = userGroupMappingRepository.findByUserID(uid);
        for( UserGroupMapping userGroupMapping : userGroupMappingList )
        {
            if ( gid == userGroupMapping.getUserGroupID() )
            {
                return userGroupMapping;
            }
        }

        return null;
    }


    // ユーザーグループマッピング情報(USER_GROUP_MAPPINGテーブル)を新規作成する.
    public UserGroupMapping createUserGroupMapping(UserGroupMapping userGroupMapping) throws LoulanDNSSystemServiceException
    {
        long uid = userGroupMapping.getUserID();
        long gid = userGroupMapping.getUserGroupID();

        if ( isBelongingToUserGroup( uid, gid ) == true )
        {
            // 指定されたIDのユーザーグループマッピング情報は既に存在する.
            String msg = String.format("UserGroupMapping is already exists. uid=%d, gid=%d", uid, gid );
            LoulanDNSSystemServiceException execption = new LoulanDNSSystemServiceException( msg );
            throw execption;
        }

        // ユーザーグループマッピング情報のDBレコードを新規作成する.
        // (ユーザーグループマッピング情報には固有のIDがないのでこのまま新規登録する.)
        userGroupMappingRepository.save(userGroupMapping);

        // 新規作成したユーザーグループマッピング情報のDBレコードを取得して返す.
        UserGroupMapping savedUserGroupMapping = getUserGroupMapping(uid, gid);
        return savedUserGroupMapping;
    }
    

    // ユーザーグループマッピング情報(USER_GROUP_MAPPINGテーブル)を更新する.
    public UserGroupMapping updateUserGroupMapping(UserGroupMapping userGroupMapping) throws LoulanDNSSystemServiceException
    {
        long uid = userGroupMapping.getUserID();
        long gid = userGroupMapping.getUserGroupID();

        if ( isBelongingToUserGroup( uid, gid ) == false )
        {
            // 指定されたIDのユーザーグループマッピング情報がDB上に存在しない.
            String msg = String.format("UserGroupMapping is not found. uid=%d, gid=%d", uid, gid );
            LoulanDNSSystemServiceException execption = new LoulanDNSSystemServiceException( msg );
            throw execption;
        }

        String updateDate = LoulanDNSUtils.getCurrentDateTimeString();
        userGroupMapping.setUpdateDate( updateDate );

        // 既存のユーザーグループマッピング情報のDBレコードを更新する.
        userGroupMappingRepository.save(userGroupMapping);
        
        // 更新したユーザーグループマッピング情報のDBレコードを取得して返す.
        UserGroupMapping savedUserGroupMapping = getUserGroupMapping(uid, gid);
        return savedUserGroupMapping;
    }



    public boolean isBelongingToUserGroup(String userName, String userGroupName) throws LoulanDNSSystemServiceException
    {
        User user = getUser(userName);
        if ( user == null )
        {
            // 指定されたIDのユーザーは存在しない.
            String msg = String.format("User is not found. UserName=%d", userName );
            LoulanDNSSystemServiceException execption = new LoulanDNSSystemServiceException( msg );
            throw execption;
        }

        UserGroup userGroup = getUserGroup(userGroupName);
        if ( userGroup == null )
        {
            // 指定されたIDのユーザーグループは存在しない.
            String msg = String.format("UserGroup is not found. UserGroupName=%s", userGroupName );
            LoulanDNSSystemServiceException execption = new LoulanDNSSystemServiceException( msg );
            throw execption;
        }


        long uid = user.getUserID();
        long gid = userGroup.getUserGroupID();

        boolean ret = isBelongingToUserGroup(uid, gid);
        return ret;
    }

    public boolean isBelongingToUserGroup(long userID, long userGroupID) throws LoulanDNSSystemServiceException
    {

        if ( isExistingUser(userID) == false )
        {
            // 指定されたIDのユーザーは存在しない.
            String msg = String.format("User is not found. UserID=%d", userID );
            LoulanDNSSystemServiceException execption = new LoulanDNSSystemServiceException( msg );
            throw execption;
        }

        if ( isExistingUserGroup(userGroupID) == false)
        {
            // 指定されたIDのユーザーグループは存在しない.
            String msg = String.format("UserGroup is not found. UserGroupID=%d", userGroupID );
            LoulanDNSSystemServiceException execption = new LoulanDNSSystemServiceException( msg );
            throw execption;
        }


        List<UserGroupMapping> mappingList = getUserGroupMappingListByUserGroupdID(userGroupID);
        for( UserGroupMapping mapping : mappingList)
        {
            if ( userID == mapping.getUserID() )
            {
                // 指定されたユーザーとグループはDB上において所属関係にある.
                return true;
            }
        }

        return false;
    }


    
    // ユーザーグループ権限情報(USER_GROUP_PRIVILEGEテーブル)を取得する.
    public UserGroupPrivilege getUserGroupPrivilege(long userGroupPrivilegeID) throws LoulanDNSSystemServiceException
    {
        List<UserGroupPrivilege> userGroupPrivilegeList = userGroupPrivilegeRepository.findByUserGroupPrivilegeID(userGroupPrivilegeID);

        if (userGroupPrivilegeList.isEmpty())
        {
            return null;
        }

        UserGroupPrivilege userGroupPrivilege = userGroupPrivilegeList.get(0);
        return userGroupPrivilege;
    }


    // ユーザーグループ権限情報(USER_GROUP_PRIVILEGEテーブル)を取得する.
    public UserGroupPrivilege getUserGroupPrivilegeByUserGroupID(long userGroupID) throws LoulanDNSSystemServiceException
    {
        List<UserGroupPrivilege> userGroupPrivilegeList = userGroupPrivilegeRepository.findByUserGroupID( userGroupID );

        if (userGroupPrivilegeList.isEmpty())
        {
            return null;
        }

        UserGroupPrivilege userGroupPrivilege = userGroupPrivilegeList.get(0);
        return userGroupPrivilege;
    }
    

    // DoHインスタンス情報(DOH_SERVER_INSTANCEテーブル)の情報を取得する.
    public DNSServiceInstance getDNServiceInstance(long dnsServiceInstanceID) throws LoulanDNSSystemServiceException
    {
        List<DNSServiceInstance> dnsServiceInstancesList = dnsServiceInstanceRepository.findByDNSServiceInstanceID(dnsServiceInstanceID);
        if ( dnsServiceInstancesList.isEmpty() )
        {
            return null;
        }

        DNSServiceInstance dnsServiceInstance = dnsServiceInstancesList.get(0);
        return dnsServiceInstance;
    }


    // DoHインスタンス情報(DOH_SERVER_INSTANCEテーブル)の一覧を取得する.
    public List<DNSServiceInstance> getDNSServiceInstanceList() throws LoulanDNSSystemServiceException
    {
        List<DNSServiceInstance> dohServerInstancesList = dnsServiceInstanceRepository.findAll();
        return dohServerInstancesList;
    }


    // DNSサービスインスタンス情報(DNS_SERVICE_INSTANCEテーブル)の一覧を取得する.
    public List<DNSServiceInstance> getDNSServiceInstanceListByUserID(long userID) throws LoulanDNSSystemServiceException
    {
        List<DNSServiceInstance> dnsServiceInstancesList = dnsServiceInstanceRepository.findByUserID(userID);
        return dnsServiceInstancesList;
    }

    // DNSサービスインスタンス情報(DNS_SERVICE_INSTANCEテーブル)をユーザーIDとDNSサービスインスタンス名をもとに取得する.
    public DNSServiceInstance getDNSServiceInstance(long userID, String dohServerInstanceName) throws LoulanDNSSystemServiceException
    {
        List<DNSServiceInstance> dnsServiceInstancesList = getDNSServiceInstanceListByUserID(userID);
        if ( dnsServiceInstancesList == null )
        {
            return null;
        }

        DNSServiceInstance dnsServiceInstance = null;
        for( DNSServiceInstance tmpDNSServiceInstance : dnsServiceInstancesList )
        {
            if ( dohServerInstanceName.equals( tmpDNSServiceInstance.getDNSServiceInstanceName() ) )
            {
                dnsServiceInstance = tmpDNSServiceInstance;
                break;
            }
        }

        return dnsServiceInstance;
    }

    // DNSサービスインスタンス情報(DNS_SERVICE_INSTANCEテーブル)の一覧を取得する.
    public DNSServiceInstance getDNSServiceInstance(long dnsServiceInstanceID) throws LoulanDNSSystemServiceException
    {
        List<DNSServiceInstance> dnsServiceInstanceList = dnsServiceInstanceRepository.findByDNSServiceInstanceID(dnsServiceInstanceID);

        if ( dnsServiceInstanceList == null )
        {
            return null;
        }

        if ( dnsServiceInstanceList.size() == 0 )
        {
            return null;
        }

        DNSServiceInstance dnsServiceInstance = dnsServiceInstanceList.get(0);
        return dnsServiceInstance;
    }





    // DoHインスタンス情報(DOH_SERVER_INSTANCEテーブル)を新規追加する.
    public DNSServiceInstance createDNSServiceInstance(DNSServiceInstance dnsServiceInstance) throws LoulanDNSSystemServiceException
    {
        // DOH_INSTANCE_IDはNULLに設定して、DB側で新規採番する.
        dnsServiceInstance.setDNSServiceInstanceID(null);

        // レコードの更新日付と作成日付を現在時刻で設定する.
        String currentDate = LoulanDNSUtils.getCurrentDateTimeString();
        dnsServiceInstance.setUpdateDate( currentDate );
        dnsServiceInstance.setCreateDate( currentDate );


        dnsServiceInstanceRepository.save(dnsServiceInstance);

        DNSServiceInstance savedDNSServiceInstance = getDNSServiceInstance( dnsServiceInstance.getUserID(), dnsServiceInstance.getDNSServiceInstanceName() );
        return savedDNSServiceInstance;
    }

    // DoHインスタンス情報(DOH_SERVER_INSTANCEテーブル)を更新する.
    public DNSServiceInstance updateDoHServerInstance(DNSServiceInstance dnsServiceInstance) throws LoulanDNSSystemServiceException
    {

        long dnsServiceInstanceID = dnsServiceInstance.getDNSServiceInstanceID();
        if ( getDNSServiceInstance( dnsServiceInstanceID ) == null )
        {
            // 指定されたIDのDNSサービスインスタンスは存在しない.
            String msg = String.format("DNSServiceInstance is not found. DNSServiceInstanceID=%d", dnsServiceInstanceID );
            LoulanDNSSystemServiceException execption = new LoulanDNSSystemServiceException( msg );
            throw execption;
        }


        // レコードの更新日付を現在時刻で設定する.
        String currentDate = LoulanDNSUtils.getCurrentDateTimeString();
        dnsServiceInstance.setUpdateDate( currentDate );

        dnsServiceInstanceRepository.save(dnsServiceInstance);


        DNSServiceInstance savedDoHServerInstance = getDNSServiceInstance( dnsServiceInstance.getUserID(), dnsServiceInstance.getDNSServiceInstanceName() );
        return savedDoHServerInstance;
    }


    // DNSインスタンス情報(DNS_SERVICE_INSTANCEテーブル)を削除する.
    public void deleteDNSServiceInstance(long dnsServiceInstanceID ) throws LoulanDNSSystemServiceException
    {
        DNSServiceInstance dnsServiceInstance = getDNSServiceInstance( dnsServiceInstanceID );
        if ( dnsServiceInstance == null )
        {
            // 指定されたIDのDoHインスタンスのレコードは存在しない.
            String msg = String.format("DNSServiceInstance is not found. dnsServiceInstanceID=%d", dnsServiceInstanceID );
            LoulanDNSSystemServiceException execption = new LoulanDNSSystemServiceException( msg );
            throw execption;
        }

        dnsServiceInstanceRepository.deleteDNSServiceInstanceByDNSServiceInstanceID( dnsServiceInstance.getDNSServiceInstanceID() );
    }


    // DNSサービスインスタンスプロパティ情報(DNS_SERVICE_INSTANCE_PROPERTIESテーブル)の一覧をDNSサービスインスタンスIDをキーに取得する.
    public List<DNSServiceInstanceProperties> getDNSServiceInstancePropertiesListByDNSServiceInstanceID(long dnsServiceInstanceID) throws LoulanDNSSystemServiceException
    {
        List<DNSServiceInstanceProperties> dnsServiceInstancePropertiesList = dnsServiceInstancePropertiesRepository.findByDNSServiceInstanceID(dnsServiceInstanceID);
        return dnsServiceInstancePropertiesList;
    }

    // DNSサービスインスタンスプロパティ情報(DNS_SERVICE_INSTANCE_PROPERTIESテーブル)をDNSサービスインスタンスIDとプロパティ名をキーに取得する.
    public DNSServiceInstanceProperties getDNSServiceInstancePropertiesByPropertyKey(long dnsServiceInstanceID, String propetyKey) throws LoulanDNSSystemServiceException
    {
        List<DNSServiceInstanceProperties> propertiesList = getDNSServiceInstancePropertiesListByDNSServiceInstanceID(dnsServiceInstanceID);
        if ( propertiesList == null )
        {
            return null;
        }

        DNSServiceInstanceProperties properties = null;
        for( DNSServiceInstanceProperties tmpProperties : propertiesList )
        {
            if ( propetyKey.equals( tmpProperties.getDnsServiceInstancePropertyKey() ) )
            {
                properties = tmpProperties;
                break;
            }
        }

        return properties;
    }


    // DNSサービスインスタンスプロパティ情報(DNS_SERVICE_INSTANCE_PROPERTIESテーブル)をDNSサービスインスタンスプロパティIDをキーに取得する.
    public DNSServiceInstanceProperties getDNSServiceInstanceProperties(long dnsServiceInstancePropertiesID) throws LoulanDNSSystemServiceException
    {
        List<DNSServiceInstanceProperties> list = dnsServiceInstancePropertiesRepository.findByDNSServiceInstancePropertyID( dnsServiceInstancePropertiesID );
        if ( list.size() == 0 )
        {
            return null;
        }

        DNSServiceInstanceProperties dnsServiceInstanceProperties = list.get(0);
        return dnsServiceInstanceProperties;
    }


    // 指定されたDNSサービスインスタンスプロパティ情報が既に存在するかを判定する.
    public boolean isExistsDNSServiceInstancePropertiesByPropertyKey(long dnsServiceInstanceID, String propertyKey ) throws LoulanDNSSystemServiceException
    {
        DNSServiceInstanceProperties properties = getDNSServiceInstancePropertiesByPropertyKey(dnsServiceInstanceID, propertyKey);
        if ( properties == null )
        {
            return false;
        }

        return true;
    }
    


    // DNSサービスインスタンスプロパティ情報(DNS_SERVICE_INSTANCE_PROPERTIESテーブル)を新規作成する.
    public DNSServiceInstanceProperties createDNSServiceInstanceProperties(DNSServiceInstanceProperties dnsServiceInstanceProperties) throws LoulanDNSSystemServiceException
    {

        if ( isExistsDNSServiceInstancePropertiesByPropertyKey(dnsServiceInstanceProperties.getDnsServiceInstanceID(), dnsServiceInstanceProperties.getDnsServiceInstancePropertyKey() ) )
        {
            // 指定されたプロパティ情報は既に存在する.
            String msg = String.format("DNSServiceInstanceProperties is already exists. userID=%d, dnsServiceInstancePropertyKey=%s", dnsServiceInstanceProperties.getDnsServiceInstanceID(), dnsServiceInstanceProperties.getDnsServiceInstancePropertyKey() );
            LoulanDNSSystemServiceException execption = new LoulanDNSSystemServiceException( msg );
            throw execption;
        }

        // 主キーのID値をnullで設定しDB側で採番させる.
        dnsServiceInstanceProperties.setDnsServiceInstanceID(null);

        // レコードの更新日付と作成日付を現在時刻で設定する.
        String currentDate = LoulanDNSUtils.getCurrentDateTimeString();
        dnsServiceInstanceProperties.setUpdateDate( currentDate );
        dnsServiceInstanceProperties.setCreateDate( currentDate );

        dnsServiceInstancePropertiesRepository.save(dnsServiceInstanceProperties);

        DNSServiceInstanceProperties savedDNSServiceInstanceProperties = getDNSServiceInstancePropertiesByPropertyKey(dnsServiceInstanceProperties.getDnsServiceInstanceID(), dnsServiceInstanceProperties.getDnsServiceInstancePropertyKey() );
        return savedDNSServiceInstanceProperties;
    }


    // DNSサービスインスタンスプロパティ情報(DNS_SERVICE_INSTANCE_PROPERTIESテーブル)を更新する.
    public DNSServiceInstanceProperties updateDNSServiceInstanceProperties(DNSServiceInstanceProperties dnsServiceInstanceProperties) throws LoulanDNSSystemServiceException
    {
        DNSServiceInstanceProperties tmpProperties = 
            getDNSServiceInstancePropertiesByPropertyKey( dnsServiceInstanceProperties.getDnsServiceInstanceID(), dnsServiceInstanceProperties.getDnsServiceInstancePropertyKey() );
        if (  tmpProperties != null && dnsServiceInstanceProperties.getDnsServiceInstancePropertyKey().equals( tmpProperties.getDnsServiceInstancePropertyKey() ) )
        {
            // 指定されたプロパティ情報は既に存在する.
            String msg = String.format("DNSServiceInstanceProperties is already exists. userID=%d, dnsServiceInstancePropertyKey=%s", dnsServiceInstanceProperties.getDnsServiceInstanceID(), dnsServiceInstanceProperties.getDnsServiceInstancePropertyKey() );
            LoulanDNSSystemServiceException execption = new LoulanDNSSystemServiceException( msg );
            throw execption;
        }


        // レコードの更新日付を現在時刻で設定する.
        String currentDate = LoulanDNSUtils.getCurrentDateTimeString();
        dnsServiceInstanceProperties.setUpdateDate( currentDate );

        dnsServiceInstancePropertiesRepository.save( dnsServiceInstanceProperties );

        DNSServiceInstanceProperties savedDNSServiceInstanceProperties = getDNSServiceInstancePropertiesByPropertyKey(dnsServiceInstanceProperties.getDnsServiceInstanceID(), dnsServiceInstanceProperties.getDnsServiceInstancePropertyKey() );
        return savedDNSServiceInstanceProperties;
    }




    // DNSサービスインスタンスプロパティ情報(DNS_SERVICE_INSTANCE_PROPERTIESテーブル)を削除する.
    public void deleteDNSServiceInstanceProperties(long dnsServiceInstancePropertyID ) throws LoulanDNSSystemServiceException
    {
        DNSServiceInstanceProperties dnsServiceInstanceProperties = getDNSServiceInstanceProperties( dnsServiceInstancePropertyID );
        if ( dnsServiceInstanceProperties == null )
        {
            // 指定されたIDのDNSリゾルバのレコードは存在しない.
            String msg = String.format("DNSServiceInstanceProperties is not found. dnsServiceInstancePropertyID=%d", dnsServiceInstancePropertyID );
            LoulanDNSSystemServiceException execption = new LoulanDNSSystemServiceException( msg );
            throw execption;
        }

        dnsServiceInstancePropertiesRepository.deleteDNSServiceInstancePropertiesByDNSServiceInstancePropertyID( dnsServiceInstancePropertyID );
    }





    // DNSリゾルバ情報(DNS_RESOLVERテーブル)のレコードを新規作成する.
    public DNSResolverInstance createDNSResolverInstance(DNSResolverInstance dnsResolverInstance) throws LoulanDNSSystemServiceException
    {

        if( isExistDNSResolverInstanceByName(dnsResolverInstance.getUserID(), dnsResolverInstance.getDnsResolverInstanceName() ) == true )
        {
            // 指定されたDNSリゾルバ情報は既にDB上に存在する.
            String msg = String.format("DNSResolverInstance is already exists. userID=%d, dnsResolverInstanceName=%s", dnsResolverInstance.getUserID(),  dnsResolverInstance.getDnsResolverInstanceName() );
            LoulanDNSSystemServiceException execption = new LoulanDNSSystemServiceException( msg );
            throw execption;
        }
        
        // DNS_RESOLVER_INSTANCE_IDはNULLに設定して、DB側で新規採番する.
        dnsResolverInstance.setDnsResolverInstanceID(null);

        // レコードの更新日付と作成日付を現在時刻で設定する.
        String currentDate = LoulanDNSUtils.getCurrentDateTimeString();
        dnsResolverInstance.setUpdateDate( currentDate );
        dnsResolverInstance.setCreateDate( currentDate );


        dnsResolverInstanceRepository.save(dnsResolverInstance);


        DNSResolverInstance savedDNSResolverInstance = getDNSResolverInstanceByName( dnsResolverInstance.getUserID(), dnsResolverInstance.getDnsResolverInstanceName() );
        return savedDNSResolverInstance;  
    }


    // DNSリゾルバ情報(DNS_RESOLVER_INSTANCEテーブル)のレコードを更新する.
    public DNSResolverInstance updateDNSResolverInstance(DNSResolverInstance dnsResolverInstance) throws LoulanDNSSystemServiceException
    {
        DNSResolverInstance tmpDNSResolverInstance = getDNSResolverInstanceByName(dnsResolverInstance.getUserID(), dnsResolverInstance.getDnsResolverInstanceName() );
        if ( dnsResolverInstance.getDnsResolverInstanceID() != tmpDNSResolverInstance.getDnsResolverInstanceID() )
        {
            // レコード更新により重複するDNSリゾルバ情報が作成されようとしている.
            String msg = String.format("DNSResolverInstance is already exists. userID=%d, dnsResolverInstanceName=%s", dnsResolverInstance.getUserID(),  dnsResolverInstance.getDnsResolverInstanceName() );
            LoulanDNSSystemServiceException execption = new LoulanDNSSystemServiceException( msg );
            throw execption;
        }
        

        // レコードの更新日付を現在時刻で設定する.
        String currentDate = LoulanDNSUtils.getCurrentDateTimeString();
        dnsResolverInstance.setUpdateDate( currentDate );

        dnsResolverInstanceRepository.save(dnsResolverInstance);

        DNSResolverInstance savedDNSResolverInstance = getDNSResolverInstanceByName( dnsResolverInstance.getUserID(), dnsResolverInstance.getDnsResolverInstanceName() );        
        return savedDNSResolverInstance;  
    }


    // DNSリゾルバ情報(DNS_RESOLVERテーブル)のレコードを削除する.
    public void deleteDNSResolverInstance(long dnsResolverInstanceID ) throws LoulanDNSSystemServiceException
    {
        DNSResolverInstance dnsResolverInstance = getDNSResolverInstanceByDNSResolverInstanceID( dnsResolverInstanceID);
        if ( dnsResolverInstance == null )
        {
            // 指定されたIDのDNSリゾルバのレコードは存在しない.
            String msg = String.format("DNSResolverInstance is not found. DNSResolverInstanceID=%d", dnsResolverInstanceID );
            LoulanDNSSystemServiceException execption = new LoulanDNSSystemServiceException( msg );
            throw execption;
        }

        dnsResolverInstanceRepository.deleteDNSResolverInstanceByDNSResolverInstanceID( dnsResolverInstanceID );
    }



    // DNSリゾルバ情報(DNS_RESOLVERテーブル)を取得する.
    public List<DNSResolverInstance> getDNSResolverInstanceByUserID(long userID) throws LoulanDNSSystemServiceException
    {
        if ( isExistingUser(userID) == false )
        {
            // 指定されたIDのユーザーは存在しない.
            String msg = String.format("User is not found. userID=%d", userID );
            LoulanDNSSystemServiceException execption = new LoulanDNSSystemServiceException( msg );
            throw execption;
        }

        List<DNSResolverInstance> dnsResolverInstanceList = dnsResolverInstanceRepository.findByUserID(userID);
        return dnsResolverInstanceList;
    }


    // DNSリゾルバ情報(DNS_RESOLVERテーブル)を取得する.
    public DNSResolverInstance getDNSResolverInstanceByName(long userID, String dnsResolverInstanceName) throws LoulanDNSSystemServiceException
    {
        List<DNSResolverInstance> dnsResolverInstanceList = getDNSResolverInstanceByUserID(userID);
        if ( dnsResolverInstanceList == null )
        {
            return null;
        }

        DNSResolverInstance dnsResolverInstance = null;
        for( DNSResolverInstance tmpDNSResolverInstance : dnsResolverInstanceList )
        {
            if ( dnsResolverInstanceName.equals( tmpDNSResolverInstance) )
            {
                dnsResolverInstance = tmpDNSResolverInstance;
            }
        }

        return dnsResolverInstance;
    }


    // DNSリゾルバ情報(DNS_RESOLVERテーブル)を取得する.
    public DNSResolverInstance getDNSResolverInstanceByDNSResolverInstanceID(long dnsResolverInstanceID) throws LoulanDNSSystemServiceException
    {
        List<DNSResolverInstance> dnsResolverInstanceList = dnsResolverInstanceRepository.findByDNSResolverInstanceID(dnsResolverInstanceID);

        if ( dnsResolverInstanceList.isEmpty() )
        {
            return null;
        }

        DNSResolverInstance dnsResolverInstance = dnsResolverInstanceList.get(0);
        return dnsResolverInstance;
    }


    // 指定されたDNSリゾルバ情報(DNS_RESOLVERテーブル)が既に存在するかを判定する.
    public boolean isExistDNSResolverInstanceByName(long userID, String dnsResolverInstanceName) throws LoulanDNSSystemServiceException
    {
        DNSResolverInstance dnsResolverInstnce = getDNSResolverInstanceByName(userID, dnsResolverInstanceName);
        if ( dnsResolverInstnce == null )
        {
            return false;
        }

        return true;
    }


    // DNSリゾルバプロパティ情報(DNS_RESOLVER_PROPERTIESテーブル)の一覧をDNSリゾルバIDをキーに取得する.
    public List<DNSResolverInstanceProperties> getDNSResolverInstancePropertiesListByDNSResolverInstanceID(long dnsResolverInstanceID) throws LoulanDNSSystemServiceException
    {
        List<DNSResolverInstanceProperties> dnsResolverInstancePropertiesList = dnsResolverInstancePropertiesRepository.findByDNSResolverInstanceID(dnsResolverInstanceID);
        return dnsResolverInstancePropertiesList;
    }

    // DNSリゾルバプロパティ情報(DNS_RESOLVER_PROPERTIESテーブル)をDNSリゾルバIDとプロパティ名をキーに取得する.
    public DNSResolverInstanceProperties getDNSResolverInstancePropertiesByPropertyKey(long dnsResolverInstanceID, String propetyKey) throws LoulanDNSSystemServiceException
    {
        List<DNSResolverInstanceProperties> propertiesList = getDNSResolverInstancePropertiesListByDNSResolverInstanceID(dnsResolverInstanceID);
        if ( propertiesList == null )
        {
            return null;
        }

        DNSResolverInstanceProperties properties = null;
        for( DNSResolverInstanceProperties tmpProperties : propertiesList )
        {
            if ( propetyKey.equals( tmpProperties.getDnsResolverInstancePropertyKey() ) )
            {
                properties = tmpProperties;
                break;
            }
        }

        return properties;
    }


    // DNSリゾルバプロパティ情報(DNS_RESOLVER_PROPERTIESテーブル)をDNSリゾルバプロパティIDをキーに取得する.
    public DNSResolverInstanceProperties getDNSResolverInstanceProperties(long dnsResolverInstancePropertiesID) throws LoulanDNSSystemServiceException
    {
        List<DNSResolverInstanceProperties> list = dnsResolverInstancePropertiesRepository.findByDNSResolverInstancePropertyID( dnsResolverInstancePropertiesID );
        if ( list.size() == 0 )
        {
            return null;
        }

        DNSResolverInstanceProperties dnsResolverInstanceProperties = list.get(0);
        return dnsResolverInstanceProperties;
    }


    // 指定されたDNSリゾルバプロパティ情報が既に存在するかを判定する.
    public boolean isExistsDNSResolverInstancePropertiesByPropertyKey(long dnsResolverInstanceID, String propertyKey ) throws LoulanDNSSystemServiceException
    {
        DNSResolverInstanceProperties properties = getDNSResolverInstancePropertiesByPropertyKey(dnsResolverInstanceID, propertyKey);
        if ( properties == null )
        {
            return false;
        }

        return true;
    }
    


    // DNSリゾルバプロパティ情報(DNS_RESOLVER_PROPERTIESテーブル)を新規作成する.
    public DNSResolverInstanceProperties createDNSResolverInstanceProperties(DNSResolverInstanceProperties dnsResolverInstanceProperties) throws LoulanDNSSystemServiceException
    {

        if ( isExistsDNSResolverInstancePropertiesByPropertyKey(dnsResolverInstanceProperties.getDnsResolverInstanceID(), dnsResolverInstanceProperties.getDnsResolverInstancePropertyKey()) )
        {
            // 指定されたプロパティ情報は既に存在する.
            String msg = String.format("DNSResolverInstanceProperties is already exists. userID=%d, dnsResolverInstancePropertyKey=%s", dnsResolverInstanceProperties.getDnsResolverInstanceID(), dnsResolverInstanceProperties.getDnsResolverInstancePropertyKey() );
            LoulanDNSSystemServiceException execption = new LoulanDNSSystemServiceException( msg );
            throw execption;
        }

        // 主キーのID値をnullで設定しDB側で採番させる.
        dnsResolverInstanceProperties.setDnsResolverInstancePropertyID(null);

        // レコードの更新日付と作成日付を現在時刻で設定する.
        String currentDate = LoulanDNSUtils.getCurrentDateTimeString();
        dnsResolverInstanceProperties.setUpdateDate( currentDate );
        dnsResolverInstanceProperties.setCreateDate( currentDate );

        dnsResolverInstancePropertiesRepository.save(dnsResolverInstanceProperties);

        DNSResolverInstanceProperties savedDNSResolverInstanceProperties = getDNSResolverInstancePropertiesByPropertyKey(dnsResolverInstanceProperties.getDnsResolverInstanceID(), dnsResolverInstanceProperties.getDnsResolverInstancePropertyKey() );
        return savedDNSResolverInstanceProperties;
    }


    // DNSリゾルバプロパティ情報(DNS_RESOLVER_PROPERTIESテーブル)を更新する.
    public DNSResolverInstanceProperties updateDNSResolverInstanceProperties(DNSResolverInstanceProperties dnsResolverInstanceProperties) throws LoulanDNSSystemServiceException
    {
        DNSResolverInstanceProperties tmpProperties = 
            getDNSResolverInstancePropertiesByPropertyKey( dnsResolverInstanceProperties.getDnsResolverInstanceID(), dnsResolverInstanceProperties.getDnsResolverInstancePropertyKey() );
        if (  tmpProperties != null && dnsResolverInstanceProperties.getDnsResolverInstancePropertyKey().equals( tmpProperties.getDnsResolverInstancePropertyKey()) )
        {
            // 指定されたプロパティ情報は既に存在する.
            String msg = String.format("DNSResolverInstanceProperties is already exists. userID=%d, dnsResolverInstancePropertyKey=%s", dnsResolverInstanceProperties.getDnsResolverInstanceID(), dnsResolverInstanceProperties.getDnsResolverInstancePropertyKey() );
            LoulanDNSSystemServiceException execption = new LoulanDNSSystemServiceException( msg );
            throw execption;
        }


        // レコードの更新日付を現在時刻で設定する.
        String currentDate = LoulanDNSUtils.getCurrentDateTimeString();
        dnsResolverInstanceProperties.setUpdateDate( currentDate );

        dnsResolverInstancePropertiesRepository.save( dnsResolverInstanceProperties );

        DNSResolverInstanceProperties savedDNSResolverInstanceProperties = getDNSResolverInstancePropertiesByPropertyKey(dnsResolverInstanceProperties.getDnsResolverInstanceID(), dnsResolverInstanceProperties.getDnsResolverInstancePropertyKey() );
        return savedDNSResolverInstanceProperties;
    }




    // DNSリゾルバプロパティ情報(DNS_RESOLVER_PROPERTIESテーブル)を削除する.
    public void deleteDNSResolverInstanceProperties(long dnsResolverInstancePropertyID ) throws LoulanDNSSystemServiceException
    {
        DNSResolverInstanceProperties dnsResolverInstanceProperties = getDNSResolverInstanceProperties( dnsResolverInstancePropertyID );
        if ( dnsResolverInstanceProperties == null )
        {
            // 指定されたIDのDNSリゾルバのレコードは存在しない.
            String msg = String.format("DNSResolverInstanceProperties is not found. dnsResolverInstancePropertyID=%d", dnsResolverInstancePropertyID );
            LoulanDNSSystemServiceException execption = new LoulanDNSSystemServiceException( msg );
            throw execption;
        }

        dnsResolverInstancePropertiesRepository.deleteDNSResolverInstancePropertiesByDNSResolverInstancePropertyID( dnsResolverInstancePropertyID );
    }


    // DNSサービスエンドポイント情報(DNS_SERVICE_ENDPOINTテーブル)のDBレコードを取得する.
    public DNSServiceEndpointInstance getDNSServiceEndpointInstance(long dnsServiceEndpointInstanceID) throws LoulanDNSSystemServiceException
    {
        List<DNSServiceEndpointInstance> list = dnsServiceEndpointInstanceRepository.findByDNSServiceEndpointInstanceID(dnsServiceEndpointInstanceID);
 
        if ( list.isEmpty() )
        {
            return null;
        }

        DNSServiceEndpointInstance dnsServiceEndpointInstance = list.get(0);
        return dnsServiceEndpointInstance;
    }


    // DNSサービスエンドポイント情報(DNS_SERVICE_ENDPOINTテーブル)のDBレコードを取得する.
    public DNSServiceEndpointInstance getDNSServiceEndpointInstanceByDNSServiceEndpointName(long dnsServiceInstanceID, String dnsServiceEndpointName) throws LoulanDNSSystemServiceException
    {
        List<DNSServiceEndpointInstance> list = getDNSServiceEndpointInstanceListByDNSServiceInstanceID(dnsServiceInstanceID);

        DNSServiceEndpointInstance dnsServiceEndpointInstance = null;
        for( var record : list )
        {
            if ( record.dnsServiceEndpointInstanceName.equals(dnsServiceEndpointName) )
            {
                dnsServiceEndpointInstance = record;
                break;
            }
        }

        return dnsServiceEndpointInstance;
    }

    // 指定されたDNSサービスインスタンスIDに紐づく、DNSサービスエンドポイント情報(DNS_SERVICE_ENDPOINTテーブル)のDBレコードの一覧を、DNSサービスインスタンスを取得する.
    public List<DNSServiceEndpointInstance> getDNSServiceEndpointInstanceListByDNSServiceInstanceID(long dnsServiceInstanceID) throws LoulanDNSSystemServiceException
    {
        DNSServiceInstance  dnsServiceInstance = getDNSServiceInstance(dnsServiceInstanceID);        
        if ( dnsServiceInstance == null )
        {
            String msg = String.format("Specified DNSServiceInstance is not exists. dnsServiceInstanceID=%d", dnsServiceInstanceID);
            LoulanDNSSystemServiceException exception = new LoulanDNSSystemServiceException(msg);
            throw exception;
        }

        List<DNSServiceEndpointInstance> list = dnsServiceEndpointInstanceRepository.findByDNSServiceInstanceID(dnsServiceInstanceID);
        return list;
    }


    // DNSサービスエンドポイント情報(DNS_SERVICE_ENDPOINTテーブル)のDBレコードを新規作成する.
    public DNSServiceEndpointInstance createDNSServiceEndpointInstance(DNSServiceEndpointInstance dnsServiceEndpointInstance) throws LoulanDNSSystemServiceException
    {


        Long dnsServiceInstanceID = dnsServiceEndpointInstance.getDnsServiceInstanceID();
        String dnsServiceEndpointName = dnsServiceEndpointInstance.getDnsServiceEndpointInstanceName();


        if ( isExitstsDNSServiceEndpointInstance(dnsServiceInstanceID, dnsServiceEndpointName ) == true )
        {
            // 指定されたDNSサービスエンドポイント情報は既に存在する.
            String msg = String.format("DNSServiceEndpointInstance is already exists. dnsServiceInstanceID=%d, dnsServiceEndpointName=%s", dnsServiceInstanceID, dnsServiceEndpointName );
            LoulanDNSSystemServiceException execption = new LoulanDNSSystemServiceException( msg );
            throw execption;
        }

        // 主キーのID値をnullで設定しDB側で採番させる.
        dnsServiceEndpointInstance.setDNSServiceEndpointInstanceID(null);


        // レコードの更新日付と作成日付を現在時刻で設定する.
        String currentDate = LoulanDNSUtils.getCurrentDateTimeString();
        dnsServiceEndpointInstance.setUpdateDate( currentDate );
        dnsServiceEndpointInstance.setCreateDate( currentDate );

        dnsServiceEndpointInstanceRepository.save( dnsServiceEndpointInstance );

        DNSServiceEndpointInstance savedDNSServiceEndpointInstance = getDNSServiceEndpointInstanceByDNSServiceEndpointName(dnsServiceInstanceID, dnsServiceEndpointName);
        return savedDNSServiceEndpointInstance;    
    }

    // DNSサービスエンドポイント情報(DNS_SERVICE_ENDPOINTテーブル)のDBレコードを更新する.
    public DNSServiceEndpointInstance updateDNSServiceEndpointInstance(DNSServiceEndpointInstance dnsServiceEndpointInstance) throws LoulanDNSSystemServiceException
    {
        List<DNSServiceEndpointInstance> list = dnsServiceEndpointInstanceRepository.findByDNSServiceEndpointInstanceID(dnsServiceEndpointInstance.getDnsServiceEndpointInstanceID());
        if ( list.isEmpty() )
        {
            // 指定されたDNSサービスエンドポイント情報のDBレコードは存在しない.
            String msg = String.format("DNSServiceEndpointInstance is NOT exists. dnsServiceEndpointInstanceID=%d, dnsServiceEndpointName=%s", dnsServiceEndpointInstance.getDnsServiceEndpointInstanceID(), dnsServiceEndpointInstance.getDnsServiceEndpointInstanceName() );
            LoulanDNSSystemServiceException execption = new LoulanDNSSystemServiceException( msg );
            throw execption;
        }

        // レコードの更新日付を現在時刻で設定する.
        String currentDate = LoulanDNSUtils.getCurrentDateTimeString();
        dnsServiceEndpointInstance.setUpdateDate( currentDate );

        dnsServiceEndpointInstanceRepository.save( dnsServiceEndpointInstance );

        DNSServiceEndpointInstance savedDNSServiceEndpointInstance = getDNSServiceEndpointInstanceByDNSServiceEndpointName(dnsServiceEndpointInstance.getDnsServiceInstanceID(), dnsServiceEndpointInstance.getDnsServiceEndpointInstanceName());
        return savedDNSServiceEndpointInstance;
    }


    // DNSサービスエンドポイント情報(DNS_SERVICE_ENDPOINTテーブル)のDBレコードを削除する.
    public void deleteDNSServiceEndpointInstance(long dnsServiceEndpointInstanceID ) throws LoulanDNSSystemServiceException
    {
        DNSServiceEndpointInstance dnsServiceEndpointInstance = getDNSServiceEndpointInstance(dnsServiceEndpointInstanceID);

        if ( dnsServiceEndpointInstance == null )
        {
            // 指定されたDNSサービスエンドポイント情報のDBレコードは存在しない.
            String msg = String.format("DNSServiceEndpointInstance is NOT exists. dnsServiceEndpointInstanceID=%d, dnsServiceEndpointName=%s", dnsServiceEndpointInstance.getDnsServiceEndpointInstanceID(), dnsServiceEndpointInstance.getDnsServiceEndpointInstanceName() );
            LoulanDNSSystemServiceException execption = new LoulanDNSSystemServiceException( msg );
            throw execption;
        }

        dnsServiceEndpointInstanceRepository.deleteByDNSServiceEndpointInstanceID(dnsServiceEndpointInstanceID);
            
    }


    /**
     * 指定されたDNSサービスエンドポイント情報がDB上に既に存在するかを判定する.
     * 
     * @param dnsServiceInstanceID      DNSサービスエンドポイントが所属するDNSサービスインスタンスのDNSサービスインスタンスID
     * @param dnsServiceEndpointName    DNSサービスエンドポイント名
     * @return
     * @throws LoulanDNSSystemServiceException
     */
    public boolean isExitstsDNSServiceEndpointInstance(long dnsServiceInstanceID, String dnsServiceEndpointName) throws LoulanDNSSystemServiceException
    {
        DNSServiceEndpointInstance dnsServiceEndpointInstance = getDNSServiceEndpointInstanceByDNSServiceEndpointName(dnsServiceInstanceID, dnsServiceEndpointName);
        if ( dnsServiceEndpointInstance == null )
        {
            return false;
        }

        return true;
    }



    /**
     * DNSサービスエンドポイントのインスタンスのプロパティを、プロパティレコードのID値をもとに取得する.
     * 
     * @param dnsServiceEndpointInstancePropertyID
     * @return
     * @throws LoulanDNSSystemServiceException
     */
    public DNSServiceEndpointInstanceProperties getDNSServiceEndpointInstanceProperties(long dnsServiceEndpointInstancePropertyID) throws LoulanDNSSystemServiceException
    {
        List<DNSServiceEndpointInstanceProperties> list = dnsServiceEndpointInstancePropertiesRepository.findByDNSServiceEndpointInstancePropertyID(dnsServiceEndpointInstancePropertyID);

        if ( list.isEmpty() ) 
        {
            return null;
        }

        if ( list.size() > 1 )
        {
            // 主キーのIDで取得したのにレコードが複数件存在するのは異常.
            String msg = String.format("Some DNSServiceEndpointInstanceProperties records are exists. The records count is %d. dnsServiceEndpointInstancePropertyID=%d", list.size(), dnsServiceEndpointInstancePropertyID );
            LoulanDNSSystemServiceException execption = new LoulanDNSSystemServiceException( msg );
            throw execption;
        }

        DNSServiceEndpointInstanceProperties dnsServiceEndpointInstanceProperties = list.get(0);
        return dnsServiceEndpointInstanceProperties;
    }


    /**
     * DNSサービスエンドポイントのインスタンスのプロパティのリストを、指定したDNSサービスエンドポイントインスタンスのID値をもとに取得する.
     * 
     * @param dnsServiceEndpointInstanceID
     * @return
     * @throws LoulanDNSSystemServiceException
     */
    public List<DNSServiceEndpointInstanceProperties> getDNSServiceEndpointInstancePropertiesByDNSServiceEndpointInstanceID(long dnsServiceEndpointInstanceID) throws LoulanDNSSystemServiceException
    {

        List<DNSServiceEndpointInstanceProperties> list; 

        DNSServiceEndpointInstance dnsServiceEndpointInstance = getDNSServiceEndpointInstance(dnsServiceEndpointInstanceID); 
        if ( dnsServiceEndpointInstance == null )
        {
            // 指定されたDNSサービスエンドポイント情報のDBレコードは存在しない.
            String msg = String.format("DNSServiceEndpointInstance is NOT exists. dnsServiceEndpointInstanceID=%d", dnsServiceEndpointInstanceID );
            LoulanDNSSystemServiceException execption = new LoulanDNSSystemServiceException( msg );
            throw execption;
        }

        list = dnsServiceEndpointInstancePropertiesRepository.findByDNSServiceEndpointInstanceID(dnsServiceEndpointInstanceID);

        if ( list == null )
        {
            // 指定されたDNSサービスエンドポイント情報のプロパティ情報のレコードが一件も存在しない.
            // 中身が空のリストを生成する.
            list = new ArrayList<DNSServiceEndpointInstanceProperties>();
        }
        
        return list;
    }


    /**
     * DNSサービスエンドポイントのインスタンスのプロパティ情報を新規に作成する.
     * 
     * @return
     */
    public DNSServiceEndpointInstanceProperties createDNSServiceEndpointInstanceProperties(DNSServiceEndpointInstanceProperties dnsServiceEndpointInstanceProperties) throws LoulanDNSSystemServiceException
    {

        // 親レコードのDNSServiceEndpointInstanceが存在するかをチェックする.
        long dnsServiceEndpointInstanceID = dnsServiceEndpointInstanceProperties.getDnsServiceEndpointInstanceID();
        DNSServiceEndpointInstance dnsServiceEndpointInstance = getDNSServiceEndpointInstance( dnsServiceEndpointInstanceProperties.getDnsServiceEndpointInstanceID() ); 
        if ( dnsServiceEndpointInstance == null )
        {
            // 指定されたDNSサービスエンドポイント情報のDBレコードは存在しない.
            String msg = String.format("DNSServiceEndpointInstance is NOT exists. dnsServiceEndpointInstanceID=%d", dnsServiceEndpointInstanceID );
            LoulanDNSSystemServiceException execption = new LoulanDNSSystemServiceException( msg );
            throw execption;
        }

        // 主キーのID値をnullで初期化しておき、DB側で新規に採番させる.
        dnsServiceEndpointInstanceProperties.setDnsServiceEndpointInstancePropertyID(null);
    
        // DBにレコードを新規作成する.
        DNSServiceEndpointInstanceProperties created = dnsServiceEndpointInstancePropertiesRepository.save(dnsServiceEndpointInstanceProperties);

        return created;
    }

    /**
     * DNSサービスエンドポイントのインスタンスのプロパティ情報を更新する.
     * 
     * @param dnsServiceEndpointInstanceProperties
     * @return
     * @throws LoulanDNSSystemServiceException
     */
    public DNSServiceEndpointInstanceProperties updateDNSServiceEndpointInstanceProperties(DNSServiceEndpointInstanceProperties dnsServiceEndpointInstanceProperties) throws LoulanDNSSystemServiceException
    {
        // 親レコードのDNSServiceEndpointInstanceが存在するかをチェックする.
        long dnsServiceEndpointInstanceID = dnsServiceEndpointInstanceProperties.getDnsServiceEndpointInstanceID();
        DNSServiceEndpointInstance dnsServiceEndpointInstance = getDNSServiceEndpointInstance( dnsServiceEndpointInstanceProperties.getDnsServiceEndpointInstanceID() ); 
        if ( dnsServiceEndpointInstance == null )
        {
            // 指定されたDNSサービスエンドポイント情報のDBレコードは存在しない.
            String msg = String.format("DNSServiceEndpointInstance is NOT exists. dnsServiceEndpointInstanceID=%d", dnsServiceEndpointInstanceID );
            LoulanDNSSystemServiceException execption = new LoulanDNSSystemServiceException( msg );
            throw execption;
        }

        // 更新対象のDNSサービスエンドポイントのインスタンスのプロパティ情報のレコードが存在するかを確認する.
        long dnsServiceEndpointInstancePropertyID = dnsServiceEndpointInstanceProperties.getDnsServiceEndpointInstancePropertyID();
        if ( getDNSServiceEndpointInstanceProperties( dnsServiceEndpointInstancePropertyID ) == null )
        {
            // 更新対象のDNSサービスエンドポイントのインスタンスのプロパティ情報のレコード
            String msg = String.format("DNSServiceEndpointInstanceProperties is NOT exists. dnsServiceEndpointInstanceID=%d, dnsServiceEndpointInstancePropertyID=%d", dnsServiceEndpointInstanceID, dnsServiceEndpointInstancePropertyID );
            LoulanDNSSystemServiceException execption = new LoulanDNSSystemServiceException( msg );
            throw execption;
        }


        DNSServiceEndpointInstanceProperties updated = dnsServiceEndpointInstancePropertiesRepository.save(dnsServiceEndpointInstanceProperties);

        return updated;
    }


    /**
     * DNSサービスエンドポイントのインスタンスのプロパティ情報のレコードを削除する.
     * 
     * @param dnsServiceEndpointInstancePropertyID
     * @throws LoulanDNSSystemServiceException
     */
    public void deleteDNSServiceEndpointInstanceProperties(long dnsServiceEndpointInstancePropertyID) throws LoulanDNSSystemServiceException
    {

        // 削除対象のDNSサービスエンドポイントのインスタンスのプロパティ情報のレコードが存在するかを確認する.
        if ( getDNSServiceEndpointInstanceProperties( dnsServiceEndpointInstancePropertyID ) == null )
        {
            // 更新対象のDNSサービスエンドポイントのインスタンスのプロパティ情報のレコード
            String msg = String.format("DNSServiceEndpointInstanceProperties is NOT exists. dnsServiceEndpointInstancePropertyID=%d", dnsServiceEndpointInstancePropertyID );
            LoulanDNSSystemServiceException execption = new LoulanDNSSystemServiceException( msg );
            throw execption;
        }


        dnsServiceEndpointInstancePropertiesRepository.deleteByDNSServiceEndpointInstancePropertyID(dnsServiceEndpointInstancePropertyID);
        return ;
    }




    // システムプロパティ情報(SYSTEM_PROPERTYテーブル)の一覧を取得する.
    public List<SystemProperties> getSystemPropertiesList() throws LoulanDNSSystemServiceException
    {
        List<SystemProperties> systemPropertiesList = systemPropertiesRepository.findAll();
        return systemPropertiesList;
    }

    // システムプロパティ情報(SYSTEM_PROPERTYテーブル)から指定したプロパティキーのリストを取得する.
    public List<SystemProperties> getSystemPropertiesListBySystemPropertyKey(String systemPropertyKey) throws LoulanDNSSystemServiceException
    {
        List<SystemProperties> systemPropertiesList = systemPropertiesRepository.findBySystemPropertyKey(systemPropertyKey);
        return systemPropertiesList;
    }


    /**
     * 共通ログ情報(COMMON_SYSTEM_LOGテーブル)から主キーをもとにレコードを取得する.
     * 
     * @param commonSystemLogID
     * @return
     * @throws LoulanDNSSystemServiceException
     */
    public List<CommonSystemLog> getCommonSystemLogListByID(long commonSystemLogID) throws LoulanDNSSystemServiceException
    {
        List<CommonSystemLog> commonSystemLogList = commonSystemLogRepository.findByCommonSystemLogID(commonSystemLogID);
        return commonSystemLogList;
    }

    /**
     * 共通ログ情報(COMMON_SYSTEM_LOGテーブル)からタグ値をもとにレコードを取得する.
     * 
     * @param commonSystemLogTag
     * @return
     * @throws LoulanDNSSystemServiceException
     */
    public List<CommonSystemLog> getCommonSystemLogListByTag(String commonSystemLogTag) throws LoulanDNSSystemServiceException
    {
        List<CommonSystemLog> commonSystemLogList = commonSystemLogRepository.findByCommonSystemLogTag(commonSystemLogTag);
        return commonSystemLogList;
    }

    /**
     * 共通ログ情報(COMMON_SYSTEM_LOGテーブル)から、指定したタグ値と、指定したプロパティ情報を持つレコードを取得する.
     * 
     * @param commonSystemLogTag
     * @param propertyKey
     * @param propertyValue
     * @return
     * @throws LoulanDNSSystemServiceException
     */
    public List<CommonSystemLog> getCommonSystemLogListByTagAndProperty(String commonSystemLogTag, String propertyKey, String propertyValue) throws LoulanDNSSystemServiceException
    {
        List<CommonSystemLog> commonSystemLogList = commonSystemLogRepository.findByCommonSystemLogTagAndProperty(commonSystemLogTag, propertyKey, propertyValue);
        return commonSystemLogList;
    }


    public CommonSystemLogProperties getCommonSystemLogPropertiesByID(long commonSystemLogPropertyID) throws LoulanDNSSystemServiceException
    {
        CommonSystemLogProperties commonSystemLogProperties  = commonSystemLogPropertiesRepository.findByCommonSystemLogPropertyID(commonSystemLogPropertyID);
        return commonSystemLogProperties;
    }

    /**
     * 共通ログのプロパティ情報(COMMON_SYSTEM_LOG_PROPERTIESテーブル)の一覧を、親レコードである共通ログ情報(COMMON_SYSTEM_LOGテーブル)のID値から取得する.
     * 
     * @param commonSystemLogID
     * @return
     * @throws LoulanDNSSystemServiceException
     */
    public List<CommonSystemLogProperties> getCommonSystemLogPropertiesByCommonSystemLogID(long commonSystemLogID) throws LoulanDNSSystemServiceException
    {
        List<CommonSystemLogProperties> commonSystemLogPropertiesList = commonSystemLogPropertiesRepository.findByCommonSystemLogID(commonSystemLogID);
        return commonSystemLogPropertiesList;
    }


    
    /**
     * 共通ログのレコードをDBに新規追加する.
     * 
     */
    public CommonSystemLog createCommonSystemLog(CommonSystemLog commonSystemLog) throws LoulanDNSSystemServiceException
    {

        // 主キーのID値をnullで初期化しておき、DB側で新規に採番させる.
        commonSystemLog.setCommonSystemLogID(null);

        // 新規追加.
        CommonSystemLog saved = commonSystemLogRepository.save(commonSystemLog);
        return saved;
    }

    /**
     * 共通ログのレコードを更新する.
     * 
     */
    public CommonSystemLog updateCommonSystemLog(CommonSystemLog commonSystemLog) throws LoulanDNSSystemServiceException
    {
        Long id = commonSystemLog.getCommonSystemLogID();
        if ( getCommonSystemLogListByID(id) == null )
        {
            // DBには既存レコードが存在しない.
            String msg = String.format("Failed to update CommonSystemLog record. The specified record is NOT exists. CommonSystemLogID=%d", id);
            LoulanDNSSystemServiceException exception = new LoulanDNSSystemServiceException(msg);
            throw exception;
        }

        // 更新処理.
        CommonSystemLog saved = commonSystemLogRepository.save(commonSystemLog);
        return saved;
    }


    


    /**
     * 共通ログのプロパティレコードをDBに新規追加する.
     * 
     */
    public CommonSystemLogProperties createCommonSystemLogProperties(CommonSystemLogProperties commonSystemLogProperties) throws LoulanDNSSystemServiceException
    {

        // 主キーのID値をnullで初期化しておき、DB側で新規に採番させる.
        commonSystemLogProperties.setCommonSystemLogPropertyID(null);

        // 新規追加.
        CommonSystemLogProperties saved = commonSystemLogPropertiesRepository.save(commonSystemLogProperties);
        return saved;
    }


    /**
     * 共通ログプロパティのレコードを更新する.
     * 
     */
    public CommonSystemLogProperties updateCommonSystemLogProperties(CommonSystemLogProperties commonSystemLogProperties) throws LoulanDNSSystemServiceException
    {
        // 主キーを取得.
        Long id = commonSystemLogProperties.getCommonSystemLogPropertyID();

        if ( getCommonSystemLogPropertiesByID(id) == null )
        {
            // DBには既存レコードが存在しない.
            String msg = String.format("Failed to update CommonSystemLog record. The specified record is NOT exists. CommonSystemLogID=%d", id);
            LoulanDNSSystemServiceException exception = new LoulanDNSSystemServiceException(msg);
            throw exception;
        }

        // 更新処理.
        CommonSystemLogProperties saved = commonSystemLogPropertiesRepository.save(commonSystemLogProperties);
        return saved;
    }


    /**
     * 共通ログプロパティのレコードを削除する.
     * 
     * @param commonSystemLogPropertyID
     * @throws LoulanDNSSystemServiceException
     */
    public void deleteCommonSystemLogProperties(long commonSystemLogPropertyID) throws LoulanDNSSystemServiceException
    {
        if ( getCommonSystemLogPropertiesByID(commonSystemLogPropertyID) == null )
        {
            // 存在しないレコードを削除しようしている.
            String msg = String.format("Failed to delete CommonSystemLogProperties record. The specified record is NOT found. commonSystemLogPropertyID=%d", commonSystemLogPropertyID );
            LoulanDNSSystemServiceException exception = new LoulanDNSSystemServiceException(msg);
            throw exception;
        }

        commonSystemLogPropertiesRepository.deleteById(commonSystemLogPropertyID);
    }




}