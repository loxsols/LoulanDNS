package org.loxsols.net.service.dns.loulandns.server.logical.service.system.launcher;

import java.util.Properties;
import java.time.ZonedDateTime;

import org.loxsols.net.service.dns.loulandns.server.common.LoulanDNSSystemServiceException;

import android.providers.settings.GlobalSettingsProto.DateTime;

/**
 * 動的サービスのディスクリプタI/F
 * 
 */
public interface IDyanmicServiceDescriptor
{

    public Long getDynamicServiceID() throws LoulanDNSSystemServiceException;
    public String getDynamicServiceName() throws LoulanDNSSystemServiceException;

    public ZonedDateTime getDynamicServiceStartTime() throws LoulanDNSSystemServiceException;
    public ZonedDateTime getDynamicServiceEndTime() throws LoulanDNSSystemServiceException;

    public Properties getDynamicServiceProperties() throws LoulanDNSSystemServiceException;
    public String getDynamicServiceProperty(String key) throws LoulanDNSSystemServiceException;
    
    public String getDynamicServiceMemo() throws LoulanDNSSystemServiceException;

    public int getDynamicServiceStatus() throws LoulanDNSSystemServiceException;


    public void startDynamicService() throws LoulanDNSSystemServiceException;
    public void stopDynamicService() throws LoulanDNSSystemServiceException;

    

}

