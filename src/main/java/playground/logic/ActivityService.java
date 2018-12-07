package playground.logic;

import java.util.List;

public interface ActivityService {
	public ActivityEntity getActivity(String id) throws Exception;
	
	public ActivityEntity createActivity(ActivityEntity activityEntity) throws Exception;
	
	public void cleanup();
	
	public List<ActivityEntity> getAllActivities(int size, int page);
	
	public void updateActivity(String id, ActivityEntity newActivity) throws Exception;
	
}