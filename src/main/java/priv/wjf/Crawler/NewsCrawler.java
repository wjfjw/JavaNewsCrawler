package priv.wjf.Crawler;

import java.util.concurrent.BlockingQueue;

public interface NewsCrawler 
{
	void crawl(BlockingQueue<String> urlQueue , int days);
}
