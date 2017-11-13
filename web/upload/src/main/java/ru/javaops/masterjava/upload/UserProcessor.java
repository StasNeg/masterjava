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
import java.util.concurrent.*;
import java.util.stream.Collectors;



public class UserProcessor {
    private final static UserDao dao = DBIProvider.getDao(UserDao.class);
    private static ExecutorService uploadExecutor;
    private static CompletionService<UploadResult> completionService;
    private static final String OK = "OK";
    private static final String DUPLICATE_EMAIL = "Users with duplicate email";
    private final static UserDao DAO = DBIProvider.getDao(UserDao.class);

    public List<String> process(final InputStream is, int chunkSize) throws XMLStreamException {
        final StaxStreamProcessor processor = new StaxStreamProcessor(is);
        uploadExecutor = Executors.newFixedThreadPool(8);
        completionService = new ExecutorCompletionService<>(uploadExecutor);
        List<User> users = new ArrayList<>();
        List<Future<UploadResult>> futures = new ArrayList<>();
        Map<Integer, UploadResult> uploadResultHashMap = new HashMap<>();

        while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {
            final String email = processor.getAttribute("email");
            final UserFlag flag = UserFlag.valueOf(processor.getAttribute("flag"));
            final String fullName = processor.getReader().getElementText();
            final User user = new User(fullName, email, flag);
            users.add(user);

            if (users.size() == chunkSize) {
                futures.add(uploadUsers(new ArrayList<>(users)));
                users.clear();
            }
        }
        if (users.size() > 0) {
            futures.add(uploadUsers(new ArrayList<>(users)));

        }
        List<String> results = loadResults(futures).stream().filter(uploadResult -> !uploadResult.isOK).map(UploadResult::toString).collect(Collectors.toList());
        uploadExecutor.shutdown();
        return results;
    }

    public static Future<UploadResult> uploadUsers(final List<User> usersUpload) {
        return completionService.submit(() -> saveToDataBase(usersUpload));
    }

    public static List<UploadResult> loadResults(List<Future<UploadResult>> futures) {
        List<UploadResult> result = new ArrayList<>();
        while (!futures.isEmpty()) {
            try {
                Future<UploadResult> future = completionService.poll(10, TimeUnit.SECONDS);
                result.add(future.get());
                futures.remove(future);
            } catch (ExecutionException | InterruptedException e) {
                throw new IllegalStateException("ExecutionException | InterruptedException");
            }
        }
        return result;
    }

    private static UploadResult saveToDataBase(List<User> users) throws Exception {
        UploadResult result = new UploadResult();
        int[] resultBatsh = DAO.insertBatch(users);
        if (users.size() == resultBatsh.length) {
            result.isOK = true;
            result.cause = OK;
        } else {
            result.emails = users.stream().map(User::getEmail).collect(Collectors.joining(" : "));
            result.isOK = false;
            result.cause = DUPLICATE_EMAIL;
        }
        return result;
    }

    private static class UploadResult {
        private boolean isOK;
        String emails;
        private String cause;

        @Override
        public String toString() {
            return "Bad upload{" +
                    "emails(starts|end)='" + emails + '\'' +
                    ", cause='" + cause + '\'' +
                    '}';
        }
    }
}
