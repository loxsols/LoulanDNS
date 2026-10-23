package org.loxsols.net.service.dns.loulandns.server.common.util;

import java.util.TimeZone;

import static org.mockito.ArgumentMatchers.endsWith;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.regex.Matcher;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.loxsols.net.service.dns.loulandns.client.common.DNSClientCommonException;
import org.loxsols.net.service.dns.loulandns.server.common.*;
import org.loxsols.net.service.dns.loulandns.server.common.constants.LoulanDNSConstants;
import org.loxsols.net.service.dns.loulandns.server.logical.model.UserInfo;
import org.xbill.DNS.Zone;
import org.loxsols.net.service.dns.loulandns.server.logical.model.UserGroupInfo;
import org.loxsols.net.service.dns.loulandns.server.logical.model.UserGroupMappingInfo;
import org.loxsols.net.service.dns.loulandns.server.logical.model.UserGroupPrivilegeInfo;

public class LoulanDNSUtils
{


    // DBレコード上で使用する時刻表記の文字列を取得する.
    // 例) TimeZone型クラス -> "2024/06/11 12:34:56.123 JST"
    public static String toDateTimeString(ZonedDateTime dateTimeObject)
    {
        String value = LoulanDNSConstants.DB_CONST_DATE_TIME_FORMAT.format(dateTimeObject);
        return value;
    }

    // DBレコード上で使用する時刻表記の文字列を取得する.
    // 例) "2024/06/11 12:34:56.123 JST" -> TimeZone型クラス
    public static ZonedDateTime toDateTimeObject(String dateTimeString) throws DNSServiceCommonException
    {
        ZonedDateTime dateTime;

        if ( isISO8601DateTimeFormatString(dateTimeString) )
        {
            // 2026/10/06追加.
            // ISO8601形式文字列("20260914T164342+0900"など)の場合も拾うことにする.
            dateTime = toDateTimeObjectFromISO8601(dateTimeString);
            return dateTime;
        }
        else if ( isISO8601DateTimeFormatSubString(dateTimeString) )
        {
            // 2026/10/19追加.
            // ISO8601形式の部分文字列("20260914"など)の場合も拾うことにする.
            dateTime = toDateTimeObjectFromISO8601SubString(dateTimeString);
            return dateTime;
        }

        // 以下のタイムゾーン付きの文字列を正規表現で空白文字で区切る.
        // 例)"2024/06/11 12:34:56.123 Asia/Tokyo"  -> ["2024/06/11", "12:34:56.123", "Asia/Tokyo"]
        String[] dateTimeStringArray = dateTimeString.split("\\s+");

        if ( dateTimeStringArray.length < 3)
        {
            String msg = String.format("Failed to convert to ZonedDateTime object from String. Invalid array size. array-size=%d, dateTimeString=%s", dateTimeStringArray.length, dateTimeString);
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }


        // タイムゾーンを表すコードが文字列末尾に存在するはずなので取得する.
        // 例1) "2024/06/11 12:34:56.123 JST"  -> "JST"
        // 例2) "2024/06/11 12:34:56.123 Asia/Tokyo"  -> "Asia/Tokyo"
        // String timeZoneCode = dateTimeString.substring( dateTimeString.length() - 3 );
        String timeZoneCode = dateTimeStringArray[2];

        // "JST"のようなタイムゾーンの名称からTimeZoneクラスを生成する.
        TimeZone timeZone = getTimeZone(timeZoneCode);
        ZoneId zoneID = ZoneId.of( timeZone.toZoneId().getId() );

        // 指定された時刻文字列からタイムゾーンコードを取り除く.
        // 例) "2024/06/11 12:34:56.123 JST"  -> "2024/06/11 12:34:56.123" 
        String dateTimeStr2 = String.format("%s %s", dateTimeStringArray[0], dateTimeStringArray[1]);
        dateTimeStr2 = dateTimeStr2.trim();

        // 変換する
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern( LoulanDNSConstants.LOCAL_DATE_TIME_FORMAT_PATTERN );

        LocalDateTime localDateTime = LocalDateTime.parse(dateTimeStr2, formatter );
        dateTime = ZonedDateTime.of( localDateTime, zoneID );
        return dateTime;
    }


