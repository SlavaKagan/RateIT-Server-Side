package playground.dal;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import playground.logic.ActivityEntity;

public interface ActivityDao extends PagingAndSortingRepository<ActivityEntity, String> {
	
	public List<ActivityEntity> findAllByTypeEqualsAndElementIdEquals(
			@Param("type") String type,
			@Param("elementId") String id,
			Pageable pageable);
}
