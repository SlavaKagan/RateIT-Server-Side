package playground.layout;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
		validateParamsNotNull(userPlayground,email);
		element.setPlayground(playground);
		return new ElementTO(elementservice.createElement(element.toEntity(), userPlayground, email));
	}
	
	@RequestMapping(
			method = RequestMethod.GET, 
			path = "/playground/elements/{userPlayground}/{email}/{playground}/{id}", 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementTO getElement(
			@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email, 
			@PathVariable("playground") String playground,
			@PathVariable("id") String id) throws Exception {
		validateParamsNotNull(userPlayground,email,playground,id);
		return new ElementTO(elementservice.getElement(id, playground));
	}
	
	@RequestMapping(
			method = RequestMethod.GET, 
			path = "/playground/elements/{userPlayground}/{email}/all", 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementTO[] getAllElements(
			@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email,
			@RequestParam(name="size", required=false, defaultValue="10") int size, 
			@RequestParam(name="page", required=false, defaultValue="0") int page) throws Exception {
		validateParamsNotNull(userPlayground,email);
		ElementTO[] element = elementservice.getAllElements(size, page)
				.stream()
				.map(ElementTO::new)
				.collect(Collectors.toList())
				.toArray(new ElementTO[0]);
		return element;
	}
	
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
		validateParamsNotNull(userPlayground, playground, email, id);
		elementservice.updateElement(id, playground, newElement.toEntity());
	}

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
		validateParamsNotNull(userPlayground,email,x,y,distance);
		ElementTO[] element = elementservice
				.getAllElementsByDistance(size, page, 
						Double.parseDouble(x), Double.parseDouble(y), Double.parseDouble(distance))
				.stream()
				.map(ElementTO::new)
				.collect(Collectors.toList())
				.toArray(new ElementTO[0]);
		
		if (element.length <= 0 && page == 0)			
			throw new ElementNotFoundException("No elements at the distance specified from the (x, y) specified");
		return element;
	}
	
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
		validateParamsNotNull(userPlayground,email, attributeName);
		ElementTO[] elements = elementservice.getAllElementsByAttributeAndItsValue(size, page, attributeName, value)
				.stream()
				.map(ElementTO::new)
				.collect(Collectors.toList())
				.toArray(new ElementTO[0]);
		
		if (elements.length <= 0 && page == 0)			
			throw new ElementNotFoundException("No element was found with key: " + attributeName + " and value: " + value);
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
	
	private void validateParamsNotNull(String... strings) throws Exception {
		for(String string : strings) 
			if ("null".equals(string) || string == null)
				throw new Exception("One of the paramters provided was null");					
	}
}