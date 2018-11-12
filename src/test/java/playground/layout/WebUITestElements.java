package playground.layout;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

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

import playground.logic.ElementService;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
public class WebUITestElements {
	@Autowired
	private ElementService service;
	
	private RestTemplate restTemplate;
	private String url;
	
	@LocalServerPort
	private int port;
	
	@PostConstruct
	public void init() {
		this.restTemplate = new RestTemplate();
		this.url = "http://localhost:" + port + "/playground/elements";
		
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
	
	@Test(expected=Exception.class)
	public void testGetSpecificMessageByInvalidName() throws Exception {
		// when 
		this.restTemplate.getForObject(this.url + "/{name}", ElementTO.class, "null");
	}
}



















