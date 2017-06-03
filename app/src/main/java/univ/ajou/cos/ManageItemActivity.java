package univ.ajou.cos;

import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import univ.ajou.cos.util.HttpCallback;
import univ.ajou.cos.util.HttpConnector;

import static univ.ajou.cos.Constant.URL_HOST;

public class ManageItemActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Button btnAdd;
    private ProgressBar loadingbar;

    private List<Item> item;

    private final static String TAG = "ManageItemActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_item);

        initData();
        initView();

    }

    private void initData() {

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
        connector.setEndpoint(URL_HOST);
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

                            mAdapter = new ManageItemAdapter(item, R.layout.list_manage_item, new ManageItemAdapter.ItemManage() {
                                @Override
                                public void deleteItem(int itemid) {
                                    deleteOneItem(itemid);
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
        toolbar.setTitle(R.string.manage_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.item_list_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        btnAdd = (Button) findViewById(R.id.btn_add_item);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem();
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

    public void addItem() {

        LayoutInflater inflater = (LayoutInflater) getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        LinearLayout dialogLayout = (LinearLayout) inflater.inflate( R.layout.dialog_add_item, null );

        final EditText inputName = (EditText) dialogLayout.findViewById(R.id.input_name);
        final EditText inputPrice = (EditText) dialogLayout.findViewById(R.id.input_price);
        final EditText inputDesc = (EditText) dialogLayout.findViewById(R.id.input_desc);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.title_add_item);
        builder.setView(dialogLayout);
        builder.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = inputName.getText().toString();
                        String strPrice = inputPrice.getText().toString();
                        String strDesc = inputDesc.getText().toString();
                        if(strName.equals("")) {
                            Toast.makeText(getApplicationContext(), "Please input name", Toast.LENGTH_LONG).show();
                        }
                        else if(strPrice.equals("")) {
                            Toast.makeText(getApplicationContext(), "Please input price", Toast.LENGTH_LONG).show();
                        }
                        else if(strDesc.equals("")) {
                            Toast.makeText(getApplicationContext(), "Please input description", Toast.LENGTH_LONG).show();
                        }
                        else {
                            makeNewItem(strName, strPrice, strDesc);
                        }
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

    public void makeNewItem(String name, String price, String desc) {
        HttpConnector connector = new HttpConnector(this);
        try {
            JSONObject params = new JSONObject();
            params.put("mode", Constant.MODE_ADD_ITEM);
            params.put("name", name);
            params.put("price", Integer.parseInt(price));
            params.put("desc", desc);
            connector.setParam(params);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        connector.setEndpoint(URL_HOST);
        connector.get(new HttpCallback() {
            @Override
            public void onFailure(int code, final String msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ManageItemActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(int code, final String msg, String data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ManageItemActivity.this, msg, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        });
    }

    public void deleteOneItem(final int itemid) {
        HttpConnector connector = new HttpConnector(this);
        try {
            JSONObject params = new JSONObject();
            params.put("mode", Constant.MODE_DELETE_ITEM);
            params.put("itemid", itemid);
            connector.setParam(params);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        connector.setEndpoint(URL_HOST);
        connector.get(new HttpCallback() {
            @Override
            public void onFailure(int code, final String msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ManageItemActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(int code, final String msg, String data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ManageItemActivity.this, msg, Toast.LENGTH_SHORT).show();

                        for(int i=0; i<item.size(); i++) {
                            if(item.get(i).getId() == itemid) {
                                item.remove(i);
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
