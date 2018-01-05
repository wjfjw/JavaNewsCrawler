package priv.wjf.Crawler;

import java.util.Calendar;
import java.util.concurrent.BlockingQueue;

public interface NewsCrawler 
{
	void crawl(BlockingQueue<News> newsQueue, String category);
}
