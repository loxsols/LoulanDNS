package org.loxsols.net.service.dns.loulandns.server.logical.service.system.launcher.factory.impl;

import org.loxsols.net.service.dns.loulandns.server.logical.service.system.launcher.IDynamicServiceLauncher;
import org.loxsols.net.service.dns.loulandns.server.common.LoulanDNSSystemServiceException;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.launcher.IDyanmicServiceDescriptor;

import org.loxsols.net.service.dns.loulandns.server.logical.service.system.launcher.factory.IDynamicServiceLauncherFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.launcher.*;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.launcher.impl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.deployer.spi.app.AppDeployer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * 動的サービスランチャーのファクトリI/F
 * 
 */
public class SpringCloudDeployerDynamicServiceLauncherFactoryImpl implements IDynamicServiceLauncherFactory
{


    AppDeployer appDeployer;
    /**
     * 
     * AppDeployerは、どのクラスを使用するかで、ローカルの別プロセスで起動するか、リモートやクラウド上で起動するかを制御できる.
     * LocalAppDeployerなどをConfigクラスでインスタンス化してDIするのがいいだろう.
     * 
     * @param instance
     */
    @Autowired
    @Qualifier("springCloundAppDeployer")
    public void setAppDeployer(AppDeployer instance)
    {
        this.appDeployer = instance;
    }


    public IDynamicServiceLauncher getOrCreateDynamicServiceLauncher() throws LoulanDNSSystemServiceException
    {
        IDynamicServiceLauncher serviceLauncher = new SpringCloudDeployerDynamicServiceLauncherImpl(this.appDeployer);
        return serviceLauncher;
    }
}