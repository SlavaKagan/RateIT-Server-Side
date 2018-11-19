package playground.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

@Service
public class ThreadSafeElementServiceStub implements Constants, ElementService {

	private Map<String, ElementEntity> elements;
	
	@PostConstruct
	public void init() {
		elements = Collections.synchronizedMap(new HashMap<>());
	}

	public ThreadSafeElementServiceStub() {
		elements = new HashMap<>();
	}

	public Map<String, ElementEntity> getAllElements() {
		return elements;
	}

	public void addElement(ElementEntity element) {
		elements.put(element.getId(), element);
	}

	public void setElements(Map<String, ElementEntity> elements) {
		this.elements = elements;
	}

	public void createElement(ElementEntity elementEntity, String userPlayground, String email) throws Exception {
		checkForNulls(elementEntity);
		checkIfExists(elementEntity);
		elementEntity.setCreatorEmail(email);
		elementEntity.setCreatorPlayground(userPlayground);
		addElement(elementEntity);
	}

	public ElementEntity getElement(String userPlayground, String email, String playground, String id)
			throws ElementNotFoundException {
		ElementEntity element = elements.get(id);
		if (element == null || 
				!(element.getPlayground().equals(playground) 
					&& checkPlaygroundAndEmail(element, userPlayground, email)))
			throw new ElementNotFoundException("Element not found");
		return element;
	}

	public List<ElementEntity> getAllElements(String userPlayground, String email) {
		Collection<ElementEntity> copy;
				
		synchronized (this.elements) {
			copy = new ArrayList<>(this.elements.values());
		}		
		
		return 	copy
				.stream()
				.filter(element -> checkPlaygroundAndEmail(element, userPlayground, email))
				.collect(Collectors.toList());
	}

	public List<ElementEntity> getAllElementsByDistance(String userPlayground, String email, double x, double y,
			double distance) {
		
		Collection<ElementEntity> copy;
		
		synchronized (this.elements) {
			copy = new ArrayList<>(this.elements.values());
		}
		
		return 	copy
				.stream()
				.filter(element -> 
					checkPlaygroundAndEmail(element, userPlayground, email)
					&& Location.getDistance(new Location(x,y),element.getLocation()) <= distance)
				.collect(Collectors.toList());
	}

	public List<ElementEntity> getAllElementsByAttributeAndItsValue(String userPlayground, String email,
			String attributeName, Object value) {
		
		Collection<ElementEntity> copy;
		
		synchronized (this.elements) {
			copy = new ArrayList<>(this.elements.values());
		}
		
		return 	copy
				.stream()
				.filter(element -> checkPlaygroundAndEmail(element, userPlayground, email)
						&& element.getAttributes().get(attributeName).equals(value))
				.collect(Collectors.toList());
	}

	public void updateElement(String userPlayground, String email, String playground, String id,
			ElementEntity newElement) throws Exception {
		checkForNulls(newElement);
		checkIfExists(newElement);
		ElementEntity element = getElement(userPlayground, email, playground, id);
		if (element == null)
			throw new ElementNotFoundException("Element does not exist");
		this.elements.remove(id);
		this.elements.put(newElement.getId(), newElement);
	}
	
	private void checkForNulls(ElementEntity e) throws Exception {
		if(e.getName() == null || e.getType() == null || e.getId() == null)
			throw new Exception("Null was given to name or type");
	}
	
	private void checkIfExists(ElementEntity e) throws Exception {
		if (this.elements.containsKey(e.getId()))
			throw new Exception("Element already exists");
	}
	
	private boolean checkPlaygroundAndEmail(ElementEntity element, String playground, String email) {
		return element.getCreatorEmail().equals(email) && element.getPlayground().equals(playground);
	}

	@Override
	public void cleanup() {
		this.elements.clear();
	}

}