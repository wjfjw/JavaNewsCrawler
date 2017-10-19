package priv.wjf.Crawler;

public class App 
{
	
    public static void main( String[] args )
    {
    	String url = "http://news.qq.com/a/20171017/032694.htm";
    	NewsParser newsParser = new QQNewsParser();
    	newsParser.parse(url);
    	
    	System.out.println("新闻标题： " + newsParser.getNewsTitle());
		System.out.println("新闻类别： " + newsParser.getNewsCategory());
		System.out.println("新闻来源： " + newsParser.getNewsSource());
		System.out.println("新闻发布时间： " + newsParser.getNewsTime());
		System.out.println("新闻正文内容： " + newsParser.getNewsContent());
		System.out.println("新闻标签： " + newsParser.getNewsTag());
    }
}
