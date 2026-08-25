package org.loxsols.net.service.dns.loulandns.server.logical.service.system.launcher.factory.impl;

import org.loxsols.net.service.dns.loulandns.server.logical.service.system.launcher.IDynamicServiceLauncher;
import org.loxsols.net.service.dns.loulandns.server.common.LoulanDNSSystemServiceException;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.launcher.IDyanmicServiceDescriptor;

import org.loxsols.net.service.dns.loulandns.server.logical.service.system.launcher.factory.IDynamicServiceLauncherFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.launcher.*;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.launcher.impl.*;

/**
 * 動的サービスランチャーのファクトリI/F
 * 
 */
public class SimpleLocalThreadDynamicServiceLauncherFactoryImpl implements IDynamicServiceLauncherFactory
{
    public IDynamicServiceLauncher getOrCreateDynamicServiceLauncher() throws LoulanDNSSystemServiceException
    {
        IDynamicServiceLauncher serviceLauncher = new SimpleLocalThreadDynamicServiceLauncherImpl();
        return serviceLauncher;
    }
}