package org.loxsols.net.service.dns.loulandns.server.common.constants;


import java.text.Format;
import java.util.regex.Pattern;

// DNSプロトコル関係の定数値のクラス.
public class DNSProtocolConstants
{

    // DNSヘッダーセクションのサイズ.
    public static int SIZE_OF_DNS_HEADER_SECION = 12;

    // FQDN名(末尾に.を含む文字列)の最大長
    public static int MAX_DOMAIN_NAME_LENGTH = 254;

    // ドメイン名中のラベル文字列の最大長
    public static int MAX_DOMAIN_NAME_LABEL_LENGTH = 63;

    // RDATA(65535(rdlengthの最大表現値))
    public static int MAX_RDATA_LENGTH = 65535;


    // DNS問い合わせ部分の最大長は 255byte(name) + 2byte(type) 2byte(class) = 259byte
    public static int MAX_QUESTION_PART_SIZE = (MAX_DOMAIN_NAME_LENGTH + 1) + 2 + 2;


    // DNSリソースレコードの最大長 : NAME(255byte) + TYPE(2byte) + CLASS(2byte) + TTL(4byte) + RDLENGTH(2byte) + RDATA(65535(rdlengthの最大表現値))
    public static int MAX_RESOURCE_RECORD_SIZE = (MAX_DOMAIN_NAME_LENGTH + 1) + 2 + 2 + 4 + 2 + MAX_RDATA_LENGTH;


    // UDP版のDNSパケットの最大長(512バイト).
    public static int MAX_DNS_UDP_PACKET_SIZE = 512;



    // ----- DNSオペコード(OPCODE)
    public static int DNS_OPCODE_QUERY          = 0;
    public static int DNS_OPCODE_IQUERY         = 1;
    public static int DNS_OPCODE_STATUS         = 2;
    public static int DNS_OPCODE_RESERVED_03    = 3;    // OPCODE3は予約番号のまま利用されていない.
    public static int DNS_OPCODE_NOTIFY         = 4;
    public static int DNS_OPCODE_UPDATE         = 5;
    public static int DNS_OPCODE_DSO            = 6;
    public static int DNS_OPCODE_RESERVED_07    = 7;    // OPCODE 7-15は予約番号のまま利用されていない.
    public static int DNS_OPCODE_RESERVED_08    = 8;    // OPCODE 7-15は予約番号のまま利用されていない.
    public static int DNS_OPCODE_RESERVED_09    = 9;    // OPCODE 7-15は予約番号のまま利用されていない.
    public static int DNS_OPCODE_RESERVED_10    = 10;   // OPCODE 7-15は予約番号のまま利用されていない.
    public static int DNS_OPCODE_RESERVED_11    = 11;   // OPCODE 7-15は予約番号のまま利用されていない.
    public static int DNS_OPCODE_RESERVED_12    = 12;   // OPCODE 7-15は予約番号のまま利用されていない.
    public static int DNS_OPCODE_RESERVED_13    = 13;   // OPCODE 7-15は予約番号のまま利用されていない.
    public static int DNS_OPCODE_RESERVED_14    = 14;   // OPCODE 7-15は予約番号のまま利用されていない.
    public static int DNS_OPCODE_RESERVED_15    = 15;   // OPCODE 7-15は予約番号のまま利用されていない.



    
    // ----- DNSクラス(CLASS)
    public static int DNS_CLASS_IN = 1; // IN 1 the Internet
    public static int DNS_CLASS_CS = 2; // CS 2 the CSNET class (Obsolete - used only for examples in some obsolete RFCs)
    public static int DNS_CLASS_CH = 3; // CH 3 the CHAOS class
    public static int DNS_CLASS_HS = 4; // HS 4 Hesiod [Dyer 87]


    



 
    // ----- DNSレスポンスコード(RCODE) ---
    public static int DNS_RCODE_NOERROR     = 0;    // RCODE:0  NOERROR	    DNS Query completed successfully
    public static int DNS_RCODE_FORMERR     = 1;    // RCODE:1	FORMERR	    DNS Query Format Error
    public static int DNS_RCODE_SERVFAIL    = 2;    // RCODE:2	SERVFAIL	Server failed to complete the DNS request
    public static int DNS_RCODE_NXDOMAIN    = 3;    // RCODE:3	NXDOMAIN	Domain name does not exist
    public static int DNS_RCODE_NOTIMP      = 4;    // RCODE:4	NOTIMP	    Function not implemented
    public static int DNS_RCODE_REFUSED     = 5;    // RCODE:5	REFUSED	    The server refused to answer for the query
    public static int DNS_RCODE_YXDOMAIN    = 6;    // RCODE:6	YXDOMAIN	Name that should not exist, does exist
    public static int DNS_RCODE_YXRRSET     = 7;    // RCODE:7	YXRRSET	    RR Set Exists when it should not
    public static int DNS_RCODE_NXRRSET     = 8;    // RCODE:8	NXRRSET	    RR Set that should exist does not
    public static int DNS_RCODE_NOTAUTH     = 9;    // RCODE:9	NOTAUTH	    Server not authoritative for the zone[RFC2136] / Not Authorized[RFC8945]

