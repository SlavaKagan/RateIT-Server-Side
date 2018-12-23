package playground.plugins;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.databind.ObjectMapper;
import playground.dal.ActivityDao;
import playground.logic.ActivityEntity;

public class PostLikeDislikePlugin implements Plugin {
	private ActivityDao activities;
	private ObjectMapper jackson;

	@PostConstruct
	public void init() {
		this.jackson = new ObjectMapper();
	}

	@Autowired
	public void setActivities(ActivityDao activities) {
		this.activities = activities;
	}

	@Override
	public Object execute(ActivityEntity command) throws Exception {
		LikeDislike likeStatus = jackson.readValue(command.getAttributesJson(), LikeDislike.class);
		return likeStatus;
	}

}
