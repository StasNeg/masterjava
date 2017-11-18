package ru.javaops.masterjava.persist;

import com.google.common.collect.ImmutableList;
import ru.javaops.masterjava.persist.dao.GroupDao;
import ru.javaops.masterjava.persist.dao.ProjectDao;
import ru.javaops.masterjava.persist.model.Group;
import ru.javaops.masterjava.persist.model.GroupType;
import ru.javaops.masterjava.persist.model.Project;

import java.util.List;

/**
 * Created by Stanislav on 18.11.2017.
 */
public class ProjectGroupTestData {
    public static Project TOPJAVA;
    public static Project MASTERJAVA;
    public static Group TOPJAVA10;
    public static Group TOPJAVA11;
    public static Group TOPJAVA1;
    public static Group MASTERJAVA1;
    public static Group MASTERJAVA2;
    public static List<Group> FIRST3_GROUP;
    public static List<Group> SECOND2_GROUP;
    public static void init() {
        TOPJAVA = new Project("topjava");
        MASTERJAVA = new Project("masterjava");

        TOPJAVA1 = new Group("topjava1", GroupType.FINISHED,"topjava");//   this(name, type, project);
        TOPJAVA11 = new Group("topjava10", GroupType.REGISTERING,"topjava");//   this(name, type, project);
        TOPJAVA10 = new Group("topjava11", GroupType.CURRENT,"topjava");//   this(name, type, project);
        MASTERJAVA1 = new Group("masterjava1", GroupType.FINISHED,"masterjava");//   this(name, type, project);
        MASTERJAVA2 = new Group("masterjava2", GroupType.CURRENT,"masterjava");//   this(name, type, project);
        FIRST3_GROUP = ImmutableList.of(TOPJAVA1,TOPJAVA10,TOPJAVA11);
        SECOND2_GROUP = ImmutableList.of(MASTERJAVA1,MASTERJAVA2);

    }

    public static void setUp() {
        ProjectDao projectDao = DBIProvider.getDao(ProjectDao.class);
        projectDao.clean();
        DBIProvider.getDBI().useTransaction((conn, status) -> {
            projectDao.insert(TOPJAVA);
            projectDao.insert(MASTERJAVA);
        });
        GroupDao groupDao = DBIProvider.getDao(GroupDao.class);
        DBIProvider.getDBI().useTransaction((conn, status) -> {
            FIRST3_GROUP.forEach(groupDao::insert);
            SECOND2_GROUP.forEach(groupDao::insert);
        });
    }
}
