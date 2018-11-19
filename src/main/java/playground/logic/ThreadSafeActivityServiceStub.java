package playground.logic;

import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class ThreadSafeActivityServiceStub implements Constants, ActivityService {
	private Map<String, ActivityEntity> activities;

	@Override
	public void cleanup() {
		this.activities.clear();
	}

}
