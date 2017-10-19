package priv.wjf.Crawler;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class QQNewsParser implements NewsParser
{
	private String title;
	private String category;
	private String source;
	private String time;
	private String content;
	private String tag;
	
	public QQNewsParser(){
		title = null;
		category = null;
		source = null;
		time = null;
		content = null;
		tag = null;
	}
	
	public void parse(String url){
		Document doc;
		try {
			doc = Jsoup.connect(url).get();
			
			//整篇新闻节点
			Element newsNode = doc.getElementsByClass("qq_article").first();
			if(newsNode == null){
				throw new NodeLackException("Lacking newsNode!");
			}
			
			//新闻头部节点
			Element headNode = newsNode.getElementsByClass("hd").first();
			if(headNode == null){
				throw new NodeLackException("Lacking headNode!");
			}
			
			//新闻标题
			Element titleNode = headNode.getElementsByTag("h1").first();
			if(titleNode != null){
				title = titleNode.ownText();
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
			
			//新闻来源
			Element sourceNode = headNode.getElementsByClass("a_source").first();
			if(sourceNode != null){
				if(sourceNode.childNodeSize() > 0){
					Element a_sourceNode = sourceNode.getElementsByTag("a").first();
					if(a_sourceNode != null){
						source = a_sourceNode.ownText();
					}
				}else{
					source = sourceNode.ownText();
				}
				
			}
			
			//新闻发布时间
			Element timeNode = headNode.getElementsByClass("a_time").first();
			if(timeNode != null){
				time = timeNode.ownText();
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
			
			//新闻标签
			Element tagNode =  doc.getElementById("videokg");
			if(tagNode != null){
				Element a_tagNode =  tagNode.getElementsByTag("a").first();
				if(a_tagNode != null){
					tag = a_tagNode.ownText();
				}
			}

		}
		catch (IOException e) {
			e.printStackTrace();
		}catch (NodeLackException e) {
			e.printStackTrace();
		}
	}
	
	public String getNewsTitle(){
		return title;
	}
	
	public String getNewsCategory(){
		return category;
	}
	
	public String getNewsSource(){
		return source;
	}
	
	public String getNewsTime(){
		return time;
	}
	
	public String getNewsContent(){
		return content;
	}
	
	public String getNewsTag(){
		return tag;
	}
}

class NodeLackException extends Exception
{
	public NodeLackException(){}
	public NodeLackException(String message){
		super(message);
	}
}
