package playground.dal;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import playground.logic.ElementEntity;

public interface ElementDao extends PagingAndSortingRepository<ElementEntity, String> {
	public List<ElementEntity> findAllByXBetweenAndYBetween(
			@Param("lessX") double lessX,
			@Param("moreX") double moreX,
			@Param("lessY") double lessy,
			@Param("moreY") double morey,
			Pageable pageable);
	
	public List<ElementEntity> findAllByNameEquals(
			@Param("value") String value,
			Pageable pageable);
	
	public List<ElementEntity> findAllByTypeEquals(
			@Param("value") String value,
			Pageable pageable);
}