    /**
     * 指定したタイムゾーンコードからTimeZoneオブジェクトを生成する.
     * Java8以降のJVMでは非推奨となっている"JST"は本メソッド内で"Asia/Tokyo"に読み替えて対応する.
     * 
     * @param timeZoneCode
     * @return
     * @throws DNSServiceCommonException
     */
    public static TimeZone getTimeZone(String timeZoneCode) throws DNSServiceCommonException
    {
        if ( timeZoneCode == null || timeZoneCode.isEmpty() )
        {
            String msg = String.format("Failed to get TimeZone object from String. value=%s", timeZoneCode);
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        // 非推奨の古いタイムゾーン表記("JST"など)を変換する.
        if ( timeZoneCode.equals("JST") )
        {
            timeZoneCode = "Asia/Tokyo";
        }

        // "Aisa/Tokyo"のようなタイムゾーンの名称からTimeZoneクラスを生成する.
        TimeZone timeZone = TimeZone.getTimeZone(timeZoneCode);
        return timeZone;
    }


    /**
     * 本システムのデフォルトタイムゾーンを取得する.
     * 
     * @return
     * @throws DNSServiceCommonException
     */
    public static TimeZone getSystemDefaultTimeZone() throws DNSServiceCommonException
    {
        TimeZone timeZone = TimeZone.getDefault();
        return timeZone;
    }


    /**
     *  指定したTimeZoneのZoneID値を取得する.
     * 
     * @param timeZone
     * @return
     * @throws DNSServiceCommonException
     */
    public static ZoneId getTimeZoneID(TimeZone timeZone) throws DNSServiceCommonException
    {
        ZoneId zoneID = timeZone.toZoneId();
        return zoneID;
    }

    /**
     * ZoneIDからタイムゾーン型を取得する.
     * 
     * @param zoneID
     * @return
     * @throws DNSServiceCommonException
     */
    public static TimeZone getTimeZone(ZoneId zoneID) throws DNSServiceCommonException
    {
        TimeZone timeZone = TimeZone.getTimeZone(zoneID);
        return timeZone;
    }


    // 現在時刻を表すZonedDateTimeオブジェクトを取得する.
    // タイムゾーンは本システムの設定に従い設定する(現状はシステムのデフォルト値).
    public static ZonedDateTime getCurrentZonedDateTime()
    {
        ZonedDateTime current = ZonedDateTime.now();
        return current;
    }

    // 現在時刻を表す文字列を所得する.
    public static String getCurrentDateTimeString()
    {
        ZonedDateTime dateTime = getCurrentZonedDateTime();
        String dateTimeString = toDateTimeString(dateTime);
        return dateTimeString;
    }


    /**
     * 本システムにおける下限表現日時(エポック時刻)を返す.
     * 
     * @return
     */
    public static ZonedDateTime getSystemMinimumZonedDateTime() throws DNSServiceCommonException
    {
        String minValue = getSystemMinimumDateTimeString();
        ZonedDateTime minDateTime = toDateTimeObject(minValue);
        return minDateTime;
    }


    /**
     * 本システムにおける下限表現日時(エポック時刻)の文字列を返す.
     * 
     * @return
     */
    public static String getSystemMinimumDateTimeString()
    {
        // "1900/01/01 00:00:00.000 Asia/Tokyo"
        String minValue = "1900/01/01 00:00:00.000 Asia/Tokyo";
        return minValue;
    }


    /**
     * 本システムにおける上限表現日時(限界時刻)を返す.
     * 
     * @return
     */
    public static ZonedDateTime getSystemMaxZonedDateTime() throws DNSServiceCommonException
    {
        String maxValue = getSystemMaxDateTimeString();
        ZonedDateTime maxDateTime = toDateTimeObject(maxValue);
        return maxDateTime;
    }


    /**
     * 本システムにおける下限表現日時(エポック時刻)の文字列を返す.
     * 
     * @return
     */
    public static String getSystemMaxDateTimeString()
    {
        // "9999/12/31 23:59:59.999 Asia/Tokyo"
        String maxValue = "9999/12/31 23:59:59.999 Asia/Tokyo";
        return maxValue;
    }



    /**
     * ISO8601形式の日時文字列かを判定する.
     * 
     * @param iso8601DateTimeString
     * @return
     */
    public static boolean isISO8601DateTimeFormatString(String iso8601DateTimeString)
    {
        try
        {
            toDateTimeObjectFromISO8601(iso8601DateTimeString);
        }
        catch(DNSServiceCommonException cause)
        {
            return false;
        }

        return true;
    }



    /**
     * ISO8601形式の日時文字列をZonedDateTime型に変換する.
     * Ex.1) 基本型 : 20220904T160748.53
     * Ex.2) 拡張型 : 2022-09-04T16:07:48.53 
     * 
     * @param iso8601DateTimeString
     * @return
     * @throws DNSServiceCommonException
     */
    public static ZonedDateTime toDateTimeObjectFromISO8601(String iso8601DateTimeString) throws DNSServiceCommonException
    {
        ZonedDateTime dateTime;

        try
        {
            // 指定したISO8601文字列をタイムゾーン情報付きとして解析する.
            dateTime = ZonedDateTime.parse(iso8601DateTimeString, DateTimeFormatter.ISO_DATE_TIME);
        }
        catch(DateTimeParseException cause)
        {
            try
            {
                // タイムゾーン情報が付加されてない場合に備えて、システムのデフォルトタイムゾーン情報を付加して再度読み取る.
                TimeZone defaultTimeZone = getSystemDefaultTimeZone();
                DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone( getTimeZoneID(defaultTimeZone) );
                dateTime = ZonedDateTime.parse(iso8601DateTimeString, formatter);
            }
            catch(DateTimeParseException cause2)
            {
                String msg = String.format("Failed to parse ISO8601 DateTime format String. value=%s", iso8601DateTimeString);
                DNSServiceCommonException exception = new DNSServiceCommonException(msg, cause2);
                throw exception;
            }
        }

        return dateTime;
    }


    /**
     * ISO8601形式の日時形式の部分文字列かを判定する.
     * 
     * @param iso8601DateTimeString
     * @return
     */
    public static boolean isISO8601DateTimeFormatSubString(String dateTimeString)
    {
        try
        {
            toDateTimeObjectFromISO8601SubString(dateTimeString);
        }
        catch(DNSServiceCommonException cause)
        {
            return false;
        }

        return true;
    }


    /**
     * ISO8601形式の部分文字列からZonedDateTimeの日付時刻型に変換する.
     * 以下のISO8601形式の文字列をベースに、その部分文字列をも変換する.
     *      基本形式    20260914T164342+0900
     *      拡張形式	2026-09-14T16:43:42+09:00
     * 
     * Ex.1 ) "20260914"            -> "2026/09/14 00:00:00.000 Asia/Tokyo";
     * Ex.2 ) "2026-09-14"          -> "2026/09/14 00:00:00.000 Asia/Tokyo";
     * Ex.3 ) "20260914T164342"     -> "2026/09/14 16:43:42.000 Asia/Tokyo";
     * Ex.4 ) "2026-09-14T16:43:42" -> "2026/09/14 16:43:42.000 Asia/Tokyo";
     * 
     * 
     * @param dateTimeString
     * @return
     * @throws DNSServiceCommonException
     */
    protected static ZonedDateTime toDateTimeObjectFromISO8601SubString(String dateTimeString) throws DNSServiceCommonException
    {

        // 2026/10/19 本関数内部はGeminiで実装した.

        if (dateTimeString == null || dateTimeString.trim().isEmpty())
        {
            String msg = String.format("Failed to parse ISO8601SubString. The value is not specified : %s", dateTimeString );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        String s = dateTimeString.trim();

        // 1. "T" の有無で日付のみか、時刻も含むかを判定
        int tIndex = s.indexOf('T');
        boolean hasTime = (tIndex >= 0);

        // 2. 日付部分(Tより前)にハイフンが含まれるかで「拡張形式 / 基本形式」を判定
        String datePart = hasTime ? s.substring(0, tIndex) : s;
        boolean isExtendedDate = datePart.contains("-");

        // 3. 時刻部分にタイムゾーン (+, -, Z) が含まれるかを判定
        boolean hasZone = false;
        if (hasTime)
        {
            String timePart = s.substring(tIndex + 1);
            hasZone = timePart.contains("+") || timePart.contains("-") || timePart.endsWith("Z");
        }

        DateTimeFormatter formatter;

        try
        {
            // 文字列の特徴に応じたフォーマッターの選択
            if (isExtendedDate)
            {
                // [拡張形式] yyyy-MM-dd...
                if (hasTime)
                {
                    formatter = hasZone ? DateTimeFormatter.ISO_OFFSET_DATE_TIME : DateTimeFormatter.ISO_LOCAL_DATE_TIME;
                }
                else
                {
                    formatter = DateTimeFormatter.ISO_LOCAL_DATE;
                }

            }
            else
            {
                // [基本形式] yyyyMMdd...
                if (hasTime)
                {
                    // [XXX][XX][X] は +09:00, +0900, +09, Z などの各フォーマットを柔軟にパースするための指定
                    formatter = hasZone ? DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss[XXX][XX][X]") : DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");
                }
                else
                {
                    formatter = DateTimeFormatter.BASIC_ISO_DATE;
                }
            }

            // 解析を実行
            TemporalAccessor accessor = formatter.parse(s);

            // タイムゾーンの取得
            // とりあえずシステムデフォルトのタイムゾーンを強制的に設定する.
            // TODO : 将来的にタイムゾーンを引数文字列から動的に指定できるようにする.
            ZoneId zoneId;
            TimeZone defaultTimeZone = getSystemDefaultTimeZone();
            zoneId = getTimeZoneID(defaultTimeZone);

            // 日付の取得 (必須)
            LocalDate date = LocalDate.from(accessor);

            // 時刻の取得
            // とりえあず、00:00:00を設定しておく
            // TODO : いずれは、引数文字列から動的に指定できるようにする.
            LocalTime time = LocalTime.MIDNIGHT;

            // 最終的なZonedDateTimeを生成して返却
            ZonedDateTime zonedDateTime = ZonedDateTime.of(date, time, zoneId);
            return zonedDateTime;
        }
        catch (DateTimeParseException cause)
        {
            // 解析失敗時に共通例外にラップしてスローする
            String msg = String.format("Failed to parse ISO8601SubString. The value is %s", dateTimeString);
            DNSServiceCommonException exception = new DNSServiceCommonException(msg, cause);
            throw exception;
        }
    }




    // 指定されたユーザー名が正しい書式か検証する.
    public static boolean checkFormatUserName(String userName)
    {
        Matcher matcher = LoulanDNSConstants.REGULAR_EXPRESSION_PATTERN_OF_USER_NAME.matcher(userName);
        if ( matcher.matches() == true )
        {
            return true;
        }

        return false;
    }


    // 指定されたユーザーパスワードが正しい書式か検証する.
    public static boolean checkFormatUserPassword(String userPassword)
    {
        Matcher matcher = LoulanDNSConstants.REGULAR_EXPRESSION_PATTERN_OF_USER_PASSWORD.matcher(userPassword);
        if ( matcher.matches() == true )
        {
            return true;
        }

        return false;
    }


    // 指定されたユーザーグループ名が正しい書式か検証する.
    public static boolean checkFormatUserGroupName(String userGroupName)
    {
        Matcher matcher = LoulanDNSConstants.REGULAR_EXPRESSION_PATTERN_OF_USER_GROUP_NAME.matcher(userGroupName);
        if ( matcher.matches() == true )
        {
            return true;
        }

        return false;
    }



    // ユーザークラスのオブジェクトをメモリ上で作成する. (DB上のレコードとは無関係にシステム上で作成するだけ.)
    // ユーザーIDは特に設定しない.
    // レコード作成時刻、レコード更新時効の値は現在のシステム上の時刻値を入力する. (使用時はDBのレコードをそのまま上書きしないように注意.)
    public static UserInfo createUserInfoObject(String userName, String userPassword, int recordStatus, String memo) throws LoulanDNSSystemServiceException
    {
        // レコード作成時刻と更新時刻のインスタンスを現在時刻/本システム所定タイムゾーンで作成.
        String createDate = getCurrentDateTimeString();
        String updateDate = createDate;

        // ユーザー情報を新規作成する.
        UserInfo userInfo = createUserInfoObject(null, userName, userPassword, recordStatus, memo, createDate, updateDate);

        return userInfo;
    }


    // ユーザークラスのオブジェクトをメモリ上で作成する. (DB上のレコードとは無関係にシステム上で作成するだけ.)
    // オブジェクト作成時に入力値チェックも併せて行う.
    public static UserInfo createUserInfoObject(Long userID, String userName, String userPassword, int recordStatus, String memo, String createDate, String updateDate) throws LoulanDNSSystemServiceException
    {

        if( userName == null )
        {
            // 指定されたユーザー名がnull
            String msg = String.format("The UserName is null.");
            LoulanDNSSystemServiceException execption = new LoulanDNSSystemServiceException( msg );
            throw execption;
        }
        
        if( userPassword == null )
        {
            // 指定されたパスワードがnull
            String msg = String.format("The UserPassword is null.");
            LoulanDNSSystemServiceException execption = new LoulanDNSSystemServiceException( msg );
            throw execption;
        }

    
        if ( LoulanDNSUtils.checkFormatUserName(userName) == false)
        {
            // ユーザー名の値が不正.
            String msg = String.format("Invalid UserName value. userName=%d", userName );
            LoulanDNSSystemServiceException execption = new LoulanDNSSystemServiceException( msg );
            throw execption;
        }


        if ( LoulanDNSUtils.checkFormatUserPassword(userPassword) == false)
        {
            // ユーザーパスワードの値が不正.
            String msg = String.format("Invalid UserPassword value. userPassword=%d", userPassword );
            LoulanDNSSystemServiceException execption = new LoulanDNSSystemServiceException( msg );
            throw execption;
        }


        
        if( recordStatus != LoulanDNSConstants.DB_CONST_VALUE_RECORD_STATUS_INACTIVE &&
            recordStatus != LoulanDNSConstants.DB_CONST_VALUE_RECORD_STATUS_ACTIVE )
        {
            // レコードステータスの値が不正.
            String msg = String.format("Invalid RecordStatus value. recordStatus=%d", recordStatus);
            LoulanDNSSystemServiceException execption = new LoulanDNSSystemServiceException( msg );
            throw execption;
        }

        if( memo == null )
        {
            // 指定されたメモがnull値の場合は空文字列に置き換えて処理を続行する.
            memo = "";
        }


        Matcher createDateMatcher = LoulanDNSConstants.REGULAR_EXPRESSION_PATTERN_OF_DATE_TIME_STRING.matcher(createDate);
        if ( createDateMatcher.matches() == false )
        {
            // レコード作成時刻の値が不正.
            String msg = String.format("Invalid CreateDate value. createDate=%s", createDate );
            LoulanDNSSystemServiceException execption = new LoulanDNSSystemServiceException( msg );
            throw execption;
        }

        Matcher updateDateMatcher = LoulanDNSConstants.REGULAR_EXPRESSION_PATTERN_OF_DATE_TIME_STRING.matcher(updateDate);
        if ( updateDateMatcher.matches() == false )
        {
            // レコード更新時刻の値が不正.
            String msg = String.format("Invalid UpdateDate value. updateDate=%s", updateDate );
            LoulanDNSSystemServiceException execption = new LoulanDNSSystemServiceException( msg );
            throw execption;
        }

        ZonedDateTime createDateObject;
        try
        {
            createDateObject = toDateTimeObject(createDate);
        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to set convert ZonedDateTime from String. value=%s", createDate);
            LoulanDNSSystemServiceException exception = new LoulanDNSSystemServiceException(msg, cause);
            throw exception;
        }

        ZonedDateTime updateDateObject;
        
        try
        {
            updateDateObject = toDateTimeObject(updateDate);
        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to set convert ZonedDateTime from String. value=%s", updateDate);
            LoulanDNSSystemServiceException exception = new LoulanDNSSystemServiceException(msg, cause);
            throw exception;
        }





        if ( createDateObject.isAfter(updateDateObject) == true )
        {
            // レコード作成時刻がレコード更新時刻よりも後の時刻で設定されている.
            String msg = String.format("Invalid DateTime value. CreateDate is after time of UpdateDate. createDate=%s, updateDate=%s", createDate, updateDate );
            LoulanDNSSystemServiceException execption = new LoulanDNSSystemServiceException( msg );
            throw execption;           
        }


        // ユーザー情報を新規作成する.
        UserInfo userInfo = new UserInfo();
        userInfo.setUserID(userID);
        userInfo.setUserName(userName);
        userInfo.setUserPassword(userPassword);
        userInfo.setRecordStatus(recordStatus);
        userInfo.setMemo(memo);
        userInfo.setCreateDate(createDate);
        userInfo.setUpdateDate(updateDate);

        return userInfo;
    }




    // ユーザーグループクラスのオブジェクトをメモリ上で作成する. (DB上のレコードとは無関係にシステム上で作成するだけ.)
    // ユーザーグループIDは特に設定しない.
    // レコード作成時刻、レコード更新時効の値は現在のシステム上の時刻値を入力する. (使用時はDBのレコードをそのまま上書きしないように注意.)
    public static UserGroupInfo createUserGroupInfoObject(String userGroupName, int recordStatus, String memo) throws LoulanDNSSystemServiceException
    {

        // レコード作成時刻と更新時刻のインスタンスを現在時刻/本システム所定タイムゾーンで作成.
        String createDate = getCurrentDateTimeString();
        String updateDate = createDate;

        UserGroupInfo userGroupInfo = createUserGroupInfoObject(null, userGroupName, recordStatus, memo, createDate, updateDate);
        return userGroupInfo;

    }

       // ユーザーグループクラスのオブジェクトをメモリ上で作成する. (DB上のレコードとは無関係にシステム上で作成するだけ.)
    // ユーザーグループIDは特に設定しない.
    // レコード作成時刻、レコード更新時効の値は現在のシステム上の時刻値を入力する. (使用時はDBのレコードをそのまま上書きしないように注意.)
    public static UserGroupInfo createUserGroupInfoObject(Long userGroupID, String userGroupName, int recordStatus, String memo, String createDate, String updateDate) throws LoulanDNSSystemServiceException
    {
        
        if( userGroupName == null )
        {
            // 指定されたユーザーグループ名がnull
            String msg = String.format("The UserGroupName is null.");
            LoulanDNSSystemServiceException execption = new LoulanDNSSystemServiceException( msg );
            throw execption;
        }
        

    
        if ( LoulanDNSUtils.checkFormatUserGroupName(userGroupName) == false)
        {
            // ユーザーグループ名の値が不正.
            String msg = String.format("Invalid UserGroupName value. userGroupName=%d", userGroupName );
            LoulanDNSSystemServiceException execption = new LoulanDNSSystemServiceException( msg );
            throw execption;
        }

        
        if( recordStatus != LoulanDNSConstants.DB_CONST_VALUE_RECORD_STATUS_INACTIVE &&
            recordStatus != LoulanDNSConstants.DB_CONST_VALUE_RECORD_STATUS_ACTIVE )
        {
            // レコードステータスの値が不正.
            String msg = String.format("Invalid RecordStatus value. recordStatus=%d", recordStatus);
            LoulanDNSSystemServiceException execption = new LoulanDNSSystemServiceException( msg );
            throw execption;
        }

        if( memo == null )
        {
            // 指定されたメモがnull値の場合は空文字列に置き換えて処理を続行する.
            memo = "";
        }


        Matcher createDateMatcher = LoulanDNSConstants.REGULAR_EXPRESSION_PATTERN_OF_DATE_TIME_STRING.matcher(createDate);
        if ( createDateMatcher.matches() == false )
        {
            // レコード作成時刻の値が不正.
            String msg = String.format("Invalid CreateDate value. createDate=%s", createDate );
            LoulanDNSSystemServiceException execption = new LoulanDNSSystemServiceException( msg );
            throw execption;
        }

        Matcher updateDateMatcher = LoulanDNSConstants.REGULAR_EXPRESSION_PATTERN_OF_DATE_TIME_STRING.matcher(updateDate);
        if ( updateDateMatcher.matches() == false )
        {
            // レコード更新時刻の値が不正.
            String msg = String.format("Invalid UpdateDate value. updateDate=%s", updateDate );
            LoulanDNSSystemServiceException execption = new LoulanDNSSystemServiceException( msg );
            throw execption;
        }

        ZonedDateTime createDateObject;
        try
        {
            createDateObject = toDateTimeObject(createDate);
        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to set convert ZonedDateTime from String. value=%s", createDate);
            LoulanDNSSystemServiceException exception = new LoulanDNSSystemServiceException(msg, cause);
            throw exception;
        }

        ZonedDateTime updateDateObject;
        
        try
        {
            updateDateObject = toDateTimeObject(updateDate);
        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to set convert ZonedDateTime from String. value=%s", updateDate);
            LoulanDNSSystemServiceException exception = new LoulanDNSSystemServiceException(msg, cause);
            throw exception;
        }


        if ( createDateObject.isAfter(updateDateObject) == true )
        {
            // レコード作成時刻がレコード更新時刻よりも後の時刻で設定されている.
            String msg = String.format("Invalid DateTime value. CreateDate is after time of UpdateDate. createDate=%s, updateDate=%s", createDate, updateDate );
            LoulanDNSSystemServiceException execption = new LoulanDNSSystemServiceException( msg );
            throw execption;           
        }


        // ユーザーグループ情報を新規作成する.
        UserGroupInfo userGroupInfo = new UserGroupInfo();
        userGroupInfo.setUserGroupID(userGroupID);
        userGroupInfo.setUserGroupName(userGroupName);
        userGroupInfo.setRecordStatus(recordStatus);
        userGroupInfo.setMemo(memo);
        userGroupInfo.setCreateDate(createDate);
        userGroupInfo.setUpdateDate(updateDate);

        return userGroupInfo;
    }

    
    // 指定された権限情報が管理者権限(システム全体を管理できる権限)に該当するかを判定する.
    public static boolean checkForAdministratorPrivilege(UserGroupPrivilegeInfo userGroupPrivilegeInfo)
    {
        if ( userGroupPrivilegeInfo.getDnsInfoMaskValue() < LoulanDNSConstants.CONST_PRIVILEGE_DNS_INFO_MASK_VALUE_CREATE_SYSTEM_INFO )
        {
            // 右権限なし : 300 : DNS情報(システム全体)の作成/削除可能
            return false;
        }

        if ( userGroupPrivilegeInfo.getUserInfoMaskValue() < LoulanDNSConstants.CONST_PRIVILEGE_USER_INFO_MASK_VALUE_CREATE_SYSTEM_INFO )
        {
            // 右権限なし : 300 : ユーザー情報(システム全体)の作成/削除可能
            return false;
        }

        if ( userGroupPrivilegeInfo.getUserInfoMaskValue() < LoulanDNSConstants.CONST_PRIVILEGE_SYSTEM_INFO_MASK_VALUE_CREATE_SYSTEM_INFO )
        {
            // 右権限なし : 300 : システム情報(システム全体)の作成/削除可能
            return false;
        }

        return true;

    }


    /**
     * サービスタイプコードからサービスタイプ名に変換する.
     * 
     * @param code
     * @return
     */
    public String serviceTypeCodeToServiceTypeName(long code)
    {
        String name;
        if ( code == LoulanDNSConstants.CONST_DNS_SERVICE_INSTANCE_TYPE_CODE_MINIMUM )
        {  
            name = LoulanDNSConstants.CONST_DNS_SERVICE_INSTANCE_TYPE_NAME_MINIMUM;
        }
        else
        {
            name = null;
        }

        return name;
    }


    /**
     * サービスタイプ名からサービスタイプコードに変換する.
     * 
     * @param name
     * @return
     */
    public Long serviceTypeNameToServiceTypeCode(String name)
    {
        Long code = null;

        if ( name.equals(LoulanDNSConstants.CONST_DNS_SERVICE_INSTANCE_TYPE_NAME_MINIMUM) )
        {
            code = (long)LoulanDNSConstants.CONST_DNS_SERVICE_INSTANCE_TYPE_CODE_MINIMUM;
        }
        else
        {
            code = null;
        }

        return code;
    }


    /**
     * リゾルバのタイプ名から、リゾルバのタイプコードに変換する.
     * 
     * @param name
     * @return
     */
    public Long resolverTypeNameToResolverTypeCode(String name)
    {
        Long code;

        if ( name.equals("UDP") || name.equals("udp"))
        {
            // 単なるUDPなどのプロトコル指定文字列の場合は、外部問い合わせ用のリゾルバが指定されたものとみなす.
            code = (long)LoulanDNSConstants.CONST_DNS_RESOLVER_TYPE_OUTBOUND_UDP;
        }
        else if ( name.equals("TCP") || name.equals("tcp") )
        {
            code = (long)LoulanDNSConstants.CONST_DNS_RESOLVER_TYPE_OUTBOUND_TCP;
        }
        else if ( name.equals("DOH") || name.equals("doh") )
        {
            code = (long)LoulanDNSConstants.CONST_DNS_RESOLVER_TYPE_OUTBOUND_DOH;
        }
        else if ( name.equals("DOT") || name.equals("dot") )
        {
            code = (long)LoulanDNSConstants.CONST_DNS_RESOLVER_TYPE_OUTBOUND_DOH;
        }
        else 
        {
            code = null;
        }

        return code;
    }

    /**
     * エンドポイントタイプコードからエンドポイント名に変換する.
     * 
     * @param code
     * @return
     */
    public String endpointTypeCodeToEndpointTypeName(long code)
    {
        String name = null;
        if ( code == LoulanDNSConstants.CONST_DNS_SERVICE_ENDPOINT_TYPE_CODE_UDP )
        {
            name = LoulanDNSConstants.CONST_DNS_SERVICE_ENDPOINT_TYPE_NAME_UDP;
        }
        else if ( code == LoulanDNSConstants.CONST_DNS_SERVICE_ENDPOINT_TYPE_CODE_TCP )
        {
            name = LoulanDNSConstants.CONST_DNS_SERVICE_ENDPOINT_TYPE_NAME_TCP;
        }
        else if ( code == LoulanDNSConstants.CONST_DNS_SERVICE_ENDPOINT_TYPE_CODE_DOH )
        {
            name = LoulanDNSConstants.CONST_DNS_SERVICE_ENDPOINT_TYPE_NAME_DOH;
        }
        else if ( code == LoulanDNSConstants.CONST_DNS_SERVICE_ENDPOINT_TYPE_CODE_DOT  )
        {
            name = LoulanDNSConstants.CONST_DNS_SERVICE_ENDPOINT_TYPE_NAME_DOT;
        }
        else if ( code == LoulanDNSConstants.CONST_DNS_SERVICE_ENDPOINT_TYPE_CODE_JSON_RFC8427  )
        {
            name = LoulanDNSConstants.CONST_DNS_SERVICE_ENDPOINT_TYPE_NAME_JSON_RFC8427;
        }
        else
        {
            name = null;
        }


        return name;
    }


    public Long endpointTypeNameToEndpointTypeCode(String name)
    {
        Long code = null;

        if ( name.equals(LoulanDNSConstants.CONST_DNS_SERVICE_ENDPOINT_TYPE_NAME_UDP) )
        {
            code = (long)LoulanDNSConstants.CONST_DNS_SERVICE_ENDPOINT_TYPE_CODE_UDP;
        }
        else if ( name.equals(LoulanDNSConstants.CONST_DNS_SERVICE_ENDPOINT_TYPE_NAME_TCP) )
        {
            code = (long)LoulanDNSConstants.CONST_DNS_SERVICE_ENDPOINT_TYPE_CODE_TCP;
        }
        else if ( name.equals(LoulanDNSConstants.CONST_DNS_SERVICE_ENDPOINT_TYPE_NAME_DOH) )
        {
            code = (long)LoulanDNSConstants.CONST_DNS_RESOLVER_TYPE_OUTBOUND_DOH;
        }
        else if ( name.equals( LoulanDNSConstants.CONST_DNS_SERVICE_ENDPOINT_TYPE_NAME_DOT)  )
        {
            code = (long)LoulanDNSConstants.CONST_DNS_RESOLVER_TYPE_OUTBOUND_DOT;
        }
        else if ( name.equals( LoulanDNSConstants.CONST_DNS_SERVICE_ENDPOINT_TYPE_NAME_JSON_RFC8427)  )
        {
            code = (long)LoulanDNSConstants.CONST_DNS_SERVICE_ENDPOINT_TYPE_CODE_JSON_RFC8427;
        }
        else
        {
            code = null;
        }

        return code;
    }


    // DNS統計情報ログサービスのインスタンスの名前を生成する.
    public static String getDNSStatLogServiceName(String userName, String serviceInstanceName) throws DNSServiceCommonException
    {
        String dnsStatLogServiceName;

        // DNS統計情報ログサービスのインスタンス名は、"<ユーザー名>/<サービスインスタンス名>"とする.
        dnsStatLogServiceName = String.format("%s/%s",  userName, serviceInstanceName );
        return dnsStatLogServiceName;
    }



    /**
     * 指定したbyte配列をgzip圧縮して返す.
     * 
     */
    public byte[] encodeGZIP(byte[] rawBytes) throws DNSServiceCommonException
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try
        {
            GZIPOutputStream gzipStream = new GZIPOutputStream(outputStream);

            // データを書き込んでフラッシュ（圧縮実行）
            gzipStream.write(rawBytes);
            gzipStream.finish();
        }
        catch(IOException cause)
        {
            String msg = String.format("Failed to encode to GZIP bytes. rawBytes.length=%d", rawBytes.length );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg, cause);
            throw exception;
        }
        
        byte[] gzipBytes = outputStream.toByteArray();
        return gzipBytes;
    }


    /**
     * 指定したgzip圧縮済みbyte配列を平文のbyte配列に解凍して返す.
     * 
     */
    public byte[] decodeGZIP(byte[] gzipBytes)  throws DNSServiceCommonException
    {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try
        {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(gzipBytes);
            GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);

            byte[] buffer = new byte[8192];
            int len;
            while ((len = gzipInputStream.read(buffer)) > 0)
            {
                outputStream.write(buffer, 0, len);
            }
        }
        catch(IOException cause)
        {
            String msg = String.format("Failed to decode from GZIP bytes. gzipBytes.length=%d", gzipBytes.length );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg, cause);
            throw exception;
        }
            
        byte[] rawBytes = outputStream.toByteArray();
        return rawBytes;
    }



}