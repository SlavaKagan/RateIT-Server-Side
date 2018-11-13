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
import playground.logic.ElementService;
import playground.logic.NewUserForm;
import playground.logic.UserService;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
public class WebUITestElements {
	@Autowired
	private ElementService elementservice;
	
	@Autowired
	private UserService userservice;
	
	private RestTemplate restTemplate;
	private String url;
	private NewUserForm form;
	
	public static final String EMAIL = "rubykozel@gmail.com";
	
	@LocalServerPort
	private int port;
	
	@PostConstruct
	public void init() {
		this.restTemplate = new RestTemplate();
		this.url = "http://localhost:" + port + "/playground/elements";
		form = new NewUserForm(EMAIL, "ruby", ":-)", "Manager");
	}
	
	@Before
	public void setup () {
		
	}

	@After
	public void teardown() {
		this.userservice.cleanup();
		this.elementservice.cleanup();
	}

	
	@Test
	public void testServerInitializesProperly() throws Exception {
		
	}
	
	@Test
	public void testCreatingAnElementSuccessfully() throws Exception {
		/*
		  	Given the server is up 
			And there's an account with playground: "2019A.Kagan", email: "rubykozel@gmail.com", role: "Manager" 
		*/
		userservice.createUser(form);
		
		
		/*When I POST "/playground/elements/2019A.Kagan/rubykozel@gmail.com" with 
			{
				"type":"Messaging Board", 
				"name":"Messaging Board"
			} 
		*/
		
		ElementTO actualElement = this.restTemplate
				.postForObject(url + "/{userPlayground}/{email}", 
						new ElementTO("Messaging Board","Messaging Board",Constants.PLAYGROUND,EMAIL), 
						ElementTO.class, Constants.PLAYGROUND,EMAIL);
		
		//Then the response is 200 
	
		/* And the output is 
		 	{
		 		"playground": "2019A.Kagan",
		 		"id": Any ID ,
		 		"location": {"x": Any X,"y": Any Y},
		 		"name": "Messaging Board",
		 		"creationDate": Any valid date,
		 		"expirationDate": null,
		 		"type": "Messaging Board",
		 		"attributes": {
		 						"creatorsName": "Manager",
		 						"isActive": "True",
		 						"isAMovie": "False",
		 						"movieName": "Venom 2018"
		 					  },
		 		"creatorPlayground": "2019A.Kagan",
		 		"creatorEmail": "rubykozel@gmail.com"
		 	}
		 
		 */
		assertThat(actualElement)
			.isNotNull()
			.extracting(
					"playground",
					"id",
					"location",
					"name",
					"creationDate",
					"expirationDate",
					"type",
					"attributes",
					"creatorPlayground",
					"creatorEmail")
			.containsExactly(
					Constants.PLAYGROUND,
					actualElement.getId(),
					actualElement.getLocation(),
					"Messaging Board",
					actualElement.getCreationDate(),
					actualElement.getExpirationDate(),
					"Messaging Board",
					actualElement.getAttributes(),
					Constants.PLAYGROUND,
					EMAIL);
	}
	
	@Test(expected = Exception.class)
	public void testCreatingAnElementWithAUserThatIsNotAManager() throws Exception {
		/*
		 	Given the server is up
			And theres an account with playground: "2019A.Kagan", email: "rubykozel@gmail.com", role: "Reviewer",
		 */
		form.setRole(Constants.REVIEWER);
		userservice.createUser(form);
		
		/* When I POST "/playground/elements/2019A.Kagan/rubykozel@gmail.com" with 
		 	{
		 		"type":"Messaging Board", 
		 		"name":"Messaging Board"
		 	}
		 */
		
		ElementTO actualElement = this.restTemplate
				.postForObject(url + "/{userPlayground}/{email}", 
						new ElementTO("Messaging Board","Messaging Board",Constants.PLAYGROUND,EMAIL), 
						ElementTO.class, Constants.PLAYGROUND,EMAIL);
		
		//Then the response is 500
	}
	
	@Test(expected = Exception.class)
	public void testCreatingAnElementWithoutDeliveringAnyValidJSON() throws Exception {
		/*
		  	Given the server is up
			And theres an account with playground: "2019A.Kagan", email: "rubykozel@gmail.com", role: "Manager"
		 */
		userservice.createUser(form);
		
		//When I POST "/playground/elements/2019A.Kagan/rubykozel@gmail.com" with nothing
		
		this.restTemplate.postForObject(url + "/{userPlayground}/{email}", null, ElementTO.class, Constants.PLAYGROUND,EMAIL);
		
		//Then the response is <> 2xx
	}
	
	// Currently not passing, the response is 200 with a valid element
	// Where should the logic of checking for null values be?
	@Test(expected = Exception.class)
	public void testCreatingAnElementWithEmailAsNull() throws Exception {
		/*
		  	Given the server is up
			And theres an account with playground: "2019A.Kagan", email: "rubykozel@gmail.com", role: "Manager"
		 */
		userservice.createUser(form);
		
		//When I POST "/playground/elements/2019A.Kagan/rubykozel@gmail.com" with '{"type": null, "name":"Messaging Board"}'
		
		this.restTemplate
		.postForObject(url + "/{userPlayground}/{email}", 
				new ElementTO(null,"Messaging Board",Constants.PLAYGROUND,EMAIL), 
				ElementTO.class, Constants.PLAYGROUND,EMAIL);
		
		//Then the response is 500 with 
	}
}



















