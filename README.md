
نمونه‌ای از استفاده از کتابخانه‌ی پرداخت نیواد 
----------------------------------------------

کتابخانه‌ی پرداخت نیواد راه حلی بی دردسر، حرفه‌ای و امن برای اضافه کردن پرداخت درون‌برنامه‌ای به اپلیکیشن‌های اندروید است.
این اپلیکیشن از کتابخانه‌ی پرداخت نیواد برای پیاده سازی پرداخت درون برنامه‌ای استفاده می‌کند.

**کتابخانه‌ی پرداخت نیواد با اتصال به سرورهای نیواد و بررسی وضعیت واقعی خرید اپلیکیشن و بازی شما را از جعل و هک پرداخت درون برنامه‌ای حفظ می‌کند**

شروع سریع!
----------

جهت اضافه کردن کتابخانه‌ی نیواد از سایت نیواد فایل jar کتابخانه را دانلود کنید یا خط زیر را به فایل build.gradle اضافه کنید:

    compile 'io.nivad.billing:library:+'

در متد onCreate اکتیویتی‌ای که پرداخت صورت می‌گیرد خط زیر را اضافه کنید:

    final String BAZAAR_KEY = "کلید RSA که از کافه بازار دریافت کردید";
    final String NIVAD_APPLICATION_ID = "مقدار Application ID که در پنل نیواد، بخش پرداخت امن دریافت کردید";
    final String NIVAD_APPLICATION_SECRET = "مقدار Application Secret که در پنل نیواد، بخش پرداخت امن دریافت کردید";
    mNivadBilling = new BillingProcessor(this, BAZAAR_KEY, NIVAD_APPLICATION_ID, NIVAD_APPLICATION_SECRET, MarketName.CAFE_BAZAAR, mBillingMethods);

در ابتدای متد onActivityResult خطوط زیر را اضافه کنید:

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mNivadBilling.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

در ابتدای متد onDestroy خطوط زیر را اضافه کنید:


    @Override
    protected void onDestroy() {
        if (mNivadBilling != null) {
            mNivadBilling.release();
        }
        super.onDestroy();
    }


و مقدار متغیر mBillingMethods را در خارج از متد onCreate مشابه زیر تعیین کنید:


    private BillingProcessor.IBillingHandler mBillingMethods = new BillingProcessor.IBillingHandler() {

        @Override
        public void onProductPurchased(String sku, TransactionDetails transactionDetails) {
           // این تابع پس از خرید موفق صدا زده می‌شود
           //  برای محصولات مصرف شدنی اینجا تابع
           // consumePurchase یا consumePurchaseInBackground
           // را صدا بزنید و در صورتی که مقدار بازگشتی
           // true
           // بود اثر آن‌ را اعمال کنید.
        }

        @Override
        public void onPurchaseHistoryRestored() {
           // این متد زمانی صدا زده می‌شود که محصولاتی که کاربر خریده اما هنوز مصرف نشده‌اند از بازار دریافت شده اند. این محصولات را با متد‌های isPurchased و isSubscribed می‌توانید چک کنید
        }

        @Override
        public void onBillingError(int code, Throwable error) {
           // این متد در زمانی که اشکالی در فرایند پرداخت یا راه اندازی سرویس پرداخت درون‌برنامه‌ای به وجود بیاید صدا زده می‌شود.
           // برخی از مقادیر پارامتر code در آدرس زیر معرفی و توضیح داده شده اند:
           // https://nivad.io/docs/iab-methods-and-error-codes/
        }

        @Override
        public void onBillingInitialized() {
           // این متد زمانی که سرویس پرداخت درون برنامه‌ای آماده‌ی کار می‌شود صدا زده می‌شود
        }
    };


تبریک! از الان برنامه‌ی شما می‌تواند پرداخت درون برنامه‌ای داشته باشد! پرداخت درون برنامه‌ای به صورتی که نه تنها راه‌اندازی‌ و استفاده از آن سریع‌تر و راحت‌تر از روش‌های دیگر است بلکه در مقابل ابزارهای جعل و هک پرداخت نیز ایمن و مقاوم است!
