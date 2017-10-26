package priv.wjf.Crawler;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class App 
{
	
    public static void main( String[] args ) throws FileNotFoundException
    {
    	BlockingQueue<String> urlQueue = new LinkedBlockingQueue<String>();
    	NewsCrawler newsCrawler = new QQNewsCrawler();
    	int crawlDays = 2;
    	
    	//抓取url线程
    	Thread crawlerThread = new Thread(new CrawlerRunnable(urlQueue, crawlDays, newsCrawler));
    	crawlerThread.start();
    	
    	//解析网页线程
//    	Thread[] parserThread = new Thread[5];
//    	for(int i=0 ; i<5 ; ++i) {
//    		parserThread[i] = new Thread(new ParserRunnable(urlQueue));
//    		parserThread[i].start();
//    	}
    	Thread parserThread = new Thread(new ParserRunnable(urlQueue));
    	parserThread.start();
    }
}

//抓取url线程运行方法
class CrawlerRunnable implements Runnable
{
	private BlockingQueue<String> urlQueue;
	private int crawlDays;
	private NewsCrawler newsCrawler;
	
	public CrawlerRunnable(BlockingQueue<String> urlQueue , int crawlDays , NewsCrawler newsCrawler) {
		this.urlQueue = urlQueue;
		this.crawlDays = crawlDays;
		this.newsCrawler = newsCrawler;
	}
	
	@Override
	public void run() {
		newsCrawler.crawl(urlQueue, crawlDays);
	}
}


//解析网页线程运行方法
class ParserRunnable implements Runnable
{
	private BlockingQueue<String> urlQueue;
	private NewsParser newsParser;
	
	public ParserRunnable(BlockingQueue<String> urlQueue) {
		this.urlQueue = urlQueue;
		newsParser = new QQNewsParser();
	}
	
	@Override
	public void run() {
		try(PrintWriter out = new PrintWriter("./data/qqNews")) {
			String url = null;
			while(true) {
//				synchronized (ParserRunnable.class) {
				url = urlQueue.poll(1, TimeUnit.SECONDS);
				if(url==null) {
					break;
				}
				if(newsParser.parse(url)) {
						out.println(newsParser.getNewsTitle());
				    	out.print(newsParser.getNewsCategory());
				    	out.print(newsParser.getNewsSource());
				    	out.print(newsParser.getNewsTag());
				    	out.println(newsParser.getNewsTime());
				    	out.println(newsParser.getNewsContent());
				    	out.println();
				    	out.flush();
				}
//				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
