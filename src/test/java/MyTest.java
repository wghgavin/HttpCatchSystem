import org.junit.Test;

import java.util.Properties;

public class MyTest {
    @Test
    public void test1(){
        String s ="我sdadsa_11123去你的#$#@$#$#";
        s = s.replaceAll("[^\\u4e00-\\u9fa5a-zA-Z0-9]","");

        System.out.println(s);
    }
}
