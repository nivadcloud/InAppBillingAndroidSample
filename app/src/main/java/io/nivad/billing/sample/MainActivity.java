package io.nivad.billing.sample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import io.nivad.iab.BillingProcessor;
import io.nivad.iab.TransactionDetails;

/**
 * Please read the following for more information:
 * - https://nivad.io/docs/iab-android-studio/
 * - https://nivad.io/docs/iab-methods-and-error-codes/
 *
 */
public class MainActivity extends AppCompatActivity {

    private static final String BAZAAR_KEY = "Bazaar RSA Key";
    private static final String NIVAD_APPLICATION_ID = "Nivad Application ID";
    private static final String NIVAD_APPLICATION_SECRET = "Nivad Application Secret";

    private BillingProcessor mNivadBilling;

    // Widgets
    private Button mBtnPurchaseTest1;
    private Button mBtnConsumeTest1;
    private Button mBtnPurchaseTest2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupViews();
        setButtonsEnabled(false);
        mNivadBilling = new BillingProcessor(this, BAZAAR_KEY, NIVAD_APPLICATION_ID, NIVAD_APPLICATION_SECRET, mBillingMethods);
    }

    private void setupViews() {
        mBtnPurchaseTest1 = (Button) findViewById(R.id.btnPurchaseTest1);
        mBtnConsumeTest1 = (Button) findViewById(R.id.btnConsumeTest1);
        mBtnPurchaseTest2 = (Button) findViewById(R.id.btnPurchaseTest2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mNivadBilling.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        if (mNivadBilling != null) {
            mNivadBilling.release();
        }
        super.onDestroy();
    }

    private void setButtonsEnabled(boolean enabled) {
        Button[] buttons = {mBtnPurchaseTest1, mBtnPurchaseTest2, mBtnConsumeTest1};
        for (Button btn : buttons) {
            btn.setEnabled(enabled);
        }
    }


    /**
     * For more info about this object, its methods and possible error codes please visit
     * https://nivad.io/docs/iab-methods-and-error-codes/
     */
    private BillingProcessor.IBillingHandler mBillingMethods = new BillingProcessor.IBillingHandler() {

        @Override
        public void onProductPurchased(String sku, TransactionDetails transactionDetails) {
            makeToast(String.format(getString(R.string.toast_product_purchased), sku));
            /**
             * برای محصولات مصرف شدنی اینجا تابع
             * consumePurchase یا consumePurchaseInBackground
             * را صدا بزنید و در صورتی که مقدار بازگشتی
             * true
             * بود اثر آن‌ را اعمال کنید. در اینجا صرفا دکمه‌ی مصرف را فعال می‌کنیم تا دنبال کردن روند اجرا ساده تر باشد.
             */
            if ("nivad.billing_sample.consumable".equals(sku)) {
                mBtnConsumeTest1.setEnabled(true);
            }
        }

        @Override
        public void onPurchaseHistoryRestored() {
            for(String sku : mNivadBilling.listOwnedProducts()) {
                Log.d("nivad", "Owned Product: " + sku);
            }

            for(String sku : mNivadBilling.listOwnedSubscriptions()) {
                Log.d("nivad", "Owned Subscription: " + sku);
            }
        }

        @Override
        public void onBillingError(int code, Throwable error) {
            if (code == 205) {
                // تشخیص یک تلاش ناموفق برای هک
                makeToast("این اپلیکیشن توسط نیواد محافظت می‌شود و امکان هک و دورزدن پرداخت آن وجود ندارد");
                Log.d("nivad", "Hack attempt prevented");
            } else {
                Log.d("nivad", "Billing error. Error code = " + code
                        + ". For more information please visit https://nivad.io/docs/iab-methods-and-error-codes/");
            }
        }

        @Override
        public void onBillingInitialized() {
            setButtonsEnabled(true);

            if (!mNivadBilling.isPurchased("nivad.billing_sample.consumable")) {
                mBtnConsumeTest1.setEnabled(false);
            }
        }
    };

    public void btnConsumeTest1Click(View v) {
        boolean success = mNivadBilling.consumePurchase("nivad.billing_sample.consumable");
        if (success) {
            makeToast(R.string.toast_test1_consume_success);
            mBtnConsumeTest1.setEnabled(false);
        } else {
            makeToast(R.string.toast_test1_consume_fail);
        }
    }

    public void btnPurchaseTest2Click(View v) {
        mNivadBilling.purchase(MainActivity.this, "nivad.billing_sample.non_consumable");
        makeToast(R.string.toast_buying_test2);
    }

    public void btnPurchaseTest1Click(View v) {
        mNivadBilling.purchase(MainActivity.this, "nivad.billing_sample.consumable");
        makeToast(R.string.toast_buying_test1);
    }

    // Utility
    private void makeToast(int resId) {
        makeToast(getString(resId));
    }

    private void makeToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
