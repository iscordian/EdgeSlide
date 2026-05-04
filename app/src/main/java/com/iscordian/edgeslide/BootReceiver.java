package com.iscordian.edgeslide;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // This ping helps the system remember to start the service
            Intent i = new Intent(context, GestureService.class);
            context.startService(i);
        }
    }
            }
