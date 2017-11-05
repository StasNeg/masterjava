package ru.javaops.masterjava.service;

import ru.javaops.masterjava.dto.UserTO;
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
    private final static JaxbParser PARSER = new JaxbParser(User.class);

    public static List<UserTO> getUsers(InputStream stream) {
        List<UserTO> users = new ArrayList<>();
        try (StaxStreamProcessor processor = new StaxStreamProcessor(stream)) {
            while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {
                User user = PARSER.unmarshal(processor.getReader(), User.class);
                    users.add(new UserTO(user.getValue(),user.getEmail(),user.getFlag().value()));
            }
        } catch (XMLStreamException | JAXBException e) {
            e.printStackTrace();
        }
        return users;
    }
}
