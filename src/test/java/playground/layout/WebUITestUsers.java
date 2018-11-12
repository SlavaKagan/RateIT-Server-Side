package playground.layout;

import static org.assertj.core.api.Assertions.assertThat;
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

import playground.logic.Constants;
import playground.logic.NewUserForm;
import playground.logic.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class WebUITestUsers {
	
	@Autowired
	private UserService service;

	private RestTemplate restTemplate;
	private String url;
	private NewUserForm form;

	@LocalServerPort
	private int port;

	@PostConstruct
	public void init() {
		this.restTemplate = new RestTemplate();
		this.url = "http://localhost:" + port + "/playground/users/";
		form = new NewUserForm("rubykozel@gmail.com", "ruby", ":-)", "Guest");
		System.err.println(this.url);
	}

	@Before
	public void setup() {
		
	}

	@After
	public void teardown() {
		this.service.cleanup();
	}
	
	@Test
	public void testServerInitializesProperly() throws Exception {
		
	}

	@Test
	public void testConfirmingANewRegisteredUserSuccessfully() {
		service.createUser(form);

		UserTO actualUser = this.restTemplate.getForObject(url + "confirm/{playground}/{email}/{code}", UserTO.class,
				Constants.PLAYGROUND, "rubykozel@gmail.com", 1234);
		
		assertThat(actualUser)
			.isNotNull()
			.extracting("role")
			.containsExactly(Constants.REVIEWER);
	}

}
