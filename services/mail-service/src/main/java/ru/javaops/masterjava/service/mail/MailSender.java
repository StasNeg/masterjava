package ru.javaops.masterjava.service.mail;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.mail.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class MailSender {
    private static Email email;

    static void sendMail(List<Addressee> to, List<Addressee> cc, String subject, String body) {
        Config configEmail = ConfigFactory.parseResources("mail.conf").resolve().getConfig("mail");
        email = new SimpleEmail();
        email.setHostName(configEmail.getString("mail.host"));
        email.setSmtpPort(configEmail.getInt("mail.port"));
        email.setAuthenticator(new DefaultAuthenticator(configEmail.getString("mail.username"), configEmail.getString("mail.password")));

        email.setSSLOnConnect(configEmail.getBoolean("mail.useSSL"));
        email.setStartTLSRequired(configEmail.getBoolean("mail.useTLS"));
        email.setDebug(configEmail.getBoolean("mail.debug"));
        try {
            email.setFrom(configEmail.getString("mail.username"));
        } catch (EmailException e) {
        }
        String[] toStrings = new String[to.size()];
        String[] ccStrings = new String[cc.size()];
        try {
            email.addTo(getEmails(to, toStrings));
            email.addCc(getEmails(cc, ccStrings));
            email.setSubject(subject);
            email.setMsg(body);
            email.send();
        } catch (EmailException e) {
            e.printStackTrace();
        }
        log.info("Send mail to \'" + to + "\' cc \'" + cc + "\' subject \'" + subject + (log.isDebugEnabled() ? "\nbody=" + body : ""));
    }

    private static String[] getEmails(List<Addressee> to, String[] toStrings) {
        return to.stream().map(Addressee::getEmail).collect(Collectors.toList()).toArray(toStrings);
    }
}
