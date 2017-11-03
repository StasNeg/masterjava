package ru.javaops.masterjava.service;

import ru.javaops.masterjava.DTO.UserTO;
import ru.javaops.masterjava.xml.schema.User;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;



public class UserService {

    public static List<UserTO> getUser(InputStream stream){
        List<UserTO> users = new ArrayList<>();
        try (StaxStreamProcessor processor =
                     new StaxStreamProcessor(stream)) {

            JaxbParser parser = new JaxbParser(User.class);
            while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {
                    User user = parser.unmarshal(processor.getReader(), User.class);
                    users.add(new UserTO(user.getValue(),user.getEmail(),user.getFlag().value()));
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return users;
    }
}
