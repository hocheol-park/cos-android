package univ.ajou.cos;

public class Item {

    public int id;
    public String name;
    public int price;
    public String desc;
    public int count;

    public Item(int id, String name, int price, String desc) {
        super();
        this.id = id;
        this.name = name;
        this.price = price;
        this.desc = desc;
        this.count = 0;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getDesc() {
        return desc;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}
