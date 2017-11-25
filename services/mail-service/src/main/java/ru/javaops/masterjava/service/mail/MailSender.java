package ru.javaops.masterjava.service.mail;

import com.typesafe.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.mail.*;
import org.apache.commons.mail.Email;
import ru.javaops.masterjava.persist.config.Configs;
import ru.javaops.masterjava.persist.model.DBIProvider;
import ru.javaops.masterjava.service.persist.DBIEmailProvider;
import ru.javaops.masterjava.service.persist.dao.EmailDao;
import ru.javaops.masterjava.service.persist.model.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class MailSender {
    static {
        DBIEmailProvider.initDBI();
    }
    private final static Config configEmail = Configs.getConfig("mail.conf","mail");
    private final static EmailDao DAO = DBIProvider.getDao(EmailDao.class);
    static String sendMail(List<Addressee> to, List<Addressee> cc, String subject, String body) {
        String[] toStrings;
        String[] ccStrings;
        Email email = new SimpleEmail();
        try {
            email.setHostName(configEmail.getString("mail.host"));
            email.setSmtpPort(configEmail.getInt("mail.port"));
            email.setAuthenticator(new DefaultAuthenticator(configEmail.getString("mail.username"), configEmail.getString("mail.password")));
            email.setSSLOnConnect(configEmail.getBoolean("mail.useSSL"));
            email.setStartTLSRequired(configEmail.getBoolean("mail.useTLS"));
            email.setDebug(configEmail.getBoolean("mail.debug"));
            try {
                email.setFrom(configEmail.getString("mail.username"));
            } catch (EmailException e) {
                return e.getMessage();
            }
            toStrings = getEmails(to);
            email.addTo(toStrings);
            ccStrings = getEmails(cc);
            if (ccStrings.length > 0) email.addCc(ccStrings);
            email.setSubject(subject);
            email.setMsg(body);
            email.send();
        } catch (EmailException e) {
            return e.getMessage();
        }
        log.info("Send mail to \'" + to + "\' cc \'" + cc + "\' subject \'" + subject + (log.isDebugEnabled() ? "\nbody=" + body : ""));
        saveToEmail(to, subject,body, Timestamp.valueOf(LocalDateTime.now()));
        return "Send mail to \'" + to + "\' cc \'" + cc + "\' subject \'" + subject + (log.isDebugEnabled() ? "\nbody=" + body : "");
    }

    private static void saveToEmail(List<Addressee> to, String subject, String body, Timestamp sendDateTime) {
        List<EmailEntity> emails = new ArrayList<>();
        to.forEach(x->emails.add(new EmailEntity(x.getEmail(),subject,body,sendDateTime)));
        DAO.insertBatch(emails);
    }

    private static String[] getEmails(List<Addressee> to) {
        List<String> emails = to.stream().filter(adresse -> !adresse.getEmail().isEmpty()).map(Addressee::getEmail).collect(Collectors.toList());
        return emails.toArray(new String[emails.size()]);
    }
}
