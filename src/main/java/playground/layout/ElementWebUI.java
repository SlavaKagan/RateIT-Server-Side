package playground.layout;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import playground.logic.ConfirmationException;
import playground.logic.ElementNotFoundException;
import playground.logic.ElementService;
import playground.logic.NotFoundExceptions;

@CrossOrigin(origins = "http://localhost:8080", maxAge = 3600)
@RestController
public class ElementWebUI {
	private ElementService elementservice;
	
	@Value("${playground:default}")
	private String playground;

	@Autowired
	public void setService(ElementService elementervice) {
		this.elementservice = elementervice;
	}
	
	@RequestMapping(
			method = RequestMethod.POST, 
			path = "/playground/elements/{userPlayground}/{email}", 
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public ElementTO createElement(
			@RequestBody ElementTO element,
			@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email) throws Exception {
		element.setPlayground(playground);
		return new ElementTO(elementservice.createElement(userPlayground, email, element.toEntity()));
	}
	
	// Check if user exists
	@RequestMapping(
			method = RequestMethod.GET, 
			path = "/playground/elements/{userPlayground}/{email}/{playground}/{id}", 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementTO getElement(
			@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email, 
			@PathVariable("playground") String playground,
			@PathVariable("id") String id) throws Exception {
		return new ElementTO(elementservice.getElement(id, playground));
	}
	
	// Check if user exists
	@RequestMapping(
			method = RequestMethod.GET, 
			path = "/playground/elements/{userPlayground}/{email}/all", 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementTO[] getAllElements(
			@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email,
			@RequestParam(name="size", required=false, defaultValue="10") int size, 
			@RequestParam(name="page", required=false, defaultValue="0") int page) throws Exception {
		ElementTO[] element = elementservice.getAllElements(size, page)
				.stream()
				.map(ElementTO::new)
				.collect(Collectors.toList())
				.toArray(new ElementTO[0]);
		return element;
	}
	
	// Checking if user exists already in ManagerValidator, don't need to check
	@RequestMapping(
			method = RequestMethod.PUT, 
			path = "/playground/elements/{userPlayground}/{email}/{playground}/{id}", 
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public void updateElement(
			@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email, 
			@PathVariable("playground") String playground,
			@PathVariable("id") String id, 
			@RequestBody ElementTO newElement) throws Exception {
		elementservice.updateElement(userPlayground, email, id, playground, newElement.toEntity());
	}
	
	// Check if user exists
	@RequestMapping(
			method = RequestMethod.GET, 
			path = "/playground/elements/{userPlayground}/{email}/near/{x}/{y}/{distance}", 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementTO[] getElementsByDistnace(
			@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email, 
			@PathVariable("x") String x, 
			@PathVariable("y") String y,
			@PathVariable("distance") String distance,
			@RequestParam(name="size", required=false, defaultValue="10") int size, 
			@RequestParam(name="page", required=false, defaultValue="0") int page) throws Exception {
		ElementTO[] element = elementservice
				.getAllElementsByDistance(size, page, 
						Double.parseDouble(x), Double.parseDouble(y), Double.parseDouble(distance))
				.stream()
				.map(ElementTO::new)
				.collect(Collectors.toList())
				.toArray(new ElementTO[0]);
		return element;
	}
	
	// Check if user exists
	@RequestMapping(
			method = RequestMethod.GET,
			path = "/playground/elements/{userPlayground}/{email}/search/{attributeName}/{value}",
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementTO[] getAllElementsByAttributeAndItsValue( 
			@PathVariable("userPlayground") String userPlayground, 
			@PathVariable("email") String email,
			@PathVariable("attributeName") String attributeName, 
			@PathVariable("value") String value,
			@RequestParam(name="size", required=false, defaultValue="10") int size, 
			@RequestParam(name="page", required=false, defaultValue="0") int page) throws Exception {
		ElementTO[] elements = elementservice.getAllElementsByAttributeAndItsValue(size, page, attributeName, value)
				.stream()
				.map(ElementTO::new)
				.collect(Collectors.toList())
				.toArray(new ElementTO[0]);

		return elements;
	}
	
	@ExceptionHandler({
		ElementNotFoundException.class, 
		ConfirmationException.class
		})
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorMessage handleException(NotFoundExceptions e) {
		String msg = e.getMessage();
		return new ErrorMessage(msg == null ? "There's no specified message for this exception" : msg);
	}
}