package ru.javaops.masterjava.persist.dao;

import com.bertoncelj.jdbi.entitymapper.EntityMapperFactory;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.BatchChunkSize;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import ru.javaops.masterjava.persist.model.Group;

import java.util.List;

@RegisterMapperFactory(EntityMapperFactory.class)
public abstract class GroupDao implements AbstractDao {

    public Group insert(Group group) {
        if (group.isNew()) {
            int id = insertGeneratedId(group);
            group.setId(id);
        } else {
            insertWitId(group);
        }
        return group;
    }

    @SqlUpdate("INSERT INTO groups (name, project, type) VALUES (:name, :project, CAST(:type AS GROUP_TYPE)) ")
    @GetGeneratedKeys
    abstract int insertGeneratedId(@BindBean Group group);

    @SqlUpdate("INSERT INTO groups (id, name, project, type) VALUES (:id, :name, :project, CAST(:type AS GROUP_TYPE)) ")
    abstract void insertWitId(@BindBean Group group);

    @SqlQuery("SELECT * FROM groups LIMIT :it")
    public abstract List<Group> getWithLimit(@Bind int limit);



    @SqlBatch("INSERT INTO groups (name, project, type) VALUES (:name, :project, CAST(:type AS GROUP_TYPE)) "
            + "ON CONFLICT DO NOTHING")
    public abstract void insertBatch(@BindBean List<Group> groups, @BatchChunkSize int chunkSize);


    @SqlUpdate("TRUNCATE groups")
    @Override
    public abstract void clean();
}
