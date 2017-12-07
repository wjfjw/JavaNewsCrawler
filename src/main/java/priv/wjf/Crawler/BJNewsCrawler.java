package priv.wjf.Crawler;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BJNewsCrawler implements NewsCrawler
{

	@Override
	public void crawl(BlockingQueue<String> urlQueue) 
	{
		final String domain = "http://www.bjnews.com.cn";
		String url = domain + "/roll?page=";
		
		for(int page=1 ; page<=200 ; ++page){
			url += page;
			try {
				Document doc = Jsoup.connect(url).get();
				
				Element newsNode = doc.getElementById("news_ul");
				if(newsNode != null){
					Elements urlsNode = newsNode.getElementsByTag("li");
					if(urlsNode != null){
						for(Element urlNode : urlsNode){
							Element a_urlNode = urlNode.getElementsByTag("a").first();
							String path = a_urlNode.attr("href");
							if(path.startsWith("/news")
									|| path.startsWith("/opinion")
									|| path.startsWith("/finance")
									|| path.startsWith("/wevideo")
									|| path.startsWith("/video")){
								urlQueue.put(domain + path);
							}
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
