import org.apache.commons.lang3.StringUtils;

/**
 * @author za-xuzhiping
 * @Date 2019/3/5
 * @Time 17:27
 */
public class LayoutAnalyser {

    public static String[] process(String[] title, String type)
    {
        if (title == null || title.length ==0) {
            return null;
        }

        StringBuilder keyDefine = new StringBuilder();
        StringBuilder keyMapping = new StringBuilder();
        StringBuilder saveItem = new StringBuilder();
        String lineNum = "1";

        for (int i = 0; i < title.length; i++)
        {
            String name = title[i];
            if (StringUtils.isEmpty(name)){
                continue;
            }

            if("data".equalsIgnoreCase(name)) {
                if(saveItem.length()>0){
                    saveItem.append(",");
                }
                saveItem.append(i);
            }else if("year".equalsIgnoreCase(name)) {
                lineNum = "INSURE_PERIOD";
            } else {
                if(keyDefine.length()>0){
                    keyDefine.append(",");
                    keyMapping.append(";");
                }
                if (name.startsWith("KEY:")) {
                    name = name.substring(4);
                }
                keyDefine.append(name);
                keyMapping.append(i);
            }
        }
        return new String[] { keyDefine.toString(), keyMapping.toString(), saveItem.toString(), lineNum };
    }
}
