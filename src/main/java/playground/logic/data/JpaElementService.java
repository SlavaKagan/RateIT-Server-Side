package playground.logic.data;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import playground.aop.annotations.ValidateManager;
import playground.aop.annotations.ValidateNull;
import playground.aop.annotations.ValidateUser;
import playground.aop.logger.Logger;
import playground.aop.logger.PlaygroundPerformance;
import playground.dal.ElementDao;
import playground.dal.NumberGenerator;
import playground.dal.NumberGeneratorDao;
import playground.logic.ElementEntity;
import playground.logic.ElementNotFoundException;
import playground.logic.ElementService;
import playground.logic.UserEntity;
import playground.logic.UserService;

@Service
public class JpaElementService implements ElementService {

	private ElementDao elements;
	private NumberGeneratorDao numberGenerator;
	private String playground;	
	private String delim;
	private UserService userService;
	private String moviePageType;
	private String messagingBoardType;

	@Autowired
	public void setElementDao(
			ElementDao elements,
			NumberGeneratorDao numberGenerator,
			UserService userService,
			@Value("${playground:Default}") String playground,
			@Value("${delim:@@}") String delim,
			@Value("${movie.page.type:Default}") String moviePageType,
			@Value("${messaging.board.type:Default}") String messagingBoardType) {
		this.elements = elements;
		this.numberGenerator = numberGenerator;
		this.playground = playground;
		this.delim = delim;
		this.userService = userService;
		this.moviePageType = moviePageType;
		this.messagingBoardType = messagingBoardType;
	}

	@Override
	@Transactional
	@ValidateManager
	@ValidateNull
	@Logger
	@PlaygroundPerformance
	public ElementEntity createElement(String userPlayground, String email, ElementEntity elementEntity)
			throws Exception {
		if (!this.elements.existsById(elementEntity.getUniqueKey())) {
			if(!elementEntity.getType().equals(this.messagingBoardType) 
					&& !elementEntity.getType().equals(this.moviePageType))
				throw new RuntimeException("Unexcpected element type " + elementEntity.getType());
			
			NumberGenerator temp = this.numberGenerator.save(new NumberGenerator());
			
			elementEntity.setNumber(temp.getNextNumber());
			
			//Inserting number to uniqueKey
			elementEntity.setUniqueKey(elementEntity.getNumber() + delim + playground);
			
			elementEntity.setCreatorPlayground(userPlayground);
			elementEntity.setCreatorEmail(email);
			
			if(elementEntity.getType().equals(this.moviePageType)) {
				elementEntity.getAttributes().put("like", 0);
				elementEntity.getAttributes().put("dislike", 0);
			} 
				
			this.numberGenerator.delete(temp);
			
			return this.elements.save(elementEntity);
		}
		throw new Exception("Element already exists!");
	}

	@Override
	@Transactional(readOnly = true)
	@ValidateNull
	@ValidateUser
	@Logger
	@PlaygroundPerformance
	public ElementEntity getElement(String userPlayground, String email, String id, String playground)
			throws ElementNotFoundException {
		Optional<ElementEntity> op = this.elements.findById(id + delim + playground);
		if (op.isPresent())
			return op.get();
		throw new ElementNotFoundException("Element not found");
	}

