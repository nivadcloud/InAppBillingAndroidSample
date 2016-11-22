package io.nivad.billing.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import io.nivad.iab.BillingProcessor;
import io.nivad.iab.MarketName;
import io.nivad.iab.TransactionDetails;

import java.util.List;

/**
 * Please read the following for more information:
 * - https://nivad.io/docs/iab-android-studio/
 * - https://nivad.io/docs/iab-methods-and-error-codes/
 *
 */
public class MainActivity extends AppCompatActivity {

    private static final String BAZAAR_KEY = "MIHNMA0GCSqGSIb3DQEBAQUAA4G7ADCBtwKBrwC6Dc/KjMIB3GGCR3YrYYILhhkAEYqGewj6Eksn7r5SdzOzEHxYgtxVDTF5A+759Z3a3+0fDeWl5NMRIMC+T/FHabShjuEtW8JygPHbuxdNeIE230DUZMqJF9qhficBWFJksu1I7TgpXP6eA2ML+SXKM6TkCC32vnQxoc1xosF0RTX7ajV3+1vIbpUlFcE3AE+A4fkDj5RbiSKwj8BGMNGKMwPU94Z3pRXOiCxN4g0CAwEAAQ==";
    private static final String NIVAD_APPLICATION_ID = null;
    private static final String NIVAD_APPLICATION_SECRET = null;

//    private static final String BAZAAR_KEY = "Bazaar RSA Key";
//    private static final String NIVAD_APPLICATION_ID = "Nivad Application ID";
//    private static final String NIVAD_APPLICATION_SECRET = "Nivad Application Secret";

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
        mNivadBilling = new BillingProcessor(
            this,
            BAZAAR_KEY, 
            NIVAD_APPLICATION_ID,
            NIVAD_APPLICATION_SECRET,
            MarketName.CAFE_BAZAAR, 
            mBillingMethods
        );
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
            for(String sku : (List<String>) mNivadBilling.listOwnedProducts()) {
                Log.d("nivad", "Owned Product: " + sku);
            }

            for(String sku : (List<String>) mNivadBilling.listOwnedSubscriptions()) {
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
        // پارامتر سوم اختیاری است. برای اطلاعات بیشتر دربارهٔ وب‌هوک‌های نیواد و نقش این پارامتر در وب‌هوک‌ها به آدرس زیر مراجعه کنید:
        // https://nivad.io/docs/webhooks/
        mNivadBilling.purchase(this, "nivad.billing_sample.non_consumable", "webhook payload");
        makeToast(R.string.toast_buying_test2);
    }

    public void btnPurchaseTest1Click(View v) {
        // پارامتر سوم اختیاری است. برای اطلاعات بیشتر دربارهٔ وب‌هوک‌های نیواد و نقش این پارامتر در وب‌هوک‌ها به آدرس زیر مراجعه کنید:
        // https://nivad.io/docs/webhooks/
        mNivadBilling.purchase(this, "nivad.billing_sample.consumable", "webhook payload");
        makeToast(R.string.toast_buying_test1);
    }


    public void btnSubscribeClick(View view) {
        // پارامتر سوم اختیاری است. برای اطلاعات بیشتر دربارهٔ وب‌هوک‌های نیواد و نقش این پارامتر در وب‌هوک‌ها به آدرس زیر مراجعه کنید:
        // https://nivad.io/docs/webhooks/
        mNivadBilling.purchase(this, "nivad.billing_sample.subscription", "webhook payload");
        makeToast(R.string.toast_buying_subscription);
    }

    public void btnCheckSubscriptionClick(View view) {
        if (mNivadBilling.loadOwnedPurchasesFromBazaar()) {
            makeToast(R.string.toast_subscriptions_updated);
        }
    }

    // Utility
    private void makeToast(int resId) {
        makeToast(getString(resId));
    }

    private void makeToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
