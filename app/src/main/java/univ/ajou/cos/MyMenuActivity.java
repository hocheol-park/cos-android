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
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import univ.ajou.cos.util.HttpCallback;
import univ.ajou.cos.util.HttpConnector;
import univ.ajou.cos.util.PrefUtil;

public class MyMenuActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ProgressBar loadingbar;
    private TextView textjson;

    private final static String TAG = "OrderActivity";

    private PrefUtil prefUtil;

    private List<Order> order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_menu);

        prefUtil = new PrefUtil(this);

        initView();
        initData();
    }

    private void initData() {

        // get item data
        order = new ArrayList<Order>();

        HttpConnector connector = new HttpConnector(this);
        try {
            JSONObject params = new JSONObject();
            params.put("mode", Constant.MODE_MY_ORDER);
            params.put("uid", prefUtil.getPrefDataString("DEVICE_ID", ""));
            connector.setParam(params);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        connector.setEndpoint(Constant.URL_HOST);
        connector.get(new HttpCallback() {
            @Override
            public void onFailure(int code, String msg) {
                Log.e(TAG, msg);
            }

            @Override
            public void onResponse(int code, final String msg, final String data) {
                Log.d(TAG, "onResponse : "+data);
                try {
                    //JSONObject json = new JSONObject(data);
                    //JSONArray jsonItem = json.getJSONArray("list");
                    JSONArray jsonItem = new JSONArray(data);
                    for(int i=0; i<jsonItem.length(); i++) {
                        Order od = new Order(
                                jsonItem.getJSONObject(i).getInt("orderId"),
                                jsonItem.getJSONObject(i).getString("description"),
                                jsonItem.getJSONObject(i).getInt("price"),
                                jsonItem.getJSONObject(i).getString("status"),
                                jsonItem.getJSONObject(i).getString("date"),
                                jsonItem.getJSONObject(i).getString("orderItem")

                        );

                        order.add(od);
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter = new OrderAdapter(order, R.layout.list_order, getApplicationContext());
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
        toolbar.setTitle(R.string.monitor_progress);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.item_list_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(MyMenuActivity.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.addItemDecoration(new DividerItemDecoration(MyMenuActivity.this, DividerItemDecoration.VERTICAL));
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
}
