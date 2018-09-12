package bakeryontology.entities;

/**
 * @author Adrian Wilk
 * @since 15.05.2017
 */

public class Transaction {
    
    private String id;
    private double cost;
    private String employee;
    private String client;
    private String baking;

    public Transaction() {
    }

    public Transaction(String id, double cost, String employee, String client, String baking) {
        this.id = id;
        this.cost = cost;
        this.employee = employee;
        this.client = client;
        this.baking = baking;
    }
    

    public String getId() {
        return id;
    }

    public double getCost() {
        return cost;
    }

    public String getEmployee() {
        return employee;
    }

    public String getClient() {
        return client;
    }

    public String getBaking() {
        return baking;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public void setBaking(String baking) {
        this.baking = baking;
    }

    @Override
    public String toString() {
        return "Transaction{" + "id=" + id + ", cost=" + cost + ", employee=" + employee + ", client=" + client + ", baking=" + baking + '}';
    }
    
    
    
}
