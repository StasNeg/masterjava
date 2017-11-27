package ru.javaops.masterjava.service.persist.dao;

import lombok.extern.slf4j.Slf4j;
import org.junit.*;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import ru.javaops.masterjava.persist.DBITestProvider;
import ru.javaops.masterjava.persist.model.DBIProvider;

import ru.javaops.masterjava.service.persist.EmailTestData;

import static ru.javaops.masterjava.service.persist.EmailTestData.FIST3_EMAIL;


@Slf4j
public class EmailDaoTest  {

    static {
        DBITestProvider.initDBI();
    }
    private EmailDao dao = DBIProvider.getDao(EmailDao.class);
    @Rule
    public TestRule testWatcher = new TestWatcher() {
        @Override
        protected void starting(Description description) {
            log.info("\n\n+++ Start " + description.getDisplayName());
        }

        @Override
        protected void finished(Description description) {
            log.info("\n+++ Finish " + description.getDisplayName() + '\n');
        }
    };

    @BeforeClass
    public static void init() throws Exception {
        EmailTestData.init();
    }

    @Before
    public void setUp() throws Exception {
        EmailTestData.setUp();
    }


    @Test
    public void insertBatch() throws Exception {
        dao.clean();
        dao.insertBatch(FIST3_EMAIL);
        Assert.assertEquals(3, dao.getAll().size());
    }

}