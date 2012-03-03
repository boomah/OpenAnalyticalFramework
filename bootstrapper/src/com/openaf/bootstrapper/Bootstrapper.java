package com.openaf.bootstrapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Proxy;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class Bootstrapper {
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Usage: javaw -jar bootstrapper.jar <serverURL> <instanceName>");
            Thread.sleep(10 * 1000);
            System.exit(1);
        }

        URL url = new URL(args[0]);
        String instanceName = args[1];
        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        File rootCacheDir = new File(tmpDir, "openaf");
        if (!rootCacheDir.exists()) rootCacheDir.mkdir();

        String cacheDirName = instanceName + "-" + url.getHost() + (url.getPort() == 80 ? "" : ("-" + url.getPort())) +
                (url.getPath().equals("") ? "" : "-" + url.getPath()).replaceAll("/", "-");
        File cacheDir = new File(rootCacheDir, cacheDirName);
        if (!cacheDir.exists()) cacheDir.mkdir();
        clearOldLogFiles(cacheDir);
        Date currentTime = new Date();
        String currentTimeString = new SimpleDateFormat("HH-mm-ss--dd-MM-yyyy").format(currentTime);
        File logFile = new File(cacheDir, "__log-" + currentTimeString + ".txt");
        System.setOut(new java.io.PrintStream(new TeeOutputStream(System.out, new FileOutputStream(logFile))));
        System.setErr(new java.io.PrintStream(new TeeOutputStream(System.err, new FileOutputStream(logFile))));
        Proxy proxy = Proxy.NO_PROXY;

    }

    private static void clearOldLogFiles(File dir) {
        File[] files = dir.listFiles();
        ArrayList<File> logFiles = new ArrayList<File>();
        for (File file : files) {
            String fileName = file.getName().toLowerCase();
            if (fileName.contains("_log") && fileName.endsWith(".txt")) {
                logFiles.add(file);
            }
        }
        int maxLogFiles = 5;
        if (logFiles.size() > maxLogFiles) {
            Collections.sort(logFiles, new Comparator<File>() {
                @Override
                public int compare(File file1, File file2) {
                    if (file1.lastModified() < file2.lastModified()) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            });
            int numberToRemove = logFiles.size() - maxLogFiles;
            for (int i = 0; i < numberToRemove; i++) {
                File logFile = logFiles.get(i);
                logFile.delete();
            }
        }
        for (File logFile : logFiles) {
            if (logFile.getName().startsWith("__log")) {
                File newFile = new File(logFile.getParent(), logFile.getName().replaceFirst("_", ""));
                logFile.renameTo(newFile);
            }
        }
    }
}

class TeeOutputStream extends OutputStream {
    private OutputStream a;
    private OutputStream b;

    public TeeOutputStream(OutputStream a, OutputStream b) {
        this.a = a;
        this.b = b;
    }

    public void write(int c) throws IOException {
        a.write(c);
        b.write(c);
    }

    public void flush() throws IOException {
        a.flush();
        b.flush();
    }

    @Override
    public void close() throws IOException {
        flush();
        a.close();
        b.close();
    }
}