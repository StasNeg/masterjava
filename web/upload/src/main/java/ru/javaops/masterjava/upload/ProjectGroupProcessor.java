package ru.javaops.masterjava.upload;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import ru.javaops.masterjava.persist.dao.CityDao;
import ru.javaops.masterjava.persist.dao.GroupDao;
import ru.javaops.masterjava.persist.dao.ProjectDao;
import ru.javaops.masterjava.persist.model.City;
import ru.javaops.masterjava.persist.model.DBIProvider;
import ru.javaops.masterjava.persist.model.Group;
import ru.javaops.masterjava.persist.model.Project;
import ru.javaops.masterjava.persist.model.type.GroupType;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.stream.XMLStreamException;
import java.util.ArrayList;
import java.util.Map;

@Slf4j
public class ProjectGroupProcessor {
    private final ProjectDao projectDao = DBIProvider.getDao(ProjectDao.class);
    private final GroupDao groupDao = DBIProvider.getDao(GroupDao.class);

    public Map<String, Group> process(StaxStreamProcessor processor) throws XMLStreamException {
        val projects = projectDao.getAsMap();
        val groups = groupDao.getAsMap();
        while(processor.startElement("Project", "Projects")){
            val projectName = processor.getAttribute("name");
            val projectText = processor.getElementValue("description");
            int idProject;
            if(projects.get(projectName)==null){
                idProject = projectDao.insertGeneratedId(new Project(projectName, projectText));
            }else{
                idProject = projects.get(projectName).getId();
            }
            while (processor.startElement("Group", "Project")){
                val groupName = processor.getAttribute("name");
                if(groups.get(groupName)==null){
                    groupDao.insert(new Group(groupName, GroupType.valueOf(processor.getAttribute("type")),idProject));
                }
            }
        }
        return groupDao.getAsMap();
    }
}
