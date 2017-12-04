package ru.javaops.masterjava.service.mail;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import ru.javaops.masterjava.persist.DBITestProvider;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Endpoint;
import java.io.File;
import java.util.List;

public class MailServicePublisher {

    public static void main(String[] args) {
        DBITestProvider.initDBI();
        System.out.println(Resources.getResource("wsdl/mailService.wsdl").getFile());
        Endpoint endpoint = Endpoint.create(new MailServiceImpl());
        List<Source> metadata = ImmutableList.of(
                new StreamSource(
                        new File(Resources.getResource("wsdl/mailService.wsdl").getFile())));

        endpoint.setMetadata(metadata);
        endpoint.publish("http://localhost:8080/mail/mailService");
    }
}
