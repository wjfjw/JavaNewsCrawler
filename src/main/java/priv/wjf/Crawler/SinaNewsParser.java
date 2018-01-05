package priv.wjf.Crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SinaNewsParser extends AbstractNewsParser
{
//	private List<String> filter;
	
	public SinaNewsParser(){
		super("新浪新闻");
		
//		filter = new ArrayList<String>();
//		filter.add(".{0,15}\\d{1,2}月\\d{1,2}日([电讯]|消息|报道)");
//		filter.add(".{0,15}(消息|报道|称|透露)");
//		filter.add("[(（【].{0,20}记者.{0,20}[)）】]");
//		filter.add("[（(].{0,15}[)）]");
//		filter.add("[\\s 　]+");
//		filter.add("资料图(片)?(：)?");
//		filter.add("\\[.{0,30}\\]");
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
			
//			//新闻类别
//			String categoryString = "";
//			Element categoryNode = doc.getElementsByClass("bread").first();
//			if(categoryNode != null) {
//				categoryString = categoryNode.getElementsByTag("a").first().ownText();
//				if(categoryString.contains("国内")) {
//					category = "gn";
//				}else if(categoryString.contains("国际")) {
//					category = "gj";
//				}
//			}else {
//				categoryNode = doc.getElementsByClass("text notInPad").first();
//				if(categoryNode != null) {
//					categoryString = categoryNode.getElementsByTag("a").first().ownText();
//					if(categoryString.contains("军")) {
//						category = "js";
//					}
//				}else {
//					
//				}
//			}
			
			
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
//			for(String filterPattern : filter){
//				content = content.replaceAll(filterPattern, "");
//			}
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
