package org.loxsols.net.service.dns.loulandns.server.common.util;

import java.util.TimeZone;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.regex.Matcher;
import java.time.format.DateTimeFormatter;

import org.loxsols.net.service.dns.loulandns.client.common.DNSClientCommonException;
import org.loxsols.net.service.dns.loulandns.server.common.*;
import org.loxsols.net.service.dns.loulandns.server.common.constants.LoulanDNSConstants;
import org.loxsols.net.service.dns.loulandns.server.common.LoulanDNSSystemServiceException;

import org.loxsols.net.service.dns.loulandns.server.logical.model.UserInfo;
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
    public static ZonedDateTime toDateTimeObject(String dateTimeString)
    {
        // タイムゾーンを表すコードが文字列末尾に存在するはずなので取得する.
        // 例) "2024/06/11 12:34:56.123 JST"  -> "JST"
        String timeZoneCode = dateTimeString.substring( dateTimeString.length() - 3 );

        // "JST"のようなタイムゾーンの名称からTimeZoneクラスを生成する.
        TimeZone timeZone = TimeZone.getTimeZone(timeZoneCode);
        // TimeZone.toZoneId().getId()を実行すると"Asia/Tokyo"のようなコードが得られる.
        ZoneId zoneID = ZoneId.of( timeZone.toZoneId().getId() );

        // 指定された時刻文字列からタイムゾーンコードを取り除く.
        // 例) "2024/06/11 12:34:56.123 JST"  -> "2024/06/11 12:34:56.123" 
        String dateTimeStr2 = dateTimeString.substring(0, dateTimeString.length() - 3 );
        dateTimeStr2 = dateTimeStr2.trim();

        // 変換する
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern( LoulanDNSConstants.LOCAL_DATE_TIME_FORMAT_PATTERN );

        LocalDateTime localDateTime = LocalDateTime.parse(dateTimeStr2, formatter );
        ZonedDateTime dateTime = ZonedDateTime.of( localDateTime, zoneID );
        return dateTime;
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

        ZonedDateTime createDateObject = toDateTimeObject(createDate);
        ZonedDateTime updateDateObject = toDateTimeObject(updateDate);
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

        ZonedDateTime createDateObject = toDateTimeObject(createDate);
        ZonedDateTime updateDateObject = toDateTimeObject(updateDate);
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
        else if ( code == LoulanDNSConstants.CONST_DNS_RESOLVER_TYPE_OUTBOUND_DOH )
        {
            name = LoulanDNSConstants.CONST_DNS_SERVICE_ENDPOINT_TYPE_NAME_DOH;
        }
        else if ( code == LoulanDNSConstants.CONST_DNS_RESOLVER_TYPE_OUTBOUND_DOT  )
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



}