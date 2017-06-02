package univ.ajou.cos;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import univ.ajou.cos.util.HttpCallback;
import univ.ajou.cos.util.HttpConnector;

public class OrderActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private TextView textTotal;
    private Button btnOrder, btnCancel;

    private int total;
    private List<Item> data;
    private List<Item> orderItem;

    private final static String URL_HOST = "http://localhost";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        initData();
        initView();

    }

    private void initData() {
        // init total price
        total = 0;

        // get item data
        data = new ArrayList<Item>();

        HttpConnector connector = new HttpConnector(this);
        try {
            JSONObject params = new JSONObject();
            params.put("mode", "getmenu");
            connector.setParam(params);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        connector.setEndpoint(URL_HOST);
        connector.get(new HttpCallback() {
            @Override
            public void onFailure(int code, String msg) {
                Log.d("SDF", "SDF : "+msg);
            }

            @Override
            public void onResponse(int code, String msg, String data) {
                Log.d("SDF", "SDF : "+data);
            }
        });

        Item item01 = new Item(1, "Americano", 1000, "This is Americano");
        Item item02 = new Item(2, "Cafe Latte", 2000, "This is Cafe Latte");
        Item item03 = new Item(3, "Cafe Mocca", 2500, "This is Cafe Mocca");
        Item item04 = new Item(4, "Cappuccino", 3000, "This is Cappuccino");
        Item item05 = new Item(5, "Smoothie", 4000, "This is Smoothie");
        Item item06 = new Item(6, "Ice Cream", 5000, "This is Ice Cream");

        data.add(item01);
        data.add(item02);
        data.add(item03);
        data.add(item04);
        data.add(item05);
        data.add(item06);
    }

    private void initView() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.order);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.item_list_view);
        mRecyclerView.setHasFixedSize(true);

        textTotal = (TextView) findViewById(R.id.text_total);

        mAdapter = new ItemAdapter(data, R.layout.list_item, new ItemAdapter.CalcTotal() {
            @Override
            public void getTotal(List<Item> items) {
                orderItem = items;
                total = 0;
                for(int i=0; i<mAdapter.getItemCount(); i++) {
                    total += (items.get(i).getCount() * items.get(i).getPrice() );
                }
                textTotal.setText(String.format("%,d", total));
            }
        });

        mRecyclerView.setAdapter(mAdapter);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        btnOrder = (Button) findViewById(R.id.btn_make_order);
        btnCancel = (Button) findViewById(R.id.btn_order_cancel);

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkOrder();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void checkOrder() {
        if(orderItem == null) {

        }
        else {
            String msg = "";
            final JSONArray jsonArray = new JSONArray();
            for(int i=0; i<orderItem.size(); i++) {
                if(orderItem.get(i).getCount() != 0) {
                    msg += orderItem.get(i).getCount() + " " + orderItem.get(i).getName() + "\n";
                    try {
                        jsonArray.put(new JSONObject("{\"id\":" + orderItem.get(i).getId() + ",\"quant\":" + orderItem.get(i).getCount() + "}"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Item List Check");
            builder.setMessage(msg + "\nDo you want to order");
            builder.setPositiveButton("Confirm",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(), jsonArray.toString(), Toast.LENGTH_LONG).show();
                        }
                    });
            builder.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(), "Choose NO", Toast.LENGTH_LONG).show();
                        }
                    });
            builder.show();
        }
    }
}
