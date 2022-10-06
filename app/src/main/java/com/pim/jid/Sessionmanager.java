package com.pim.jid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.pim.jid.views.Login;

import java.util.HashMap;

public class Sessionmanager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;
    int mode = 0;

    public static final String pref_name = "name";
    private static final String is_login = "islogin";
    public static final String kunci_id = "name";
    public static final String set_vip = "vip";
    public static final String set_scope = "scope";
    public static final String set_item = "item";
    public static final String set_info = "info";
    public static final String set_report = "report";
    public static final String set_dashboard = "dashboard";


    public Sessionmanager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(pref_name, mode);
        editor = pref.edit();
    }

    public void createSession(String name, String vip, String scope, String item, String info, String report, String dashboard){
        editor.putBoolean(is_login, true);
        editor.putString(kunci_id, name);
        editor.putString(set_vip, vip);
        editor.putString(set_scope, scope);
        editor.putString(set_scope, scope);
        editor.putString(set_item, item);
        editor.putString(set_info, info);
        editor.putString(set_report, report);
        editor.putString(set_dashboard, dashboard);

        editor.commit();
    }

    public void checkLogin() {
        if (!this.is_login()){
            Intent i = new Intent(context, Login.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }else {
            Intent i = new Intent(context, Dashboard.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }

    private boolean is_login() {
        return pref.getBoolean(is_login, false);
    }

    public void logout(){
        editor.clear();
        editor.commit();
        Intent i = new Intent(context, Login.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(pref_name, pref.getString(pref_name, null));
        user.put(kunci_id, pref.getString(kunci_id, null));
        user.put(set_vip, pref.getString(set_vip, null));
        user.put(set_report, pref.getString(set_report, null));
        user.put(set_dashboard, pref.getString(set_dashboard, null));
        user.put(set_info, pref.getString(set_info, null));
        user.put(set_item, pref.getString(set_item, null));
        user.put(set_scope, pref.getString(set_scope, null));
        return user;
    }

}
