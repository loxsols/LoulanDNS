package org.loxsols.net.service.dns.loulandns.server.common.constants;

import java.time.format.DateTimeFormatter;
import java.nio.charset.Charset;
import java.text.Format;
import java.util.regex.Pattern;

public class LoulanDNSConstants
{

    // ******************************************
    // 以下、プロパティのキー定義
    // ******************************************

    // UDPサービスのbindアドレス
    public static String PROP_KEY_UDP_SERVICE_BIND_ADDRESS = "loulan.dns.service.udp.address";

    // UDPサービスのbindポート番号
    public static String PROP_KEY_UDP_SERVICE_BIND_PORT = "loulan.dns.service.udp.port";


    // リカーシブDNSサーバー(プライマリー)のホスト名
    public static String PROP_KEY_PRIMARY_RECURSIVE_DNS_SERVER_HOST = "loulan.dns.recursive.server.primary.host";


    // リカーシブDNSサーバー(プライマリー)のポート番号
    public static String PROP_KEY_PRIMARY_RECURSIVE_DNS_SERVER_PORT = "loulan.dns.recursive.server.primary.port";





    // ******************************************
    // 以下、プロパティのデフォルト値定義
    // ******************************************


    // UDPサービスのbindアドレスのデフォルト値
    public static String DEFAULT_VALUE_OF_UDP_SERVICE_BIND_ADDRESS ="0.0.0.0";

    // UDPサービスのbindポート番号のデフォルト値
    public static String DEFAULT_VALUE_OF_UDP_SERVICE_BIND_PORT ="53";



    // リカーシブDNSサーバー(プライマリー)のホスト名のデフォルト値
    public static String DEFAULT_VALUE_OF_PRIMARY_RECURSIVE_DNS_SERVER_HOST = "1.1.1.1";

    // リカーシブDNSサーバー(プライマリー)のポート番号のデフォルト値
    public static String DEFAULT_VALUE_OF_PRIMARY_RECURSIVE_DNS_SERVER_PORT = "53";




    // ******************************************
    // 以下、DBレコードで使用する固定値
    // ******************************************

    public static final int DB_CONST_VALUE_RECORD_STATUS_ACTIVE = 101;
    public static final int DB_CONST_VALUE_RECORD_STATUS_INACTIVE = 401;

    // LoulanDNSのDBで使用する時刻表記の書式 : 例)"2024/06/11 12:34:56.123 JST"
    public static String DB_CONST_VALUE_DATE_TIME_FORMAT = "uuuu/MM/dd hh:mm:ss.SSS zzz";

    // 上記時刻表記書式のフォーマットクラス
    public static DateTimeFormatter DB_CONST_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern( DB_CONST_VALUE_DATE_TIME_FORMAT );
    public static Format DB_CONST_DATE_TIME_FORMAT = DB_CONST_DATE_TIME_FORMATTER.toFormat();

    
    // ******************************************
    // 以下、文字列チェック用の正規表現
    // ******************************************

    // ユーザー名文字列の定義
    // 半角英数字1文字 + 半角英数字記号(-_.@$のみ)任意文字数
    public static final String REGULAR_EXPRESSION_OF_USER_NAME = "^[a-zA-Z0-9].[a-zA-Z0-9-_.@$]*";
    public static final Pattern REGULAR_EXPRESSION_PATTERN_OF_USER_NAME = Pattern.compile( REGULAR_EXPRESSION_OF_USER_NAME );


    // ユーザーパスワード文字列の定義
    // 半角英数記号のみ (空文字OK)
    public static final String REGULAR_EXPRESSION_OF_USER_PASSWORD = "^[a-zA-Z0-9!-/:-@[-`{-~]*$";
    public static final Pattern REGULAR_EXPRESSION_PATTERN_OF_USER_PASSWORD = Pattern.compile( REGULAR_EXPRESSION_OF_USER_NAME );


    // ユーザーグループ名文字列の定義
    // 半角英数字1文字 + 半角英数字記号(-_.@$のみ)任意文字数
    public static final String REGULAR_EXPRESSION_OF_USER_GROUP_NAME = "^[a-zA-Z0-9].[a-zA-Z0-9-_.@$]*";
    public static final Pattern REGULAR_EXPRESSION_PATTERN_OF_USER_GROUP_NAME = Pattern.compile( REGULAR_EXPRESSION_OF_USER_NAME );



