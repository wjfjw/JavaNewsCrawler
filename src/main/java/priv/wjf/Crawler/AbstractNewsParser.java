package priv.wjf.Crawler;

public abstract class AbstractNewsParser implements NewsParser
{
	protected String title;
	protected String time;
	protected String content;
	protected String source;
	
	protected AbstractNewsParser(String source) {
		title = "";
		time = "";
		content = "";
		this.source = source;
	}
	
	@Override
	public void clear() {
		title = null;
		time = null;
		content = null;
	}
	
	@Override
	public String getNewsTitle() {
		return title;
	}

	@Override
	public String getNewsSource() {
		return source;
	}

	@Override
	public String getNewsTime() {
		return time;
	}

	@Override
	public String getNewsContent() {
		return content;
	}

}
