package playground.plugins;

public class MovieMessage {
	private String message;
	
	public MovieMessage() {
		
	}
	
	public String getReview() {
		return message;
	}

	public void setReview(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return message;
	}
}