package ru.javaops.masterjava.persist.dao;

import com.bertoncelj.jdbi.entitymapper.EntityMapperFactory;
import one.util.streamex.IntStreamEx;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.BatchChunkSize;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;

import ru.javaops.masterjava.persist.model.DBIProvider;
import ru.javaops.masterjava.persist.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RegisterMapperFactory(EntityMapperFactory.class)
public abstract class UserDao implements AbstractDao {

    public User insert(User user) {
        if (user.isNew()) {
            int id = insertGeneratedId(user);
            user.setId(id);
        } else {
            insertWitId(user);
        }
        return user;
    }

    @SqlQuery("SELECT nextval('user_seq')")
    abstract int getNextVal();

    @Transaction
    public int getSeqAndSkip(int step) {
        int id = getNextVal();
        DBIProvider.getDBI().useHandle(h -> h.execute("ALTER SEQUENCE user_seq RESTART WITH " + (id + step)));
        return id;
    }

    @SqlUpdate("INSERT INTO users (full_name, email, flag, city) VALUES (:fullName, :email, CAST(:flag AS USER_FLAG), :city) ")
    @GetGeneratedKeys
    abstract int insertGeneratedId(@BindBean User user);

    @SqlUpdate("INSERT INTO users (id, full_name, email, flag, city) VALUES (:id, :fullName, :email, CAST(:flag AS USER_FLAG), :city) ")
    abstract void insertWitId(@BindBean User user);

    @SqlQuery("SELECT * FROM users ORDER BY full_name, email LIMIT :it")
    public abstract List<User> getWithLimit(@Bind int limit);

    //   http://stackoverflow.com/questions/13223820/postgresql-delete-all-content
    @SqlUpdate("TRUNCATE users")
    @Override
    public abstract void clean();

    //    https://habrahabr.ru/post/264281/
    @SqlBatch("INSERT INTO users (id, full_name, email, flag, city) VALUES (:id, :fullName, :email, CAST(:flag AS USER_FLAG), :city)" +
            "ON CONFLICT DO NOTHING")
//            "ON CONFLICT (email) DO UPDATE SET full_name=:fullName, flag=CAST(:flag AS USER_FLAG)")
    public abstract int[] insertBatch(@BindBean List<User> users, @BatchChunkSize int chunkSize);

    @Transaction
    public Map<TypeOfErrors,List<String>> insertAndGetWrongCityAndConflictEmails(List<User> users) {
        Map<String, List<User>> collectByCity = users.stream().collect(Collectors.groupingBy(User::getCity));
        List<String> resultList = new ArrayList<>();
        Map<TypeOfErrors, List<String>> resultMap = new HashMap<>();
        CityDao dao = DBIProvider.getDao(CityDao.class);
        collectByCity.entrySet().forEach((Map.Entry<String, List<User>> x) ->{
            if(dao.getByCityId(x.getKey())==null){
                resultList.addAll(x.getValue().stream().map(User::getEmail).collect(Collectors.toList()));
                users.removeAll(x.getValue());
            }
        });
        if(!resultList.isEmpty()) resultMap.put(TypeOfErrors.INCORRECT_CITY, resultList );
        List<String> resultListEmail = insertAndGetConflictEmails(users);
        if(!resultListEmail.isEmpty()) resultMap.put(TypeOfErrors.DOUBLE_EMAIL, resultListEmail );
        return resultMap;
    }


    private List<String> insertAndGetConflictEmails(List<User> users) {
        int[] result = insertBatch(users, users.size());
        return IntStreamEx.range(0, users.size())
                .filter(i -> result[i] == 0)
                .mapToObj(index -> users.get(index).getEmail())
                .toList();
    }


}
