package univ.ajou.cos;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import univ.ajou.cos.util.HttpCallback;
import univ.ajou.cos.util.HttpConnector;
import univ.ajou.cos.util.PrefUtil;

public class ManageOrderActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private List<OrderItem> orderItem;

    private final static String TAG = "ManageOrderActivity";

    private PrefUtil prefUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_order);

        prefUtil = new PrefUtil(this);

        initData();
        initView();

    }

    private void initData() {

        // get item data
        orderItem = new ArrayList<OrderItem>();

        HttpConnector connector = new HttpConnector(this);
        try {
            JSONObject params = new JSONObject();
            params.put("mode", Constant.MODE_GET_ORDER_LIST);
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
                    //JSONObject json = new JSONObject(data);
                    //JSONArray jsonItem = json.getJSONArray("list");
                    JSONArray jsonItem = new JSONArray(data);
                    for(int i=0; i<jsonItem.length(); i++) {
                        OrderItem oi = new OrderItem(
                            jsonItem.getJSONObject(i).getInt("orderId"),
                            jsonItem.getJSONObject(i).getInt("itemId"),
                            jsonItem.getJSONObject(i).getString("itemName"),
                            jsonItem.getJSONObject(i).getInt("amount"),
                            jsonItem.getJSONObject(i).getString("status")
                        );

                        orderItem.add(oi);
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            mAdapter = new OrderItemAdapter(orderItem, R.layout.list_order_list, new OrderItemAdapter.OrderManage() {
                                @Override
                                public void changeStatus(int orderid, int itemid) {
                                    changeOrderStatus(orderid, itemid);
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
    }

    private void initView() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.manage_order_list);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.item_list_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
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

    public void changeOrderStatus(final int orderid, final int itemid) {
        HttpConnector connector = new HttpConnector(this);
        try {
            JSONObject params = new JSONObject();
            params.put("mode", Constant.MODE_CHANGE_STATUS);
            params.put("orderid", orderid);
            params.put("itemid", itemid);
            connector.setParam(params);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        connector.setEndpoint(Constant.URL_HOST);
        connector.get(new HttpCallback() {
            @Override
            public void onFailure(int code, final String msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ManageOrderActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(int code, final String msg, String data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ManageOrderActivity.this, msg, Toast.LENGTH_SHORT).show();

                        for(int i=0; i<orderItem.size(); i++) {
                            if(orderItem.get(i).getId() == itemid && orderItem.get(i).getOrderId() == orderid) {
                                //orderItem.remove(i);
                                orderItem.get(i).setStatus("done");
                                break;
                            }
                        }

                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }
}
