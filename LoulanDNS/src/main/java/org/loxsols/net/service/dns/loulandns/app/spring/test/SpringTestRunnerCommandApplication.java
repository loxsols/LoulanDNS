package org.loxsols.net.service.dns.loulandns.app.spring.test;

import java.util.stream.Collectors;
import java.io.IOException;
import java.util.*;


import com.google.common.reflect.*;

import org.junit.runner.RunWith;
import org.loxsols.net.service.dns.loulandns.server.http.spring.service.test.LoulanDNSLogicalDBServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import android.providers.settings.GlobalSettingsProto.App;


import org.loxsols.net.service.dns.loulandns.server.http.spring.test.*;

/*
 * 
 * 以下のQiita記事を参考に実装した.
 * 
 * Spring Boot でCommandLineRunnerを使いながらJUnitのテストを実行する
 * https://qiita.com/tosh_m/items/80b00f8fb8129ecbdf97
 * 
 */


@SpringBootApplication
@Import(SpringTestRunnerCommandApplicationConfig.class)
@TestPropertySource(locations = "/application-test.properties")
public class SpringTestRunnerCommandApplication implements CommandLineRunner
{
    

    // @Autowired
    // private static ApplicationContext applicationContext;

    public static ApplicationContext getApplicationContext()
    {
        // return applicationContext;
        var context = new AnnotationConfigApplicationContext(SpringTestRunnerCommandApplicationConfig.class);
        return context;
    }


