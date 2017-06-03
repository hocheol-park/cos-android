package univ.ajou.cos;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class ManageItemAdapter extends RecyclerView.Adapter<ManageItemAdapter.ViewHolder> {

    private List<Item> items;
    private int itemLayout;
    private ItemManage itemManage;

    public ManageItemAdapter(List<Item> items, int itemLayout, ItemManage itemManage) {
        this.items = items;
        this.itemLayout = itemLayout;
        this.itemManage = itemManage;
    }

    public interface ItemManage {
        public void deleteItem(int itemid);
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new ViewHolder(v);
    }

    @Override public void onBindViewHolder(ViewHolder holder, final int position) {
        Item item = items.get(position);

        final int itemId = item.getId();
        holder.id.setText(itemId+"");
        holder.name.setText(item.getName());
        holder.price.setText(String.format("%,d", item.getPrice())+"원");
        holder.desc.setText(item.getDesc());
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemManage.deleteItem(itemId);
            }
        });
    }

    @Override public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView id;
        public TextView name;
        public TextView price;
        public TextView desc;
        public Button btnDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            id = (TextView) itemView.findViewById(R.id.item_id);
            name = (TextView) itemView.findViewById(R.id.item_name);
            price = (TextView) itemView.findViewById(R.id.item_price);
            desc = (TextView) itemView.findViewById(R.id.item_desc);
            btnDelete = (Button) itemView.findViewById(R.id.btn_delete_item);
        }
    }
}


