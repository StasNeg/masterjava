package ru.javaops.masterjava.persist.dao;


import com.bertoncelj.jdbi.entitymapper.EntityMapperFactory;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.BatchChunkSize;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import ru.javaops.masterjava.persist.model.Project;

import java.util.List;

@RegisterMapperFactory(EntityMapperFactory.class)
public abstract class ProjectDao implements AbstractDao{

    public Project insert(Project project) {
        if (project.isNew()) {
            int id = insertGeneratedId(project);
            project.setId(id);
        } else {
            insertWitId(project);
        }
        return project;
    }

    @SqlUpdate("INSERT INTO projects (name) VALUES (:name) ")
    @GetGeneratedKeys
    abstract int insertGeneratedId(@BindBean Project project);

    @SqlUpdate("INSERT INTO projects (id, name) VALUES (:id, :name) ")
    abstract void insertWitId(@BindBean Project project);

    @SqlQuery("SELECT * FROM projects LIMIT :it")
    public abstract List<Project> getWithLimit(@Bind int limit);


    @SqlUpdate("TRUNCATE projects CASCADE")
    @Override
    public abstract void clean();
}
