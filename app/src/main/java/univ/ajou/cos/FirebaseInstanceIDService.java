package univ.ajou.cos;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import univ.ajou.cos.util.PrefUtil;

public class FirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + token);

        PrefUtil prefUtil = new PrefUtil(this);
        prefUtil.setPrefDataString("FCM_TOKEN", token);

        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.

        // OkHttpClient client = new OkHttpClient();
        // RequestBody body = new FormBody.Builder()
        //         .add("Token", token)
        //         .build();

        // //request
        // Request request = new Request.Builder()
        //         .url("http://서버주소/fcm/register.php") 
        //         .post(body)
        //         .build();

        // try {
        //     client.newCall(request).execute();
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }

    }
}