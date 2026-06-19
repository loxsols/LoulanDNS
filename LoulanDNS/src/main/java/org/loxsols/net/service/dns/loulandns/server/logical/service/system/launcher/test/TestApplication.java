package org.loxsols.net.service.dns.loulandns.server.logical.service.system.launcher.test;


import java.io.*;

import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class TestApplication
{

    public static void main(String[] args)
    {
        System.out.println("Hello");

        try
        {
            new File("./test.txt").createNewFile();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }


    }

}