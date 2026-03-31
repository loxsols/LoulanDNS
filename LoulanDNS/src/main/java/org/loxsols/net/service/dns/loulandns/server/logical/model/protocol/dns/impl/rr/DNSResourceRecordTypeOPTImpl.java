package org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.rr;


import java.util.*;
import java.util.Arrays;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;

import org.loxsols.net.service.dns.loulandns.util.*;


import org.loxsols.net.service.dns.loulandns.server.common.*;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSDebugUtils;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSProtocolUtils;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;
import org.loxsols.net.service.dns.loulandns.server.common.constants.DNSProtocolConstants;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.DNSResourceRecordImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.*;



// OPT DNSリソースレコードの実装クラス.
public class DNSResourceRecordTypeOPTImpl extends DNSResourceRecordImpl implements IDNSResourceRecord
{

    public DNSResourceRecordTypeOPTImpl(byte[] rrBytes) throws DNSServiceCommonException
    {
        super.setDNSResourceRecordBytes(rrBytes);


        if ( getResourceType() !=  DNSProtocolConstants.DNS_RR_TYPE_OPT )
        {

            LoulanDNSDebugUtils.printDebug( this.getClass(), "DNSResourceRecordTypeOPTImpl() : rrBytes.length=", Integer.toString(rrBytes.length) );
            LoulanDNSDebugUtils.printHexString(this.getClass(), "DNSResourceRecordTypeOPTImpl() : rrBytes", rrBytes );

            LoulanDNSDebugUtils.printDebug( this.getClass(), "DNSResourceRecordTypeOPTImpl() : getDNSResourceName()=", getDNSResourceName() );
            LoulanDNSDebugUtils.printDebug( this.getClass(), "DNSResourceRecordTypeOPTImpl() : getResourceType()=", Integer.toString( getResourceType() ) );
            LoulanDNSDebugUtils.printDebug( this.getClass(), "DNSResourceRecordTypeOPTImpl() : getRTTL()=", Integer.toString( getRTTL() ) );
            LoulanDNSDebugUtils.printDebug( this.getClass(), "DNSResourceRecordTypeOPTImpl() : getRdlength()=", Integer.toString( getRdlength() ) );

            String msg = String.format("Specified DNS Resource Record Type is not OPT(Type41) : %d", getResourceType() );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

    }

    public DNSResourceRecordTypeOPTImpl(IDNSResourceRecord baseRR) throws DNSServiceCommonException
    {
        byte[] rrBytes = baseRR.getDNSResourceRecordBytes();
        super.setDNSResourceRecordBytes(rrBytes);
    }





}