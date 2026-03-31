package org.loxsols.net.service.dns.loulandns.server.logical.service.system.log.logger;


import org.loxsols.net.service.dns.loulandns.server.common.LoulanDNSSystemServiceException;

import java.util.logging.*;

/**
 * ロガーのインスタンス
 * 
 */
public interface ILoulanDNSLogger
{

    public void log(int logLevel, String msg) throws LoulanDNSSystemServiceException;
    public void log(int logLevel, String code, String msg) throws LoulanDNSSystemServiceException;    
    public void log(int logLevel, String code, String msg, Object[] params) throws LoulanDNSSystemServiceException;
    public void log(int logLevel, String code, String msg, Throwable thrown) throws LoulanDNSSystemServiceException;



    // デバッグ
    public void debug(String msg) throws LoulanDNSSystemServiceException;
    public void debug(String msg, Object[] params) throws LoulanDNSSystemServiceException;
    public void debug(String msg, Throwable thrown) throws LoulanDNSSystemServiceException;

    public void debug(String code, String msg) throws LoulanDNSSystemServiceException;
    public void debug(String code, String msg, Object[] params) throws LoulanDNSSystemServiceException;
    public void debug(String code, String msg, Throwable thrown) throws LoulanDNSSystemServiceException;

    // 情報メッセージ
    public void info(String msg) throws LoulanDNSSystemServiceException;
    public void info(String msg, Object[] params) throws LoulanDNSSystemServiceException;
    public void info(String msg, Throwable thrown) throws LoulanDNSSystemServiceException;

    public void info(String code, String msg) throws LoulanDNSSystemServiceException;
    public void info(String code, String msg, Object[] params) throws LoulanDNSSystemServiceException;
    public void info(String code, String msg, Throwable thrown) throws LoulanDNSSystemServiceException;

    // 正常ではあるが重要な状態
    public void notice(String msg) throws LoulanDNSSystemServiceException;
    public void notice(String msg, Object[] params) throws LoulanDNSSystemServiceException;
    public void notice(String msg, Throwable thrown) throws LoulanDNSSystemServiceException;

    public void notice(String code, String msg) throws LoulanDNSSystemServiceException;
    public void notice(String code, String msg, Object[] params) throws LoulanDNSSystemServiceException;
    public void notice(String code, String msg, Throwable thrown) throws LoulanDNSSystemServiceException;

    // 警告
    public void warn(String msg) throws LoulanDNSSystemServiceException;
    public void warn(String msg, Object[] params) throws LoulanDNSSystemServiceException;
    public void warn(String msg, Throwable thrown) throws LoulanDNSSystemServiceException;

    public void warn(String code, String msg) throws LoulanDNSSystemServiceException;
    public void warn(String code, String msg, Object[] params) throws LoulanDNSSystemServiceException;
    public void warn(String code, String msg, Throwable thrown) throws LoulanDNSSystemServiceException;

    // エラー
    public void error(String msg) throws LoulanDNSSystemServiceException;
    public void error(String msg, Object[] params) throws LoulanDNSSystemServiceException;
    public void error(String msg, Throwable thrown) throws LoulanDNSSystemServiceException;

    public void error(String code, String msg) throws LoulanDNSSystemServiceException;
    public void error(String code, String msg, Object[] params) throws LoulanDNSSystemServiceException;
    public void error(String code, String msg, Throwable thrown) throws LoulanDNSSystemServiceException;

    // アクションをすぐに起こさなければならない
    public void alert(String msg) throws LoulanDNSSystemServiceException;
    public void alert(String msg, Object[] params) throws LoulanDNSSystemServiceException;
    public void alert(String msg, Throwable thrown) throws LoulanDNSSystemServiceException;

    public void alert(String code, String msg) throws LoulanDNSSystemServiceException;
    public void alert(String code, String msg, Object[] params) throws LoulanDNSSystemServiceException;
    public void alert(String code, String msg, Throwable thrown) throws LoulanDNSSystemServiceException;


}