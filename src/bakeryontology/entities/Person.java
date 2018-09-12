package bakeryontology.entities;

/**
 * @author Adrian Wilk
 * @since 15.05.2017
 */

public class Person {
    
    private int id;
    private String fullName;
    private String type;

    public Person() {
    }

    
    public Person(int id, String fullName, String type) {
        this.id = id;
        this.fullName = fullName;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getType() {
        return type;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Person{" + "id=" + id + ", fullName=" + fullName + ", type=" + type + '}';
    }
    
    
    
}