    public static int DNS_RCODE_NOTZONE       = 10;    // RCODE:10	NOTZONE	    Name not contained in zone
    public static int DNS_RCODE_DSOTYPENI     = 11;    // RCODE:11	DSOTYPENI   DSO-TYPE Not Implemented
    
    // 12-15は未割り当て (予約済み)
    public static int DNS_RCODE_RESERVED_12   = 12;
    public static int DNS_RCODE_RESERVED_15   = 15;

    public static int DNS_RCODE_BADVERS       = 16;     // RCODE:16	BADVERS   Bad OPT Version
    public static int DNS_RCODE_BADKEY        = 17;      // RCODE:17	BADKEY    	Key not recognized
    public static int DNS_RCODE_BADTIME        = 18;      // RCODE:18	BADTIME   	Signature out of time window
    public static int DNS_RCODE_BADMODE        = 19;      // RCODE:19	BADMODE   	Bad TKEY Mode
    public static int DNS_RCODE_BADNAME        = 20;      // RCODE:20	BADNAME   	Duplicate key name
    public static int DNS_RCODE_BADALG        = 21;      // RCODE:21	BADNAME   	Algorithm not supported
    public static int DNS_RCODE_BADTRUNC        = 22;      // RCODE:22	BADNAME   	Bad Truncation
    public static int DNS_RCODE_BADCOOKIE        = 23;      // RCODE:23	BADNAME   	Bad/missing Server Cookie

    // 24-3840は未割り当て (予約済み)    
    public static int DNS_RCODE_RESERVED_24   = 24;
    public static int DNS_RCODE_RESERVED_3840   = 3840;

    // 3841-4095は私的利用可能な範囲(Reserved for Private Use) [RFC6895]
    public static int DNS_RCODE_RESERVED_FOR_PRIVATE_USE_3841   = 3841;
    public static int DNS_RCODE_RESERVED_FOR_PRIVATE_USE_4095   = 4095;

    // 4096-65534は未割り当て (予約済み)
    public static int DNS_RCODE_RESERVED_4096   = 4096;
    public static int DNS_RCODE_RESERVED_65534   = 65534;

    // 65535 : Reserved, can be allocated by Standards Action[RFC6895]
    public static int DNS_RCODE_RESERVED_65535   = 65535;


