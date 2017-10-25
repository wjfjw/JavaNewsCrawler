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
    	NewsParser newsParser = new QQNewsParser();
    	PrintWriter out = new PrintWriter("./data/qqNews");
    	
    	//抓取url线程
    	Thread crawlerThread = new Thread(new Runnable() {
			@Override
			public void run() {
				newsCrawler.crawl(urlQueue, 2);
			}
		});
    	
    	//解析网页线程
    	Thread parserThread = new Thread(new Runnable() {
			@Override
			public void run() {
				String url = "";
				try {
					while(true) {
						url = urlQueue.poll(1, TimeUnit.SECONDS);
						if(url==null) {
							break;
						}
				    	newsParser.parse(url);
				    	System.out.println("--------------------------");    	
				    	out.println(newsParser.getNewsTitle());
				    	out.print(newsParser.getNewsCategory());
				    	out.print(newsParser.getNewsSource());
				    	out.print(newsParser.getNewsTag());
				    	out.println(newsParser.getNewsTime());
				    	out.println(newsParser.getNewsContent());
				    	out.println();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
		});
    	
    	Thread thread = new Thread(new MyRunnable());
    	
//    	crawlerThread.start();
//    	parserThread.start();
    	thread.start();
    	
    	out.close();
    }
    
    
}

class MyRunnable implements Runnable{
	
	@Override
	public void run() {
		PrintWriter out = null;
		try {
			out = new PrintWriter("./data/qqNews");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		out.println("123");
	}
}
