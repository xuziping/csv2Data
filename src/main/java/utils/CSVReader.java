package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * @author za-xuzhiping
 * @Date 2019/3/5
 * @Time 16:35
 */
public class CSVReader implements AutoCloseable {

    String line = null;
    String title = null;
    BufferedReader dis = null;
    private File file;

    public CSVReader(File file) {
        this.file = file;
    }

    public void open() throws Exception {
        if (dis != null) {
            throw new Exception("File has opened: " + file.getAbsolutePath());
        }

        dis = new BufferedReader(new InputStreamReader(new FileInputStream(
                file)));
        read();
        title = line;
        line = null;
    }

    @Override
    public void close() throws Exception {
        if (dis != null) {
            try {
                dis.close();
            } catch (Exception e) {
                throw e;
            } finally {
                dis = null;
            }
        }
    }

    public boolean hasNext() {
        boolean next = false;
        read();
        if (line != null) {
            next = true;
        }
        return next;
    }

    private void read() {
        try {
            line = dis.readLine();
        } catch (Exception e) {
            line = null;
        }
    }

    public String[] getTitle() {
        if (title == null) {
            return null;
        }
         return split(title);
    }

    public String[] next() {
        if (line == null) {
            read();
        }
        if (line != null) {
            String[] next = split(line);
            line = null;
            return next;
        }
        return null;
    }

    private String[] split(String str) {
        int index1 = 0, index2;
        while (index1 >= 0) {
            index1 = str.indexOf("\"");
            if (index1 < 0) {
                break;
            }
            index2 = str.indexOf("\"", index1 + 1);
            str = str.substring(0, index1)
                    + str.substring(index1 + 1, index2).replaceAll(",", "")
                    + str.substring(index2 + 1);
        }
        return str.split(",");
    }
}
