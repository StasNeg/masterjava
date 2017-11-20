package ru.javaops.masterjava.persist.dao;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.javaops.masterjava.persist.CityUserTestData;
import ru.javaops.masterjava.persist.ProjectGroupTestData;
import ru.javaops.masterjava.persist.model.City;
import ru.javaops.masterjava.persist.model.Group;

import java.util.List;

import static ru.javaops.masterjava.persist.CityUserTestData.FIRST3_CITIES;
import static ru.javaops.masterjava.persist.CityUserTestData.MOW;
import static ru.javaops.masterjava.persist.ProjectGroupTestData.FIRST3_GROUP;


public class GroupDaoTest extends AbstractDaoTest<GroupDao> {

    public GroupDaoTest() {
        super(GroupDao.class);
    }

    @BeforeClass
    public static void init() throws Exception {
        ProjectGroupTestData.init();
    }

    @Before
    public void setUp() throws Exception {
       ProjectGroupTestData.setUp();
    }

    @Test
    public void insertBatch() throws Exception {
        dao.clean();
        dao.insertBatch(FIRST3_GROUP, 2);
        Assert.assertEquals(3, dao.getAll().size());
    }

}