import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class CatchHttpTest {
    HashMap<String, Set<String>> map = new HashMap<>();
    final Object synObj = new Object();

    public static void main(String[] args) {
        new CatchHttpTest().analizeHtml();
    }

    void analizeHtml() {
        ExecutorService excutor = Executors.newFixedThreadPool(5);
        String html = getHtmlByUrl("https://poi.mapbar.com/");
        Document doc = Jsoup.parse(html);
        Element element = doc.select("dl[id=city_list]").select("dd").select("a").stream().filter(ele -> ele.text().contains("澳门")).findFirst().get();
        String h1 = element.attr("href");
        String aoMenHtml = getHtmlByUrl(h1);
        Document aoMenDoc = Jsoup.parse(aoMenHtml);
        Elements elements = aoMenDoc.select("div[class=isortBox]").first().select("a");
        for (Element ele : elements
        ) {
            String url = ele.attr("href");
            excutor.execute(() -> {
                String title = ele.text();
                String htmlData = getHtmlByUrl(url);
                Document detailDoc = Jsoup.parse(htmlData);
                Elements detailEles = detailDoc.select("div[class=sortC]").select("dd").select("a");
                Set<String> detailDataList = new HashSet<>();
                for (Element detail : detailEles
                ) {
                    detailDataList.add(detail.text());
                }
                addDataToMap(title, detailDataList);
            });
        }
//        CompletableFuture<String> task1 = CompletableFuture.supplyAsync(() -> {
//            String html = getHtmlByUrl("https://poi.mapbar.com/");
//            Document doc = Jsoup.parse(html);
//            Element element = doc.select("dl[id=city_list]").select("dd").select("a").stream().filter(ele -> ele.text().contains("澳门")).findFirst().get();
//            return element.attr("href");
//        }, excutor);
//        task1.thenAccept((e) -> {
//            String aoMenHtml = getHtmlByUrl(e);
//            Document aoMenDoc = Jsoup.parse(aoMenHtml);
//            Elements elements = aoMenDoc.select("div[class=isortBox]").first().select("a");
//            for (Element ele : elements
//            ) {
//                String url = ele.attr("href");
//                excutor.execute(() -> {
//                    String title = ele.text();
//                    String htmlData = getHtmlByUrl(url);
//                    Document detailDoc = Jsoup.parse(htmlData);
//                    Elements detailEles = detailDoc.select("div[class=sortC]").select("dd").select("a");
//                    Set<String> detailDataList = new HashSet<>();
//                    for (Element detail : detailEles
//                    ) {
//                        detailDataList.add(detail.text());
//                    }
//                    addDataToMap(title, detailDataList);
//                });
//            }
//        });
        try {
            //关闭线程池，这里会等待线程完成再关闭
            excutor.shutdown();//先让他关闭
            excutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);//这里会同步等待
            XmlHelper.createXML(map);
            System.out.println("执行完毕");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 用来存放最终的数据
     *
     * @param title
     * @param data
     */
    void addDataToMap(String title, Set<String> data) {
        synchronized (synObj) {
            map.put(title,data);
           // System.out.println(title);
        }
    }

    String getHtmlByUrl(String url) {
        //建立新的请求客户端
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        //获取网址的返回结果
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //获取返回实体
        HttpEntity entity = response.getEntity();
        try {
            return (EntityUtils.toString(entity));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        } finally {
            try {
                EntityUtils.consume(entity);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
