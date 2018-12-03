package playground.logic;

import java.util.List;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;

public interface ActivityService {
	public ActivityEntity createActivity(ActivityEntity activityEntity) throws Exception;
	public  ActivityEntity getElement(String id) throws  NotFoundException;
	public List<ActivityEntity> getAllActivities(int size, int page);
	public void updateActivity(String id, ActivityEntity newActivity) throws Exception;
	
	public void cleanup();
}