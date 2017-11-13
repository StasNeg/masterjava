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

    private static ExecutorService uploadExecutor;
    //    private static CompletionService<UploadResult> completionService;
    private static final String OK = "OK";
    private static final String DUPLICATE_EMAIL = "Users with duplicate email";
    private static final String EXCEPTIONS_CAUSE = "TimeoutException | InterruptedException | ExecutionException caused";
    private final static UserDao DAO = DBIProvider.getDao(UserDao.class);

    public List<String> process(final InputStream is, int chunkSize) throws XMLStreamException {
        final StaxStreamProcessor processor = new StaxStreamProcessor(is);
        uploadExecutor = Executors.newFixedThreadPool(8);
//        completionService = new ExecutorCompletionService<>(uploadExecutor);
        List<User> users = new ArrayList<>();
        List<Future<UploadResult>> futures = new ArrayList<>();
        Map<Integer, List<User>> chunks = new HashMap<>();
        int chunkCounter = 0;
        while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {
            final String email = processor.getAttribute("email");
            final UserFlag flag = UserFlag.valueOf(processor.getAttribute("flag"));
            final String fullName = processor.getReader().getElementText();
            final User user = new User(fullName, email, flag);
            users.add(user);
            if (users.size() == chunkSize) {
                chunkCounter = getChunkCounter(users, futures, chunks, chunkCounter);
            }
        }
        if (users.size() > 0) {
            getChunkCounter(users, futures, chunks, chunkCounter);
        }
        List<String> results = loadResults(futures, chunks).stream().filter(uploadResult -> !uploadResult.isOK).map(UploadResult::toString).collect(Collectors.toList());
        uploadExecutor.shutdown();
        return results;
    }

    private int getChunkCounter(List<User> users, List<Future<UploadResult>> futures, Map<Integer, List<User>> chunks, int chunkCounter) {
        List<User> currentChunk = new ArrayList<>(users);
        futures.add(uploadUsers(currentChunk));
        chunks.put(chunkCounter++, currentChunk);
        users.clear();
        return chunkCounter;
    }

    public static Future<UploadResult> uploadUsers(final List<User> usersUpload) {
        return uploadExecutor.submit(() -> saveToDataBase(usersUpload));
    }

    public static List<UploadResult> loadResults(List<Future<UploadResult>> futures, Map<Integer, List<User>> chunks) {
        List<UploadResult> result = new ArrayList<>();
        int length = futures.size();
        for (int i = 0; i < length; i++) {
            try {
                UploadResult future = futures.get(i).get(10, TimeUnit.SECONDS); //completionService.poll(10, TimeUnit.SECONDS);
                result.add(future);
            } catch (TimeoutException | InterruptedException | ExecutionException e) {
                UploadResult exeption = new UploadResult();
                exeption.isOK = false;
                exeption.notAddingUser = chunks.get(i);
                exeption.cause = EXCEPTIONS_CAUSE;
                result.add(exeption);
            }
        }
        return result;
    }

    private static UploadResult saveToDataBase(List<User> users) throws Exception {
        UploadResult result = new UploadResult();
        int[] resultBatsh = DAO.insertBatch(users);
        for (int i = 0; i < resultBatsh.length; i++) {
            if (resultBatsh[i] == 0) {
                result.isOK = false;
                result.notAddingUser.add(users.get(i));
                result.cause = DUPLICATE_EMAIL;
            }
        }
        return result;
    }

    private static class UploadResult {
        private boolean isOK = true;
        List<User> notAddingUser = new ArrayList<>();
        private String cause = OK;

        @Override
        public String toString() {
            return "UploadResult{" +
                    "Users = " + notAddingUser.stream().map(user -> "[Name " + user.getFullName() + " : email " +
                    user.getEmail() + "]").collect(Collectors.joining(" || ")) +
                    " CAUSE='" + cause.toUpperCase() + '\'' +
                    '}';
        }
    }
}
