package playground.layout;

import javax.annotation.PostConstruct;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import playground.logic.ActivityService;
import playground.logic.NewUserForm;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class WebUITestActivities {
	@Autowired
	private ActivityService service;
	private RestTemplate restTemplate;
	private String url;
	private NewUserForm form;
	public static final String EMAIL = "rubykozel@gmail.com";
	
	@LocalServerPort
	private int port;

	@PostConstruct
	public void init() {
		this.restTemplate = new RestTemplate();
		this.url = "http://localhost:" + port + "/playground/activities/";
		
		System.err.println(this.url);
	}
	
	@Before
	public void setup () {
		
	}
	
	@After
	public void teardown() {
		this.service.cleanup();
	}
	
	@Test
	public void testServerInitializesProperly() throws Exception {
		
	}
	

	
}