package ru.javaops.masterjava.persist;

import com.google.common.collect.ImmutableList;
import ru.javaops.masterjava.persist.dao.CityDao;
import ru.javaops.masterjava.persist.model.City;
import ru.javaops.masterjava.persist.model.DBIProvider;

import java.util.List;

public class CityTestData {
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
    }

    public static void setUp() {
        CityDao dao = DBIProvider.getDao(CityDao.class);
        dao.clean();
        DBIProvider.getDBI().useTransaction((conn, status) -> {
            FIRST3_CITIES.forEach(dao::insert);
            dao.insert(MSK);
        });
    }
}
