package org.loxsols.net.service.dns.loulandns.server.logical.model;


import java.util.*;
import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;



import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;



@Getter
@Setter
public class UserGroupInfo
{

    private Long userGroupID;
    private String userGroupName;
    private long recordStatus;
    private String memo;
    private ZonedDateTime createDate;
    private ZonedDateTime updateDate;

    List<UserGroupPrivilegeInfo> userGroupPrivilegeInfoList;


    public void setCreateDate(String createDate)
    {
        ZonedDateTime dateTime = LoulanDNSUtils.toDateTimeObject(createDate);
        this.createDate = dateTime;
    }

    public void setUpdateDate(String updateDate)
    {
        ZonedDateTime dateTime = LoulanDNSUtils.toDateTimeObject(updateDate);
        this.updateDate = dateTime;
    }


    // 本ユーザーが管理者権限を有しているのかを判定する.
    public boolean isAdministrator()
    {
        List<UserGroupPrivilegeInfo> privileges = this.getUserGroupPrivilegeInfoList();

        for( UserGroupPrivilegeInfo privilege : privileges )
        {
            boolean flg = privilege.isAdministratorPrivilege();
            if ( flg == true )
            {
                return true;
            }
        }

        return false;
        
    }

    public void addUserGroupPrivilegeInfo(UserGroupPrivilegeInfo userGroupPrivilegeInfo)
    {
        if ( this.userGroupPrivilegeInfoList == null )
        {
            this.userGroupPrivilegeInfoList = new ArrayList<UserGroupPrivilegeInfo>();
        }

        this.userGroupPrivilegeInfoList.add( userGroupPrivilegeInfo );

    }



}