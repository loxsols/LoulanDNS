package org.loxsols.net.service.dns.loulandns.server.logical.service.system.launcher.impl;

import org.loxsols.net.service.dns.loulandns.server.common.LoulanDNSSystemServiceException;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.launcher.impl.base.DynamicServiceLauncherBaseImpl;
import org.springframework.cloud.deployer.spi.app.AppDeployer;
import org.springframework.cloud.deployer.spi.core.AppDefinition;
import org.springframework.cloud.deployer.spi.core.AppDeploymentRequest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.io.File;

import java.util.ArrayList;
import java.util.Properties;
import java.util.List;
import java.io.*;

import org.loxsols.net.service.dns.loulandns.server.logical.service.system.launcher.*;
import org.loxsols.net.service.dns.loulandns.server.common.LoulanDNSSystemServiceException;

import org.loxsols.net.service.dns.loulandns.server.logical.service.system.launcher.impl.base.*;



/**
 * SpringCloudDeployerの機能を利用して、サービスインスタンスを動的に別のJVMプロセスなどで立ち上げるためランチャー実装.
 * 
 */
public class SpringCloudDeployerDynamicServiceLauncherImpl extends DynamicServiceLauncherBaseImpl implements IDynamicServiceLauncher
{



    AppDeployer appDeployer;
    public void setAppDeployer(AppDeployer instance)
    {
        this.appDeployer = instance;
    }


    public SpringCloudDeployerDynamicServiceLauncherImpl(AppDeployer appDeployer)
    {
        setAppDeployer(appDeployer);
    }



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
        String jarPath = getCurrentClassPath();
        int port = 58080;
        IDyanmicServiceDescriptor serviceDescriptor 
            = new SpringCloudDeployerDynamicServiceDescriptorImpl(this.appDeployer, serviceName, jarPath, mainClassName, args, properties, port);
        
        return serviceDescriptor;
    }

    public IDyanmicServiceDescriptor createDynamicServiceDiscriptor(String serviceName, String jarFilePath, String mainClassName, String[] args, Properties jvmProperties) throws LoulanDNSSystemServiceException
    {
        LoulanDNSSystemServiceException exception = new LoulanDNSSystemServiceException("NOT Implemented.");
        throw exception;
    }






}