    // 日付文字列の定義
    // 例) "2024/06/11 12:34:56.789 JST"
    public static final String REGULAR_EXPRESSION_OF_DATE_TIME_STRING = "^[0-9]{4}\\/[0-9]{2}\\/[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]{3} [A-Z]{3}$";
    public static final Pattern REGULAR_EXPRESSION_PATTERN_OF_DATE_TIME_STRING = Pattern.compile( REGULAR_EXPRESSION_OF_DATE_TIME_STRING );





    // ******************************************
    // 以下、日付・時刻変換用の文字列
    // ******************************************

    // LocalDateTimeクラスで使用する日付時刻文字列の解析パターン
    public static final String LOCAL_DATE_TIME_FORMAT_PATTERN = "yyyy/MM/dd HH:mm:ss.SSS";


    // ******************************************
    // 以下、雑多な定数値
    // ******************************************

    // パスワードをマスクする文字列
    public static final String CONST_PASSWORD_MASK_STRING = "********";

    // システム管理者のユーザー名
    public static final String CONST_ADMIN_USER_NAME = "admin";



    // ******************************************
    // 以下、ユーザーグループ権限テーブル(USER_GROUP_PRIVILEGEテーブル)の定数値
    // ******************************************
    // -- 		DNS_INFO_MASK_VALUE 列
    
    // -- 			000	: DNS情報の参照権限なし
    public static final int CONST_PRIVILEGE_DNS_INFO_MASK_VALUE_NOT_PERMITTED = 000;

    // --			001 : DNS情報(自身)の読み取り可能
    public static final int CONST_PRIVILEGE_DNS_INFO_MASK_VALUE_READ_OWN_INFO = 001;

    // -- 			002 : DNS情報(グループ内)の読み取り可能
    public static final int CONST_PRIVILEGE_DNS_INFO_MASK_VALUE_READ_GROUP_INFO = 020;

    // -- 			003 : DNS情報(システム全体)の読み取り可能
    public static final int CONST_PRIVILEGE_DNS_INFO_MASK_VALUE_READ_SYSTEM_INFO = 030;


    // --			010 : DNS情報(自身)の更新可能
    public static final int CONST_PRIVILEGE_DNS_INFO_MASK_VALUE_WRITE_OWN_INFO = 010;

    // -- 			020 : DNS情報(グループ内)の更新可能
    public static final int CONST_PRIVILEGE_DNS_INFO_MASK_VALUE_WRITE_GROUP_INFO = 020;

    // -- 			030 : DNS情報(システム全体)の更新可能
    public static final int CONST_PRIVILEGE_DNS_INFO_MASK_VALUE_WRITE_SYSTEM_INFO = 030;


    // -- 			100 : DNS情報(自身)の作成/削除可能
    public static final int CONST_PRIVILEGE_DNS_INFO_MASK_VALUE_CREATE_OWN_INFO = 100;

    // -- 			200 : DNS情報(グループ内)の作成/削除可能
    public static final int CONST_PRIVILEGE_DNS_INFO_MASK_VALUE_CREATE_GROUP_INFO = 200;

    // -- 			300 : DNS情報(システム全体)の作成/削除可能
    public static final int CONST_PRIVILEGE_DNS_INFO_MASK_VALUE_CREATE_SYSTEM_INFO = 300;

    
    // -- 		USER_INFO_MASK_VALUE 列
    // --  		    000	: ユーザー情報の参照権限なし
    public static final int CONST_PRIVILEGE_USER_INFO_MASK_VALUE_NOT_PERMITTED = 000;

    
    // -- 			001 : ユーザー情報(自身)の読み取り可能
    public static final int CONST_PRIVILEGE_USER_INFO_MASK_VALUE_READ_OWN_INFO = 001;

    // -- 			002 : ユーザー情報(グループ内)の読み取り可能
    public static final int CONST_PRIVILEGE_USER_INFO_MASK_VALUE_READ_GROUP_INFO = 002;

    // -- 			003 : ユーザー情報(システム全体)の読み取り可能
    public static final int CONST_PRIVILEGE_USER_INFO_MASK_VALUE_READ_SYSTEM_INFO = 003;


