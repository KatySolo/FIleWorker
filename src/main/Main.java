package main;

import executor.Md5Executor;
import worker.FileWorker;

public class Main {
    public static void main(String[] args) {
        FileWorker fw = new FileWorker("/Users/KatySolo/IdeaProjects/file_worker/src/test_path/animals");
        fw.setRecursive(true);
        fw.execute(new Md5Executor(fw));
    }
}
