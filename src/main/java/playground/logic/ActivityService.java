package playground.logic;

public interface ActivityService {
	
	public ActivityEntity createActivity(String userPlayground, String email, ActivityEntity activityEntity) throws Exception;
	
	public void cleanup();
}