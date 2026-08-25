package org.loxsols.net.service.dns.loulandns.server.logical.service.system.launcher.impl;

import java.util.ArrayList;
import java.util.Properties;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;

import java.io.*;
import java.lang.reflect.Method;

import org.loxsols.net.service.dns.loulandns.server.logical.service.system.launcher.*;
import org.loxsols.net.service.dns.loulandns.server.common.LoulanDNSSystemServiceException;

import org.loxsols.net.service.dns.loulandns.server.logical.service.system.launcher.impl.base.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.deployer.spi.app.AppDeployer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;





/**
 * 動的サービスランチャーの実装クラス.
 * 
 */
public class SimpleLocalThreadDynamicServiceLauncherImpl extends DynamicServiceLauncherBaseImpl implements IDynamicServiceLauncher
{


    /**
     * 動的にサービスを開始する.
     * 
     * @param serviceName
     * @param mainClassName
     * @param args
     * @param properties
     * @return
     * @throws LoulanDNSSystemServiceException
     */
    public IDyanmicServiceDescriptor createDynamicServiceDiscriptor(String serviceName, String mainClassName, String[] args, Properties properties) throws LoulanDNSSystemServiceException
    {
        Class mainClass;
        try
        {
            mainClass = Class.forName(mainClassName);
        }
        catch(ClassNotFoundException cause)
        {
            String msg = String.format("Failed to create DynamicServiceDescriptor, caused by ClassNotFoundException. mainClassName=%s", mainClassName );
            LoulanDNSSystemServiceException exception = new LoulanDNSSystemServiceException(msg);
            throw exception;
        }

        Method method;
        try
        {
            method = mainClass.getMethod("main", String[].class);
        }
        catch(NoSuchMethodException |  SecurityException cause)
        {
            String msg = String.format("Failed to create DynamicServiceDescriptor, caused by failed to create main Method instance." );
            LoulanDNSSystemServiceException exception = new LoulanDNSSystemServiceException(msg);
            throw exception;
        }

        System.setProperties(properties);

        List<Object> argsList = new ArrayList<Object>();
        for( String arg : args )
        {
            argsList.add( arg );
        }

        IDyanmicServiceDescriptor serviceDescriptor = new SimpleLocalThreadDyanmicServiceDescriptorImpl(method, null, argsList );
        
        return serviceDescriptor;
    }

    public IDyanmicServiceDescriptor createDynamicServiceDiscriptor(String serviceName, String jarFilePath, String mainClassName, String[] args, Properties jvmProperties) throws LoulanDNSSystemServiceException
    {
        LoulanDNSSystemServiceException exception = new LoulanDNSSystemServiceException("NOT Implemented.");
        throw exception;
    }




}