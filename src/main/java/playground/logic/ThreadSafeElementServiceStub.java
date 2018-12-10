package playground.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import playground.aop.annotations.ValidateNull;

//@Service
public class ThreadSafeElementServiceStub implements ElementService {

	private Map<String, ElementEntity> elements;
	
	@PostConstruct
	public void init() {
		elements = Collections.synchronizedMap(new HashMap<>());
	}

	public ThreadSafeElementServiceStub() {
		elements = new HashMap<>();
	}

	public Map<String, ElementEntity> getAllElementsMap() {
		return elements;
	}

	public void addElement(ElementEntity element) {
		elements.put(element.getUniqueKey().split("@@")[0], element);
	}

	public void setElements(Map<String, ElementEntity> elements) {
		this.elements = elements;
	}
	
	@ValidateNull
	public ElementEntity createElement(String creatorPlayground, String email, ElementEntity elementEntity) throws Exception {
		checkIfExists(elementEntity);
		elementEntity.setCreatorPlayground(creatorPlayground);
		elementEntity.setCreatorEmail(email);
		addElement(elementEntity);
		return elementEntity;
	}

	public ElementEntity getElement(String id, String playground)
			throws ElementNotFoundException {
		ElementEntity element = elements.get(id);
		if (element == null)
			throw new ElementNotFoundException("Element not found");
		return element;
	}

	public List<ElementEntity> getAllElements(int size, int page) {
		
		Collection<ElementEntity> copy;
				
		synchronized (this.elements) {
			copy = new ArrayList<>(this.elements.values());
		}		
		
		return 	copy
				.stream()
				.skip(page * size)
				.limit(size)
				.collect(Collectors.toList());
	}

	public List<ElementEntity> getAllElementsByDistance(int size, int page, double x, double y, double distance) {
		
		Collection<ElementEntity> copy;
		
		synchronized (this.elements) {
			copy = new ArrayList<>(this.elements.values());
		}
		
		return 	copy
				.stream()
				.filter(element -> 
					Location.getDistance(new Location(x,y),new Location(element.getX(),element.getY())) <= distance)
				.skip(page * size)
				.limit(size)
				.collect(Collectors.toList());
	}

	public List<ElementEntity> getAllElementsByAttributeAndItsValue(int size, int page, String attributeName, String value) {
		
		Collection<ElementEntity> copy;
		
		synchronized (this.elements) {
			copy = new ArrayList<>(this.elements.values());
		}
		
		return 	copy
				.stream()
				.filter(element -> element.getAttributes().get(attributeName).equals(value))
				.skip(page * size)
				.limit(size)
				.collect(Collectors.toList());
	}

	@ValidateNull
	public void updateElement(String userPlayground, String email, String id, String playground,
			ElementEntity newElement) throws ElementNotFoundException, Exception {
		ElementEntity element = getElement(id, playground);
		if (element == null)
			throw new ElementNotFoundException("Element does not exist");
		this.elements.remove(id);
		this.elements.put(newElement.getUniqueKey().split("@@")[0], newElement);
	}
	
	private void checkIfExists(ElementEntity e) throws Exception {
		if (this.elements.containsKey(e.getUniqueKey().split("@@")[0]))
			throw new Exception("Element already exists");
	}

	@Override
	public void cleanup() {
		this.elements.clear();
	}
}