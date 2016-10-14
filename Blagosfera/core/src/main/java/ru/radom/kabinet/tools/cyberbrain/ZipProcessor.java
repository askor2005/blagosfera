package ru.radom.kabinet.tools.cyberbrain;

public interface ZipProcessor {
    public boolean compress(String sourceFileName, String targetFileName, boolean deleteSourceFile) throws Exception;
}
