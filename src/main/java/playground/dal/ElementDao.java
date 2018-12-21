package playground.dal;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import playground.logic.ElementEntity;

public interface ElementDao extends PagingAndSortingRepository<ElementEntity, String> {
	public Page<ElementEntity> findAllByXBetweenAndYBetween(
			@Param("lessX") double lessX,
			@Param("moreX") double moreX,
			@Param("lessY") double lessy,
			@Param("moreY") double morey,
			Pageable pageable);
	
	public Page<ElementEntity> findAllByNameLike(
			@Param("value") String value,
			Pageable pageable);
	
	public Page<ElementEntity> findAllByTypeEquals(
			@Param("value") String value,
			Pageable pageable);
}
