package org.loxsols.net.service.dns.loulandns.server.http.spring.test;


import org.junit.*;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;

import java.lang.Class;
import java.util.*;
import java.lang.reflect.*;
import java.lang.annotation.*;


/**
 * Spring系機能のテストクラスの抽象クラス.
 * SpringTestRunnerCommandApplicationから呼び出す.
 */
public abstract class LoulanDNSSSpringTestTargeImpltBase implements LoulanDNSSSpringTestTarget
{


    /**
     * 指定したテストメソッドを呼び出す.
     * 
     * @param methodName
     * @param args
     * @throws LoulanDNDSpringTestException
     */
    public Object callTest(String methodName) throws LoulanDNDSpringTestException
    {
        Object[] voidArgs = new Object[]{};

        Object ret = callTest(methodName, voidArgs);
        return ret;
    }


    /**
     * 指定したテストメソッドを呼び出す.
     * 
     * @param methodName
     * @param args
     * @throws LoulanDNDSpringTestException
     */
    public Object callTest(String methodName, Object[] args) throws LoulanDNDSpringTestException
    {
        // 引数タイプを生成.
        Class[] argTypes = new Class[args.length];
        for( int i=0; i < args.length; i++)
        {
            argTypes[i] = args[i].getClass();
        }

        // テストメソッドの呼び出し.
        Object ret = callTest(methodName, argTypes, args);
        return ret;
    }



    /**
     * 指定したテストメソッドを呼び出す.
     * 
     * @param methodName
     * @param args
     * @throws LoulanDNDSpringTestException
     */
    public Object callTest(String methodName, Class[] argTypes, Object[] args) throws LoulanDNDSpringTestException
    {
        Method m = getTestMethod(methodName, argTypes);

        if ( m == null )
        {
            // 指定されたメソッドが存在しない.
            String msg = String.format("Specified Test method is not exists. methodName=%s, argTypes.length=%d, argTypes=%s", methodName, argTypes.length, formatArgTypesString(argTypes) );
            LoulanDNDSpringTestException exception = new LoulanDNDSpringTestException(msg);
            throw exception;
        }

        try
        {
            Object ret = m.invoke(this, args);
            return ret;
        }
        catch(IllegalAccessException cause)
        {
            // 不当なリフレクションアクセス(IllegalAccessException)でこけた.
            String msg = String.format("Failed to call Test method caused by IllegalAccessException. methodName=%s, argTypes.length=%d, argTypes=%s", methodName, argTypes.length, formatArgTypesString(argTypes) );
            LoulanDNDSpringTestException exception = new LoulanDNDSpringTestException(msg, cause);
            throw exception;
        }
        catch(InvocationTargetException cause)
        {
            // リフレクションメソッド呼び出しの例外(InvocationTargetException)でこけた.
            String msg = String.format("Failed to call Test method caused by InvocationTargetException. methodName=%s, argTypes.length=%d, argTypes=%s", methodName, argTypes.length, formatArgTypesString(argTypes) );
            LoulanDNDSpringTestException exception = new LoulanDNDSpringTestException(msg, cause);
            throw exception;
        }
    }



    /**
     * 本クラスに実装されたテストメソッドの名前の一覧を返す.
     * 
     * @return
     * @throws LoulanDNDSpringTestException
     */
    public String[] getTestMethodNames() throws LoulanDNDSpringTestException
    {
        List<Method> targetMethodList = getJUnitTestMethods();

        String[] methodNames = new String[ targetMethodList.size() ];

        for( int i=0; i < targetMethodList.size(); i++)
        {
            Method m = targetMethodList.get(i);
            methodNames[i] = m.getName();
        }

        return methodNames;
    }


    /**
     * 本クラスに実装されたテストメソッドの一覧を返す.
     * 
     * @return
     * @throws LoulanDNDSpringTestException
     */
    public Method[] getTestMethods() throws LoulanDNDSpringTestException
    {

        List<Method> targetMethodList = getJUnitTestMethods();

        Method[] methods = new Method[ targetMethodList.size() ];

        for( int i=0; i < targetMethodList.size(); i++)
        {
            Method m = targetMethodList.get(i);
            methods[i] = m;
        }

        return methods;
    }


