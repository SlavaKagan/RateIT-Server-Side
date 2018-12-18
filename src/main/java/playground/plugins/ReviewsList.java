package playground.plugins;

import java.util.List;

public class ReviewsList {
	private List<String> reviews;
	
	public ReviewsList() {
		
	}
	
	public ReviewsList(List<String> reviews) {
		this.reviews = reviews;
	}

	public List<String> getReviews() {
		return reviews;
	}

	public void setReviews(List<String> reviews) {
		this.reviews = reviews;
	}

	@Override
	public String toString() {
		return "ReviewsList [reviews=" + reviews + "]";
	}
}