    // -- 			010 : ユーザー情報(自身)の更新可能
    public static final int CONST_PRIVILEGE_USER_INFO_MASK_VALUE_WRITE_OWN_INFO = 010;

    // -- 			020 : ユーザー情報(グループ内)の更新可能
    public static final int CONST_PRIVILEGE_USER_INFO_MASK_VALUE_WRITE_GROUP_INFO = 020;

    // -- 			030 : ユーザー情報(システム全体)の更新可能
    public static final int CONST_PRIVILEGE_USER_INFO_MASK_VALUE_WRITE_SYSTEM_INFO = 030;


    // -- 			100 : ユーザー情報(自身)の作成/削除可能
    public static final int CONST_PRIVILEGE_USER_INFO_MASK_VALUE_CREATE_OWN_INFO = 100;

    // -- 			200 : ユーザー情報(グループ内)の作成/削除可能
    public static final int CONST_PRIVILEGE_USER_INFO_MASK_VALUE_CREATE_GROUP_INFO = 200;

    // -- 			300 : ユーザー情報(システム全体)の作成/削除可能
    public static final int CONST_PRIVILEGE_USER_INFO_MASK_VALUE_CREATE_SYSTEM_INFO = 300;

    
    // -- 		SYSTEM_INFO_MASK_VALUE 列
    // --  		    000	: システム情報の参照権限なし
    public static final int CONST_PRIVILEGE_SYSTEM_INFO_MASK_VALUE_NOT_PERMITTED = 000;


    // -- 			001 : システム情報(自身)の読み取り可能
    public static final int CONST_PRIVILEGE_SYSTEM_INFO_MASK_VALUE_READ_OWN_INFO = 001;

    // -- 			002 : システム情報(グループ内)の読み取り可能
    public static final int CONST_PRIVILEGE_SYSTEM_INFO_MASK_VALUE_READ_GROUP_INFO = 002;

    // -- 			003 : システム情報(システム全体)の読み取り可能
    public static final int CONST_PRIVILEGE_SYSTEM_INFO_MASK_VALUE_READ_SYSTEM_INFO = 003;


    // -- 			010 : システム情報(自身)の更新可能
    public static final int CONST_PRIVILEGE_SYSTEM_INFO_MASK_VALUE_WRITE_OWN_INFO = 010;

    // -- 			020 : システム情報(グループ内)の更新可能
    public static final int CONST_PRIVILEGE_SYSTEM_INFO_MASK_VALUE_WRITE_GROUP_INFO = 020;

    // -- 			030 : システム情報(システム全体)の更新可能
    public static final int CONST_PRIVILEGE_SYSTEM_INFO_MASK_VALUE_WRITE_SYSTEM_INFO = 030;


    // -- 			100 : システム情報(自身)の作成/削除可能
    public static final int CONST_PRIVILEGE_SYSTEM_INFO_MASK_VALUE_CREATE_OWN_INFO = 100;

    // -- 			200 : システム情報(グループ内)の作成/削除可能
    public static final int CONST_PRIVILEGE_SYSTEM_INFO_MASK_VALUE_CREATE_GROUP_INFO = 200;

    // -- 			300 : システム情報(システム全体)の作成/削除可能
    public static final int CONST_PRIVILEGE_SYSTEM_INFO_MASK_VALUE_CREATE_SYSTEM_INFO = 300;



    // ******************************************
    // 以下、DNSリゾルバタイプのコード定数値
    // ******************************************

    // --			外部問い合わせリゾルバ
    // -- 				201 : UDP(DNS)問い合わせ
    public static final int CONST_DNS_RESOLVER_TYPE_OUTBOUND_UDP = 201;

    // -- 				202 : TCP(DNS)問い合わせ
    public static final int CONST_DNS_RESOLVER_TYPE_OUTBOUND_TCP = 202;

    // --	 			203 : DNSSec問い合わせ
    public static final int CONST_DNS_RESOLVER_TYPE_OUTBOUND_DNSSEC = 203;

    // -- 				204 : DoT(DNS over TLS)問い合わせ
    public static final int CONST_DNS_RESOLVER_TYPE_OUTBOUND_DOT = 204;

