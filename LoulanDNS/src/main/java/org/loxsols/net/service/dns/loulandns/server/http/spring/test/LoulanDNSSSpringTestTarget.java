package org.loxsols.net.service.dns.loulandns.server.http.spring.test;


import java.lang.reflect.*;

/**
 * Spring系機能のテストクラスのインターフェース
 * SpringTestRunnerCommandApplicationから呼び出す.
 */
public interface LoulanDNSSSpringTestTarget
{

    /**
     * 指定したテストメソッドを呼び出す.
     * 
     * @param methodName
     * @param args
     * @throws LoulanDNDSpringTestException
     */
    public Object callTest(String methodName) throws LoulanDNDSpringTestException;


    /**
     * 指定したテストメソッドを呼び出す.
     * 
     * @param methodName
     * @param args
     * @throws LoulanDNDSpringTestException
     */
    public Object callTest(String methodName, Object[] args) throws LoulanDNDSpringTestException;


    /**
     * 指定したテストメソッドを呼び出す.
     * 
     * @param methodName
     * @param args
     * @throws LoulanDNDSpringTestException
     */
    public Object callTest(String methodName, Class[] argTypes, Object[] args) throws LoulanDNDSpringTestException;


    /**
     * 本クラスに実装されたテストメソッドの名前の一覧を返す.
     * 
     * @return
     * @throws LoulanDNDSpringTestException
     */
    public String[] getTestMethodNames() throws LoulanDNDSpringTestException;


    /**
     * 本クラスに実装されたテストメソッドの一覧を返す.
     * 
     * @return
     * @throws LoulanDNDSpringTestException
     */
    public Method[] getTestMethods() throws LoulanDNDSpringTestException;



    /**
     * 本クラスに実装されたテストメソッドの中から指定されたメソッド名のメソッドの一覧を返す.
     * 
     * @param methodName
     * @return
     * @throws LoulanDNDSpringTestException
     */
    public Method[] getTestMethodsByName(String methodName)  throws LoulanDNDSpringTestException;


    /**
     * 本クラスに実装されたテストメソッドの中から指定されたメソッドを返す.
     * 
     * @param methodName
     * @param argTypes
     * @return
     * @throws LoulanDNDSpringTestException
     */
    public Method getTestMethod(String methodName, Class[] argTypes)  throws LoulanDNDSpringTestException;







}