package playground.plugins;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import playground.dal.ActivityDao;
import playground.dal.ElementDao;
import playground.logic.ActivityEntity;
import playground.logic.ElementEntity;

@Component
public class PostLikeDislikePlugin implements Plugin {
	private ElementDao elements;
	private ObjectMapper jackson;
	private final static String LIKE = "like";
	private final static String DISLIKE = "dislike";
	private String delim;
	
	@PostConstruct
	public void init() {
		this.jackson = new ObjectMapper();
	}

	@Autowired
	public void setElements(
			ElementDao elements,
			@Value("${delim:@@}") String delim) {
		this.elements = elements;
		this.delim = delim;
	}

	@Override
	public Object execute(ActivityEntity command) throws Exception {
		LikeDislike likeStatus = jackson.readValue(command.getAttributesJson(), LikeDislike.class);
		System.err.println(command.getElementId() + delim + command.getElementPlayground());
		ElementEntity theElement = elements
				.findById(command.getElementId() + delim + command.getElementPlayground())
				.get();
		
		int totalLike = (int) theElement.getAttributes().get(LIKE) + likeStatus.getLike();
		int totalDislike = (int) theElement.getAttributes().get(DISLIKE) + likeStatus.getDislike();
		
		theElement.getAttributes().put(LIKE, totalLike);
		theElement.getAttributes().put(DISLIKE, totalDislike);
		
		return new LikeDislike(totalLike, totalDislike);
	}

}
