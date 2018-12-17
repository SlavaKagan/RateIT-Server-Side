package playground.logic.data;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import playground.aop.annotations.ValidateNull;
import playground.aop.logger.MyLog;
import playground.aop.logger.PlaygroundPerformance;
import playground.dal.ActivityDao;
import playground.dal.NumberGenerator;
import playground.dal.NumberGeneratorDao;
import playground.logic.ActivityEntity;
import playground.logic.ActivityService;
import playground.plugins.Plugin;

@Service
public class JpaActivityService implements ActivityService {

	private ActivityDao activities;
	private NumberGeneratorDao numberGenerator;
	private ObjectMapper jackson;
	private ConfigurableApplicationContext spring;

	@Autowired
	public void setActivityDao(ActivityDao activities, NumberGeneratorDao numberGenerator, ConfigurableApplicationContext spring) {
		this.activities = activities;
		this.numberGenerator = numberGenerator;
		this.jackson = new ObjectMapper();
		this.spring = spring;
	}

	@Override
	@Transactional
	@MyLog
	@PlaygroundPerformance
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
	@MyLog
	@PlaygroundPerformance
	public ActivityEntity createActivity(ActivityEntity activityEntity) throws Exception {
		if (!this.activities.existsById(activityEntity.getUniqueKey())) {
			NumberGenerator temp = this.numberGenerator.save(new NumberGenerator());
			activityEntity.setNumber("" + temp.getNextNumber());

			this.numberGenerator.delete(temp);
			
			
			try {
				if (activityEntity.getType() != null) {
					String type = activityEntity.getType();
					Plugin plugin = (Plugin) spring.getBean(Class.forName("playground.plugins." + type + "Plugin"));
					Object content = plugin.execute(activityEntity);
					activityEntity.getAttributes().putAll(jackson.readValue(jackson.writeValueAsString(content), Map.class));
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

			return this.activities.save(activityEntity);
		} else {
			throw new Exception("Activity already exists"); // TODO "Activity already exists" ?
		}
	}

	@Override
	@Transactional
	@MyLog
	@PlaygroundPerformance
	public void cleanup() {
		this.activities.deleteAll();
	}

	@Override
	@Transactional(readOnly = true)
	@MyLog
	@PlaygroundPerformance
	public List<ActivityEntity> getAllActivities(int size, int page) {
		return 
				this
				.activities
				.findAll(PageRequest.of(page, size, Direction.DESC, "creationDate"))
				.getContent();
	}
	
	@Override
	@ValidateNull
	@MyLog
	@PlaygroundPerformance
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