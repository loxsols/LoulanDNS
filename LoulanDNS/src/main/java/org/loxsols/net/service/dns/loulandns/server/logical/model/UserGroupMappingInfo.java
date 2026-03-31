package org.loxsols.net.service.dns.loulandns.server.logical.model;


import java.util.*;
import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;



import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;



@Getter
@Setter
public class UserGroupMappingInfo
{

    private Long userID;
    private Long userGroupID;
    private long recordStatus;
    private String memo;
    private ZonedDateTime createDate;
    private ZonedDateTime updateDate;


    List<UserGroupInfo> userGroupInfoList;


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

}