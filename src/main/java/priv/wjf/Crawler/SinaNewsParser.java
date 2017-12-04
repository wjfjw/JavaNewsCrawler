package priv.wjf.Crawler;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SinaNewsParser implements NewsParser
{
	private String title;
	private String time;
	private String content;
	private String source;
	
	private final int patternNum = 4;
	private String[] filter;
	
	public SinaNewsParser(){
		title = null;
		time = null;
		content = null;
		source = "新浪新闻";
		
		filter = new String[patternNum];
		filter[0] = ".{0,15}(\\d{1,2}月\\d{1,2}日)?([电讯]|消息|报道)";
		filter[1] = "[(（].{0,20}记者.{0,20}[)）]";
		filter[2] = "\\[.{0,30}\\]";
		filter[3] = "[\\s　]+";
//		filter[4] = "据.{0,20}报道";
	}


	@Override
	public boolean parse(String url) {
		try {
			Document doc = Jsoup.connect(url).get();
			
			//新闻标题
			Element titleNode = doc.getElementById("artibodyTitle");
			if(titleNode != null){
				title = titleNode.ownText();
			}
			if(title==null || title.isEmpty()) {
				return false;
			}
			
			//新闻发布时间
			Element timeNode = doc.getElementById("navtimeSource");
			if(timeNode != null){
				time = timeNode.ownText();
			}
			Pattern pattern = Pattern.compile("[年月日:\\s]");
			Matcher matcher = pattern.matcher(time);
			time = matcher.replaceAll("");
			if(time==null || time.isEmpty()) {
				return false;
			}
			
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
						for(int i=0 ; i<patternNum ; ++i){
							paragraph = paragraph.replaceAll(filter[i], "");
						}
						contentBuilder.append( paragraph );
					}
				}
			}
			content = contentBuilder.toString();
			content.replaceAll(",", "，");
			if(content==null || content.isEmpty()) {
				return false;
			}
			
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return true;
	}

	@Override
	public String getNewsTitle() {
		return title;
	}

	@Override
	public String getNewsTime() {
		return time;
	}

	@Override
	public String getNewsContent() {
		return content;
	}

	@Override
	public String getNewsSource() {
		return source;
	}

}
