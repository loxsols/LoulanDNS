package org.loxsols.net.service.dns.loulandns.client.subway.common;


public class DNSSubwayConstants
{

    // -------------------- DNS-SubwayのLINEタイプ --------------------------
    public static final String COSNT_DNS_SUBWAY_LINE_TYPE_SSH_TUNNEL = "SSH_TUNNEL";
    // --------------------------------------------------------------------


    // -------------------- GWで転送するプロトコル. --------------------------
    public static final String COSNT_DNS_SUBWAY_PROTOCOL_TYPE_TCP = "TCP";
    // --------------------------------------------------------------------


    // -------------------- DNS-Subwayのその他の共通パラメータ --------------------------
    
    //      DNS-SubwayのGW構築の対象とするターゲットのドメイン(","区切りのリストで指定する)
    public static final String CONST_PROP_KEY_DNS_SUBWAY_TARGET_DOMAIN = "loulan.dns.subway.target.domain";
    
    //      DNS-SubwayのGW構築の対象とするターゲットのポート番号(","区切りのリストで指定する)
    public static final String CONST_PROP_KEY_DNS_SUBWAY_TARGET_PORT = "loulan.dns.subway.target.port";

    //     DNS-SubwayのGW構築時に使用するサブリゾルバのインスタンス名.
    public static final String CONST_PROP_KEY_DNS_SUBWAY_SUB_RESOLVER_NAME = "loulan.dns.subway.sub-resolver.name";

    //     DNS-SubwayのGW構築時に使用するサブリゾルバの所属するユーザー名.
    public static final String CONST_PROP_KEY_DNS_SUBWAY_SUB_RESOLVER_USER_NAME = "loulan.dns.subway.sub-resolver.user.name";



    // -------------------- SSHトンネルのパラメータ設定キー --------------------------
    public static final String CONST_PROP_KEY_DNS_SUBWAY_GW_SSH_TUNNEL_TARGET_HOST = "loulan.dns.subway.gateway.ssh.tunnel.remote.host";
    public static final String CONST_PROP_KEY_DNS_SUBWAY_GW_SSH_TUNNEL_TARGET_PORT = "loulan.dns.subway.gateway.ssh.tunnel.remote.port";

    public static final String CONST_PROP_KEY_DNS_SUBWAY_GW_SSH_TUNNEL_SERVER_HOST = "loulan.dns.subway.gateway.ssh.tunnel.server.host";
    public static final String CONST_PROP_KEY_DNS_SUBWAY_GW_SSH_TUNNEL_SERVER_PORT = "loulan.dns.subway.gateway.ssh.tunnel.server.port";

    public static final String CONST_PROP_KEY_DNS_SUBWAY_GW_SSH_TUNNEL_LOCAL_ADDRESS = "loulan.dns.subway.gateway.ssh.tunnel.local.address";
    public static final String CONST_PROP_KEY_DNS_SUBWAY_GW_SSH_TUNNEL_LOCAL_PORT = "loulan.dns.subway.gateway.ssh.tunnel.local.port";

    public static final String CONST_PROP_KEY_DNS_SUBWAY_GW_SSH_TUNNEL_SERVER_USER = "loulan.dns.subway.gateway.ssh.tunnel.server.user";
    public static final String CONST_PROP_KEY_DNS_SUBWAY_GW_SSH_TUNNEL_SERVER_PASSWORD = "loulan.dns.subway.gateway.ssh.tunnel.server.password";

    //      SSHトンネルの構築時にSSHセッションを共有するか(true/false)
    public static final String CONST_PROP_KEY_DNS_SUBWAY_GW_SSH_TUNNEL_MODE_SESSION_SHARED = "loulan.dns.subway.gateway.ssh.tunnel.mode.session.shared";






}