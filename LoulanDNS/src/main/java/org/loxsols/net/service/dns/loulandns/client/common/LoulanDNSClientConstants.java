package org.loxsols.net.service.dns.loulandns.client.common;


public class LoulanDNSClientConstants
{

    // ******************************************
    // 以下、プロパティのキー定義
    // ******************************************

    // 問い合わせ先のDNSサーバーのアドレス
    public static final String PROP_KEY_DNS_SERVER_ADDRESS = "loulan.dns.server.address";

    // 問い合わせ先のDNSサーバーのアドレス
    public static final String PROP_KEY_DNS_SERVER_PORT = "loulan.dns.server.port";

    // DNSプロトコルタイプ (UDP / TCP / DOH / DOT / ...etc )
    public static final String PROP_KEY_DNS_PROTOCOL_TYPE = "loulan.dns.protocol.type";

    // DNSのドメイン名
    public static final String PROP_KEY_DNS_DOMAIN_NAME = "loulan.dns.protocol.domain.name";

    // DNSのドメインが所属するクラス
    public static final String PROP_KEY_DNS_DOMAIN_CLASS = "loulan.dns.protocol.domain.class";

    // DNSリソースレコードタイプ
    public static final String PROP_KEY_DNS_DOMAIN_RR_TYPE = "loulan.dns.protocol.domain.rr.type";





    // ******************************************
    // 以下はEDNSオプションのプロパティキー
    // ******************************************
    // EDNSオプション   :   EDNS(0)
    public static final String PROP_KEY_DNS_EDNS_ENABLE = "loulan.dns.protocol.dns.edns.enable";

    // EDNSオプション   :   Name Server Identifier (NSID)
    public static final String PROP_KEY_DNS_EDNS_NSID_ENABLE = "loulan.dns.protocol.dns.edns.nsid.enable";

    // EDNSオプション   :   EDNS Client Subnet (ECS) 
    public static final String PROP_KEY_DNS_EDNS_ECS_ENABLE = "loulan.dns.protocol.dns.edns.ecs.enable";

    // EDNSオプション   :   EDNS Client Subnet (ECS) Address
    public static final String PROP_KEY_DNS_EDNS_ECS_ADDRESS = "loulan.dns.protocol.dns.edns.ecs.address";

    // EDNSオプション   :   EDNS Client Subnet (ECS) Prefix
    public static final String PROP_KEY_DNS_EDNS_ECS_PREFIX_BITS = "loulan.dns.protocol.dns.edns.ecs.prefix.bits";


    // EDNSオプション   :   EDNS Expire
    public static final String PROP_KEY_DNS_EDNS_EXPIRE_ENABLE = "loulan.dns.protocol.dns.edns.expire.enable";

    // EDNSオプション   :   DNS Cookie
    public static final String PROP_KEY_DNS_EDNS_DNSCOOKIE_ENABLE = "loulan.dns.protocol.dns.edns.dnscookie.enable";

    // EDNSオプション   :   KEEPALIVE
    public static final String PROP_KEY_DNS_EDNS_KEEPALIVE_ENABLE = "loulan.dns.protocol.dns.edns.keepalive.enable";

    // EDNSオプション   :   PADDING
    public static final String PROP_KEY_DNS_EDNS_PADDING_SIZE = "loulan.dns.protocol.dns.edns.padding.size";

    // EDNSオプション   :   EDNSOPT (+[no]ednsopt[=CODE[:VALUE]] 指定したコードとペイロードによる任意のEDNS オプションを付与)
    //                      実際のキー値は"loulan.dns.protocol.dns.edns.ednsopt.${code}"になる
    public static final String  PROP_KEY_DNS_EDNS_ENDSPOPT_CODEX = "loulan.dns.protocol.dns.edns.ednsopt.%s";

   

    // ******************************************
    // 以下、DNSプロトコルタイプの定義値
    // ******************************************
    public static final String DNS_PROTOCOL_TYPE_UDP = "UDP";
    public static final String DNS_PROTOCOL_TYPE_TCP = "TCP";
    public static final String DNS_PROTOCOL_TYPE_DOH = "DOH";
    public static final String DNS_PROTOCOL_TYPE_DOT = "DOT";


    // ******************************************
    // 以下、HTTPパラメータの定義値.
    // ******************************************

    // HTTPメソッド
    public static final String HTTP_METHOD_TYPE_GET = "GET";
    public static final String HTTP_METHOD_TYPE_POST = "POST";

    // HTTP Content-Type ヘッダー キー
    public static final String HTTP_HEADER_KEY_CONTENT_TYPE = "Content-Type";

    // HTTP Accept-Type ヘッダー キー
    public static final String HTTP_HEADER_KEY_ACCEPT_TYPE = "Accept-Type";


    // HTTP Content-Typeヘッダー値
    public static final String HTTP_CONTENT_TYPE_APPLICATION_DNS_MESSAGE = "application/dns-message";

    // HTTP Accept-Typeヘッダー値
    public static final String HTTP_ACCEPTT_TYPE_APPLICATION_DNS_MESSAGE = "application/dns-message";

    // HTTPクエリパラメータ
    public static final String HTTP_QUERY_PARAMETER_TYPE_DNS = "dns";

    // HTTPステータスコード
    public static final int HTTP_STATUS_CODE_OK = 200;
    public static final int HTTP_STATUS_CODE_MOVED_PERMANENTLY = 301;







    // ******************************************
    // 以下、DNSクライアントの定数値
    // ******************************************

    public static final int UDP_CLIENT_MAX_SEND_BUFFER_SIZE = 4096;
    public static final int UDP_CLIENT_MAX_RECEIVE_BUFFER_SIZE = 4096;


    // ******************************************
    // 以下、DNSクライアントのデフォルト値
    // ******************************************
    public static final int UDP_CLIENT_DEFAULT_SERVER_PORT = 53;
    public static final int UDP_CLIENT_DEFAULT_QTYPE = 1;
    public static final int UDP_CLIENT_DEFAULT_QCLASS = 1;



    // ******************************************
    // 以下、DoHクライアントの定数値
    // ******************************************
    // DoHサーバーのURLパス(フルURL)   :  "https://1.1.1.1/dns-query"のようなフルURL
    public static final String PROP_KEY_DOH_SERVER_FULL_URL = "loulan.dns.server.doh.url.full";

    // DoHサーバーのURLのコンテキストパス   :  "/dns-query"のような部分的なURLパスのコンテキストパス.
    public static final String PROP_KEY_DOH_SERVER_URL_CONTEXT_PATH = "loulan.dns.server.doh.url.context.path";

    // DoHサーバーにアクセスするときに使用するURLスキーム (例 : "https", "http" )
    public static final String PROP_KEY_DOH_SERVER_URL_SCHEME = "loulan.dns.server.doh.url.scheme";

    // DoHサーバーにアクセスするときに使用するHTTPメソッドタイプ (例 : "GET", "POST" )
    public static final String PROP_KEY_DOH_SERVER_HTTP_METHOD_TYPE = "loulan.dns.server.doh.http.method.type";

    // DoHサーバーにアクセスするときに使用するHTTPのCONTENTタイプ (例 : application/dns-message )
    public static final String PROP_KEY_DOH_SERVER_HTTP_CONTENT_TYPE = "loulan.dns.server.doh.http.content-type";

    // DoHサーバーにアクセスするときに使用するHTTPのACCEPTタイプ (例 : application/dns-message )
    public static final String PROP_KEY_DOH_SERVER_HTTP_ACCEPT_TYPE = "loulan.dns.server.doh.http.accept-type";





}