package playground.logic;

import java.util.List;

public interface ElementService{

	public ElementEntity createElement(String userPlayground, String email, ElementEntity elementEntity) throws Exception;

	public ElementEntity getElement(String userPlayground, String email, String id, String playground) throws ElementNotFoundException;

	public List<ElementEntity> getAllElements(String userPlayground, String email, int size, int page) throws Exception;

	public List<ElementEntity> getAllElementsByDistance(String userPlayground, String email, int size, int page, double x, double y, double distance) throws Exception;

	public List<ElementEntity> getAllElementsByAttributeAndItsValue(String userPlayground, String email, int size, int page, String attributeName,
			String value) throws Exception;

	public void updateElement(String userPlayground, String email, String id, String playground, ElementEntity newElement) throws ElementNotFoundException, Exception;

	public void cleanup();
}