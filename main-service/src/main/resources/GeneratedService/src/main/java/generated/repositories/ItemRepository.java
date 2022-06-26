package generated.repositories;

import generated.Item;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("all")
public interface ItemRepository extends CrudRepository<Item, String> {
  List<Item> findByName(@Param(value = "name") final String name);
  
  List<Item> findByType(@Param(value = "type") final String type);
  
  List<Item> findByPrice(@Param(value = "price") final int price);
}
