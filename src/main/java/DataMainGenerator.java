import org.apache.commons.lang3.StringUtils;
import utils.CSVReader;

import java.io.File;

/**
 * @author za-xuzhiping
 * @Date 2019/3/5
 * @Time 16:35
 */
public class DataMainGenerator {

    private File csvFolder;

    private String id;

    public DataMainGenerator(String id, File csvFolder) {
        this.id = id;
        this.csvFolder = csvFolder;
    }

    public static void main(String[] args) throws Exception {
        if (args == null || args.length == 0) {
            System.err.println("Invalid args!");
            System.err.println("Usage 1: java -jar DataMainGenerator.jar `路径` `id`");
            System.err.println("Usage 2: java -jar DataMainGenerator.jar `路径`");
            return;
        }
        File csvPath = new File(args[0]);
        if (!csvPath.exists()) {
            System.err.println("csv path doesn't exist: " + args[0]);
            return;
        }

        String id = null;
        if (args.length == 2) {
            id = args[1].trim().toUpperCase();
        } else if (args.length == 1) {
            id = csvPath.getName().trim().toUpperCase();
        }

        DataMainGenerator mainGenerator = new DataMainGenerator(id, csvPath);
        mainGenerator.process(csvPath);
        System.out.println("Finished.");
    }

    public void process(File csvPath) throws Exception{
        if (csvPath.isFile() && csvPath.getName().endsWith(".csv")) {
            String key = csvPath.getName();
            if (key.startsWith(id)) {
                key = key.replaceFirst(id, "");
            }
            key = key.substring(0, key.indexOf(".csv"));
            try(CSVReader reader = new CSVReader(csvPath);){
                reader.open();
                String[] analyse = LayoutAnalyser.process(reader.getTitle(), key);
                if(analyse==null || analyse.length < 3 || StringUtils.isEmpty(analyse[0])
                        || StringUtils.isEmpty(analyse[1]) || StringUtils.isEmpty(analyse[2]) ){
                    throw new Exception(analyse[0] + "/" + analyse[1] + "/" + analyse[2]);
                }
                DataFileBuilder.build(reader, csvPath.getName() + "_" + key, analyse[0], analyse[1], analyse[2], analyse[3], 4, 10000);
            }
        } else {
            for (File csvFile : csvPath.listFiles()) {
                process(csvFile);
            }
        }
    }
}
