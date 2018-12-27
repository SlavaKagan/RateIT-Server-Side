package playground.dal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import playground.logic.ActivityEntity;

public interface ActivityDao extends PagingAndSortingRepository<ActivityEntity, String> {
	
	public Page<ActivityEntity> findAllByTypeEqualsAndElementIdEquals(
			@Param("type") String type,
			@Param("elementId") String id,
			Pageable pageable);
}
