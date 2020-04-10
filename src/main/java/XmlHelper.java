import org.jdom2.*;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class XmlHelper {
    public static void createXML(Map<String, Set<String>> map) {
        //1.生成一个根节点
        Element root=new Element("travel");
        //2.为节点添加属性
        root.setAttribute("version", "1.0");
        Document document=new Document(root);
        for (Map.Entry<String,Set<String>> entry:map.entrySet()
             ) {
            String title = entry.getKey();
            title = title.replaceAll("[^\\u4e00-\\u9fa5a-zA-Z0-9]","");
            Element childRoot = new Element(title);
            root.addContent(childRoot);
            for (String data:entry.getValue()
                 ) {
               Element detail = new Element("detail");
               detail.setContent(new Text(data));
               childRoot.addContent(detail);
            }
            //生成xml格式
            Format format = Format.getCompactFormat();
            format.setIndent("");
            format.setEncoding("GBK");
            XMLOutputter outputter = new XMLOutputter(format);
            try {
                outputter.output(document, new FileOutputStream(new File("travelData.xml")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
