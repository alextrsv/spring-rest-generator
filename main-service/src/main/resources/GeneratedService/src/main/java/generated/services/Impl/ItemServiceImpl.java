package generated.services.Impl;

import generated.Item;
import generated.entity.dto.ItemDTO;
import generated.repositories.ItemRepository;
import generated.services.ItemService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@SuppressWarnings("all")
public class ItemServiceImpl implements ItemService {
  @Autowired
  private ItemRepository itemRepository;
  
  @Override
  public Optional<Item> getItem(final String id) {
    return itemRepository.findById(id);
  }
  
  @Override
  public Optional<List<Item>> getAll() {
    return Optional.of((List<Item>) itemRepository.findAll());
  }
  
  public Optional<Item> addItem(final ItemDTO itemDTO) {
    Item newitem = new Item(itemDTO);
    return Optional.ofNullable(itemRepository.save(newitem));
  }
}
