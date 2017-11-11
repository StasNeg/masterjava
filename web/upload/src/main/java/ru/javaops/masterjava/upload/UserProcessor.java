package ru.javaops.masterjava.upload;

import ru.javaops.masterjava.persist.dao.UserDao;
import ru.javaops.masterjava.persist.model.DBIProvider;
import ru.javaops.masterjava.persist.model.User;
import ru.javaops.masterjava.persist.model.UserFlag;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static ru.javaops.masterjava.upload.UploadServiseExecutor.uploadUsers;

public class UserProcessor {
    private final static UserDao dao = DBIProvider.getDao(UserDao.class);
    private static ExecutorService uploadExecutor;

    public List<String> process(final InputStream is, int chunkSize) throws XMLStreamException {
        final StaxStreamProcessor processor = new StaxStreamProcessor(is);
        uploadExecutor = Executors.newFixedThreadPool(8);
        List<User> users = new ArrayList<>();
        Map<Integer, UploadServiseExecutor.UploadResult> uploadResultHashMap = new HashMap<>();
        int chunkCounter = 0;
        while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {
            final String email = processor.getAttribute("email");
            final UserFlag flag = UserFlag.valueOf(processor.getAttribute("flag"));
            final String fullName = processor.getReader().getElementText();
            final User user = new User(fullName, email, flag);
            users.add(user);
            if (users.size() == chunkSize) {
                uploadResultHashMap.put(chunkCounter, uploadUsers(users, uploadExecutor));
                chunkCounter++;
                users.clear();
            }
        }
        if (users.size() > 0) {
            uploadResultHashMap.put(chunkCounter, uploadUsers(users, uploadExecutor));
        }
        uploadExecutor.shutdown();
        return uploadResultHashMap.entrySet().stream()
                .map(x -> "Chunk: " + x.getKey() + "\tAdded:" + x.getValue())
                .collect(Collectors.toList());

    }

}
