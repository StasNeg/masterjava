package ru.javaops.masterjava.upload;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import ru.javaops.masterjava.persist.dao.CityDao;
import ru.javaops.masterjava.persist.dao.TypeOfErrors;
import ru.javaops.masterjava.persist.dao.UserDao;
import ru.javaops.masterjava.persist.model.City;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.model.User;
import ru.javaops.masterjava.persist.model.UserFlag;
import ru.javaops.masterjava.xml.schema.CityType;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Slf4j
public class UserProcessor {
    private static final int NUMBER_THREADS = 4;

    private static final JaxbParser jaxbParser = new JaxbParser(ObjectFactory.class);
    private static UserDao userDao = DBIProvider.getDao(UserDao.class);
    private static CityDao cityDao = DBIProvider.getDao(CityDao.class);
    private ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_THREADS);

    @AllArgsConstructor
    public static class FailedEmails {
        public String emailsOrRange;
        public String reason;

        @Override
        public String toString() {
            return emailsOrRange + " : " + reason;
        }
    }

    /*
     * return failed users chunks
     */
    public List<FailedEmails> process(final InputStream is, int chunkSize) throws XMLStreamException, JAXBException {
        log.info("Start processing with chunkSize=" + chunkSize);

        Map<String, Future<Map<TypeOfErrors,List<String>>>> chunkFutures = new LinkedHashMap<>();  // ordered map (emailRange -> chunk future)

        int id = userDao.getSeqAndSkip(chunkSize);
        List<User> chunk = new ArrayList<>(chunkSize);
        List<City> cities = new ArrayList<>();
        val processor = new StaxStreamProcessor(is);
        val unmarshaller = jaxbParser.createUnmarshaller();
        XMLStreamReader reader = processor.getReader();
        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLEvent.START_ELEMENT) {
                if ("City".equals(reader.getLocalName())) {
                    CityType xmlCity = unmarshaller.unmarshal(processor.getReader(), CityType.class);
                    cities.add(new City(xmlCity.getValue(), xmlCity.getId()));
                }
                else if("Users".equals(reader.getLocalName())){
                    break;
                }
            }
        }
        cityDao.insertBatch(cities, cities.size());
        while (processor.doUntil(XMLEvent.START_ELEMENT, "User") ) {
            String city = processor.getAttribute("city");
            ru.javaops.masterjava.xml.schema.User xmlUser = unmarshaller.unmarshal(processor.getReader(), ru.javaops.masterjava.xml.schema.User.class);
            final User user = new User(id++, xmlUser.getValue(), xmlUser.getEmail(), UserFlag.valueOf(xmlUser.getFlag().value()), city);
            chunk.add(user);
            if (chunk.size() == chunkSize) {
                addChunkFutures(chunkFutures, chunk);
                chunk = new ArrayList<>(chunkSize);
                id = userDao.getSeqAndSkip(chunkSize);
            }
        }
        if (!chunk.isEmpty()) {
            addChunkFutures(chunkFutures, chunk);
        }
        List<FailedEmails> failed = new ArrayList<>();
        chunkFutures.forEach((emailRange, future) -> {
            try {
                Map<TypeOfErrors,List<String>> failedEmails = future.get();
                log.info("{} successfully executed with already presents: {}", emailRange, failedEmails);
                failed.addAll(failedEmails.entrySet().stream().map(x->new FailedEmails(x.getValue().toString(),x.getKey().toString())).collect(Collectors.toList()));
            } catch (InterruptedException | ExecutionException e) {
                log.error(emailRange + " failed", e);
                failed.add(new FailedEmails(emailRange, e.toString()));
            }
        });
        return failed;
    }

    private void addChunkFutures(Map<String, Future<Map<TypeOfErrors,List<String>>>> chunkFutures, List<User> chunk) {
        String emailRange = String.format("[%s-%s]", chunk.get(0).getEmail(), chunk.get(chunk.size() - 1).getEmail());
        Future<Map<TypeOfErrors,List<String>>> future = executorService.submit(() -> userDao.insertAndGetWrongCityAndConflictEmails(chunk));
        chunkFutures.put(emailRange, future);
        log.info("Submit chunk: " + emailRange);
    }
}