    /**
     * 本クラスに実装されたテストメソッドの中から指定されたメソッド名のメソッドの一覧を返す.
     * 
     * @param methodName
     * @return
     * @throws LoulanDNDSpringTestException
     */
    public Method[] getTestMethodsByName(String methodName)  throws LoulanDNDSpringTestException
    {
        Method[] allTestMethods = getTestMethods();

        List<Method> methodList = new ArrayList<Method>();
        for( Method m : allTestMethods )
        {
            if ( m.getName().equals(methodName) )
            {
                methodList.add(m);
            }
        }

        Method[] targetMethods = new Method[ methodList.size() ];
        for( int i=0; i < methodList.size(); i++)
        {
            targetMethods[i] = methodList.get(i);
        }

        return targetMethods;
    }



    /**
     * 本クラスに実装されたテストメソッドの中から指定されたメソッドを返す.
     * 
     * @param methodName
     * @param argTypes
     * @return
     * @throws LoulanDNDSpringTestException
     */
    public Method getTestMethod(String methodName, Class[] argTypes)  throws LoulanDNDSpringTestException
    {
        Method[] methods = getTestMethodsByName(methodName);

        for( Method m : methods )
        {
            if ( m.getName().equals(methodName) )
            {
                if ( compareMethodParameterTypes(m, argTypes ) == true )
                {
                    return m;
                }
            }
        }

        return null;
    }







    /**
     * 本クラスに実装されたJUnitテストメソッドの一覧を返す.
     * @return
     * @throws LoulanDNDSpringTestException
     */
    public List<Method> getJUnitTestMethods() throws LoulanDNDSpringTestException
    {
        List<Method> list = new ArrayList<Method>();

        Method[] allMethods = this.getClass().getMethods();
        for( Method m : allMethods )
        {
            if ( isJUnitTestMethod(m) )
            {
                list.add(m);
            }
        }

        return list;
    }



    /**
     * テストの実行時ログを出力する.
     * 
     * @param msgBody
     * @throws LoulanDNDSpringTestException
     */
    public void printTestLog(String msgBody) throws LoulanDNDSpringTestException
    {
        // とりえあず、標準出力する.
        // (将来的にはロガークラスに出力する)

        // 現在日時を"2024/06/11 12:34:56.123 JST"形式で取得する.
        String dateString = LoulanDNSUtils.getCurrentDateTimeString();

        String record = String.format("[TEST] : %s : %s", dateString, msgBody );
        System.out.println(record);

    }



    

    /**
     * 指定されたメソッドがテストメソッドか(Unitの@Testアノテーションがついているか)を判定するユーティリティメソッド.
     * 本メソッドはユーティリティメソッドのため、static属性を付与する.
     * 
     * @param method
     * @return
     */
    public static boolean isJUnitTestMethod(Method method)
    {
        Annotation a = method.getAnnotation( org.junit.Test.class );
        if ( a != null )
        {
            return true;
        }

        return false;
    }



    /**
     * 指定されたメソッドの引数タイプが指定されたクラスに一致するかを判定する.
     * 
     * @param m
     * @param argTypes
     * @return
     */
    public static boolean compareMethodParameterTypes(Method m, Class[] cmpParametrerTypes)
    {
        Class[] parameterTypes = m.getParameterTypes();

        if ( parameterTypes.length != cmpParametrerTypes.length )
        {
            return false;
        }

        for( int i=0; i < parameterTypes.length; i++)
        {
            if ( parameterTypes[i].equals( cmpParametrerTypes[i] ) == false )
            {
                return false;
            }
        }

        return true;
    }


    /**
     * 指定された引数タイプを表す文字列を生成する.
     * 
     * @param argTypes
     * @return
     */
    public static String formatArgTypesString(Class[] argTypes)
    {
        String argTypesString = "(";

        for( Class type : argTypes )
        {
            argTypesString += type.toString() + ";";
        }

        argTypesString += ")";

        return argTypesString;
    }



}