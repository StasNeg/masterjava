package ru.javaops.masterjava.upload;

import lombok.val;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.CityDao;
import ru.javaops.masterjava.persist.model.City;
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
import java.util.List;


public class CityProcessor {
    private static final JaxbParser jaxbParser = new JaxbParser(ObjectFactory.class);
    private static CityDao cityDao = DBIProvider.getDao(CityDao.class);

    public StaxStreamProcessor process(InputStream is) throws XMLStreamException, JAXBException {
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
        return processor;
    }
}
