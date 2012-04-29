package com.openaf.bootstrapper;

import java.io.*;
import java.lang.reflect.Method;
import java.net.Proxy;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.*;
import java.text.SimpleDateFormat;
import java.util.*;

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
        URL configURL = new URL(url + "/gui/config.txt");

        List<String> configLines = readLines(openConnection(configURL, proxy));
        String[] mainClassAndArgs = configLines.get(0).split(" ");
        String mainClass = mainClassAndArgs[0];
        String[] configArgsArray = Arrays.copyOfRange(mainClassAndArgs, 1, mainClassAndArgs.length);
        String[] argsToPassToGUI = new String[configArgsArray.length + 2];
        argsToPassToGUI[0] = url.toExternalForm();
        argsToPassToGUI[1] = instanceName;
        System.arraycopy(configArgsArray, 0, argsToPassToGUI, 2, configArgsArray.length);

        List<String> latestJARLines = configLines.subList(1, configLines.size());
        Map<String,String> latestJARsToMD5 = jarsWithMD5(latestJARLines);
        File localConfigFile = new File(cacheDir, "config.txt");
        Map<String, String> localJARsToMD5 = new HashMap<String,String>();
        if (localConfigFile.exists()) {
            List<String> allLocalConfigLines = readLines(new FileInputStream(localConfigFile));
            List<String> localConfigLines = allLocalConfigLines.subList(1, allLocalConfigLines.size());
            localJARsToMD5 = jarsWithMD5(localConfigLines);
        }

        Map<String, String> missingOrOutOfDateJARs = new HashMap<String, String>();
        for (Map.Entry<String, String> entry : latestJARsToMD5.entrySet()) {
            String jar = entry.getKey();
            String md5 = entry.getValue();
            if (!localJARsToMD5.containsKey(jar) || !md5.equals(localJARsToMD5.get(jar))) {
                missingOrOutOfDateJARs.put(jar, md5);
            }
        }

        if (missingOrOutOfDateJARs.size() > 0) {
            for (String jarName : missingOrOutOfDateJARs.keySet()) {
                String md5 = missingOrOutOfDateJARs.get(jarName);
                URL jarURL = new URL(url + "/gui/" + jarName + "?md5=" + md5);
                InputStream jarInputStream = openConnection(jarURL, proxy);
                File localJARFile = new File(cacheDir, jarName);
                BufferedOutputStream localJAROutputStream = new BufferedOutputStream(new FileOutputStream(localJARFile));
                copyStreams(jarInputStream, localJAROutputStream);
                jarInputStream.close();
                localJAROutputStream.flush();
                localJAROutputStream.close();
            }
        }

        FileWriter localConfigFileWriter = new FileWriter(localConfigFile);
        String ls = System.getProperty("line.separator");
        for (String configLine : configLines) {
            localConfigFileWriter.write(configLine + ls);
        }
        localConfigFileWriter.close();

        Set<String> jarsToRemove = new HashSet<String>(localJARsToMD5.keySet());
        jarsToRemove.removeAll(latestJARsToMD5.keySet());
        for (String jarName : jarsToRemove) {
            new File(cacheDir, jarName).delete();
        }

        List<URL> urlsOfLatestJARs = new LinkedList<URL>();
        for (String jarName : latestJARsToMD5.keySet()) {
            urlsOfLatestJARs.add(new File(cacheDir, jarName).toURI().toURL());
        }
        URL javaFXURL = new File("/Library/Java/JavaVirtualMachines/1.7.0.jdk/Contents/Home/jre/lib/jfxrt.jar").toURI().toURL();
        urlsOfLatestJARs.add(javaFXURL);

        URLClassLoader urlClassLoader = new URLClassLoader(urlsOfLatestJARs.toArray(new URL[urlsOfLatestJARs.size()]));
        Policy.setPolicy(new Policy() {
            public PermissionCollection getPermissions(CodeSource codesource) {
                Permissions permissions = new Permissions();
                permissions.add(new AllPermission());
                return permissions;
            }
            public void refresh() {}
        });
        Thread.currentThread().setContextClassLoader(urlClassLoader);
        Class launcher = urlClassLoader.loadClass(mainClass);
        Method mainMethod = launcher.getMethod("main", new Class[]{String[].class});
        mainMethod.invoke(null, new Object[]{argsToPassToGUI});
    }

    private static InputStream openConnection(URL url, Proxy proxy) throws Exception {
        return url.openConnection(proxy).getInputStream();
    }

    private static long copyStreams(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024];
        long count = 0;
        int n;
        while ((n = input.read(buffer)) != -1) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    private static List<String> readLines(InputStream in) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        List<String> lines = new LinkedList<String>();
        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
        in.close();
        return lines;
    }

    private static Map<String,String> jarsWithMD5(List<String> lines) {
        Map<String, String> jarsToMd5s = new HashMap<String, String>();
        for (String line : lines) {
            String[] components = line.split(" ");
            jarsToMd5s.put(components[0], components[1]);
        }
        return jarsToMd5s;
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