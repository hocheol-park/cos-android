package univ.ajou.cos;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import univ.ajou.cos.util.HttpCallback;
import univ.ajou.cos.util.HttpConnector;
import univ.ajou.cos.util.PrefUtil;

public class OrderActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private TextView textTotal;
    private Button btnOrder, btnCancel;
    private ProgressBar loadingbar;

    private int total;
    private List<Item> item;
    private List<Item> orderItem;

    private final static String TAG = "OrderActivity";

    private PrefUtil prefUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        prefUtil = new PrefUtil(this);

        initData();
        initView();

    }

    private void initData() {
        // init total price
        total = 0;

        // get item data
        item = new ArrayList<Item>();

        HttpConnector connector = new HttpConnector(this);
        try {
            JSONObject params = new JSONObject();
            params.put("mode", Constant.MODE_GET_MENU);
            connector.setParam(params);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        connector.setEndpoint(Constant.URL_HOST);
        connector.get(new HttpCallback() {
            @Override
            public void onFailure(int code, String msg) {
                Log.d(TAG, "onFailure : "+msg);
            }

            @Override
            public void onResponse(int code, String msg, String data) {
                Log.d(TAG, "onResponse : "+data);
                try {
                    JSONObject json = new JSONObject(data);
                    JSONArray jsonItem = json.getJSONArray("list");
                    for(int i=0; i<jsonItem.length(); i++) {
                        Item it = new Item(
                            jsonItem.getJSONObject(i).getInt("id"),
                            jsonItem.getJSONObject(i).getString("name"),
                            jsonItem.getJSONObject(i).getInt("price"),
                            jsonItem.getJSONObject(i).getString("description")
                        );

                        item.add(it);
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            mAdapter = new ItemAdapter(item, R.layout.list_item, new ItemAdapter.CalcTotal() {
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
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

//        Item item01 = new Item(1, "Americano", 1000, "This is Americano");
//        Item item02 = new Item(2, "Cafe Latte", 2000, "This is Cafe Latte");
//        Item item03 = new Item(3, "Cafe Mocca", 2500, "This is Cafe Mocca");
//        Item item04 = new Item(4, "Cappuccino", 3000, "This is Cappuccino");
//        Item item05 = new Item(5, "Smoothie", 4000, "This is Smoothie");
//        Item item06 = new Item(6, "Ice Cream", 5000, "This is Ice Cream");
//
//        item.add(item01);
//        item.add(item02);
//        item.add(item03);
//        item.add(item04);
//        item.add(item05);
//        item.add(item06);
    }

    private void initView() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.order);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.item_list_view);
        mRecyclerView.setHasFixedSize(true);

        textTotal = (TextView) findViewById(R.id.text_total);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

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

        loadingbar = (ProgressBar) findViewById(R.id.loadingbar);
        loadingbar.setVisibility(View.GONE);
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
            Toast.makeText(this, R.string.msg_choose_one, Toast.LENGTH_SHORT).show();
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
            builder.setTitle(R.string.title_check_order);
            builder.setMessage(msg + "\nDo you want to order");
            builder.setPositiveButton("Confirm",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            makeNewSale(jsonArray.toString());
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

    private void makeNewSale(String data) {

        HttpConnector connector = new HttpConnector(this);
        try {
            JSONObject params = new JSONObject();
            params.put("mode", Constant.MODE_NEW_SALE);
            params.put("data", data);
            params.put("uid", prefUtil.getPrefDataString("DEVICE_ID", ""));
            params.put("unum", prefUtil.getPrefDataString("PHONE_NUMBER", ""));
            connector.setParam(params);
            Log.e(TAG, "pamars : "+params);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        connector.setEndpoint(Constant.URL_HOST);

        loadingbar.setVisibility(View.VISIBLE);
        connector.post(new HttpCallback() {
            @Override
            public void onFailure(int code, final String msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingbar.setVisibility(View.GONE);
                        Toast.makeText(OrderActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(int code, final String msg, final String data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("SDF", msg);
                        loadingbar.setVisibility(View.GONE);
                        Toast.makeText(OrderActivity.this, msg, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        });

    }
}
