package org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part;


import java.util.*;
import java.time.ZonedDateTime;

import org.loxsols.net.service.dns.loulandns.server.common.*;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;


import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.*;


// DNSのOPTリソースレコードのRDATA部分を構成するオプションデータのモデルクラス.
// 定義はRFC6891のpp.8にある.
// OPT RRのRDATA部は本クラスのデータが複数件(0個の場合もある)含まれている.
public interface IDNSRROptPseudoRRData
{

    public int getOptionCode() throws DNSServiceCommonException;
    public int getOptionLength() throws DNSServiceCommonException;
    public byte[] getOptionData() throws DNSServiceCommonException;

    // 本オプションデータがRDATA部に占めるサイズ.
    public int sizeOfThisOption() throws DNSServiceCommonException;

    // 本オプションデータのRDATA部におけるbyte配列表現.
    public byte[] toBytes() throws DNSServiceCommonException;


    // 本クラスの内容を初期化する.
    // 復帰値はOPT RRのrdata部の占有サイズ.
    public int setOptionData(int optCode, int optLength, byte[] optData) throws DNSServiceCommonException;

    // 本本クラスの内容を指定したrdataフィールドの値で初期化する.なお、rdataフィールドのデータが後続する場合は読み飛ばす.
    // 復帰値はOPT RRのrdata部の占有サイズ.
    public int setOptionData(byte[] rdata) throws DNSServiceCommonException;



}