package ru.javaops.masterjava.upload;

import ru.javaops.masterjava.persist.dao.UserDao;
import ru.javaops.masterjava.persist.model.DBIProvider;
import ru.javaops.masterjava.persist.model.User;
import java.util.List;
import java.util.concurrent.*;



public class UploadServiseExecutor {

    private static final String OK = "OK";
    private static final String DUPLICATE_EMAIL = "Users with duplicate email";
    private final static UserDao DAO = DBIProvider.getDao(UserDao.class);

    public static UploadResult uploadUsers(final List<User> usersUpload, ExecutorService uploadExecutor) {
        final CompletionService<UploadResult> completionService = new ExecutorCompletionService<>(uploadExecutor);
        Future<UploadResult> futures = completionService.submit(() -> uploadUser(usersUpload));
        return new Callable<UploadResult>() {
            private UploadResult result = new UploadResult();

            @Override
            public UploadResult call() {
                result.countTotal = usersUpload.size();
                try {
                    Future<UploadResult> future = completionService.poll(10, TimeUnit.SECONDS);
                    result = future.get();
                } catch (ExecutionException e) {
                    result.cause = e.getMessage();
                    return result;
                } catch (InterruptedException e) {
                    result.cause = e.getMessage();
                    return result;
                }
                return result;
            }
        }.call();
    }

    private static UploadResult uploadUser(List<User> users) throws Exception {
        UploadResult result = new UploadResult();
        DBIProvider.getDBI().useTransaction((conn, status) -> {
            int[] resultBatsh = DAO.insertBatch(users);
            if (users.size() == resultBatsh.length) {
                result.cause = OK;
            } else {
                result.cause = DUPLICATE_EMAIL;
            }
            result.countTotal = users.size();
            result.countUpload = resultBatsh.length;
        });
        return result;
    }

    public static class UploadResult {
        private int countUpload;
        private int countTotal;
        private String cause;

        public UploadResult() {
        }

        @Override
        public String toString() {
            return "UploadResult{" +
                    "countUpload=" + countUpload +
                    ", countTotal=" + countTotal +
                    ", cause='" + cause + '\'' +
                    '}';
        }
    }
}
