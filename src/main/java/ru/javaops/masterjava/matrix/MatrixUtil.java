package ru.javaops.masterjava.matrix;

import java.util.*;
import java.util.concurrent.*;

public class MatrixUtil {

    public static int[][] concurrentMultiply(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws Exception {
        class Task {
            private int part;
            private int[][] parts;

            private Task(int part, int[][] parts) {
                this.part = part;
                this.parts = parts;
            }

            private int getPart() {
                return part;
            }

            private int[][] getParts() {
                return parts;
            }
        }
        final CompletionService<Task> completionService = new ExecutorCompletionService<>(executor);
        final int matrixSize = matrixA.length;
        final int partsNumber = 8;
        final int partMain = matrixSize / partsNumber;
        List<Future<Task>> futures = new ArrayList<>();
        for (int i = 0; i < partsNumber; i++) {
            int finalI = i;
            futures.add(completionService.submit(() -> new Task(finalI, partMultiply(matrixA, matrixB, partMain * finalI, partMain * finalI + partMain))));
        }
        return ((Callable<Task>) () -> {
            Task tasks = new Task(0, new int[matrixSize][matrixSize]);
            int[][] matrixC = new int[matrixSize][matrixSize];
            while (!futures.isEmpty()) {
                Future<Task> future = completionService.poll(10, TimeUnit.SECONDS);
                if (future == null) {
                    throw new Exception("TimeOut");
                }
                futures.remove(future);
                Task task = future.get();
                final int start = task.getPart() * partMain;
                final int end = start + partMain;
                final int[][] partsFromTask = task.getParts();
                for (int j = 0; j < matrixSize; j++) {
                    System.arraycopy(partsFromTask[j], start, matrixC[j], start, end - start);
                }
            }
            tasks.parts = matrixC;
            return tasks;
        }).call().getParts();
    }

    public static int[][] singleThreadMultiplyBasicVersion(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                int sum = 0;
                for (int k = 0; k < matrixSize; k++) {
                    sum += matrixA[i][k] * matrixB[k][j];
                }
                matrixC[i][j] = sum;
            }
        }
        return matrixC;
    }


    public static int[][] singleThreadMultiply(int[][] matrixA, int[][] matrixB) {
        return partMultiply(matrixA, matrixB, 0, matrixA.length);
    }

    // TODO optimize by https://habrahabr.ru/post/114797/
    public static int[][] partMultiply(int[][] matrixA, int[][] matrixB, int start, int end) {        
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];
        final int thatColumn[] = new int[matrixSize];
        for (int i = start; i < end; i++) {
            for (int j = 0; j < matrixSize; j++) {
                thatColumn[j] = matrixB[j][i];
            }
            for (int j = 0; j < matrixSize; j++) {
                int sum = 0;
                int thisRow[] = matrixA[j];
                for (int k = 0; k < matrixSize; k++) {
                    sum += thisRow[k] * thatColumn[k];
                }
                matrixC[j][i] = sum;
            }
        }
        return matrixC;
    }

    public static int[][] create(int size) {
        int[][] matrix = new int[size][size];
        Random rn = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = rn.nextInt(10);
            }
        }
        return matrix;
    }

    public static boolean compare(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (matrixA[i][j] != matrixB[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
}
