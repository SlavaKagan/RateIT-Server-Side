package playground.plugins;

public class MovieMessage {
	private String message;
	
	public MovieMessage() {
		
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return message;
	}
}