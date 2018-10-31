package playground.layout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import playground.logic.Constants;
import playground.logic.ElementTO;
import playground.logic.ElementsPool;
import playground.logic.NewUserForm;
import playground.logic.UserPool;
import playground.logic.UserTO;

@RestController
public class WebUI implements Constants {

	private UserPool userpool;
	private ElementsPool elementpool;

	@Autowired
	public void setUserPool(UserPool userpool, ElementsPool elementpool) {
		this.userpool = userpool;
		this.elementpool = elementpool;

	}

	// Please check this when you finish with UserTO class
	@RequestMapping(
			method = RequestMethod.POST, 
			path = "/playground/users", 
			produces = MediaType.APPLICATION_JSON_VALUE, 
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public UserTO createUser(@RequestBody NewUserForm userForm) {
		return userpool.createUser(userForm);
	}
	
	// Have not checked this
	@RequestMapping(
			method = RequestMethod.GET,
			path = "/playground/users/confirm/{playground}/{email}/{code}",
			produces = MediaType.APPLICATION_JSON_VALUE)
	public UserTO confirmUser(
			@PathVariable("playground") String playground,
			@PathVariable("email") String email,
			@PathVariable("code") String code) throws Exception {
		if (code.equals(TEMPORARY_CODE) && userpool.getUser(playground, email).getRole().equals(GUEST))
			return userpool.confirmUser(playground, email);
		else if (!code.equals(TEMPORARY_CODE))
			throw new Exception("You have entered the wrong confirmation code");
		else
			throw new Exception("User is already confirmed");
	}

	// Please check this when you finish with UserTO class
	@RequestMapping(
			method = RequestMethod.GET, 
			path = "/playground/users/login/{playground}/{email}", 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public UserTO getUser(
			@PathVariable("playground") String playground,
			@PathVariable("email") String email) {
		return userpool.getUser(playground, email);
	}

	// Checked this, working, although i don't know what attribute belongs to what
	// parameter
	@RequestMapping(
			method = RequestMethod.GET, 
			path = "/playground/elements/{userPlayground}/{email}/{playground}/{id}", 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementTO getElement(
			@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email, 
			@PathVariable("playground") String playground,
			@PathVariable("id") String id) {
		return elementpool.getElement(userPlayground, email, playground, id);
	}

	// Checked this, working
	@RequestMapping(
			method = RequestMethod.GET, 
			path = "/playground/elements/{userPlayground}/{email}/all", 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementTO[] getAllElements(
			@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email) {
		return elementpool.getAllElements(userPlayground, email).toArray(new ElementTO[0]);
	}

	// Didn't check this
	@RequestMapping(
			method = RequestMethod.PUT, 
			path = "/playground/elements/{userPlayground}/{email}/{playground}/{id}", 
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public void updateElement(@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email, @PathVariable("playground") String playground,
			@PathVariable("id") String id, @RequestBody ElementTO newElement) {
	}

	// checked this, working
	@RequestMapping(
			method = RequestMethod.GET, 
			path = "/playground/elements/{userPlayground}/{email}/near/{x}/{y}/{distance}", 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementTO[] getElementsByDistnace(
			@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email, 
			@PathVariable("x") String x, 
			@PathVariable("y") String y,
			@PathVariable("distance") String distance) {
		return elementpool.getAllElementsByDistance(userPlayground, email, Double.parseDouble(x), Double.parseDouble(y),
				Double.parseDouble(distance)).toArray(new ElementTO[0]);
	}
	
	@RequestMapping(
			method = RequestMethod.GET,
			path = "/playground/elements/{userPlayground}/{email}/search/{attributeName}/{value}",
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementTO[] getAllElementsByAttributeAndItsValue( 
			@PathVariable("userPlayground") String userPlayground, 
			@PathVariable("email") String email,
			@PathVariable("attributeName") String attributeName, 
			@PathVariable("value") Object value) {
		return elementpool.getAllElementsByAttributeAndItsValue(userPlayground, email, attributeName, value).toArray(new ElementTO[0]);
	}
}
