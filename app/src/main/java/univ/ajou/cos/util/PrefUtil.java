package univ.ajou.cos.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefUtil {

    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor ;


    public PrefUtil(Context context) {
        pref = context.getSharedPreferences(context.getPackageName().replace(".", ""), Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public Boolean getPrefDataBool(String keydata,Boolean data) {
        return pref.getBoolean(keydata,data);
    }

    public void setPrefDataBool(String keydatam, Boolean data) {
        editor.putBoolean(keydatam, data);
        editor.commit();
    }

    public String getPrefDataString(String keydata, String data)	{
        String retrunData =null;
        try {
            retrunData = CryptoUtil.decrypt(pref.getString(keydata, (data != null ? CryptoUtil.encrypt(data) : null)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retrunData;
    }

    public void setPrefDataString(String keydatam, String data) {
        try {
            editor.putString(keydatam, CryptoUtil.encrypt(data));
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removePref(String keyData) {
        try {
            editor.remove(keyData);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void removeAllPref() {
        editor.clear();
        editor.commit();
    }
}
