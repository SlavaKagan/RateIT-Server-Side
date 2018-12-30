package playground.plugins;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
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
		LikeDislike likeStatus = jackson.readValue(
				jackson.writeValueAsString(command.getAttributes()), LikeDislike.class);
		
		ElementEntity theElement = elements
				.findById(command.getElementId() + delim + command.getElementPlayground())
				.get();
		
		theElement.getAttributes().put(LIKE,
				(int) theElement.getAttributes().get(LIKE) + likeStatus.getLike());
		theElement.getAttributes().put(DISLIKE,
				(int) theElement.getAttributes().get(DISLIKE) + likeStatus.getDislike());
		
		elements.save(theElement);
		
		return new LikeDislike(
				(int) theElement.getAttributes().get(LIKE),
				(int) theElement.getAttributes().get(DISLIKE));
	}

}
