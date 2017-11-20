package ru.javaops.masterjava.persist.dao;

import com.bertoncelj.jdbi.entitymapper.EntityMapperFactory;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.BatchChunkSize;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import ru.javaops.masterjava.persist.model.City;

import java.util.List;

@RegisterMapperFactory(EntityMapperFactory.class)
public abstract class CityDao  implements AbstractDao{

    public City insert(City city) {
        if (city.isNew()) {
            int id = insertGeneratedId(city);
            city.setId(id);
        } else {
            insertWitId(city);
        }
        return city;
    }

    @SqlUpdate("INSERT INTO cities (full_name, cityid) VALUES (:fullName, :cityId) ")
    @GetGeneratedKeys
    abstract int insertGeneratedId(@BindBean City city);

    @SqlUpdate("INSERT INTO cities (id, full_name, cityid) VALUES (:id, :fullName, :cityId)")
    abstract void insertWitId(@BindBean City city);

    @SqlQuery("SELECT * FROM cities")
    public abstract List<City> getAll();

    @SqlQuery("SELECT * FROM cities WHERE cityId=:cityId")
    public abstract City getByCityId(@Bind(value = "cityId") String cityId);


    @SqlBatch("INSERT INTO cities (full_name, cityid) VALUES (:fullName, :cityId) " +
            "ON CONFLICT DO NOTHING")
    public abstract void insertBatch(@BindBean List<City> cities, @BatchChunkSize int chunkSize);


    @SqlUpdate("TRUNCATE cities CASCADE ")
    @Override
    public abstract void clean();
}
