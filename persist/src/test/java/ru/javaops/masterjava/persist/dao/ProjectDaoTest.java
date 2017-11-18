package ru.javaops.masterjava.persist.dao;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.javaops.masterjava.persist.ProjectGroupTestData;
import ru.javaops.masterjava.persist.model.Group;
import ru.javaops.masterjava.persist.model.Project;

import java.util.Arrays;
import java.util.List;

import static ru.javaops.masterjava.persist.ProjectGroupTestData.FIRST3_GROUP;


public class ProjectDaoTest extends AbstractDaoTest<ProjectDao> {

    public ProjectDaoTest() {
        super(ProjectDao.class);
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
    public void getWithLimit() {
        List<Project> groups = dao.getWithLimit(3);
        Assert.assertEquals(2, groups.size());
    }
}