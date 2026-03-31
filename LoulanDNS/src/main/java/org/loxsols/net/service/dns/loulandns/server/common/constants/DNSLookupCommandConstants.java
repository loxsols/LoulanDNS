package org.loxsols.net.service.dns.loulandns.server.common.constants;


import java.text.Format;
import java.util.regex.Pattern;

// DNS Lookupコマンド関係の定数クラス.
public class DNSLookupCommandConstants
{


    // ------ EDNS関係のクエリオプション
    // +[no]edns (EDNS0)
    public static final String QUERY_OPTION_CODE_EDNS = "edns";

    // +[no]nsid Name Server Identifier (NSID)
    public static final String QUERY_OPTION_CODE_NSID = "nsid";

    // +[no]subnet=ADDR[/PREFIX-LEN] EDNS Client Subnet (ECS) を指定
    public static final String QUERY_OPTION_CODE_SUBNET = "subnet";

    // +[no]expire EDNS Expire
    public static final String QUERY_OPTION_CODE_EXPIRE = "expire";

    // +[no]cookie[=VALUE] DNS Cookie (クッキーの値を指定)
    public static final String QUERY_OPTION_CODE_COOKIE = "cookie";

    // +[no]keepalive EDNS Keepalive
    public static final String QUERY_OPTION_CODE_KEEPALIVE = "keepalive";

    // +padding=VALUE バイト数で指定したブロックサイズにパディング
    public static final String QUERY_OPTION_CODE_PADDING = "padding";

    // +[no]ednsopt[=CODE[:VALUE]] 指定したコードとペイロードによるEDNS オプションを付与
    public static final String QUERY_OPTION_CODE_EDNSOPT = "ednsopt";

}