    // --				205	: DoH(DNS over HTTPS)問い合わせ
    public static final int CONST_DNS_RESOLVER_TYPE_OUTBOUND_DOH = 205;



    // ******************************************
    // 以下、DNSプロトコルに関連する定数値
    // ******************************************
    public static final int CONST_MAX_DNS_MESSAGE_SIZE = 8192;

    public static final Charset CONST_DOMAIN_NAME_CHARSET = Charset.forName("UTF-8");


    // ******************************************
    // 以下、DNSリゾルバインスタンスのパラメータ設定用キー
    // ******************************************
    
    // 外部DNSサーバー参照用リゾルバの参照先外部ホスト(プライマリ)
    // "loulan.dns.resolver.outbound.server.host.primary"
    public static final String PROP_KEY_RESOLVER_OUTBOUND_SERVER_HOST_PRIMARY = "loulan.dns.resolver.outbound.server.host.primary";

    // 外部DNSサーバー参照用リゾルバの参照先外部ポート番号(プライマリ)
    // "loulan.dns.resolver.outbound.server.port.primary"
    public static final String PROP_KEY_RESOLVER_OUTBOUND_SERVER_PORT_PRIMARY = "loulan.dns.resolver.outbound.server.port.primary";

    // 外部DoHサーバー参照用リゾルバの参照先URI(プライマリ)
    // "loulan.dns.resolver.outbound.server.doh.uri.primary"
    public static final String PROP_KEY_RESOLVER_OUTBOUND_DOH_SERVER_URI_PRIMARY = "loulan.dns.resolver.outbound.server.doh.uri.primary";

    // 外部DoHサーバー参照用リゾルバの参照用HTTPメソッドタイプ(プライマリ)
    // "loulan.dns.resolver.outbound.server.doh.http.methodtype.primary"
    public static final String PROP_KEY_RESOLVER_OUTBOUND_DOH_HTTP_METHOD_TYPE_PRIMARY = "loulan.dns.resolver.outbound.server.doh.http.methodtype.primary";



    // ******************************************
    // 以下、DNSサービスインスタンスのパラメータ設定用キー
    // ******************************************

    // DNSサービスインスタンスが所属するユーザーの名前
    // "loulan.dns.user.name"
    public static String PROP_KEY_USER_NAME = "loulan.dns.user.name";

    // DNSサービスインスタンスの名前.
    // "loulan.dns.service.instance.name"
    public static String PROP_KEY_SERVICE_INSTANCE_NAME = "loulan.dns.service.instance.name";


    // デフォルトDNSサービスインスタンスのインスタンス名.
    public static String PROP_KEY_DEFAULT_SERVICE_INSTANCE_NAME = "loulan.dns.service.instance.default.instance.name";
    
    // デフォルトDNSサービスインスタンスが所属するユーザーの名前
    public static String PROP_KEY_DEFAULT_SERVICE_INSTANCE_USER_NAME = "loulan.dns.service.instance.default.instance.user.name";

    // **************************************************
    // 以下、DNSサービスインスタンスのタイプコード
    // **************************************************
    // 最小構成のDNSサービスインスタンス
    public static int CONST_DNS_SERVICE_INSTANCE_TYPE_CODE_MINIMUM = 0;


    // **************************************************
    // 以下、DNSサービスインスタンスのタイプ名
    // **************************************************
    // 最小構成のDNSサービスインスタンス
    public static String CONST_DNS_SERVICE_INSTANCE_TYPE_NAME_MINIMUM = "MINIMUM";



    // **************************************************
    // 以下、DNSサービスエンドポイントのタイプコード
    // **************************************************

    // UDPサービスエンドポイント
    public static int CONST_DNS_SERVICE_ENDPOINT_TYPE_CODE_UDP = 10101;

    // TCPサービスエンドポイント
    public static int CONST_DNS_SERVICE_ENDPOINT_TYPE_CODE_TCP = 10201;

    // DoHサービスエンドポイント
    public static int CONST_DNS_SERVICE_ENDPOINT_TYPE_CODE_DOH = 81101;

    // JSON(RFC8427)サービスエンドポイント
    public static int CONST_DNS_SERVICE_ENDPOINT_TYPE_CODE_JSON_RFC8427 = 82101;

