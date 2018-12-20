package playground.plugins;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import playground.dal.ActivityDao;
import playground.logic.ActivityEntity;

public class PostMessagePlugin implements Plugin {
	
	private ObjectMapper jackson;
	private ActivityDao activities;
	
	public PostMessagePlugin() {
		
	}
	
	@PostConstruct
	public void init() {
		jackson = new ObjectMapper();
	}
	
	@Override
	public Object execute(ActivityEntity command) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
