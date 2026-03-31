package org.loxsols.net.service.dns.loulandns.server.logical.model;

import java.util.*;
import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;



import org.loxsols.net.service.dns.loulandns.server.common.constants.LoulanDNSConstants;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;

// LoulanDNSの論理モデルクラスのうち、ユーザーオブジェクトを表すクラス.
@Getter
@Setter
public class UserInfo
{
    public Long userID;
    public String userName;
    public String userPassword;
    public long recordStatus;
    public String memo;
    public ZonedDateTime createDate;
    public ZonedDateTime updateDate;

    public List<UserGroupMappingInfo> userGroupMappingInfoList;
    public List<DNSServiceInstanceInfo> dnsServiceInstanceInfoList;
    public List<DNSResolverInstanceInfo> dnsResolverInstanceInfoList;

    public void setCreateDate(String createDate)
    {
        ZonedDateTime dateTime = LoulanDNSUtils.toDateTimeObject(createDate);
        this.createDate = dateTime;
    }

    public void setCreateDate(ZonedDateTime createDate)
    {
        this.createDate = createDate;
    }

    public void setUpdateDate(String updateDate)
    {
        ZonedDateTime dateTime = LoulanDNSUtils.toDateTimeObject(updateDate);
        this.updateDate = dateTime;
    }

    public void setUpdateDate(ZonedDateTime updateDate)
    {
        this.updateDate = updateDate;
    }

    public List<UserGroupMappingInfo> getUserGroupMappingInfoList()
    {
        return userGroupMappingInfoList;
    }

    public void setUserGroupMappingInfoList(List<UserGroupMappingInfo> list)
    {
        userGroupMappingInfoList = list;
    }

    





    // 本ユーザーが所属するユーザーグループの一覧を返す.
    List<UserGroupInfo> getBelongingToUserGroups()
    {
        List<UserGroupInfo> groups = new ArrayList<UserGroupInfo>();

        if ( getUserGroupMappingInfoList() == null )
        {
            return groups;
        }

        for( UserGroupMappingInfo mappingInfo : getUserGroupMappingInfoList() )
        {
            groups.addAll(mappingInfo.getUserGroupInfoList() );
        }

        return groups;

    }


    // 本ユーザーが指定されたユーザーグループに所属するか判定する.
    public boolean isBelongingToUserGroup(long gid)
    {
        List<UserGroupInfo> groupInfoList = getBelongingToUserGroups();
        for ( UserGroupInfo groupInfo : groupInfoList)
        {
            if ( gid == groupInfo.getUserGroupID() )
            {
                return true;
            }            
        }

        return false;
    }


    public List<DNSServiceInstanceInfo> getDNSServiceInstanceInfoList()
    {
        return this.dnsServiceInstanceInfoList;
    }

    public void setDNSServiceInstanceInfoList(List<DNSServiceInstanceInfo> list)
    {
        this.dnsServiceInstanceInfoList = list;
    }



    
    // 本ユーザーに紐づく指定されたDNSServiceインスタンス情報を返す.
    public DNSServiceInstanceInfo getDNSServiceInstanceInfo(String dnsServiceInstanceName)
    {
        for( DNSServiceInstanceInfo dnsServiceInstanceInfo : getDNSServiceInstanceInfoList() )
        {
            if ( dnsServiceInstanceName.equals(dnsServiceInstanceInfo.dnsServiceInstanceName ) )
            {
                return dnsServiceInstanceInfo;
            }
        }

        return null;
    }


    // 本ユーザーが指定されたDNSServiceインスタンス情報を保持しているかを判定する.
    public boolean hasDNSServiceInstance(long dnsServiceInstanceID)
    {
        for( DNSServiceInstanceInfo dnsServiceInstanceInfo : getDNSServiceInstanceInfoList() )
        {
            if ( dnsServiceInstanceID == dnsServiceInstanceInfo.getDNSServiceInstanceID() )
            {
                return true;
            }
        }

        return false;
    }


    // 本ユーザーが管理者権限を有しているのかを判定する.
    public boolean isAdministrator()
    {
        List<UserGroupInfo> groups = getBelongingToUserGroups();

        for( UserGroupInfo group : groups )
        {
            boolean flg = group.isAdministrator();
            if ( flg == true )
            {
                return true;
            }
            
        }

        return false;

    }
    


}