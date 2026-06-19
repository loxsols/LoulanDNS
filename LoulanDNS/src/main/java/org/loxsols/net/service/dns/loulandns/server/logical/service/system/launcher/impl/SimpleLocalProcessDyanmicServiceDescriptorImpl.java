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
import java.util.StringTokenizer;

import org.loxsols.net.service.dns.loulandns.server.common.LoulanDNSSystemServiceException;

import android.providers.settings.GlobalSettingsProto.DateTime;


import org.loxsols.net.service.dns.loulandns.server.logical.service.system.launcher.*;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.launcher.impl.base.*;

/**
 * 動的サービスのディスクリプタの実装クラス
 * 
 */
public class SimpleLocalProcessDyanmicServiceDescriptorImpl extends DyanmicServiceDescriptorBaseImpl implements IDyanmicServiceDescriptor
{

    List<String> commandLine;

    Process process;

    /**
     * コンストラクタ
     * 
     * @param commandLine   JVMの実行ファイルを含む起動コマンドライン
     * @throws LoulanDNSSystemServiceException
     */
    public SimpleLocalProcessDyanmicServiceDescriptorImpl(List<String> commandLine) throws LoulanDNSSystemServiceException
    {
        this.commandLine = commandLine;
        
        this.setDynamicServiceStatus( DynamicServiceLauncherConstants.CONST_SERVICE_STATUS_CODE_INACTIVE );
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




    public void startDynamicService() throws LoulanDNSSystemServiceException
    {
        ProcessBuilder pb = new ProcessBuilder( this.commandLine );
        
        // ★ ログ出力を親プロセスと完全に同期（一番楽なログ確認方法）
        pb.inheritIO(); 

        try
        {
            System.out.println("====== [親] 子プロセス（別JVM）を動的に生成します ======");
            this.process = pb.start();            
        }
        catch (IOException cause)
        {
            System.err.println("子JVMのフォークに失敗しました: " + cause.getMessage());

            String msg = String.format("Failed to start DynamicService Process." );
            LoulanDNSSystemServiceException exception = new LoulanDNSSystemServiceException(msg, cause);
            throw exception;
        }

        this.setDynamicServiceStatus( DynamicServiceLauncherConstants.CONST_SERVICE_STATUS_CODE_ACTIVE );

    }


    public void stopDynamicService() throws LoulanDNSSystemServiceException
    {
        // プロセスを終了する.
        this.process.destroy();
    }
    

}

