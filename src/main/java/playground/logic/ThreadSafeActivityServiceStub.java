package playground.logic;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;

//@Service
public class ThreadSafeActivityServiceStub implements ActivityService {
	private Map<String, ActivityEntity> activities;

	@PostConstruct
	public void init() {
		activities = Collections.synchronizedMap(new HashMap<>());
	}
	
	@Override
	public void cleanup() {
		this.activities.clear();
	}

	@Override
	public ActivityEntity createActivity(ActivityEntity activityEntity) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ActivityEntity getElement(String id) throws NotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ActivityEntity> getAllActivities(int size, int page) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateActivity(String id, ActivityEntity newActivity) throws Exception {
		// TODO Auto-generated method stub
		
	}
}