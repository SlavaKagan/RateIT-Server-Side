package playground.plugins;

import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import playground.dal.ActivityDao;
import playground.logic.ActivityEntity;

@Component
public class ShowMessagesPlugin implements Plugin {

	private ObjectMapper jackson;
	private ActivityDao activities;
	
	public ShowMessagesPlugin() {
		
	}
	
	@PostConstruct
	public void init() {
		jackson = new ObjectMapper();
	}
	
	@Autowired
	public void setService(ActivityDao activities) {
		this.activities = activities;
	}

	@Override
	public Object execute(ActivityEntity command) throws Exception {
		PageAndSizeRequest rv = jackson.readValue(command.getAttributesJson(), PageAndSizeRequest.class);
		
		Page<ActivityEntity> page = activities
				.findAllByTypeEqualsAndElementIdEquals(
						"PostMessage", 
						command.getElementId(), 
						PageRequest.of(rv.getPage(), rv.getSize(), Direction.DESC, "creationDate"));
		
		long count = page.getTotalPages();
		
		return new MessagesList(
			page
			.getContent()
			.stream()
			.map(activity -> (String) activity.getAttributes().get("message"))
			.collect(Collectors.toList()), count);
	}
}