package univ.ajou.cos;

public class OrderItem {

    public int orderid;
    public int itemid;
    public String itemname;
    public int amount;
    public String status;

    public OrderItem(int id, String name, int amount, String status) {
        super();
        this.itemid = id;
        this.itemname = name;
        this.amount = amount;
        this.status = status;
    }

    public OrderItem(int orderid, int id, String name, int amount, String status) {
        super();
        this.orderid = orderid;
        this.itemid = id;
        this.itemname = name;
        this.amount = amount;
        this.status = status;
    }

    public int getOrderId() {
        return orderid;
    }

    public int getId() {
        return itemid;
    }

    public String getName() {
        return itemname;
    }

    public int getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String st) {
        this.status = st;
    }

}
