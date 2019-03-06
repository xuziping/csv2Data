import utils.CSVReader;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author za-xuzhiping
 * @Date 2019/3/5
 * @Time 17:43
 */
public class DataFileBuilder {

    public static void build(CSVReader reader, String outputFileName, String keyDefine, String keyMapping, String saveItem, String lineNum, int byteNum, int scale) throws Exception {
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File(outputFileName + ".bin")));
        DataOutputStream dos2 = new DataOutputStream(new FileOutputStream(new File(outputFileName + ".map")));
        String[] item = saveItem.split(",");

        String lastKey = null;
        int add = 0;

        List list = new ArrayList();

        list.add("1024|" + byteNum + ";" + scale + "|" + item.length + "|\n");

        try {
            int max = 0;
            while (reader.hasNext()) {
                String[] line = reader.next();

                if (line.length == 0){
                    continue;
                }

                if (line.length <= max) {
                    String[] kk = new String[max];
                    for (int j = 0; j < line.length; j++) {
                        kk[j] = line[j];
                    }
                    line = kk;
                }

                boolean con = false;
                for (int j = 0; j < item.length; j++) {
                    int x = Integer.parseInt(item[j]);
                    if (max < x + 1) {
                        max = x + 1;
                    }
                    if (line[x] == null || "".equals(line[x].trim())) {
                        System.out.println("line[" + x + "] is null, set to 0.");
                        line[x] = "0";
                    }
                }

                if (con) {
                    continue;
                }

                String key;
                    String[] keyMp = keyMapping.split(";");
                    String[] keyDf = keyDefine.split(",");
                    String result = "";
                    for (int i = 0; i < keyMp.length; i++) {
                        String addKey = (i == keyMp.length - 1) ? "" : ",";
                        if (keyMp[i].indexOf(",") < 0) {
                            boolean addon = true;

                            int pos = Integer.parseInt(keyMp[i]);
                            if ("GENDER".equals(keyDf[i]) || "GENDER_CODE".equals(keyDf[i])) {
                                if ("M".equalsIgnoreCase(line[pos])) {
                                    line[pos] = "1";
                                }  else if ("F".equalsIgnoreCase(line[pos])){
                                    line[pos] = "2";
                                }


                                else if ("0".equals(line[pos]))
                                    line[pos] = "1";
                                else if ("1".equals(line[pos]))
                                    line[pos] = "2";

                                if (!"1".equals(line[pos])
                                        && !"2".equals(line[pos])) {
                                    addon = false;
                                }
                            } else if ("SMOKE".equals(keyDf[i])) {
                                if ("Y".equals(line[pos]))
                                    line[pos] = "1";
                                else if ("N".equals(line[pos]))
                                    line[pos] = "2";
                                else if ("W".equals(line[pos]))
                                    line[pos] = "SMOKE_FLAG_W";
                                else if (!"1".equals(line[pos])
                                        && !"2".equals(line[pos])) {
                                    addon = false;
                                }
                            } else if ("PAY_VALUE".equals(keyDf[i])) {
                                if ("1000".equals(line[pos]))
                                    line[pos] = "1";
                                else if ("DRAW".equalsIgnoreCase(line[pos])) {
                                    for (int j = 0; j < keyMp.length; j++) {
                                        if ("DRAW_AGE".equals(keyDf[j])) {
                                            line[pos] = line[Integer.parseInt(keyMp[j])];
                                            System.out.println("SET DRAW = " + line[pos]);
                                            break;
                                        }
                                    }
                                }
                            }

                            if (addon)
                                result += line[pos] + addKey;
                            else {
                                keyDefine = "";
                                keyMapping = "";
                                for (int k = 0; k < keyMp.length; k++) {
                                    if (k != i) {
                                        keyDefine += keyDf[k] + ",";
                                        keyMapping += keyMp[k] + ";";
                                    }
                                }
                                keyDefine = cutToken(keyDefine);
                                keyMapping = cutToken(keyMapping);
                            }
                        } else {
                            String[] key1 = keyMp[i].split(",");
                            int totalTemp = 0;
                            for (int j = 0; j < key1.length; j++) {
                                totalTemp += Integer.parseInt(line[Integer
                                        .parseInt(key1[j])]);
                            }
                            result += totalTemp + addKey;
                        }
                    }
                    key = cutToken(result);

                if (!key.equals(lastKey)) {
                    if (key.indexOf("SMOKE_FLAG_W") >= 0) {
                        list.add(key.replaceAll("SMOKE_FLAG_W", "1") + ":"
                                + add + ";" + "\n");
                        list.add(key.replaceAll("SMOKE_FLAG_W", "2") + ":"
                                + add + ";" + "\n");
                    } else {
                        list.add(key + ":" + add + ";" + "\n");
                    }
                    lastKey = key;
                }
                add++;
                // total++;
                for (int j = 0; j < item.length; j++) {
                    byte[] ch = getChar(line[Integer.parseInt(item[j])],
                            byteNum, scale);
                    dos.write(ch);
                }


            }

            list.add(1, keyDefine + ";0;" + lineNum + "|\n");
            for (int i = 0; i < list.size(); i++) {
                dos2.writeBytes((String) list.get(i));
            }
        } finally {
            dos.close();
            dos2.close();
        }
    }

    private static String cutToken(String str) {
        if (str != null && (str.endsWith(",") || str.endsWith(";"))) {
            return str.substring(0, str.length() - 1);
        }
        return str;
    }

    private static byte[] getChar(String value, int num, int scale) {
        return getChar(Double.parseDouble(value), num, scale);
    }

    private static byte[] getChar(double value, int num, int scale) {
        long v = (long) (value * scale + 0.5);
        byte[] result = new byte[num];

        for (int i = 0; i < num; i++) {
            result[i] = (byte) (v % 256);
            v = v / 256;
        }
        return result;
    }
}
