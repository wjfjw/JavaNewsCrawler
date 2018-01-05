package priv.wjf.Crawler;

/**
 * category代号：
 * "gn"：国内
 * "gj"：国际
 * "sh"：社会
 * "js"：军事
 * "cj"：财经
 * "kj"：科技
 * "ty"：体育
 */

public class News 
{
	private String url;
	private String category;
	
	public News(String url, String category) {
		this.url = url;
		this.category = category;
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getCategory() {
		return category;
	}

}