    public static void main(String[] args)
    {

        System.out.println("main!");


        System.setProperty("spring.datasource.sqlScriptEncoding", "UTF-8");
        System.setProperty("spring.datasource.driverClassName", "org.hsqldb.jdbc.JDBCDriver");

        if ( System.getProperty("spring.datasource.url") == null )
        { 
            System.setProperty("spring.datasource.url", "jdbc:hsqldb:file:./LoulanDNS/db/HSQLDB/LoulanDNS/LoulanDNS");
        }
        
        System.setProperty("spring.datasource.username", "loulan");
        System.setProperty("spring.datasource.password", "loulan");
        System.setProperty("jpa.hibernate.ddl-auto", "validate");
        System.setProperty("logging.level.sql", "debug");


        String dataSourceURL = System.getProperty("spring.datasource.url");
        System.out.println("spring.datasource.url=" + dataSourceURL);

        String currentDir = java.nio.file.Paths.get("").toAbsolutePath().toString();
        System.out.println("current_directory=" + currentDir);


        // SpringApplication.run(SpringTestRunnerCommandApplication.class, args);

        try
        {
            SpringTestRunnerCommandApplication app = new SpringTestRunnerCommandApplication();
            app.run( args );
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    

    }
    
    @Override
    public void run(String... args) throws Exception
    {
        System.out.println("run!");

        System.out.println("args.length=" + args.length );
        System.out.println("args[0]=" + args[0] );


        if ( args.length == 0 )
        {
            System.out.println("Usage : <package> | <package>.<class> | <package>.<class>#<method>");
            return ;
        }

        String packageName = getPackageNameByArgs(args);
        String className = getClassNameByArgs(args);
        String methodName = getMethodNameByArgs(args);

        System.out.println("packageName=" + packageName );
        System.out.println("className=" + className );
        System.out.println("methodName=" + methodName);


        
        ApplicationContext context = getApplicationContext();

        // LoulanDNSLogicalDBServiceTest test = (LoulanDNSLogicalDBServiceTest)context.getBean(LoulanDNSLogicalDBServiceTest.class);
        // test.testGetUserInfo001();

        if ( methodName != null )
        {
            String fullClassName = packageName + "." + className;
            runTestMethod(fullClassName, methodName);
        }
        else if ( className != null )
        {
            String fullClassName = packageName + "." + className;
            runTestClass(fullClassName);
        }
        else
        {
            runTestPackage(packageName);
        }

    }


    /**
     * 指定されたテストメソッドを単発実行する.
     * 
     * @param beanFullClassName
     * @param methodName
     * @throws Exception
     */
    protected void runTestMethod(String beanFullClassName, String methodName) throws Exception
    {
        ApplicationContext context = getApplicationContext();

        Class beanClassType = Class.forName( beanFullClassName );
        LoulanDNSSSpringTestTarget testTarget = (LoulanDNSSSpringTestTarget)context.getBean(beanClassType);

        testTarget.callTest(methodName);
    }


    /**
     * 指定されたテストクラス内の全テストメソッドを連続実行する.
     * 
     * @param beanFullClassName
     * @param methodName
     * @throws Exception
     */
    protected void runTestClass(String beanFullClassName) throws Exception
    {
        ApplicationContext context = getApplicationContext();

        Class beanClassType = Class.forName( beanFullClassName );
        LoulanDNSSSpringTestTarget testTarget = (LoulanDNSSSpringTestTarget)context.getBean(beanClassType);

        for( String methodName : testTarget.getTestMethodNames() )
        {
            testTarget.callTest(methodName);
        }
    }


    /**
     * 指定されたパッケージのテストメソッドを実行する.
     * 
     * @param packageName
     * @throws Exception
     */
    protected void runTestPackage(String packageName) throws Exception
    {
        ApplicationContext context = getApplicationContext();

        List<Class> classList = getClassesByPackageName(packageName);
        for( Class cls : classList )
        {
            // LoulanDNSSSpringTestTargetクラスの派生クラスかを判定する.
            if ( cls.isAssignableFrom( LoulanDNSSSpringTestTarget.class ) == false )
            {
                continue;
            }

            Class beanClassType = cls;
            LoulanDNSSSpringTestTarget testTarget = (LoulanDNSSSpringTestTarget)context.getBean(beanClassType);

            for( String methodName : testTarget.getTestMethodNames() )
            {
                testTarget.callTest(methodName);
            }

        }

    }


    public List<Class> getClassesByPackageName(String packageName) throws LoulanDNDSpringTestException
    {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        List<Class> allClasses;
        
        try
        {
            allClasses           = ClassPath.from(loader)
                                            .getTopLevelClasses(packageName).stream()
                                            .map(info -> info.load())
                                            .collect(Collectors.toList());
        }
        catch(IOException cause)
        {
            String msg = String.format("Failed to list up classes in package : %s", packageName );
            LoulanDNDSpringTestException exception = new LoulanDNDSpringTestException(msg, cause);
            throw exception;
        }
        
        return allClasses;
    }


    /**
     * コマンド引数から、テスト対象のパッケージ名を取得する.
     * 
     * @param args
     * @return
     */
    public static String getPackageNameByArgs(String[] args)
    {
        String fullPackageName = "";

        String[] packages = args[0].split("\\.");

        // "."記号で区切った最後の文字列はクラス名なのでパッケージ名からは除外する.
        for( int i=0; i < packages.length - 1; i++)
        {
            fullPackageName += packages[i];
            
            if ( i + 1 < packages.length - 1 )
            {
                // パッケージ区切りのドット記号を付与する.
                fullPackageName += ".";
            }
        }

        return fullPackageName;
    }


    /**
     * コマンド引数から、テスト対象のクラス名を取得する.
     * 
     * @param args
     * @return
     */
    public static String getClassNameByArgs(String[] args)
    {
        String packageName = getPackageNameByArgs(args);

        String className = args[0].substring( packageName.length() + 1 );

        if ( className.contains("#") )
        {
            className = className.split("#")[0];
        }

        return className;
    }

    /**
     * コマンド引数から、テスト対象のメソッド名を取得する.
     * 
     * @param args
     * @return
     */
    public static String getMethodNameByArgs(String[] args)
    {

        if( args[0].contains("#") == false )
        {
            // 指定された引数にメソッド名は含まれていない.
            return null;
        }

        String fullClassPath = getPackageNameByArgs(args) + "." + getClassNameByArgs(args);

        String methodName = args[0].substring( fullClassPath.length() + 1 );
        return methodName;
    }

}
