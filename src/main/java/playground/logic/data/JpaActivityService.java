package playground.logic.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import playground.dal.ActivityDao;
import playground.dal.NumberGeneratorDao;
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
}