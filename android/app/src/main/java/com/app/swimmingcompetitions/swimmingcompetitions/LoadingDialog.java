package com.app.swimmingcompetitions.swimmingcompetitions;

/**
 * Created by amirl on 3/12/2018.
 */

import android.support.v7.app.AppCompatActivity;
import android.app.ProgressDialog;

public class LoadingDialog extends AppCompatActivity {
    public ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("טוען נתונים...");
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }
}
