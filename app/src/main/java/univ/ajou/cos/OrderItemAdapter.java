package univ.ajou.cos;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ViewHolder> {

    private List<OrderItem> items;
    private int itemLayout;
    private OrderManage orderManage;

    public OrderItemAdapter(List<OrderItem> items, int itemLayout, OrderManage orderManage) {
        this.items = items;
        this.itemLayout = itemLayout;
        this.orderManage = orderManage;
    }

    public interface OrderManage {
        public void changeStatus(int orderid, int itemid);
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new ViewHolder(v);
    }

    @Override public void onBindViewHolder(ViewHolder holder, final int position) {
        final OrderItem item = items.get(position);
        holder.name.setText(item.getName());
        holder.amount.setText(item.getAmount()+"");
        holder.orderid.setText("Order Id : "+item.getOrderId());
        holder.status.setText(item.getStatus());
        holder.btnStatusChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!item.getStatus().equals("done")) {
                    orderManage.changeStatus(item.getOrderId(), item.getId());
                }
            }
        });

        // COLOR
        if(item.getStatus().equals("done")) {
            holder.name.setTextColor(Color.RED);
            holder.amount.setTextColor(Color.RED);
            holder.orderid.setTextColor(Color.RED);
            holder.status.setTextColor(Color.RED);
        }
        else {
            holder.name.setTextColor(Color.GRAY);
            holder.amount.setTextColor(Color.GRAY);
            holder.orderid.setTextColor(Color.GRAY);
            holder.status.setTextColor(Color.GRAY);
        }
    }

    @Override public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView amount;
        public TextView orderid;
        public TextView status;
        public Button btnStatusChange;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.item_name);
            amount = (TextView) itemView.findViewById(R.id.item_amount);
            orderid = (TextView) itemView.findViewById(R.id.item_order_id);
            status = (TextView) itemView.findViewById(R.id.item_status);
            btnStatusChange = (Button) itemView.findViewById(R.id.btn_status_change);
        }
    }
}


