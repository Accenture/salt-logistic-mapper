package de.salt.sce.util;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class TextFileDetector {


    public static boolean isText(String fileContent) throws Exception {
        if(fileContent.length() == 0)  {
            return true;
        }

        if(fileContent.startsWith("%PDF-")) {
            return false;
        }

        int size = fileContent.length();
        if (size > 1000)
            size = 1000;

        String s = fileContent.substring(0,size);
        String s2 = s.replaceAll(
                "[a-zA-Z0-9ßöäüÄÖÜ\\.\\*!\"§\\$\\%&/()=\\?@~'#:,;\\" +
                        "+><\\|\\[\\]\\{\\}\\^°²³\\\\ \\n\\r\\t_\\-`´âêîô" +
                        "ÂÊÔÎáéíóàèìòÁÉÍÓÀÈÌÒ©‰¢£¥€±¿»«¼½¾™ª]", "");
        // will delete all text signs

        double d = (double) (s.length() - s2.length()) / (double) (s.length());
        // percentage of text signs in the text
        return d > 0.95;
    }
}
