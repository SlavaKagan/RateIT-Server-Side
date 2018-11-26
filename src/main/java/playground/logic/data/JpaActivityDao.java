package playground.logic.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import playground.dal.ActivityDao;
import playground.logic.ActivityService;

@Service
public class JpaActivityDao implements ActivityService {
	
	private ActivityDao activities;

	@Autowired
	public void setElementDao(ActivityDao activities){
		this.activities = activities;
	}
	
	@Override
	public void cleanup() {
		// TODO Auto-generated method stub
		
	}

}