	@Override
	@Transactional(readOnly = true)
	@ValidateNull
	@ValidateUser
	@Logger
	@PlaygroundPerformance
	public List<ElementEntity> getAllElements(String userPlayground, String email, int size, int page) 
			throws Exception {
		UserEntity userAskingForElements = userService.getRegisteredUser(userPlayground, email);
		List<ElementEntity> request = this
				.elements
				.findAll(PageRequest.of(page, size, Direction.DESC, "creationDate"))
				.getContent();
		
		if(userAskingForElements.getRole().equals("Manager"))
			return request;
		
		return request
				.stream()
				.filter(element -> 
					element.getExpirationDate() == null ? 
					true : element.getExpirationDate().getTime() - System.currentTimeMillis() > 0)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	@ValidateNull
	@ValidateUser
	@Logger
	@PlaygroundPerformance
	public List<ElementEntity> getAllElementsByDistance(String userPlayground, String email, int size, int page,
			double x, double y, double distance) throws Exception {
		UserEntity userAskingForElements = userService.getRegisteredUser(userPlayground, email);
		
		Page<ElementEntity> thePage = this.elements.findAllByXBetweenAndYBetween(x-distance, 
				x+distance, 
				y-distance, 
				y+distance, 
				PageRequest.of(page, size, Direction.DESC, "creationDate"));
		
		long count = thePage.getTotalPages();
		
		List<ElementEntity> request = thePage.getContent();
		
		if (!request.isEmpty())
			request.get(0).getAttributes().put("count", count);
		
		if(userAskingForElements.getRole().equals("Manager"))
			return request;
		
		// You get here if user is not Manager
		List<ElementEntity> fillterdRequest = 
				request
				.stream()
				.filter(element -> 
					element.getExpirationDate() == null ? 
					true : element.getExpirationDate().getTime() - System.currentTimeMillis() > 0)
				.collect(Collectors.toList());

		count -= (request.size() - fillterdRequest.size()) / size;
		
		if (!fillterdRequest.isEmpty())
			fillterdRequest.get(0).getAttributes().put("count", count);
		
		return fillterdRequest;
	}
	
	@Override
	@Transactional(readOnly = true)
	@ValidateNull
	@ValidateUser
	@Logger
	@PlaygroundPerformance
	public List<ElementEntity> getAllElementsByAttributeAndItsValue(String userPlayground, String email, int size,
			int page, String attributeName, String value) throws Exception {
		
		UserEntity userAskingForElements = userService.getRegisteredUser(userPlayground, email);
		
		if(attributeName.toLowerCase().equals("name")) {
			Page<ElementEntity> thePage = 
					this
					.elements
					.findAllByNameLike(value, PageRequest.of(page, size, Direction.DESC, "creationDate"));
			
			long count = thePage.getTotalPages();
			
			List<ElementEntity> request = thePage.getContent();
			
			if (!request.isEmpty())
				request.get(0).getAttributes().put("count", count);
			
			if(userAskingForElements.getRole().equals("Manager"))
				return request;
			
			// You get here if user is not Manager
			List<ElementEntity> fillterdRequest = 
					request
					.stream()
					.filter(element -> 
						element.getExpirationDate() == null ? 
						true : element.getExpirationDate().getTime() - System.currentTimeMillis() > 0)
					.collect(Collectors.toList());
			
			count -= (request.size() - fillterdRequest.size()) / size;
			
			if (!fillterdRequest.isEmpty())
				fillterdRequest.get(0).getAttributes().put("count", count);
			
			return fillterdRequest;	
		}
		else if(attributeName.toLowerCase().equals("type")) {
			Page<ElementEntity> thePage = this
					.elements
					.findAllByTypeEquals(
							value, 
							PageRequest.of(page, size, Direction.DESC, "creationDate"));
			
			long count = thePage.getTotalPages();
			
			List<ElementEntity> request = thePage.getContent();
			
			if (!request.isEmpty())
				request.get(0).getAttributes().put("count", count);
			
			if(userAskingForElements.getRole().equals("Manager"))
				return request;
			
			// You get here if user is not Manager
			List<ElementEntity> fillterdRequest = 
					request
					.stream()
					.filter(element -> 
						element.getExpirationDate() == null ? 
						true : element.getExpirationDate().getTime() - System.currentTimeMillis() > 0)
					.collect(Collectors.toList());
		
			count -= (request.size() - fillterdRequest.size()) / size;
			
			if (!fillterdRequest.isEmpty())
				fillterdRequest.get(0).getAttributes().put("count", count);
			
			return fillterdRequest;
		}
		else
			throw new Exception("No such attribute name");
			
	}

	@Override
	@Transactional
	@ValidateManager
	@ValidateNull
	@Logger
	@PlaygroundPerformance
	public void updateElement(String userPlayground, String email, String id, String playground, ElementEntity newElement) 
			throws Exception {
		if(this.elements.existsById(id + delim + playground)) {
			ElementEntity existing = getElement(userPlayground, email, id, playground);
			
			if(!newElement.getAttributes().equals(existing.getAttributes()))
				existing.setAttributes(newElement.getAttributes());
			
			if(!newElement.getName().equals(existing.getName()))
				existing.setName(newElement.getName());
			
			if(!newElement.getType().equals(existing.getType()))
				existing.setType(newElement.getType());
			
			if(!newElement.getX().equals(existing.getX()))
				existing.setX(newElement.getX());
			
			if(!newElement.getY().equals(existing.getY()))
				existing.setY(newElement.getY());
			
			if(!newElement.getCreatorEmail().equals(existing.getCreatorEmail())
					|| !newElement.getCreatorPlayground().equals(existing.getCreatorPlayground()))
				throw new RuntimeException("You are trying to edit read-only attributes");
			
			this.elements.save(existing);
			
		} else {
			throw new ElementNotFoundException("There's no such element");
		}
	}

	@Override
	@Transactional
	@Logger
	@PlaygroundPerformance
	public void cleanup() {
		this.elements.deleteAll();
	}

}
