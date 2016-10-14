/*
package activiti;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

*/
/**
 * Created by Otts Alexey on 02.11.2015.<br/>
 * Класс через который запускается приложение
 *//*

public class Application {

    public static void main( final String[] args ) throws IOException {
        File file = new File("blagosfera-bp.pid");
        Path path = file.toPath();
        if(file.exists()) {
            String pid = new String(Files.readAllBytes(path), Charset.defaultCharset());
            Runtime rt = Runtime.getRuntime();
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                rt.exec("taskkill /f /PID " + pid);
            } else {
                rt.exec("kill -9 " + pid);
            }
            Files.delete(path);
        }
        String name = ManagementFactory.getRuntimeMXBean().getName();
        String pid = name.split("@")[0];
        Files.write(path, pid.getBytes(Charset.defaultCharset()));
        ClassPathXmlApplicationContext applicationContext;
        try {
            applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        } catch (Throwable t) {
            Files.delete(path);
            throw t;
        }
    }
}
*/
