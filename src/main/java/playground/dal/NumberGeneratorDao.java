package playground.dal;

import org.springframework.data.repository.CrudRepository;

public interface NumberGeneratorDao extends CrudRepository<NumberGenerator, Long> {

}
