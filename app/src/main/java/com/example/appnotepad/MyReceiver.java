package com.example.appnotepad;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String intentAction = intent.getAction();
        if (intentAction != null){
            String toastMessage = "unknown intent action";
            switch (intentAction){
                case Intent.ACTION_POWER_CONNECTED:
                    toastMessage = "Power connected!";
                    break;
                case Intent.ACTION_POWER_DISCONNECTED:
                    toastMessage = "Power disconnected!";
                    break;
                case Intent.ACTION_HEADSET_PLUG :
                    int state = intent.getIntExtra("state", -1);
                    switch (state) {
                        case 0:
                            toastMessage = "Headset unplugged";

                            break;
                        case 1:
                            toastMessage = "Headset plugged";

                            break;
                    }
                    break;
                case Intent.ACTION_BATTERY_LOW :
                    toastMessage = "Battery Low ! Please Connect charger";
                    break;
                case Intent.ACTION_BATTERY_OKAY :
                    toastMessage = "Battery is okay";
                    break;
                case Intent.ACTION_AIRPLANE_MODE_CHANGED:
                    boolean status = intent.getBooleanExtra("state" , true);
                    if(status){
                        toastMessage = "Airplane Mode turned on";
                    }else {
                        toastMessage = "Airplane Mode turned off";
                    }
                    break;
            }
            //Display the toast.
            Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
        }

        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            boolean noConnectivity = intent.getBooleanExtra(
                    ConnectivityManager.EXTRA_NO_CONNECTIVITY, false
            );
            if (noConnectivity) {
                Toast.makeText(context, "Wifi Disconnected", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Wifi Connected", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
