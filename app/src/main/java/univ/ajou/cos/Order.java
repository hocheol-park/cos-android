package univ.ajou.cos;


import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class Order {

    public int orderid;
    public String desc;
    public int price;
    public String status;
    public String date;
    public List<OrderItem> orderItem;

    public Order(int id, String desc, int price, String status, String date, String oi) {
        super();
        this.orderid = id;
        this.desc = desc;
        this.price = price;
        this.status = status;
        this.date = date;

        orderItem = new ArrayList<OrderItem>();
        try {
            JSONArray jsonItem = new JSONArray(oi);
            for(int i=0; i<jsonItem.length(); i++) {
                orderItem.add(new OrderItem(
                        jsonItem.getJSONObject(i).getInt("itemId"),
                        jsonItem.getJSONObject(i).getString("itemName"),
                        jsonItem.getJSONObject(i).getInt("amount"),
                        jsonItem.getJSONObject(i).getString("status")
                ));
            }
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    public int getId() {
        return orderid;
    }

    public String getDesc() {
        return desc;
    }

    public int getPrice() {
        return price;
    }

    public String getStatus() {
        return status;
    }

    public String getDate() {
        return date;
    }

    public List<OrderItem> getOrderItem() {
        return orderItem;
    }

}
