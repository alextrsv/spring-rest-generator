package generated.entity.dto;

import generated.Item;
import lombok.Data;

@Data
@SuppressWarnings("all")
public class ItemDTO {
  private String id;
  
  private String name;
  
  private String type;
  
  private int price;
  
  private Item parent;
}
