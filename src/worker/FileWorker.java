package worker;

import executor.IExecutable;

import java.io.File;

public class FileWorker {
    private File path;
    public File basicPath;
    private boolean isRecursive = false;

    public FileWorker(String path) {
        this.path = new File(path);
        this.basicPath = new File(path);
    }

    public boolean getRecursive() {
        return isRecursive;
    }

    public void setRecursive(boolean flag) {
        isRecursive = flag;
    }

    public void execute(IExecutable command) {
        if (this.path.isFile()) {
            if (!this.path.getPath().endsWith("DS_Store")) {
                command.process(this.path);
            }
        } else {
            if (getRecursive()) {
                File[] all_subs = this.path.listFiles();
                if (all_subs != null && all_subs.length != 0) {
                    for (File file : all_subs) {
                        if (file.isFile()) {
                            if (!file.getPath().endsWith("DS_Store")) {
                                this.path = file;
                                command.process(file);
                            }
                        } else {
                            this.path = file;
                            command.process(file);
                        }
                    }
                } else {
                    command.process(this.path);
                }

            } else {
                File[] all_subs = this.path.listFiles();
                if (all_subs.length != 0) {
                    for (File file : all_subs) {
                        if (!file.getPath().endsWith("DS_Store")) {
                            this.path = file;
                            command.process(this.path);
                        }
                    }
                } else {
                    command.process(this.path);
                }
            }
        }
    }
}

