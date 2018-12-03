package playground.logic;

public interface ActivityService {
	public ActivityEntity createActivity(ActivityEntity activityEntity) throws Exception;
	
	public void cleanup();
}