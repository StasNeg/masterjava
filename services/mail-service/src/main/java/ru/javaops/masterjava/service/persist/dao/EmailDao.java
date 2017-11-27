package ru.javaops.masterjava.service.persist.dao;

import com.bertoncelj.jdbi.entitymapper.EntityMapperFactory;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import ru.javaops.masterjava.persist.dao.AbstractDao;
import ru.javaops.masterjava.service.persist.model.EmailEntity;

import java.util.List;

@RegisterMapperFactory(EntityMapperFactory.class)
public abstract class EmailDao implements AbstractDao {

    @SqlUpdate("TRUNCATE emails")
    @Override
    public abstract void clean();

    @SqlQuery("SELECT * FROM emails")
    public abstract List<EmailEntity> getAll();

    @SqlUpdate("INSERT INTO emails (to_email, subject, body, send_date_time)  VALUES (:to, :subject, :body, :sendDateTime)")
    @GetGeneratedKeys
    public abstract int insertGeneratedId(@BindBean EmailEntity emailEntity);

    @SqlBatch("INSERT INTO emails (" +
            "to_email, subject, body, send_date_time)  VALUES (:to, :subject, :body, :sendDateTime)")
    public abstract void insertBatch(@BindBean List<EmailEntity> emails);

    public void insert(EmailEntity email) {
        int id = insertGeneratedId(email);
        email.setId(id);
    }

}
