package univ.ajou.cos;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private List<Item> items;
    private int itemLayout;
    private CalcTotal calcTotal;

    public ItemAdapter(List<Item> items, int itemLayout, CalcTotal ct) {
        this.items = items;
        this.itemLayout = itemLayout;
        this.calcTotal = ct;
    }

    public interface CalcTotal {
        public void getTotal(List<Item> items);
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new ViewHolder(v);
    }

    @Override public void onBindViewHolder(ViewHolder holder, final int position) {
        Item item = items.get(position);
        holder.name.setText(item.getName());
        holder.price.setText(String.format("%,d", item.getPrice())+"원");
        holder.desc.setText(item.getDesc());
        holder.count.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int thisCount = Integer.parseInt(editable.toString().equals("") ? "0" : editable.toString());
                items.get(position).setCount(thisCount);
                calcTotal.getTotal(items);
            }
        });
    }

    @Override public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView price;
        public TextView desc;
        public EditText count;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.item_name);
            price = (TextView) itemView.findViewById(R.id.item_price);
            desc = (TextView) itemView.findViewById(R.id.item_desc);
            count = (EditText) itemView.findViewById(R.id.item_count);
        }
    }
}


