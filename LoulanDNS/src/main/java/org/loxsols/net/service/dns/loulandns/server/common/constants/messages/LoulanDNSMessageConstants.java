package org.loxsols.net.service.dns.loulandns.server.common.constants.messages;

import java.time.format.DateTimeFormatter;
import java.nio.charset.Charset;
import java.text.Format;
import java.util.regex.Pattern;

public class LoulanDNSMessageConstants
{

    // ******************************************
    // 以下、INFOコードとメッセージ本文
    // ******************************************
    public static final String INFO_100101 = "INFO-100101";
    public static final String INFO_100101_MSG= "Endpoint Service Task is going to suspend status.";

    public static final String INFO_100102 = "INFO-100102";
    public static final String INFO_100102_MSG= "Endpoint Service Task is suspended.";

    public static final String INFO_100198 = "INFO-100198";
    public static final String INFO_100198_MSG= "Endpoint Service Task is going to accidentaly STOP.";

    public static final String INFO_100199 = "INFO-100199";
    public static final String INFO_100199_MSG= "Endpoint Service Task is accidentaly STOPPED.";


    // ******************************************
    // 以下、WARNコードとメッセージ本文
    // ******************************************
    public static final String WARN_200101 = "WARN-200101";
    public static final String WARN_200101_MSG= "DNS protocol error is happend.";


    // ******************************************
    // 以下、ERRORコードとメッセージ本文
    // ******************************************
    public static final String ERROR_100101 = "ERROR-100101";
    public static final String ERROR_100101_MSG= "Invalid DNS Question Messagge.";


    public static final String ERROR_100201 = "ERROR-100201";
    public static final String ERROR_100201_MSG= "Invalid DNS Response Messagge.";


    public static final String ERROR_800101 = "ERROR-800101";
    public static final String ERROR_800101_MSG= "I/O error.";








}