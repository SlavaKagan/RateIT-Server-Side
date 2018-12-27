package playground.dal;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface NumberGeneratorDao extends PagingAndSortingRepository<NumberGenerator, String> {

}
