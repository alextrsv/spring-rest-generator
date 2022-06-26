package generated;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.xtend.lib.annotations.EqualsHashCode;
import org.eclipse.xtend.lib.annotations.ToString;
import javax.persistence.JoinColumn;
import generator.annotations.Entity;
import org.eclipse.xtend.lib.annotations.Accessors;


@Entity
public class Item {

    
    protected String id;
    
    protected String name;
    
    protected String type;
    protected int price;
    
    protected Item parent;

    }



