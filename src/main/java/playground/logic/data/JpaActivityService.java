package playground.logic.data;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import playground.dal.UserDao;
import playground.logic.ActivityEntity;
import playground.logic.ActivityService;
import playground.logic.UserEntity;
import playground.plugins.Plugin;

@Service
public class JpaActivityService implements ActivityService {

	private ActivityDao activities;
	private UserDao users;
	private NumberGeneratorDao numberGenerator;
	private ObjectMapper jackson;
	private ConfigurableApplicationContext spring;
	private String delim;
	private String playground;
	
	@Autowired
	public void setActivityDao(
			ActivityDao activities,
			UserDao users,
			NumberGeneratorDao numberGenerator,
			ConfigurableApplicationContext spring,
			@Value("${delim:@@}") String delim,
			@Value("${playground:Default}") String playground) {
		this.activities = activities;
		this.users = users;
		this.numberGenerator = numberGenerator;
		this.jackson = new ObjectMapper();
		this.spring = spring;
		this.delim = delim;
		this.playground = playground;
	}

	@Override
	@Transactional
	@ValidateNull
	@ValidateUser
	@Logger
	@PlaygroundPerformance
	public ActivityEntity createActivity(String userPlayground, String email, ActivityEntity activityEntity)
			throws Exception {
		NumberGenerator temp = this.numberGenerator.save(new NumberGenerator());
		
		activityEntity.setNumber(temp.getNextNumber());
		activityEntity.setPlayerEmail(email);
		activityEntity.setPlayerPlayground(userPlayground);

		// creating the uniqueKey
		activityEntity.setUniqueKey(activityEntity.getNumber() + delim + playground);

		this.numberGenerator.delete(temp);

		try {
			if (activityEntity.getType() != null) {
				activityEntity.getAttributes()
						.putAll(jackson.readValue(jackson.writeValueAsString(((Plugin) spring
								.getBean(Class.forName("playground.plugins." + activityEntity.getType() + "Plugin")))
										.execute(activityEntity)),
								Map.class));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		// Grant 10 points to users that posted a review
		if(activityEntity.getType().equals("PostReview")) {
			UserEntity userInvokingActivity = this.users.findById(userPlayground + delim + email).get();
			userInvokingActivity.updatePoints(10);
			this.users.save(userInvokingActivity);
			activityEntity.getAttributes().put("userPoints", userInvokingActivity.getPoints());
		}

		return this.activities.save(activityEntity);
	}

	@Override
	@Transactional
	@Logger
	@PlaygroundPerformance
	public void cleanup() {
		this.activities.deleteAll();
	}
}