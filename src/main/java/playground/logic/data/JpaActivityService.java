package playground.logic.data;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import playground.dal.ActivityDao;
import playground.dal.NumberGeneratorDao;
import playground.logic.ActivityEntity;
import playground.logic.ActivityService;

@Service
public class JpaActivityService implements ActivityService {
	
	private ActivityDao activities;
	private NumberGeneratorDao numberGenerator;

	@Autowired
	public void setElementDao(ActivityDao activities, NumberGeneratorDao numberGenerator){
		this.activities = activities;
		this.numberGenerator = numberGenerator;
	}
	
	@Override
	@Transactional
	public void cleanup() {
		this.activities.deleteAll();
	}

	@Override
	public ActivityEntity createActivity(ActivityEntity activityEntity) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}