package priv.wjf.Crawler;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SinaNewsParser extends AbstractNewsParser
{
	private final int patternNum = 5;
	private String[] filter;
	
	public SinaNewsParser(){
		super("新浪新闻");
		
		filter = new String[patternNum];
		filter[0] = ".{0,15}(\\d{1,2}月\\d{1,2}日)?([电讯]|消息|报道)";
		filter[1] = "[(（【].{0,20}记者.{0,20}[)）】]";
		filter[2] = "\\[.{0,30}\\]";
		filter[3] = "[（(].{0,10}[)）]";
		filter[4] = "[\\s 　]+";
//		filter[4] = "据.{0,20}报道";
	}

	@Override
	public boolean parse(String url) {
		try {
			Document doc = Jsoup.connect(url).get();
			
			//新闻标题
			Element titleNode = doc.getElementById("artibodyTitle");
			if(titleNode == null){
				titleNode = doc.getElementsByClass("main-title").first();
			}
			if(titleNode == null){
				titleNode = doc.getElementById("main_title");
			}
			if(titleNode != null){
				title = titleNode.ownText();
			}
			if(title==null || title.isEmpty()) {
				System.out.println(url);
				return false;
			}
			
			//新闻发布时间
			Element timeNode = doc.getElementById("navtimeSource");
			if(timeNode == null) {
				timeNode = doc.getElementById("pub_date");
			}
			if(timeNode == null) {
				timeNode = doc.getElementsByClass("time-source").first();
			}
			if(timeNode == null) {
				timeNode = doc.getElementsByClass("date").first();
			}
			if(timeNode == null) {
				timeNode = doc.getElementsByClass("titer").first();
			}
			if(timeNode != null){
				time = timeNode.text();
			}
			if(time==null || time.isEmpty()) {
				System.out.println(url);
				return false;
			}
			time = time.replaceAll("[^0-9]", "");
			
			
			//新闻正文
			StringBuilder contentBuilder = new StringBuilder();
			Element bodyNode =  doc.getElementById("artibody");
			if(bodyNode != null){
				Elements contentNodes =  bodyNode.getElementsByTag("p");
				if(contentNodes != null){
					for(Element contentNode : contentNodes){
						String paragraph = contentNode.ownText();
						//去除“原标题”、“来源”和“责任编辑”段落
						if(paragraph.contains("原标题") 
								|| paragraph.contains("来源：")
								|| contentNode.className().equals("article-editor")) {
							continue;
						}
						contentBuilder.append( paragraph );
					}
				}
			}
			content = contentBuilder.toString();
			for(int i=0 ; i<patternNum ; ++i){
				content = content.replaceAll(filter[i], "");
			}
			content.replaceAll(",", "，");
			if(content==null || content.isEmpty()) {
				System.out.println(url);
				return false;
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return true;
	}

}
