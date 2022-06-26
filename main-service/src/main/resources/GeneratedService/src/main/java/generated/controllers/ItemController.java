package generated.controllers;

import generated.Item;
import generated.entity.dto.ItemDTO;
import generated.services.ItemService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/item")
@SuppressWarnings("all")
public class ItemController {
  @Autowired
  private ItemService itemService;
  
  @GetMapping(path = "/{id}")
  public ResponseEntity<Item> getItem(@PathVariable final String id) {
    return itemService.getItem(id).map(ResponseEntity::ok)
                                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }
  
  @GetMapping
  public ResponseEntity<List<Item>> getAll() {
    return itemService.getAll().map(ResponseEntity::ok)
                                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }
  
  @PostMapping
  public ResponseEntity<Item> addNewItem(@RequestBody final ItemDTO itemDTO) {
    return itemService.addItem(itemDTO).map(e -> new ResponseEntity<>(e, HttpStatus.CREATED))
                                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }
}
