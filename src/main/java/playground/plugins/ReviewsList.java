package playground.plugins;

import java.util.List;

public class ReviewsList {
	private List<String> reviews;
	private long elementCount;
	
	public ReviewsList() {
		
	}
	
	public ReviewsList(List<String> reviews, long elementCount) {
		this.reviews = reviews;
		this.elementCount = elementCount;
	}

	public List<String> getReviews() {
		return reviews;
	}

	public void setReviews(List<String> reviews) {
		this.reviews = reviews;
	}

	public long getElementCount() {
		return elementCount;
	}

	public void setElementCount(long elementCount) {
		this.elementCount = elementCount;
	}

	@Override
	public String toString() {
		return "ReviewsList [reviews=" + reviews + "]";
	}
}
