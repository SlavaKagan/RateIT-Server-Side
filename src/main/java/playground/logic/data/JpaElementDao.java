package playground.logic.data;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import playground.dal.ElementDao;
import playground.logic.ElementEntity;
import playground.logic.ElementNotFoundException;
import playground.logic.ElementService;

@Service
public class JpaElementDao implements ElementService {

	private ElementDao elements;

	@Autowired
	public void setElementDao(ElementDao elements){
		this.elements = elements;
	}

	@Override
	@Transactional
	public void addElement(ElementEntity element) {
		// TODO Auto-generated method stub

	}

	@Override
	@Transactional
	public ElementEntity createElement(ElementEntity elementEntity, String userPlayground, String email)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public ElementEntity getElement(String userPlayground, String email, String playground, String id)
			throws ElementNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public List<ElementEntity> getAllElements(String userPlayground, String email, int size, int page) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public List<ElementEntity> getAllElementsByDistance(String userPlayground, String email, int size, int page,
			double x, double y, double distance) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public List<ElementEntity> getAllElementsByAttributeAndItsValue(String userPlayground, String email, int size,
			int page, String attributeName, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional
	public void updateElement(String userPlayground, String email, String playground, String id,
			ElementEntity newElement) throws ElementNotFoundException, Exception {
		// TODO Auto-generated method stub

	}

	@Override
	@Transactional
	public void cleanup() {
		// TODO Auto-generated method stub

	}

}
