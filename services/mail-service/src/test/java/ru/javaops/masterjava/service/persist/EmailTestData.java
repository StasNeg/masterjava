package ru.javaops.masterjava.service.persist;

import com.google.common.collect.ImmutableList;
import ru.javaops.masterjava.persist.model.DBIProvider;
import ru.javaops.masterjava.service.persist.dao.EmailDao;
import ru.javaops.masterjava.service.persist.model.EmailEntity;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;



public class EmailTestData {
    public static EmailEntity EMAIL1;
    public static EmailEntity EMAIL2;
    public static EmailEntity EMAIL3;
    public static List<EmailEntity> FIST3_EMAIL;

    public static void init() {

        Timestamp currentDate = Timestamp.valueOf(LocalDateTime.now());
        EMAIL1 = new EmailEntity("stasn@ua.fm", "admin@javaops.ru", "firstEmail", currentDate);
        EMAIL2 = new EmailEntity("user@ua.fm", "admin@javaops.ru", "firstEmail", currentDate);
        EMAIL3 = new EmailEntity("stasn@ua.fm", "admin@javaops.ru", "firstEmail", currentDate);
        FIST3_EMAIL = ImmutableList.of(EMAIL1,EMAIL2,EMAIL3);
    }

    public static void setUp() {
        EmailDao dao = DBIProvider.getDao(EmailDao.class);
        dao.clean();
        DBIProvider.getDBI().useTransaction((conn, status) -> {
            FIST3_EMAIL.forEach(dao::insert);
        });
    }
}
