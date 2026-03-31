package org.loxsols.net.service.dns.loulandns.server.logical.model;

import java.util.*;
import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;


import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;


@Getter
@Setter
public class UserGroupPrivilegeInfo
{

    private Long userGroupPrivilegeID;
    private String userGroupID;
    private long dnsInfoMaskValue;
    private long userInfoMaskValue;
    private long systemInfoMaskValue;
    private long recordStatus;
    private String memo;
    private ZonedDateTime createDate;
    private ZonedDateTime updateDate;


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
    

    // 本権限情報が管理者権限を意味する場合はtrueを返す.
    public boolean isAdministratorPrivilege()
    {
        boolean ret =  LoulanDNSUtils.checkForAdministratorPrivilege(this);
        return ret;
    }

}