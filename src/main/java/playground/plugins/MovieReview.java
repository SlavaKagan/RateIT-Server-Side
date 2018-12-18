package playground.plugins;

public class MovieReview {
	private String review;
	
	public MovieReview() {
		
	}
	
	public String getReview() {
		return review;
	}

	public void setReview(String review) {
		this.review = review;
	}

	@Override
	public String toString() {
		return review;
	}
}
