package org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl;


import java.util.*;
import java.io.ByteArrayOutputStream;
import java.time.ZonedDateTime;

import java.nio.ByteBuffer;

import org.loxsols.net.service.dns.loulandns.server.common.*;
import org.loxsols.net.service.dns.loulandns.server.common.constants.DNSProtocolConstants;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;


import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.*;


// DNS Authorityセクションの実装クラス.
public class DNSAuthoritySectionImpl extends DNSAnswerSectionImpl implements IDNSAuthoritySection
{



    public byte[] getDNSAuthoritySectionBytes() throws DNSServiceCommonException
    {
        byte[] rrSetBytes = getDNSRRSetBytes();
        return rrSetBytes;
    }


}