    // DoTサービスエンドポイント
    public static int CONST_DNS_SERVICE_ENDPOINT_TYPE_CODE_DOT = 82101;


    // **************************************************
    // 以下、DNSサービスエンドポイントのタイプ名
    // **************************************************

    // UDPサービスエンドポイント
    public static String CONST_DNS_SERVICE_ENDPOINT_TYPE_NAME_UDP = "UDP";

    // TCPサービスエンドポイント
    public static String CONST_DNS_SERVICE_ENDPOINT_TYPE_NAME_TCP = "TCP";

    // DOHサービスエンドポイント
    public static String CONST_DNS_SERVICE_ENDPOINT_TYPE_NAME_DOH = "DOH";

    // JSON(RFC8427)サービスエンドポイント
    public static String CONST_DNS_SERVICE_ENDPOINT_TYPE_NAME_JSON_RFC8427 = "JSON/RFC8427";

    // DoTサービスエンドポイント
    public static String CONST_DNS_SERVICE_ENDPOINT_TYPE_NAME_DOT = "DOT";








    // ******************************************
    // 以下、DNSサービスエンドポイントのパラメータ設定用キー
    // ******************************************


    // UDPサービスエンドポイントのbindアドレス
    // "loulan.dns.service.endpoint.udp.address"
    public static String PROP_KEY_SERVICE_ENDPOINT_UDP_BIND_ADDRESS = "loulan.dns.service.endpoint.udp.address";

    // UDPサービスエンドポイントのbindポート番号
    // "loulan.dns.service.endpoint.udp.port";
    public static String PROP_KEY_SERVICE_ENDPOINT_UDP_BIND_PORT = "loulan.dns.service.endpoint.udp.port";


    // ******************************************
    // 以下、DNSサービスインスタンスのタスクの状態値
    // ******************************************
    // DNSサービスインスタンスのタスクが停止状態
    public static int CONST_TASK_STATUS_INACTIVE_DNS_SERVICE_INSTANCE = 0;

    // DNSサービスインスタンスのタスクが実行状態
    public static int CONST_TASK_STATUS_ACTIVE_DNS_SERVICE_INSTANCE = 101;



    // ******************************************
    // 以下、DNSサービスエンドポイントタスクの状態値
    // ******************************************

    // DNSサービスエンドポイントのタスクが停止状態
    public static int CONST_TASK_STATUS_INVACTIVE_DNS_SERVICE_ENDPOINT = 0;

    // DNSサービスエンドポイントのタスクが実行状態
    public static int CONST_TASK_STATUS_ACTIVE_DNS_SERVICE_ENDPOINT = 101;

    // DNSサービスエンドポイントのタスクの終了待ち状態
    public static int CONST_TASK_STATUS_WAITING_FOR_SUSPEND_DNS_SERVICE_ENDPOINT = 401;




    // ******************************************
    // 以下、ログレベルの定義
    // ******************************************
    public static int CONST_LOG_LEVEL_DEBUG         = 10;
    public static int CONST_LOG_LEVEL_INFO          = 20;
    public static int CONST_LOG_LEVEL_NOTICE        = 30;
    public static int CONST_LOG_LEVEL_WARN          = 150;
    public static int CONST_LOG_LEVEL_ERROR         = 160;
    public static int CONST_LOG_LEVEL_ALERT         = 170;



    // ******************************************
    // 以下、ロガー(ILoulanDNSLogger)設定用のプロパティキー
    // ******************************************
    
    // ロガータイプキー
    // "loulan.dns.logger.type"
    public static final String PROP_KEY_LOGGER_TYPE = "loulan.dns.logger.type";

    // ロガーのプリント出力キー
    // "loulan.dns.logger.type.print.out.type"
    public static final String PROP_KEY_LOGGER_PRINT_OUT_TYPE = "loulan.dns.logger.type.print.out.type";


    // ロガータイプの値 : シンプルなロガー
    public static final String CONST_LOG_TYPE_SIMPLE_LOGGER = "SimpleLogger";

    // ロガータイプの値 : DB出力するロガー
    public static final String CONST_LOG_TYPE_DB_LOGGER = "DBLogger";







}