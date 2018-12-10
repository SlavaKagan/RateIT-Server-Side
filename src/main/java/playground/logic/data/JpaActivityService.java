package playground.logic.data;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import playground.aop.annotations.ValidateNull;
import playground.dal.ActivityDao;
import playground.dal.NumberGenerator;
import playground.dal.NumberGeneratorDao;
import playground.logic.ActivityEntity;
import playground.logic.ActivityService;

@Service
public class JpaActivityService implements ActivityService {

	private ActivityDao activities;
	private NumberGeneratorDao numberGenerator;

	@Autowired
	public void setActivityDao(ActivityDao activities, NumberGeneratorDao numberGenerator) {
		this.activities = activities;
		this.numberGenerator = numberGenerator;
	}

	@Override
	@Transactional
	public ActivityEntity getActivity(String uniqueKey) throws Exception {
		Optional<ActivityEntity> op = this.activities.findById(uniqueKey);
		if (op.isPresent()) {
			return op.get();
		} else {
			throw new Exception("No activity with key " + uniqueKey);
		}
	}

	@Override
	@Transactional
	@ValidateNull
	public ActivityEntity createActivity(ActivityEntity activityEntity) throws Exception {
		if (!this.activities.existsById(activityEntity.getUniqueKey())) {
			NumberGenerator temp = this.numberGenerator.save(new NumberGenerator());
			activityEntity.setNumber("" + temp.getNextNumber());

			this.numberGenerator.delete(temp);

			return this.activities.save(activityEntity);
		} else {
			throw new Exception("Activity already exists"); // TODO "Activity already exists" ?
		}
	}

	@Override
	@Transactional
	public void cleanup() {
		this.activities.deleteAll();
	}

	@Override
	@Transactional(readOnly = true)
	public List<ActivityEntity> getAllActivities(int size, int page) {
		return 
				this
				.activities
				.findAll(PageRequest.of(page, size, Direction.DESC, "creationDate"))
				.getContent();
	}
	
	@Override
	@ValidateNull
	public void updateActivity(String id, ActivityEntity newActivity) throws Exception {
		if (this.activities.existsById(id)) {
			ActivityEntity existing = this.getActivity(id);
			this.activities.delete(existing);
			this.createActivity(newActivity);
		} else {
			throw new Exception("There's no such element");
		}
	}
}