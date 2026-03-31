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


// DNS Additionalセクションの実装クラス.
public class DNSAdditionalSectionImpl extends DNSAnswerSectionImpl implements IDNSAdditionalSection
{



    public byte[] getDNSAdditionalSectionBytes() throws DNSServiceCommonException
    {
        byte[] rrSetBytes = getDNSRRSetBytes();
        return rrSetBytes;
    }


}