package ru.radom.kabinet.tools.cyberbrain;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class ZipProcessorImpl implements ZipProcessor {
    @Override
    public boolean compress(String sourceFileName, String targetFileName, boolean deleteSourceFile) throws Exception {
        FileOutputStream os = new FileOutputStream(targetFileName);
        ZipOutputStream zos = new ZipOutputStream(os);

        put(zos, new File(sourceFileName));

        zos.flush();
        zos.close();

        if (deleteSourceFile) {
            // Удаляем временный файл
            File f = new File(sourceFileName);
            f.delete();
        }

        return true;
    }

    private void put(ZipOutputStream zos, File file) throws Exception {
        byte buffer[] = new byte[8192];

        zos.putNextEntry(new ZipEntry(file.getName()));
        FileInputStream fin = new FileInputStream(file);

        int read = 0;
        while ((read = fin.read(buffer)) > 0) {
            zos.write(buffer, 0, read);
        }

        fin.close();
        zos.closeEntry();
        zos.flush();
    }
}
