package ru.javaops.masterjava.persist;

import com.google.common.collect.ImmutableList;
import ru.javaops.masterjava.persist.dao.CityDao;
import ru.javaops.masterjava.persist.dao.UserDao;
import ru.javaops.masterjava.persist.model.City;
import ru.javaops.masterjava.persist.model.User;
import ru.javaops.masterjava.persist.model.UserFlag;

import java.util.List;

public class CityUserTestData {
    public static User ADMIN;
    public static User DELETED;
    public static User FULL_NAME;
    public static User USER1;
    public static User USER2;
    public static User USER3;
    public static List<User> FIST5_USERS;

    public static City SPB;
    public static City MOW;
    public static City KIV;
    public static City MSK;
    public static List<City> FIRST3_CITIES;

    public static void init() {
        SPB = new City("Санкт-Петербург", "spb");
        MOW = new City("Москва", "mow");
        KIV = new City("Киев", "kiv");
        MSK = new City("Минск", "msk");
        FIRST3_CITIES = ImmutableList.of(SPB, MOW,KIV);
        ADMIN = new User("Admin", "admin@javaops.ru", UserFlag.superuser, "mow");
        DELETED = new User("Deleted", "deleted@yandex.ru", UserFlag.deleted, "mow");
        FULL_NAME = new User("Full Name", "gmail@gmail.com", UserFlag.active, "mow");
        USER1 = new User("User1", "user1@gmail.com", UserFlag.active, "mow");
        USER2 = new User("User2", "user2@yandex.ru", UserFlag.active, "mow");
        USER3 = new User("User3", "user3@yandex.ru", UserFlag.active, "kiv");
        FIST5_USERS = ImmutableList.of(ADMIN, DELETED, FULL_NAME, USER1, USER2);
    }

    public static void setUp() {
        CityDao cityDao = DBIProvider.getDao(CityDao.class);
        cityDao.clean();
        DBIProvider.getDBI().useTransaction((conn, status) -> {
            FIRST3_CITIES.forEach(cityDao::insert);
            cityDao.insert(MSK);
        });
        UserDao userDao = DBIProvider.getDao(UserDao.class);
//        userDao.clean();
        DBIProvider.getDBI().useTransaction((conn, status) -> {
            FIST5_USERS.forEach(userDao::insert);
            userDao.insert(USER3);
        });
    }
}
