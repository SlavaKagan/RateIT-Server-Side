package playground.plugins;

public class LikeDislike {
	private String status;

	public LikeDislike() {
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Like/Dislike status = " + status;
	}

	
}
