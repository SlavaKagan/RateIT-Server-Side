package playground.plugins;

public class PageAndSizeRequest {
	private int page;
	private int size;
	
	public PageAndSizeRequest() {
		
	}
	
	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	@Override
	public String toString() {
		return size + ", " + page;
	}

}
