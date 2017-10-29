package ru.javaops.masterjava;

import com.google.common.io.Resources;
import org.w3c.dom.NodeList;
import ru.javaops.masterjava.xml.schema.*;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.Schemas;
import ru.javaops.masterjava.xml.util.XPathProcessor;
import ru.javaops.masterjava.xml.util.XsltProcessor;


import javax.xml.bind.JAXBException;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;


import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import java.util.stream.IntStream;
import static java.util.stream.Collectors.toList;


public class MainXML {

    public static void main(String[] args) throws Exception {
        System.out.println(getUsersByProject("topjava"));
        System.out.println(getUsersByProjectWithXPath("topjava"));
        transformXmlToHtml("topjava");
    }

    private static final JaxbParser JAXB_PARSER = new JaxbParser(ObjectFactory.class);

    static {
        JAXB_PARSER.setSchema(Schemas.ofClasspath("payload.xsd"));
    }

    public static Collection<User> getUsersByProject(String projectName) throws IOException, JAXBException {
        Payload payload = JAXB_PARSER.unmarshal(
                Resources.getResource("payload.xml").openStream());
        List<GroupSimple> groupSimples = payload.getProjects().getProject()
                .stream().filter(x -> x.getName().equals(projectName)).findFirst()
                .get().getGroup();
        List<User> result = new ArrayList<>();
        for (GroupSimple group : groupSimples) {
            for (User user : payload.getUsers().getUser()) {
                boolean isPresent = user.getGroup().stream().anyMatch(groupUser -> ((GroupSimple) groupUser.getValue()).getId().equals(group.getId()));
                if (isPresent) {
                    result.add(user);
                }
            }
        }

        return result.stream().distinct().sorted(Comparator.comparing(User::getFullName)).collect(toList());
    }

    public static List<String> getUsersByProjectWithXPath(String projectName) throws IOException {
        try (InputStream is =
                     Resources.getResource("payload.xml").openStream()) {
            XPathProcessor processor = new XPathProcessor(is);
            XPathExpression expression =
                    XPathProcessor.getExpression(
                            "/*[name()='Payload']/*[name()='Projects']/*[name()='Project'][@name='" +
                                    projectName + "']/*[name()='Group']");
            NodeList nodes = processor.evaluate(expression, XPathConstants.NODESET);
            List<String> groupsName = new ArrayList<>();
            //First fet the groups
            IntStream.range(0, nodes.getLength()).forEach(
                    i -> groupsName.add(nodes.item(i).getAttributes().getNamedItem("id").getNodeValue()));

            expression = XPathProcessor.getExpression("/*[name()='Payload']/*[name()='Users']/*[name()='User']");
            NodeList users = processor.evaluate(expression, XPathConstants.NODESET);

            // Second find Users with that Group
            List<String> result = new ArrayList<>();
            IntStream.range(0, users.getLength()).forEach(i ->
                    groupsName.forEach(group -> {
                        String resultString = "";
                        if (users.item(i).getTextContent().contains(group)) {
                            String[] str = users.item(i).getTextContent().trim().split("\n");
                            resultString += str[0].trim();
                            resultString += "\t" + users.item(i).getAttributes().getNamedItem("email");
                            result.add(resultString);
                        }
                    }));
            return result.stream().distinct().sorted(Comparator.comparing(String::toString)).collect(toList());
        }
    }

    public static void transformXmlToHtml(String projectName) {

        try (InputStream xslInputStream = Resources.getResource("groups.xsl").openStream();
             InputStream xmlInputStream = Resources.getResource("payload.xml").openStream();
             ) {

            XsltProcessor processor = new XsltProcessor(xslInputStream);

            processor.setParametr("projectName", projectName );
            String html = processor.transform(xmlInputStream);
            System.out.println(html);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

}
