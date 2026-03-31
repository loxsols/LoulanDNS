package org.loxsols.net.service.dns.loulandns.client;


import org.loxsols.net.service.dns.loulandns.client.common.DNSClientCommonException;
import org.loxsols.net.service.dns.loulandns.server.common.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;

import java.util.*;

// DNSメッセージの転送クラスのIF
public interface IDNSMessageTransporter
{
    public void init(HashMap<String, String> properties) throws DNSClientCommonException;

    public IDNSResponseMessage lookup(IDNSQuestionMessage questionMessage) throws DNSClientCommonException;

}