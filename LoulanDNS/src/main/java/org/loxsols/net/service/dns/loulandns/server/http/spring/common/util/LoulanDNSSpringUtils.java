package org.loxsols.net.service.dns.loulandns.server.http.spring.common.util;

import java.util.TimeZone;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.regex.Matcher;
import java.time.format.DateTimeFormatter;

import org.loxsols.net.service.dns.loulandns.server.common.constants.LoulanDNSConstants;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;

import org.loxsols.net.service.dns.loulandns.server.common.LoulanDNSSystemServiceException;

import org.loxsols.net.service.dns.loulandns.server.http.spring.model.User;
import org.loxsols.net.service.dns.loulandns.server.http.spring.model.UserGroup;

public class LoulanDNSSpringUtils
{


    // ユーザークラスのオブジェクトをメモリ上で作成する. (DB上のレコードとは無関係にシステム上で作成するだけ.)
    // ユーザーIDは特に設定しない.
    // レコード作成時刻、レコード更新時効の値は現在のシステム上の時刻値を入力する. (使用時はDBのレコードをそのまま上書きしないように注意.)
    public static User createUserObject(String userName, String userPassword, int recordStatus, String memo) throws LoulanDNSSystemServiceException
    {
        // レコード作成時刻と更新時刻のインスタンスを現在時刻/本システム所定タイムゾーンで作成.
        String createDate = LoulanDNSUtils.getCurrentDateTimeString();
        String updateDate = createDate;

        // ユーザー情報を新規作成する.
        User user = createUserObject(null, userName, userPassword, recordStatus, memo, createDate, updateDate);

        return user;
    }


    // ユーザークラスのオブジェクトをメモリ上で作成する. (DB上のレコードとは無関係にシステム上で作成するだけ.)
    // オブジェクト作成時に入力値チェックも併せて行う.
    public static User createUserObject(Long userID, String userName, String userPassword, int recordStatus, String memo, String createDate, String updateDate) throws LoulanDNSSystemServiceException
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

        ZonedDateTime createDateObject = LoulanDNSUtils.toDateTimeObject(createDate);
        ZonedDateTime updateDateObject = LoulanDNSUtils.toDateTimeObject(updateDate);
        if ( createDateObject.isAfter(updateDateObject) == true )
        {
            // レコード作成時刻がレコード更新時刻よりも後の時刻で設定されている.
            String msg = String.format("Invalid DateTime value. CreateDate is after time of UpdateDate. createDate=%s, updateDate=%s", createDate, updateDate );
            LoulanDNSSystemServiceException execption = new LoulanDNSSystemServiceException( msg );
            throw execption;           
        }


        // ユーザー情報を新規作成する.
        User user = new User();
        user.setUserID(userID);
        user.setUserName(userName);
        user.setUserPassword(userPassword);
        user.setRecordStatus(recordStatus);
        user.setMemo(memo);
        user.setCreateDate(createDate);
        user.setUpdateDate(updateDate);

        return user;
    }




    // ユーザーグループクラスのオブジェクトをメモリ上で作成する. (DB上のレコードとは無関係にシステム上で作成するだけ.)
    // ユーザーグループIDは特に設定しない.
    // レコード作成時刻、レコード更新時効の値は現在のシステム上の時刻値を入力する. (使用時はDBのレコードをそのまま上書きしないように注意.)
    public static UserGroup createUserGroupObject(String userGroupName, int recordStatus, String memo) throws LoulanDNSSystemServiceException
    {

        // レコード作成時刻と更新時刻のインスタンスを現在時刻/本システム所定タイムゾーンで作成.
        String createDate = LoulanDNSUtils.getCurrentDateTimeString();
        String updateDate = createDate;

        UserGroup userGroup = createUserGroupObject(null, userGroupName, recordStatus, memo, createDate, updateDate);
        return userGroup;

    }

       // ユーザーグループクラスのオブジェクトをメモリ上で作成する. (DB上のレコードとは無関係にシステム上で作成するだけ.)
    // ユーザーグループIDは特に設定しない.
    // レコード作成時刻、レコード更新時効の値は現在のシステム上の時刻値を入力する. (使用時はDBのレコードをそのまま上書きしないように注意.)
    public static UserGroup createUserGroupObject(Long userGroupID, String userGroupName, int recordStatus, String memo, String createDate, String updateDate) throws LoulanDNSSystemServiceException
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

        ZonedDateTime createDateObject = LoulanDNSUtils.toDateTimeObject(createDate);
        ZonedDateTime updateDateObject = LoulanDNSUtils.toDateTimeObject(updateDate);
        if ( createDateObject.isAfter(updateDateObject) == true )
        {
            // レコード作成時刻がレコード更新時刻よりも後の時刻で設定されている.
            String msg = String.format("Invalid DateTime value. CreateDate is after time of UpdateDate. createDate=%s, updateDate=%s", createDate, updateDate );
            LoulanDNSSystemServiceException execption = new LoulanDNSSystemServiceException( msg );
            throw execption;           
        }


        // ユーザーグループ情報を新規作成する.
        UserGroup userGroup = new UserGroup();
        userGroup.setUserGroupID(userGroupID);
        userGroup.setUserGroupName(userGroupName);
        userGroup.setRecordStatus(recordStatus);
        userGroup.setMemo(memo);
        userGroup.setCreateDate(createDate);
        userGroup.setUpdateDate(updateDate);

        return userGroup;
    }


    

}