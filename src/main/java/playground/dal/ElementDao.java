package playground.dal;

import org.springframework.data.repository.PagingAndSortingRepository;

import playground.logic.ElementEntity;

public interface ElementDao extends PagingAndSortingRepository<ElementEntity, String> {

}
