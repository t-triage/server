package com.clarolab.util;

import lombok.extern.java.Log;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Log
public class CompressUtils {

    public static File unZipAndRename(File file, String outputFolder, String newName) throws IOException  {
        return FileUtils.rename(unZip(file, outputFolder), newName);
    }

    public static File unZip(File file, String outputFolder) throws IOException {
        ZipFile zipFile;
        String folder = outputFolder.endsWith("/") ? outputFolder : outputFolder+"/";
        String outDirectory = outDirectory(file);
        zipFile = new ZipFile(file);
        Enumeration entries = zipFile.entries();
        while(entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry)entries.nextElement();

            if(entry.isDirectory()) {
                //log.log(Level.INFO, "Extracting directory: " + entry.getName());
                new File(( folder + entry.getName())).mkdirs();
                continue;
            }

            //log.log(Level.INFO, "Extracting file: " + entry.getName());
            copyInputStream(zipFile.getInputStream(entry), new BufferedOutputStream(new FileOutputStream(folder + entry.getName())));
        }
        zipFile.close();
        return new File(folder + outDirectory);
    }

    private static void copyInputStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int len;

        while((len = in.read(buffer)) >= 0)
            out.write(buffer, 0, len);

        in.close();
        out.close();
    }

    private static String outDirectory(File file) throws IOException{
        try {
            return (new ZipFile(file).entries().nextElement()).getName().split("/")[0];
        } catch (Exception e){
            throw new IOException("Unable to unzip the file, possible because is empty.", e);
        }
    }

}
