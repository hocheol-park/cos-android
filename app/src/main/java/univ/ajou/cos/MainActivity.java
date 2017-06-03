package univ.ajou.cos;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import univ.ajou.cos.util.HttpCallback;
import univ.ajou.cos.util.HttpConnector;
import univ.ajou.cos.util.PermissionListener;
import univ.ajou.cos.util.PrefUtil;
import univ.ajou.cos.util.TedPermission;

import static univ.ajou.cos.Constant.URL_HOST;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Button btnOrder;
    private Button btnMonitor;
    private TextView userName, userPhone;

    private PrefUtil prefUtil;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        btnOrder = (Button) findViewById(R.id.btn_order);
        btnMonitor = (Button) findViewById(R.id.btn_monitor_progress);

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, OrderActivity.class);
                startActivity(i);
            }
        });

        btnMonitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, MyMenuActivity.class);
                startActivity(i);
            }
        });

        userName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.user_name);
        userPhone = (TextView) navigationView.getHeaderView(0).findViewById(R.id.user_phone);

        prefUtil = new PrefUtil(this);

        checkPermission();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_order) {
            Intent i = new Intent(MainActivity.this, OrderActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_mymenu) {
            Intent i = new Intent(MainActivity.this, MyMenuActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_order_list) {
            Intent i = new Intent(MainActivity.this, ManageOrderActivity.class);
            startActivity(i);

        } else if (id == R.id.nav_manage_item) {
            Intent i = new Intent(MainActivity.this, ManageItemActivity.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public String getPhoneNumber() {
        TelephonyManager telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String phoneNumber ="";

        try {
            if (telephony.getLine1Number() != null) {
                phoneNumber = telephony.getLine1Number();
            }
            else {
                if (telephony.getSimSerialNumber() != null) {
                    phoneNumber = telephony.getSimSerialNumber();
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return phoneNumber;
    }

    public String getDeviceId() {
        TelephonyManager telephony = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        return telephony.getDeviceId();
    }

    public PermissionListener permissionlistener;
    public void checkPermission() {

        permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                setUserInfo();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "You must agree the permission" , Toast.LENGTH_SHORT).show();
                finish();
            }
        };

        // 마시멜로우 이상 버전의 경우 체크.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            new TedPermission(this)
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage("You must agree the permission")
                    .setPermissions(
                            Manifest.permission.READ_PHONE_STATE
                    )
                    .check();
        }
        else {
            setUserInfo();
        }

    }

    public void setUserInfo() {

        String deviceId = getDeviceId();
        String phoneNumber = getPhoneNumber();

        prefUtil.setPrefDataString("DEVICE_ID", deviceId);
        prefUtil.setPrefDataString("PHONE_NUMBER", phoneNumber);

        userName.setText(deviceId);
        userPhone.setText(phoneNumber);

        FirebaseMessaging.getInstance().subscribeToTopic("news");
        String FCMToken = FirebaseInstanceId.getInstance().getToken();

        updateUserInfo(deviceId, phoneNumber, FCMToken);
    }

    public void updateUserInfo(String uid, String unum, String token) {

        HttpConnector connector = new HttpConnector(this);
        try {
            JSONObject params = new JSONObject();
            params.put("mode", Constant.MODE_UPDATE_USER);
            params.put("uid", uid);
            params.put("unum", unum);
            params.put("token", token);
            connector.setParam(params);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        connector.setEndpoint(URL_HOST);
        connector.get(new HttpCallback() {
            @Override
            public void onFailure(int code, String msg) {

            }

            @Override
            public void onResponse(int code, String msg, String data) {
                Log.e(TAG, "DATA : "+msg);
            }
        });
    }
}
