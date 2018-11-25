package playground.logic;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

@Service
public class ThreadSafeActivityServiceStub implements Constants, ActivityService {
	private Map<String, ActivityEntity> activities;

	@PostConstruct
	public void init() {
		activities = Collections.synchronizedMap(new HashMap<>());
	}
	
	@Override
	public void cleanup() {
		this.activities.clear();
	}

}
