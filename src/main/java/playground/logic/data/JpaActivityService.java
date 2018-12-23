package playground.logic.data;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import playground.aop.annotations.ValidateNull;
import playground.aop.annotations.ValidateUser;
import playground.aop.logger.Logger;
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
	public void setActivityDao(ActivityDao activities, NumberGeneratorDao numberGenerator,
			ConfigurableApplicationContext spring) {
		this.activities = activities;
		this.numberGenerator = numberGenerator;
		this.jackson = new ObjectMapper();
		this.spring = spring;
	}

	@Override
	@Transactional
	@ValidateNull
	@ValidateUser
	@Logger
	@PlaygroundPerformance
	public ActivityEntity createActivity(String userPlayground, String email, ActivityEntity activityEntity)
			throws Exception {
		if (!this.activities.existsById(activityEntity.getUniqueKey())) {
			NumberGenerator temp = this.numberGenerator.save(new NumberGenerator());
			activityEntity.setNumber("" + temp.getNextNumber());

			this.numberGenerator.delete(temp);

			try {
				if (activityEntity.getType() != null) {				
					activityEntity
					.getAttributes()
					.putAll(
							jackson.readValue(
							jackson.writeValueAsString(
							((Plugin) spring.getBean(
							Class.forName("playground.plugins." + activityEntity.getType() + "Plugin")))
							.execute(activityEntity)), Map.class)
							);
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

			return this.activities.save(activityEntity);
		} else {
			throw new Exception("Activity already exists");
		}
	}

	@Override
	@Transactional
	@Logger
	@PlaygroundPerformance
	public void cleanup() {
		this.activities.deleteAll();
	}
}