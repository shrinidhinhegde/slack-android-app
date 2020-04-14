package com.example.slack;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class SmsListener extends BroadcastReceiver {

    private SharedPreferences preferences;
    DatabaseHandler databaseHandler, databaseHandler1;
    SQLiteDatabase database, database1;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        databaseHandler = new DatabaseHandler(context);
        database = databaseHandler.getWritableDatabase();
        databaseHandler1 = new DatabaseHandler(context);
        database1 = databaseHandler1.getWritableDatabase();

        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs = null;
            String num;
            if (bundle != null){
                try{
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for(int i=0; i<msgs.length; i++){
                        msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                        String msg = msgs[i].getMessageBody();
                        long ms = msgs[i].getTimestampMillis();
                        Date date = new Date(ms);
                        String details = "\n"+date;
                        num = msgs[i].getOriginatingAddress() + details;
                        if(isNetworkConnected(context)) {
                            Cursor data = databaseHandler1.getData();
                            if(data.getCount()!=0){
                                while(data.moveToNext()){
                                    send(data.getString(data.getColumnIndex(DBContract.DBEntry.COL_1)), data.getString(data.getColumnIndex(DBContract.DBEntry.COL_2)), context);
                                }
                            }
                            database1.delete(DBContract.DBEntry.TABLE_NAME, null,null);
                            send(msg, num, context);
                        }
                        else {
                            notConnected(msg, num, context);
                        }
                    }
                }catch(Exception e){
//                            Log.d("Exception caught",e.getMessage());
                }
            }
        }
    }

    private boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public void send(String message, String number, Context context) throws IOException {

        OkHttpClient client = new OkHttpClient();

        JsonObject json = new JsonObject();
        json.addProperty("text", message);
        json.addProperty("username","incoming-webhook");
        json.addProperty("icon_emoji", ":smiley:");
        json.addProperty("pretext", number);

        Request request = new Request.Builder()
                .url("https://hooks.slack.com/services/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")
                .post(RequestBody.create(MediaType.parse("application/javascript; charset=utf-8"), json.toString()))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //do nothing
            }
        });
    }

    public void notConnected(String message, String number, Context context)throws IOException{
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBContract.DBEntry.COL_1, message);
            contentValues.put(DBContract.DBEntry.COL_2, number);
            database.insert(DBContract.DBEntry.TABLE_NAME, null, contentValues);
    }


}