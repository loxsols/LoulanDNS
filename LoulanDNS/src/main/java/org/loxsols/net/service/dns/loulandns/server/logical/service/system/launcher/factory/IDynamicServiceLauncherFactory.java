package org.loxsols.net.service.dns.loulandns.server.logical.service.system.launcher.factory;

import org.loxsols.net.service.dns.loulandns.server.logical.service.system.launcher.IDynamicServiceLauncher;
import org.loxsols.net.service.dns.loulandns.server.common.LoulanDNSSystemServiceException;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.launcher.IDyanmicServiceDescriptor;

/**
 * 動的サービスランチャーのファクトリI/F
 * 
 */
public interface IDynamicServiceLauncherFactory
{
    public IDynamicServiceLauncher getOrCreateDynamicServiceLauncher() throws LoulanDNSSystemServiceException;
}