package playground.plugins;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import playground.logic.ActivityEntity;

@Component
public class PostReviewPlugin implements Plugin {

	private ObjectMapper jackson;
	
	public PostReviewPlugin() {
		
	}
	
	@PostConstruct
	public void init() {
		jackson = new ObjectMapper();
	}

	@Override
	public Object execute(ActivityEntity command) throws Exception {
		MovieReview rv = jackson.readValue(command.getAttributesJson(), MovieReview.class);
		return rv;
	}

}