    // --- DNSリソースレコードのタイプ
    public static int DNS_RR_TYPE_A     = 1;        //  a host address 	[RFC1035]
    public static int DNS_RR_TYPE_NS    = 2;        //  an authoritative name server 	[RFC1035] 	
    public static int DNS_RR_TYPE_MD    = 3;        //  a mail destination (OBSOLETE - use MX)
    public static int DNS_RR_TYPE_MF    = 4;        //  a mail forwarder (OBSOLETE - use MX) 	[RFC1035] 	    
    public static int DNS_RR_TYPE_CNAME = 5;        //  the canonical name for an alias 	[RFC1035] 	
    public static int DNS_RR_TYPE_SOA   = 6;        //  marks the start of a zone of authority 	[RFC1035]
    public static int DNS_RR_TYPE_MB    = 7;        //  a mailbox domain name (EXPERIMENTAL) 	[RFC1035] 	
    public static int DNS_RR_TYPE_MG    = 8;        //  a mail group member (EXPERIMENTAL) 	[RFC1035]
    public static int DNS_RR_TYPE_MR    = 9;        //  a mail rename domain name (EXPERIMENTAL) 	[RFC1035] 	
    public static int DNS_RR_TYPE_NULL  = 10;       //  a null RR (EXPERIMENTAL) 	[RFC1035]
    public static int DNS_RR_TYPE_WKS   = 11;       //  a well known service description 	[RFC1035] 	
    public static int DNS_RR_TYPE_PTR   = 12;       //  a domain name pointer 	[RFC1035]
    public static int DNS_RR_TYPE_HINFO = 13;       //  host information 	[RFC1035]
    public static int DNS_RR_TYPE_MINFO = 14;       //  mailbox or mail list information 	[RFC1035] 	
    public static int DNS_RR_TYPE_MX    = 15;       //  mail exchange 	[RFC1035]
    public static int DNS_RR_TYPE_TXT   = 16;       //  text strings
    public static int DNS_RR_TYPE_RP    = 17;       //  for Responsible Person 	[RFC1183] 	
    public static int DNS_RR_TYPE_AFSDB = 18;       //  for AFS Data Base location 	[RFC1183][RFC5864]
    public static int DNS_RR_TYPE_X25   = 19;       //  for X.25 PSDN address 	[RFC1183]
    public static int DNS_RR_TYPE_ISDN  = 20;       //  for ISDN address 	[RFC1183]
    public static int DNS_RR_TYPE_RT    = 21;       //  for Route Through 	[RFC1183] 	
    public static int DNS_RR_TYPE_NSAP  = 22;       //  for NSAP address, NSAP style A record (DEPRECATED) 	[RFC1706][Moving TPC.INT and NSAP.INT infrastructure domains to historic] 	
    public static int DNS_RR_TYPE_NSAP_PTR  = 23;   //  for domain name pointer, NSAP style (DEPRECATED) 	[RFC1706][Moving TPC.INT and NSAP.INT infrastructure domains to historic] 	
    public static int DNS_RR_TYPE_SIG   = 24;       //  for security signature 	[RFC2536][RFC2931][RFC3110][RFC4034]
    public static int DNS_RR_TYPE_KEY   = 25;       //  for security key 	[RFC2536][RFC2539][RFC3110][RFC4034]
    public static int DNS_RR_TYPE_PX    = 26;       //  X.400 mail mapping information 	[RFC2163] 	
    public static int DNS_RR_TYPE_GPOS  = 27;       //  Geographical Position 	[RFC1712] 	
    public static int DNS_RR_TYPE_AAAA  = 28;       //  IP6 Address 	[RFC3596] 	
    public static int DNS_RR_TYPE_LOC   = 29;       //  Location Information 	[RFC1876] 	
    public static int DNS_RR_TYPE_NXT   = 30;       //  Next Domain (OBSOLETE) 	[RFC2535]
    public static int DNS_RR_TYPE_EID   = 31;       //  Endpoint Identifier 	[Michael_Patton][http://ana-3.lcs.mit.edu/~jnc/nimrod/dns.txt] 		
    public static int DNS_RR_TYPE_NIMLOC = 32;      //  Nimrod Locator 	[1][Michael_Patton][http://ana-3.lcs.mit.edu/~jnc/nimrod/dns.txt] 	
    public static int DNS_RR_TYPE_SRV   = 33;       //  Server Selection 	[1][RFC2782] 	
    public static int DNS_RR_TYPE_ATMA  = 34;       //  ATM Address 	[ ATM Forum Technical Committee, "ATM Name System, V2.0", Doc ID: AF-DANS-0152.000, July 2000. Available from and held in escrow by IANA.] 	
    public static int DNS_RR_TYPE_NAPTR  = 35;      //  Naming Authority Pointer 	[RFC3403] 	
    public static int DNS_RR_TYPE_KX    = 36;       //  Key Exchanger 	[RFC2230] 	
    public static int DNS_RR_TYPE_CERT    = 37;     //  CERT 	[RFC4398]
    public static int DNS_RR_TYPE_A6    = 38;       //  A6 (OBSOLETE - use AAAA) 	[RFC2874][RFC3226][RFC6563] 	
    public static int DNS_RR_TYPE_DNAME    = 39;    //  DNAME 	[RFC6672] 		
    public static int DNS_RR_TYPE_SINK    = 40;     //  SINK 	[Donald_E_Eastlake][draft-eastlake-kitchen-sink-02] 	
    public static int DNS_RR_TYPE_OPT    = 41;      //  OPT 	[RFC3225][RFC6891]
    public static int DNS_RR_TYPE_APL    = 42;      //  APL 	[RFC3123] 	
    public static int DNS_RR_TYPE_DS    = 43;       //  Delegation Signer 	[RFC4034]
    public static int DNS_RR_TYPE_SSHFP    = 44;    //  SSH Key Fingerprint 	[RFC4255] 	
    public static int DNS_RR_TYPE_IPSECKEY    = 45; //  IPSECKEY 	[RFC4025] 	
    public static int DNS_RR_TYPE_RRSIG    = 46;    //  RRSIG 	[RFC4034] 	
    public static int DNS_RR_TYPE_NSEC    = 47;     //  NSEC 	[RFC4034][RFC9077] 	
    public static int DNS_RR_TYPE_DNSKEY    = 48;   //  DNSKEY 	[RFC4034]
    public static int DNS_RR_TYPE_DHCID    = 49;    //  DHCID 	[RFC4701] 	
    public static int DNS_RR_TYPE_NSEC3    = 50;    //  NSEC3 	[RFC5155][RFC9077]
    public static int DNS_RR_TYPE_NSEC3PARAM    = 51; //  	NSEC3PARAM 	[RFC5155] 	
    public static int DNS_RR_TYPE_TLSA    = 52;     //  TLSA 	[RFC6698]
    public static int DNS_RR_TYPE_SMIMEA    = 53;   //  S/MIME cert association 	[RFC8162]
    public static int DNS_RR_TYPE_UNASSIGNED_54    = 54;    // Unassigned
    public static int DNS_RR_TYPE_HIP    = 55;      //  Host Identity Protocol 	[RFC8005] 	
    public static int DNS_RR_TYPE_NINFO    = 56;    //  NINFO 	[Jim_Reid]
    public static int DNS_RR_TYPE_RKEY    = 57;     //  RKEY 	[Jim_Reid]
    public static int DNS_RR_TYPE_TALINK    = 58;   //  Trust Anchor LINK 	[Wouter_Wijngaards]
    public static int DNS_RR_TYPE_CDS    = 59;      //  Child DS 	[RFC7344]
    public static int DNS_RR_TYPE_CDNSKEY    = 60;  //  DNSKEY(s) the Child wants reflected in DS 	[RFC7344]
    public static int DNS_RR_TYPE_OPENPGPKEY    = 61; // OpenPGP Key 	[RFC7929]
    public static int DNS_RR_TYPE_CSYNC    = 62;    //  Child-To-Parent Synchronization 	[RFC7477]
    public static int DNS_RR_TYPE_ZONEMD    = 63;   //   Message Digest Over Zone Data 	[RFC8976]
    public static int DNS_RR_TYPE_SVCB    = 64;     //   General-purpose service binding 	[RFC9460]
    public static int DNS_RR_TYPE_HTTPS    = 65;    //   SVCB-compatible type for use with HTTP 	[RFC9460]
    public static int DNS_RR_TYPE_DSYNC    = 66;    //   Endpoint discovery for delegation synchronization 	[RFC-ietf-dnsop-generalized-notify-09]
    public static int DNS_RR_TYPE_HHIT    = 67;     //   Hierarchical Host Identity Tag 	[draft-ietf-drip-registries-28]
    public static int DNS_RR_TYPE_BRID    = 68;     //   UAS Broadcast Remote Identification
    public static int DNS_RR_TYPE_UNASSIGNED_69   = 69;     //  Unassigned
    public static int DNS_RR_TYPE_UNASSIGNED_98   = 98;     //  Unassigned
    public static int DNS_RR_TYPE_SPF   = 99;            //  [RFC7208] 	
    public static int DNS_RR_TYPE_UINFO   = 100;    //   [IANA-Reserved]
    public static int DNS_RR_TYPE_UID   = 101;      //   [IANA-Reserved] 
    public static int DNS_RR_TYPE_GID   = 102;      //   [IANA-Reserved]
    public static int DNS_RR_TYPE_UNSPEC   = 103;   //   [IANA-Reserved]
    public static int DNS_RR_TYPE_NID   = 104;      //   [RFC6742]
    public static int DNS_RR_TYPE_L32   = 105;      //   [RFC6742]
    public static int DNS_RR_TYPE_L64   = 106;      //   [RFC6742]
    public static int DNS_RR_TYPE_LP   = 107;       //   [RFC6742]
    public static int DNS_RR_TYPE_EUI48   = 108;    //   an EUI-48 address 	[RFC7043]
    public static int DNS_RR_TYPE_EUI64   = 109;    //   an EUI-64 address 	[RFC7043]
    public static int DNS_RR_TYPE_UNASSIGNED_110   = 110;   //  Unassigned
    public static int DNS_RR_TYPE_UNASSIGNED_127   = 127;   //  Unassigned
    public static int DNS_RR_TYPE_NXNAME   = 128;   //   NXDOMAIN indicator for Compact Denial of Existence 	[RFC-ietf-dnsop-compact-denial-of-existence-07]
    public static int DNS_RR_TYPE_UNASSIGNED_129   = 129;   //  Unassigned
    public static int DNS_RR_TYPE_UNASSIGNED_248   = 248;   //  Unassigned
    public static int DNS_RR_TYPE_TKEY   = 249;     //   Transaction Key 	[RFC2930]
    public static int DNS_RR_TYPE_TSIG   = 250;     //   Transaction Signature 	[RFC8945]
    public static int DNS_RR_TYPE_IXFR   = 251;     //   incremental transfer 	[RFC1995] 	
    public static int DNS_RR_TYPE_AXFR   = 252;     //   transfer of an entire zone 	[RFC1035][RFC5936] 	
    public static int DNS_RR_TYPE_MAILB   = 253;    //   mailbox-related RRs (MB, MG or MR) 	[RFC1035]
    public static int DNS_RR_TYPE_MAILA   = 254;    //   mail agent RRs (OBSOLETE - see MX) 	[RFC1035
    public static int DNS_RR_TYPE_ANY   = 255;      //   A request for some or all records the server has available 	[RFC1035][RFC6895][RFC8482]
    public static int DNS_RR_TYPE_URI   = 256;      //   URI 	[RFC7553]
    public static int DNS_RR_TYPE_CAA   = 257;      //   Certification Authority Restriction 	[RFC865
    public static int DNS_RR_TYPE_AVC   = 258;      //   Application Visibility and Control 	[Wolfgang_Riedel]
    public static int DNS_RR_TYPE_DOA   = 259;      //   Digital Object Architecture 	[draft-durand-doa-over-dns-02]
    public static int DNS_RR_TYPE_AMTRELAY   = 260; //   Automatic Multicast Tunneling Relay 	[RFC8777]
    public static int DNS_RR_TYPE_RESINFO   = 261;  //   Resolver Information as Key/Value Pairs
    public static int DNS_RR_TYPE_WALLET   = 262;   //   Public wallet address 	[Paul_Hoffman]
    public static int DNS_RR_TYPE_CLA   = 263;      //   BP Convergence Layer Adapter 	[draft-johnson-dns-ipn-cla-07]
    public static int DNS_RR_TYPE_IPN   = 264;      //   BP Node Number 	[draft-johnson-dns-ipn-cla-07]
    public static int DNS_RR_TYPE_UNASSIGNED_265   = 265;     //  Unassigned
    public static int DNS_RR_TYPE_UNASSIGNED_32767   = 32767; //  Unassigned
    public static int DNS_RR_TYPE_TA   = 32768;     //   DNSSEC Trust Authorities 	[Sam_Weiler][ Deploying DNSSEC Without a Signed Root. Technical Report 1999-19, Information Networking Institute, Carnegie Mellon University, April 2004.] 	
    public static int DNS_RR_TYPE_DLV   = 32769;    //   DNSSEC Lookaside Validation (OBSOLETE) 	[RFC8749][RFC4431] 	
    public static int DNS_RR_TYPE_UNASSIGNED_32770  = 32770;  //  UnUnassigned 	32770-65279 	
    public static int DNS_RR_TYPE_UNASSIGNED_65279  = 65279;  //  Unassigned 	32770-65279 	
    public static int DNS_RR_TYPE_PRIVATE_USE_65280  = 65280; //  Private use 	65280-65534 	
    public static int DNS_RR_TYPE_PRIVATE_USE_65534  = 65534; //  Private use 	65280-65534 	
    public static int DNS_RR_TYPE_RESERVED_65535  = 65535;    //  Reserved
  
    
    
    
    
    
    
