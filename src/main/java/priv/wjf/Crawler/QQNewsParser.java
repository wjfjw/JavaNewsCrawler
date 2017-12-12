package priv.wjf.Crawler;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class QQNewsParser extends AbstractNewsParser
{
	private String category;
	private String tag;
	private String[] filter;
	private final int patternNum = 4;
	
	public QQNewsParser(){
		super("腾讯新闻");
		category = null;
		tag = null;
		
		filter = new String[patternNum];
		filter[0] = ".{0,15}(\\d{1,2}月\\d{1,2}日)?([电讯]|消息|报道)";
		filter[1] = "[(（【].{0,20}记者.{0,20}[)）】]";
		filter[2] = "[（(].{0,10}[)）]";
		filter[3] = "[\\s 　]+";
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
			for(int i=0 ; i<patternNum ; ++i){
				content = content.replaceAll(filter[i], "");
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
//				newUrl = newUrl.substring(0, newUrl.length()-5);
//				doc = Jsoup.connect( newUrl ).get();
//				newsNode = doc.getElementsByClass("LEFT").first();
//				if(newsNode == null) {
//					return false;
//				}
				return false;
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
			for(int i=0 ; i<patternNum ; ++i){
				content = content.replaceAll(filter[i], "");
			}
			if(content==null || content.isEmpty()) {
				return false;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;
	}
	

	public String getNewsCategory(){
		return category;
	}
	
	public String getNewsTag(){
		return tag;
	}
}
