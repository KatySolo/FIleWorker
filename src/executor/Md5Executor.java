package executor;

import worker.FileWorker;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Md5Executor implements IExecutable {

    private final String EMPTY_MD5_HASH = "d41d8cd98f00b204e9800998ecf8427e";
    private FileWorker fw;
    private HashMap<Path, String> all_files_hash = new HashMap<>();

    public Md5Executor(FileWorker fileWorker) {
        this.fw = fileWorker;
    }

    @Override
    public void process(File f) {
        byte[] buffer = new byte[1024];
        int numRead;
        if (!f.isFile()) {
            File[] inner_files = f.listFiles();
            if (!fw.getRecursive()) {
                formatOutput("<not seen>", f);
            } else if (inner_files != null) {
                if (inner_files.length == 0) {
                    all_files_hash.put(getRelativePath(f), EMPTY_MD5_HASH);
                    formatOutput(EMPTY_MD5_HASH, f);
                } else {
                    for (File inner : inner_files) {
                        process(inner);
                    }
                    formatOutput(countDirectoryCache(f), f);
                }
            } else {
                formatOutput(EMPTY_MD5_HASH, f);
            }
        } else if (!f.getPath().endsWith("DS_Store")) {
            try {
                InputStream is = new FileInputStream(f);
                MessageDigest md = MessageDigest.getInstance("MD5");
                do {
                    numRead = is.read(buffer);
                    if (numRead > 0) {
                        md.update(buffer, 0, numRead);
                    }
                } while (numRead != -1);

                is.close();
                StringBuilder result = new StringBuilder();

                for (byte aB : md.digest()) {
                    result.append(Integer.toString((aB & 0xff) + 0x100, 16).substring(1));
                }
                formatOutput(result.toString(), f);
                all_files_hash.put(getRelativePath(f), result.toString());
            } catch (NoSuchAlgorithmException | IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void formatOutput(String s, File f) {
        Path pathRelative = getRelativePath(f);
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter("/Users/KatySolo/IdeaProjects/file_worker/src/output/output.txt",true));
            String result = "/" + pathRelative + ": " + s;
            writer.append(result).append('\n');
            writer.close();
        } catch (FileNotFoundException e) {
            File file = new File("/Users/KatySolo/IdeaProjects/file_worker/src/output/output.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
//        System.out.println("/" + pathRelative + " --- " + s);
    }

    private Path getRelativePath(File f) {
        Path pathAbsolute = Paths.get(f.getPath());
        Path pathBase = Paths.get(fw.basicPath.getPath());
        return pathBase.relativize(pathAbsolute);
    }

    private String countDirectoryCache(File dir) {
        StringBuilder totalCache = new StringBuilder();
        Set<Path> all_keys = all_files_hash.keySet();
        ArrayList<Path> all_del_files = new ArrayList<>();
        String parent = null;
        String inner_parent;
        String relativePathDir = getRelativePath(dir).toString();
        for (Path key : all_keys) {

            Path dir_parents = key.getParent();
            inner_parent = (dir_parents != null) ? dir_parents.toString() : key.toString();
            if (inner_parent.startsWith(relativePathDir)) {
                totalCache.append(all_files_hash.get(key));
                all_del_files.add(key);
                if (parent == null) {
                    parent = inner_parent;
                }
            }
        }
        for (Path key : all_del_files) {
            all_files_hash.remove(key);
        }
        all_files_hash.put(Paths.get(parent + "/_"), totalCache.toString());
        return totalCache.toString();
    }
}
