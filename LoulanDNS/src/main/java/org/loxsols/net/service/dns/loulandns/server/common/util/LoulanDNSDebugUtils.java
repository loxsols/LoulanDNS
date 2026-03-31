package org.loxsols.net.service.dns.loulandns.server.common.util;

import java.io.OutputStream;
import java.io.PrintStream;

import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSProtocolUtils;

public class LoulanDNSDebugUtils
{

    static PrintStream debugPrintStream = System.out;


    static LoulanDNSProtocolUtils protocolUtils = new LoulanDNSProtocolUtils();

    public static PrintStream getDebugPrintStream()
    {
        return debugPrintStream;
    }

    static PrintStream out()
    {
        return getDebugPrintStream();
    }

    // デバッグ用出力
    public static void printDebug(Class clazz, String tag, String msg)
    {
        String debugMessage = String.format("[DEBUG] class=%s, tag=%s, msg=%s", clazz.getName(), tag, msg);
        out().println( debugMessage );
    }


    // 指定されたbyte配列のデータを16進数表記でデバッグ出力する.
    public static void printHexString(Class clazz, String tag, byte[] bytes)
    {

        String hexString = protocolUtils.toDebugHexString( bytes );

        String debugMessage = String.format("[DEBUG] class=%s, tag=%s ", clazz.getName(), tag );
        out().println( debugMessage );
        out().println( "******************************");
        out().print( hexString );
        out().println( "******************************");

    }



}