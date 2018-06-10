package com.app.swimmingcompetitions.swimmingcompetitions;

/**
 * Created by amirl on 3/12/2018.
 */

import android.support.v7.app.AppCompatActivity;
import android.app.ProgressDialog;

public class LoadingDialog extends AppCompatActivity {
    public ProgressDialog mProgressDialog;

    public void showProgressDialog(String message) {
        this.mProgressDialog = new ProgressDialog(this);
        this.mProgressDialog.setMessage(message);
        this.mProgressDialog.setCanceledOnTouchOutside(false);
        this.mProgressDialog.setIndeterminate(true);
        this.mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (this.mProgressDialog != null && this.mProgressDialog.isShowing()) {
            this.mProgressDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }
}
