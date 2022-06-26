package generated;

import generated.entity.dto.ItemDTO;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "item")
@SuppressWarnings("all")
public class Item {
  @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected String id;
  
  @Column(name = "name")
  protected String name;
  
  @Column(name = "type")
  protected String type;
  
  @Column(name = "price")
  protected int price;
  
  protected Item parent;
  
  public Item(final ItemDTO itemDTO) {
    this.name = itemDTO.getName();
    this.type = itemDTO.getType();
    this.price = itemDTO.getPrice();
    this.parent = itemDTO.getParent();
  }
}