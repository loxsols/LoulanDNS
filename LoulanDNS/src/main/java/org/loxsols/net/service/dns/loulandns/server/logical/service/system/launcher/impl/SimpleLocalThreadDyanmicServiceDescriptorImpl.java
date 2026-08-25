package org.loxsols.net.service.dns.loulandns.server.logical.service.system.launcher.impl;

import java.util.ArrayList;
import java.util.Properties;
import java.time.ZonedDateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import java.io.*;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import java.util.StringTokenizer;

import org.loxsols.net.service.dns.loulandns.server.common.LoulanDNSSystemServiceException;

import android.providers.settings.GlobalSettingsProto.DateTime;


import org.loxsols.net.service.dns.loulandns.server.logical.service.system.launcher.*;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.launcher.impl.base.*;

/**
 * 動的サービスのディスクリプタの実装クラス
 * 
 */
public class SimpleLocalThreadDyanmicServiceDescriptorImpl extends DyanmicServiceDescriptorBaseImpl implements IDyanmicServiceDescriptor
{

    Method method;
    Object instance;
    List<Object> args;

    /**
     * リフレクションで起動するシステム情報
     * 
     * @param method
     * @param instance  　thisオブジェクト(nullの場合はstaticメソッドとして起動)
     * @param args
     * @throws LoulanDNSSystemServiceException
     */
    public SimpleLocalThreadDyanmicServiceDescriptorImpl(Method method, Object instance, List<Object> args) throws LoulanDNSSystemServiceException
    {
        this.method = method;
        this.instance = instance;
        this.args = args;
        
        this.setDynamicServiceStatus( DynamicServiceLauncherConstants.CONST_SERVICE_STATUS_CODE_INACTIVE );
    }





    public void startDynamicService() throws LoulanDNSSystemServiceException
    {

        // スレッド内でリフレクションでメソッドを起動する.
        new Thread(() ->
        {
            try
            {
                method.invoke(instance, args.toArray() );
            }
            catch(IllegalAccessException | InvocationTargetException cause)
            {
                String msg = String.format("Failed to Invoke Reflection Method. method=%s", method.getName() );
                LoulanDNSSystemServiceException exception = new LoulanDNSSystemServiceException(msg, cause);
                exception.printStackTrace();
            }
        }).start();


        this.setDynamicServiceStatus( DynamicServiceLauncherConstants.CONST_SERVICE_STATUS_CODE_ACTIVE );

    }


    public void stopDynamicService() throws LoulanDNSSystemServiceException
    {
        // プロセスを終了する.
        String msg = String.format("NOT Implemented.");
        LoulanDNSSystemServiceException exception  = new LoulanDNSSystemServiceException(msg);
        throw exception;
    }
    

}

