package playground.logic;

import java.util.List;

public interface ActivityService {
	public ActivityEntity getActivity(String id) throws Exception;
	
	public ActivityEntity createActivity(ActivityEntity activityEntity) throws Exception;
	
	public List<ActivityEntity> getAllActivities(int size, int page);
	
	public List<ActivityEntity> getActivitiesOfTypeAndElementId(String type, String id, int size, int page);
	
	public void cleanup();
}