    // EDNSの疑似リソースレコードのタイプ.
    public static int EDNS_PSUEDO_RR_TYPE_ECS   = 8;        // EDNS Client Subnet( RFC7871 : Client Subnet in DNS Queries).
    
      
    // IANAのアドレスファミリ番号.
    //  IPv4=1, IPv6=2, ...etc.
    //  そのほかの値についてはIANAのWebサイト(Address Family Numbers)を参照.
    //      https://www.iana.org/assignments/address-family-numbers/address-family-numbers.xhtml
    public static int IANA_ADDRESS_FAMILY_TYPE_IP        = 1;       // IPv4
    public static int IANA_ADDRESS_FAMILY_TYPE_IP6       = 2;       // IPv6

    
    
    

    
    
    
    
    
    
   
      
        
       
    // TODO : 以下まだ実装中.



    // ASCIIコードのNULL文字
    public static int ASCII_CODE_NULL = 0x00;


    // ******************************************
    // 以下、文字列チェック用の正規表現
    // ******************************************

    // ドメイン名文字列の定義
    // 半角英数字（A-Z, a-z, 0-9）とハイフン(-)/ドット(.)記号
    //  例) www.hoge01-23.com
    public static final String REGULAR_EXPRESSION_OF_DOMAIN_NAME = "^[a-zA-Z0-9-.]*";
    public static final Pattern REGULAR_EXPRESSION_PATTERN_OF_DOMAIN_NAME = Pattern.compile( REGULAR_EXPRESSION_OF_DOMAIN_NAME );


    // ドメイン名のラベル文字列の定義
    // 半角英数字（A-Z, a-z, 0-9）とハイフン(-)記号
    //  例) hoge01-23
    public static final String REGULAR_EXPRESSION_OF_DOMAIN_NAME_LABEL = "^[a-zA-Z0-9-]*";
    public static final Pattern REGULAR_EXPRESSION_PATTERN_OF_DOMAIN_NAME_LABEL = Pattern.compile( REGULAR_EXPRESSION_OF_DOMAIN_NAME_LABEL ); 



}