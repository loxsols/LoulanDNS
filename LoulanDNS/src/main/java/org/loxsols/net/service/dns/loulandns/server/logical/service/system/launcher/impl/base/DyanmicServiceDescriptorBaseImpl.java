package org.loxsols.net.service.dns.loulandns.server.logical.service.system.launcher.impl.base;

import java.util.Properties;
import java.time.ZonedDateTime;

import org.loxsols.net.service.dns.loulandns.server.common.LoulanDNSSystemServiceException;

import android.providers.settings.GlobalSettingsProto.DateTime;


import org.loxsols.net.service.dns.loulandns.server.logical.service.system.launcher.*;

/**
 * 動的サービスのディスクリプタの実装クラス
 * 
 */
public class DyanmicServiceDescriptorBaseImpl implements IDyanmicServiceDescriptor
{

    public final static String CONST_PROP_KEY_PID = "pid";

    private Long dynamicServiceID;
    private String dynamicServiceName;
    private ZonedDateTime dynamicServiceStartTime;
    private ZonedDateTime dynamicServiceEndTime;

    private String dynamicServiceMemo;
    private Properties dynamicServiceProperties = new Properties();

    private int dynamicServiceStatus;



    public Long getDynamicServiceID() throws LoulanDNSSystemServiceException
    {
        return dynamicServiceID;
    }

    public void setDynamicServiceID(Long value) throws LoulanDNSSystemServiceException
    {
        this.dynamicServiceID = value;
    }

    public String getDynamicServiceName() throws LoulanDNSSystemServiceException
    {
        return dynamicServiceName;
    }

    public void setDynamicServiceName(String value) throws LoulanDNSSystemServiceException
    {
        this.dynamicServiceName = value;
    }

    public ZonedDateTime getDynamicServiceStartTime() throws LoulanDNSSystemServiceException
    {
        return dynamicServiceStartTime;
    }

    public void setDynamicServiceStartTime(ZonedDateTime value) throws LoulanDNSSystemServiceException
    {
        this.dynamicServiceStartTime = value;
    }

    public ZonedDateTime getDynamicServiceEndTime() throws LoulanDNSSystemServiceException
    {
        return dynamicServiceEndTime;
    }

    public void setDynamicServiceEndTime(ZonedDateTime value) throws LoulanDNSSystemServiceException
    {
        this.dynamicServiceEndTime = value;
    }

    public String getDynamicServiceMemo() throws LoulanDNSSystemServiceException
    {
        return dynamicServiceMemo;
    }

    public void setDynamicServiceMemo(String value) throws LoulanDNSSystemServiceException
    {
        this.dynamicServiceMemo = value;
    }

    public Properties getDynamicServiceProperties() throws LoulanDNSSystemServiceException
    {
        return dynamicServiceProperties;
    }

    public void setDynamicServiceProperties(Properties value) throws LoulanDNSSystemServiceException
    {
        this.dynamicServiceProperties = value;
    }

    public String getDynamicServiceProperty(String key) throws LoulanDNSSystemServiceException
    {
        String value = this.dynamicServiceProperties.getProperty(key);
        return value;
    }

    public void setDynamicServiceProperty(String key, String value) throws LoulanDNSSystemServiceException
    {
        this.dynamicServiceProperties.setProperty(key, value);
    }

    public int getDynamicServiceStatus() throws LoulanDNSSystemServiceException
    {
        return this.dynamicServiceStatus;
    }

    public void setDynamicServiceStatus(int value) throws LoulanDNSSystemServiceException
    {
        this.dynamicServiceStatus = value;
    }


    public void startDynamicService() throws LoulanDNSSystemServiceException
    {
        LoulanDNSSystemServiceException exception = new LoulanDNSSystemServiceException("NOT Implemented.");
        throw exception;
    }


    public void stopDynamicService() throws LoulanDNSSystemServiceException
    {
        LoulanDNSSystemServiceException exception = new LoulanDNSSystemServiceException("NOT Implemented.");
        throw exception;
    }


        public Long getPID() throws LoulanDNSSystemServiceException
    {
        String pidString = this.getDynamicServiceProperty( CONST_PROP_KEY_PID );
        
        if ( pidString == null )
        {
            return null;
        }

        Long pid = Long.parseLong(pidString);
        return pid;
    }

    public void setPID(Long pid) throws LoulanDNSSystemServiceException
    {
        String pidString = pid.toString();
        this.setDynamicServiceProperty(CONST_PROP_KEY_PID, pidString);
    }
    

}

