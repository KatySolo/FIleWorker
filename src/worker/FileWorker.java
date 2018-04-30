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

    public String execute(IExecutable command) {
        StringBuilder hash = new StringBuilder();
        if (this.path.isFile()) {
            if (!this.path.getPath().endsWith("DS_Store")) {
                hash.append(command.process(this.path));
            }
        } else {
            if (getRecursive()) {
                File[] all_subs = this.path.listFiles();
                if (all_subs != null && all_subs.length != 0) {
                    for (File file : all_subs) {
                        if (file.isFile()) {
                            if (!file.getPath().endsWith("DS_Store")) {
                                this.path = file;
                                hash.append(command.process(file));
                            }
                        } else {
                            this.path = file;
                            hash.append(command.process(file));
                        }
                    }
                } else {
                    hash.append(command.process(this.path));
                }

            } else {
                File[] all_subs = this.path.listFiles();
                if (all_subs.length != 0) {
                    for (File file : all_subs) {
                        if (!file.getPath().endsWith("DS_Store")) {
                            this.path = file;
                            hash.append(command.process(this.path));
                        }
                    }
                } else {
                    hash.append(command.process(this.path));
                }
            }
        }
        return hash.toString();
    }

    public void concretizePath(String filename) {
        this.path = new File(path+"/"+filename);
        this.basicPath = new File(path+"/"+filename);
    }

//    public void concretizePath(String filename) {
//        this.path = new File(newPath);
//        this.basicPath = new File(newPath);
//    }
}

