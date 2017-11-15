package ru.javaops.masterjava.persist.dao;


import ru.javaops.masterjava.persist.DBITestProvider;
import ru.javaops.masterjava.persist.model.DBIProvider;

public abstract class AbstractDaoTest<DAO extends AbstractDao> {
    static {
        DBITestProvider.initDBI();
    }

    protected DAO dao;

    protected AbstractDaoTest(Class<DAO> daoClass) {
        this.dao = DBIProvider.getDao(daoClass);
    }
}