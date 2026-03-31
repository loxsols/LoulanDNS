package org.loxsols.net.service.dns.loulandns.server.logical.model;


import java.util.*;
import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;


import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;


@Getter
@Setter
public class CommonSystemLogPropertiesInfo
{

    private Long commonSystemLogPropertyID;
    private Long commonSystemLogID;

    private String commonSystemLogPropertyKey;
    private String commonSystemLogPropertyValue;
    
    private String commonSystemLogOption1;
    private String commonSystemLogOption2;
    private String commonSystemLogOption3;

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


}