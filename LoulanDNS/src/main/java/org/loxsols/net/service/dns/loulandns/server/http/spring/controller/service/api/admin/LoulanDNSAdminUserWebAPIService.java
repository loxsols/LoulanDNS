package org.loxsols.net.service.dns.loulandns.server.http.spring.controller.service.api.admin;

import java.util.Base64;
import java.util.List;

import org.springframework.stereotype.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;


import org.loxsols.net.service.dns.loulandns.server.common.constants.LoulanDNSConstants;
import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;

import org.loxsols.net.service.dns.loulandns.server.http.spring.model.*;
import org.loxsols.net.service.dns.loulandns.server.http.spring.service.LoulanDNSDBService;
import org.loxsols.net.service.dns.loulandns.server.http.spring.service.LoulanDNSLogicalDBService;

import org.loxsols.net.service.dns.loulandns.server.logical.model.UserInfo;
import org.loxsols.net.service.dns.loulandns.server.logical.model.factory.UserInfoFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.service.LoulanDNSLogicalModelService;

import org.loxsols.net.service.dns.loulandns.server.http.spring.common.exception.HttpStatus404Exception;

// LoulanDNSのユーザー管理用WebAPIサービスクラス
@RestController
@RequestMapping("/admin/api/user")
public class LoulanDNSAdminUserWebAPIService {

    // 次の値と同等の文字列を設定する. ※ 固定値でないとコンパイルが通らないため.
    // "LoulanDNSConstatns.DB_CONST_VALUE_RECORD_STATUS_ACTIVE"
    final static String PRM_STR_DB_CONST_VALUE_RECORD_STATUS_ACTIVE = "101";
    // "LoulanDNSConstatns.DB_CONST_VALUE_RECORD_STATUS_INACTIVE"
    final static String PRM_STR_DB_CONST_VALUE_RECORD_STATUS_INACTIVE = "401";


    @Autowired
    @Qualifier("loulanDNSDBServiceImpl")
    LoulanDNSDBService loulanDNSDBService;

    @Autowired
    @Qualifier("loulanDNSLogicalDBServiceImpl")
    LoulanDNSLogicalDBService loulanDNSLogicalDBService;

    @Autowired
    @Qualifier("loulanDNSLogicalModelServiceImpl")
    LoulanDNSLogicalModelService loulanDNSLogicalModelService;

    UserInfoFactory userInfoFactory = new UserInfoFactory();

    public LoulanDNSAdminUserWebAPIService() {

    }

    // ユーザー情報一覧取得
    @GetMapping("/list/user")
    public UserInfo[] listUser() throws DNSServiceCommonException {
        List<UserInfo> userInfoList = loulanDNSLogicalModelService.getUserInfoList();
        UserInfo[] userInfoArray = userInfoList.toArray(new UserInfo[userInfoList.size()]);
        return userInfoArray;
    }

    // ユーザー情報取得
    @GetMapping("/get/user")
    public UserInfo getUser(@RequestParam(name = "UserName", required = true) String userName)
            throws DNSServiceCommonException {
        UserInfo userInfo = loulanDNSLogicalModelService.getUserInfo(userName);
        return userInfo;
    }

    // ユーザー情報作成
    @PutMapping("/create/user")
    public UserInfo createUser(@RequestParam(name = "UserName", required = true) String userName,
            @RequestParam(name = "UserPassword", required = true) String userPassword,
            @RequestParam(name = "RecordStatus", required = false, defaultValue = PRM_STR_DB_CONST_VALUE_RECORD_STATUS_ACTIVE) String recordStatus,
            @RequestParam(name = "Memo", required = false, defaultValue = "") String memo)
            throws DNSServiceCommonException {
        int recordStatusValue = Integer.parseInt(recordStatus);

        UserInfo userInfo = userInfoFactory.createUserInfoObject(null, userName, userPassword, recordStatusValue, memo);
        UserInfo savedUserInfo = loulanDNSLogicalModelService.saveUserInfo(userInfo);

        return savedUserInfo;
    }

    // ユーザー情報更新
    @PutMapping("/update/user")
    public UserInfo updateUser(@RequestParam(name = "UserName", required = true) String userName,
            @RequestParam(name = "UserPassword", required = false) String userPassword,
            @RequestParam(name = "RecordStatus", required = false) String recordStatus,
            @RequestParam(name = "Memo", required = false) String memo) throws DNSServiceCommonException {

        // 指定されたユーザーが存在しない場合は例外をスロー.
        if (loulanDNSLogicalModelService.isExistingUser(userName) == false) {
            String msg = String.format("Specifed user is not found. UserName={0}", userName);
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        // 一旦、DBにアクセスして既存のユーザー情報を取得する.
        UserInfo existUserInfo = getUser(userName);
        Long userID = existUserInfo.getUserID();

        if (userPassword == null) {
            // 本メソッドの引数がnullの場合は既存レコードの情報を利用する.
            userPassword = existUserInfo.getUserPassword();
        }

        int recordStatusValue;
        if (recordStatus == null) {
            // 本メソッドの引数がnullの場合は既存レコードの情報を利用する.
            recordStatusValue = (int) existUserInfo.getRecordStatus();
        } else {
            recordStatusValue = Integer.parseInt(recordStatus);
        }

        if (memo == null) {
            // 本メソッドの引数がnullの場合は既存レコードの情報を利用する.
            memo = existUserInfo.getMemo();
        }

        UserInfo tmpUserInfo = userInfoFactory.createUserInfoObject(userID, userName, userPassword, recordStatusValue,
                memo, existUserInfo.getCreateDate(), existUserInfo.getUpdateDate());
        UserInfo savedUserInfo = loulanDNSLogicalModelService.saveUserInfo(tmpUserInfo);

        return savedUserInfo;
    }

    // ユーザー情報削除
    @DeleteMapping("/delete/user")
    public void deleteUser(@RequestParam(name = "UserName", required = true) String userName)
            throws DNSServiceCommonException {

        // 指定されたユーザーが存在しない場合は例外をスロー.
        if (loulanDNSLogicalModelService.isExistingUser(userName) == false) {
            String msg = String.format("Specifed user is not found. UserName={0}", userName);
            HttpStatus404Exception exception = new HttpStatus404Exception(msg);
            throw exception;
        }

        // 一旦、DBにアクセスして既存のユーザー情報を取得する.
        UserInfo existUserInfo = loulanDNSLogicalModelService.getUserInfo(userName);
        Long userID = existUserInfo.getUserID();

        loulanDNSLogicalModelService.deleteUserInfo(userID);
    }

}