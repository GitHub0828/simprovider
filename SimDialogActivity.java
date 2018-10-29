
package com.android.settings.simprovider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.android.settings.simprovider.data.ApnData;

import java.util.ArrayList;

@Deprecated
public class SimDialogActivity extends Activity {

    public static void startShowDialogActivity(Context context, final int subId, String mccmnc, ArrayList<ApnData> apnDatas) {
        Intent showDialog = new Intent(context, SimDialogActivity.class);
        showDialog.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        showDialog.putExtra("subId", subId);
        showDialog.putExtra("mccmnc", mccmnc);
        showDialog.putParcelableArrayListExtra("apnDatas", apnDatas);
        context.startActivity(showDialog);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            return;
        }
        int subId = getIntent().getIntExtra("subId", -1);
        String mccmnc = getIntent().getStringExtra("mccmnc");
        ArrayList<ApnData> apnDatas =  getIntent().getParcelableArrayListExtra("apnDatas");
        SimProviderAction.popDialog(this, subId, mccmnc, apnDatas, null);
    }
}
