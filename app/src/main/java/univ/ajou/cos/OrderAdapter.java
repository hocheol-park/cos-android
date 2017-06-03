package univ.ajou.cos;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private List<Order> orders;
    private int itemLayout;
    private Context mContext;

    public OrderAdapter(List<Order> orders, int itemLayout, Context context) {
        this.orders = orders;
        this.itemLayout = itemLayout;
        this.mContext = context;
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new ViewHolder(v);
    }

    @Override public void onBindViewHolder(ViewHolder holder, final int position) {
        Order order = orders.get(position);
        holder.orderid.setText("Order ID : "+order.getId());
        holder.price.setText(String.format("%,d", order.getPrice())+"원");
        holder.desc.setText(order.getDesc());
        holder.status.setText(order.getStatus());
        holder.date.setText(order.getDate());

        for(int i=0; i<order.getOrderItem().size(); i++) {
            TextView oi = new TextView(mContext);
            oi.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            oi.setTextColor(mContext.getResources().getColor(R.color.colorTextColor));
            oi.setText(order.getOrderItem().get(i).getAmount()+" "+order.getOrderItem().get(i).getName()+" is "+order.getOrderItem().get(i).getStatus());
            oi.setGravity(Gravity.RIGHT);

            holder.layoutItem.addView(oi);
        }

    }

    @Override public int getItemCount() {
        return orders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView orderid;
        public TextView price;
        public TextView desc;
        public TextView status;
        public TextView date;
        public LinearLayout layoutItem;

        public ViewHolder(View itemView) {
            super(itemView);
            orderid = (TextView) itemView.findViewById(R.id.order_id);
            desc = (TextView) itemView.findViewById(R.id.order_desc);
            price = (TextView) itemView.findViewById(R.id.order_price);
            status = (TextView) itemView.findViewById(R.id.order_status);
            date = (TextView) itemView.findViewById(R.id.order_date);
            layoutItem = (LinearLayout) itemView.findViewById(R.id.layout_order_item);
        }
    }
}


