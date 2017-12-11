package priv.wjf.Crawler;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BJNewsParser extends AbstractNewsParser
{
	private final int patternNum = 3;
	private String[] filter;
	
	public BJNewsParser() {
		super("新京报");
		
		filter = new String[patternNum];
		filter[0] = "新京报快讯";
		filter[1] = "[(（].{0,20}记者.{0,20}[)）]";
		filter[2] = "[\\s 　]+";
	}

	@Override
	public boolean parse(String url) {
		try {
			Document doc = Jsoup.connect(url).get();
			
			//新闻标题
			Element titleNode = doc.getElementsByClass("title").first();
			title = titleNode.getElementsByTag("h1").first().ownText();
			title = title.replaceAll("\\|.*", "");
			title = title.replaceAll("\\s+", "");
			if(title==null || title.isEmpty()) {
				return false;
			}
			
			//新闻发布时间
			Element timeNode = doc.getElementsByClass("date").first();
			if(timeNode != null){
				time = timeNode.ownText();
			}
			time = time.replaceAll("[-:\\s]", "");
			time = time.substring(0, 12);
			if(time==null || time.isEmpty()) {
				return false;
			}
			
			//新闻正文
			StringBuilder contentBuilder = new StringBuilder();
			
			if(url.contains("wevideo")) {
				Element contentNode = doc.getElementById("daoy");
				if(contentNode != null) {
					contentBuilder.append(contentNode.ownText());
				}
			}else {
				Element contentNode =  doc.getElementsByClass("content").first();
				if(contentNode != null) {
					Elements pNodes = contentNode.getElementsByTag("p");
					for(Element pNode : pNodes) {
						if(pNode.attributes().size()==0) {
							String paragraph = pNode.text();
							for(int i=0 ; i<patternNum ; ++i){
								paragraph = paragraph.replaceAll(filter[i], "");
							}
							contentBuilder.append(paragraph);
						}
					}
				}
			}
			content = contentBuilder.toString();
			content.replaceAll(",", "，");
			if(content==null || content.isEmpty()
					|| (content.contains("详见") && content.length()<20) ) {
				return false;
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return true;
	}

}
