package generated.services;

import generated.Item;
import generated.entity.dto.ItemDTO;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("all")
public interface ItemService {
  Optional<List<Item>> getAll();
  
  Optional<Item> getItem(final String id);
  
  Optional<Item> addItem(final ItemDTO itemDTO);
}
