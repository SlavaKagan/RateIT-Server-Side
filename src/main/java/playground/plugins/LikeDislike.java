package playground.plugins;

public class LikeDislike {
	private int like;
	private int dislike;

	public LikeDislike() {
	}
	
	public LikeDislike(int like, int dislike) {
		this.like = like;
		this.dislike = dislike;
	}

	public int getLike() {
		return like;
	}

	public void setLike(int like) {
		this.like = like;
	}

	public int getDislike() {
		return dislike;
	}

	public void setDislike(int dislike) {
		this.dislike = dislike;
	}

	@Override
	public String toString() {
		return "LikeDislike [like=" + like + ", dislike=" + dislike + "]";
	}
	
}
