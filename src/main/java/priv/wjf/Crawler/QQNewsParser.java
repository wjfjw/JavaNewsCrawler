package priv.wjf.Crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class QQNewsParser extends AbstractNewsParser
{
	private String tag;
	private List<String> filter;
	
	public QQNewsParser(){
		super("腾讯新闻");
		tag = null;
		
		filter = new ArrayList<String>();
		filter.add(".{0,15}\\d{1,2}月\\d{1,2}日([电讯]|消息|报道)");
		filter.add(".{0,15}(消息|报道|称|透露)");
		filter.add("[(（【].{0,20}记者.{0,20}[)）】]");
		filter.add("[（(].{0,10}[)）]");
		filter.add("[\\s 　]+");
		filter.add("资料图(片)?(：)?");
	}
	
	public boolean parse(String url){
		Document doc;
		try {
			doc = Jsoup.connect(url).get();
			
			//整篇新闻节点
			Element newsNode = doc.getElementsByClass("qq_article").first();
			if(newsNode == null){
				boolean result =  parseNewPage(url);
				if(!result) {
					System.out.println(url);
					System.out.println("该网页不能被解析！");
				}
				return result;
			}
			
			//新闻头部节点
			Element headNode = newsNode.getElementsByClass("hd").first();
			if(headNode == null){
				System.out.println(url);
				System.out.println("该网页不能被解析！");
				return false;
			}
			
			//新闻标题
			Element titleNode = headNode.getElementsByTag("h1").first();
			if(titleNode != null){
				title = titleNode.ownText();
			}
			if(title==null || title.isEmpty()) {
				return false;
			}
			
			
			//新闻发布时间
			Element timeNode = headNode.getElementsByClass("a_time").first();
			if(timeNode != null){
				time = timeNode.ownText();
				time = time.replaceAll("[-:\\s]", "");
			}
			if(time==null || time.isEmpty()) {
				return false;
			}
			
			//新闻正文内容
			StringBuilder contentBuilder = new StringBuilder();
			Element bodyNode =  newsNode.getElementById("Cnt-Main-Article-QQ");
			if(bodyNode != null){
				Elements contentNodes =  bodyNode.getElementsByTag("p");
				if(contentNodes != null){
					for(Element contentNode : contentNodes){
						contentBuilder.append( contentNode.ownText() );
					}
				}
			}
			content = contentBuilder.toString();
			content.replaceAll(",", "，");
			for(String filterPattern : filter){
				content = content.replaceAll(filterPattern, "");
			}
			if(content==null || content.isEmpty()) {
				return false;
			}
			
			
			//新闻标签
			Element tagNode =  doc.getElementById("videokg");
			if(tagNode != null){
				Element a_tagNode =  tagNode.getElementsByTag("a").first();
				if(a_tagNode != null){
					tag = a_tagNode.ownText();
				}
			}
			
			//新闻类别
			Element categoryNode = headNode.getElementsByClass("a_catalog").first();
			if(categoryNode != null){
				if(categoryNode.childNodeSize() > 0){
					Element a_categoryNode = categoryNode.getElementsByTag("a").first();
					if(a_categoryNode != null){
						category = a_categoryNode.ownText();
					}
				}else{
					category = categoryNode.ownText();
				}
			}
			
//			//新闻来源
//			Element sourceNode = headNode.getElementsByClass("a_source").first();
//			if(sourceNode != null){
//				if(sourceNode.childNodeSize() > 0){
//					Element a_sourceNode = sourceNode.getElementsByTag("a").first();
//					if(a_sourceNode != null){
//						source = a_sourceNode.ownText();
//					}
//				}else{
//					source = sourceNode.ownText();
//				}
//				
//			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	//解析形如“http://new.qq.com/cmsn/20180104004104.html”的url
	private boolean parseNewPage(String url)
	{
		String newUrl = "http://new.qq.com/cmsn/";
		String date = "";
		Pattern pattern = Pattern.compile("(20\\d{6})/(\\d+)\\.(htm|html|shtml)");
		Matcher matcher = pattern.matcher(url);
		if(matcher.find()) {
			date = matcher.group(1);
			newUrl += date;
			newUrl += matcher.group(2);
			newUrl += ".html";
		}
		
		try {
			Document doc = Jsoup.connect(newUrl).get();
			
			Element newsNode = doc.getElementsByClass("LEFT").first();
			if(newsNode == null) {
				return getJsonData(date + matcher.group(2) + "00");
			}
			
			//新闻标题
			Element titleNode = newsNode.getElementsByTag("h1").first();
			if(titleNode != null){
				title = titleNode.ownText();
			}
			if(title==null || title.isEmpty()) {
				return false;
			}
			
			//新闻发布时间
			time = date + "1200";
			Element headNode = doc.getElementsByTag("head").first();
//			Elements metaElements = headNode.getElementsByTag("meta");
//			for(Element metaElement : metaElements) {
//				if(metaElement.attr("name").equals("_pubtime")) {
//					time = metaElement.attr("content");
//					break;
//				}
//			}
			Element jsNode = headNode.getElementsByTag("script").first();
			String jsString = jsNode.toString();
			pattern = Pattern.compile("\"pubtime\":\"(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2})\"");
			matcher = pattern.matcher(jsString);
			if(matcher.find()) {
				time = matcher.group(1);
			}
			time = time.replaceAll("[-:\\s]", "");
			time = time.substring(0, 12);
			if(time==null || time.isEmpty()) {
				return false;
			}
			
			//新闻正文内容
			StringBuilder contentBuilder = new StringBuilder();
			Element contentNode = newsNode.getElementsByClass("content-article").first();
			Elements pNodes = contentNode.getElementsByTag("p");
			for(Element pNode : pNodes) {
				contentBuilder.append( pNode.ownText() );
			}
			content = contentBuilder.toString();
			content.replaceAll(",", "，");
			for(String filterPattern : filter){
				content = content.replaceAll(filterPattern, "");
			}
			if(content==null || content.isEmpty()) {
				return false;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	
	//形如“http://new.qq.com/cmsn/20171120033115”的url要发请求获取json数据
	private boolean getJsonData(String id) {
		URI uri;
		URIBuilder uriBuilder;
		try(CloseableHttpClient httpCilent = HttpClients.createDefault()) {
			HttpGet httpget = new HttpGet();
			
			uriBuilder = new URIBuilder()  
			        .setScheme("http")  
			        .setHost("openapi.inews.qq.com")  
			        .setPath("/getQQNewsNormalContent")  
			        .setParameter("id", id)
			        .setParameter("chlid", "news_rss")
			        .setParameter("refer", "mobilewwwqqcom")
			        .setParameter("otype", "jsonp")
			        .setParameter("ext_data", "all")
			        .setParameter("srcfrom", "newsapp")
			        .setParameter("callback", "getNewsContentOnlyOutput")
			        ;
			uri = uriBuilder.build();
			httpget.setURI(uri);
			CloseableHttpResponse response = httpCilent.execute(httpget);
			HttpEntity entity = response.getEntity();
			InputStream in = entity.getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(in,"GBK"));
			
			String jsonString = br.readLine();
			jsonString = jsonString.substring(25, jsonString.length()-1);
			JSONObject newsObject = JSONObject.fromObject(jsonString);
			
			//新闻标题
			if(newsObject.containsKey("title")) {
				title = newsObject.getString("title");
			}
			if(title==null || title.isEmpty()) {
				return false;
			}
			
			//新闻发布时间
			time = newsObject.getString("pubtime");
			time = time.replaceAll("[^0-9]", "");
			time = time.substring(0, 12);
			if(time==null || time.isEmpty()) {
				return false;
			}
			
			//新闻正文内容
			StringBuilder contentBuilder = new StringBuilder();
			JSONArray contentArray =  newsObject.getJSONArray("content");
			for(int i=0 ; i<contentArray.size() ; ++i) {
				JSONObject contentObject = contentArray.getJSONObject(i);
				if( contentObject.getInt("type") == 1 ) {
					contentBuilder.append( contentObject.getString("value") );
				}
			}
			content = contentBuilder.toString();
			content.replaceAll(",", "，");
			for(String filterPattern : filter){
				content = content.replaceAll(filterPattern, "");
			}
			if(content==null || content.isEmpty()) {
				return false;
			}
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	
	public String getNewsTag(){
		return tag;
	}
}
