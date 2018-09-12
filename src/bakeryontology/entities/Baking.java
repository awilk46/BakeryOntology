package bakeryontology.entities;

/**
 * @author Adrian Wilk
 * @since 15.05.2017
 */

public class Baking {
        
    private int id;
    private String name;
    private int validity;
    private String type;

    public Baking() {
    }

    public Baking(int id, String name, int validity, String type) {
        this.id = id;
        this.name = name;
        this.validity = validity;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getValidity() {
        return validity;
    }

    public String getType() {
        return type;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValidity(int validity) {
        this.validity = validity;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Baking{" + "id=" + id + ", name=" + name + ", validity=" + validity + ", type=" + type + '}';
    }
    
    
    
    
}
