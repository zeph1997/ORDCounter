package com.example.zeph1.ordcounter;

import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.w3c.dom.Document;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    TextView dbsSharePrice;
    TextView ocbcSharePrice;
    org.jsoup.nodes.Document doc;
    private DrawerLayout drawerLayout;
    private Boolean drawerState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up drawer layout
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerState = false;

        final ProgressBar progBarCommission = (ProgressBar) findViewById(R.id.progressBarCommission);
        final ProgressBar progBarORD = (ProgressBar) findViewById(R.id.progressBarORD);
        final TextView tvPercentCommission = (TextView) findViewById(R.id.progressCommission);
        final TextView tvPercentageORD = (TextView) findViewById(R.id.progressORD);
        final TextView daysCommission = (TextView) findViewById(R.id.daysCommission);
        final TextView daysORD = (TextView) findViewById(R.id.daysORD);
        final TextView welcomeMsg = (TextView) findViewById(R.id.welcomeTxt);
        final TextView weeksToORD = (TextView) findViewById(R.id.weeksToOrd);
        dbsSharePrice = (TextView) findViewById(R.id.dbsSharePrice);
        ocbcSharePrice = (TextView) findViewById(R.id.ocbcSharePrice);

        Date todayDateRaw = Calendar.getInstance().getTime();
        SimpleDateFormat dt = new SimpleDateFormat("dd-MMM-yyyy");
        String todayDatess = dt.format(todayDateRaw);

        String greeting = "";
        Calendar nowIs = Calendar.getInstance();

        if (nowIs.get(Calendar.HOUR_OF_DAY) < 12) {
            greeting = "Good Morning! ";
        } else if (nowIs.get(Calendar.HOUR_OF_DAY) < 18) {
            greeting = "Good Afternoon! ";
        } else {
            greeting = "Good Evening! ";
        }

        welcomeMsg.setText(greeting + "Today is " + todayDatess);

        Calendar commissionDate = Calendar.getInstance();
        Calendar ordDate = Calendar.getInstance();
        Calendar enlistDate = Calendar.getInstance();
        Calendar ocsDate = Calendar.getInstance();
        Calendar todayDate = Calendar.getInstance();

        todayDate.clear();
        todayDate.setTime(todayDateRaw);
        commissionDate.clear();
        commissionDate.set(2018, 12, 15);
        ordDate.clear();
        ordDate.set(2019, 8, 8);
        enlistDate.clear();
        enlistDate.set(2017, 10, 27);
        ocsDate.clear();
        ocsDate.set(2018, 3, 19);

        long daysToCommission = commissionDate.getTimeInMillis() - todayDate.getTimeInMillis();
        long daysToORD = ordDate.getTimeInMillis() - todayDate.getTimeInMillis();
        long totalDaysCommission = commissionDate.getTimeInMillis() - ocsDate.getTimeInMillis();
        long totalDaysORD = ordDate.getTimeInMillis() - enlistDate.getTimeInMillis();

        float dayToCommission = (float) daysToCommission / (24 * 60 * 60 * 1000);
        float dayToORD = (float) daysToORD / (24 * 60 * 60 * 1000);
        float totalDayCommission = (float) totalDaysCommission / (24 * 60 * 60 * 1000);
        float totalDayORD = (float) totalDaysORD / (24 * 60 * 60 * 1000);

        //To check if commissioning day is over
        int toCommission;
        if(dayToCommission - 30 > 0){
            toCommission = (int) dayToCommission - 30;
        } else {
            toCommission = 0;
        }

        int toORD;
        if(dayToORD - 30 > 0){
            toORD = (int) dayToORD - 30;
        } else {
            toORD = 0;
        }
        int toTotalCommission = (int) totalDayCommission;
        int toTotalORD = (int) totalDayORD;

        if(toCommission == 0){
            daysCommission.setText("Commission Lo!");
        } else {
            daysCommission.setText(Integer.toString(toCommission));
        }

        if(toORD <= 0){
            daysORD.setText("ORD Lo!");
            weeksToORD.setText("ORD Lo!");
        } else {
            daysORD.setText(Integer.toString(toORD));
            int weeksLeft;
            if(toORD>7){
                weeksLeft = toORD/7;
                weeksToORD.setText(weeksLeft+" WEEKS LEFT!");
            } else {
                weeksToORD.setText("FINAL WEEK!");
            }

        }



        float percentageDoneCommission;
        if(((totalDayCommission - dayToCommission + 30) / totalDayCommission) * 100 < 100){
            percentageDoneCommission = ((totalDayCommission - dayToCommission + 30) / totalDayCommission) * 100;
        } else {
            percentageDoneCommission = 100;
        }

        float percentageDoneORD;
        if(((totalDayORD - dayToORD + 30) / totalDayORD) * 100 < 100){
            percentageDoneORD = ((totalDayORD - dayToORD + 30) / totalDayORD) * 100;
        } else {
            percentageDoneORD = 100;
        }


        progBarCommission.setProgress((int) percentageDoneCommission);
        progBarORD.setProgress((int) percentageDoneORD);

        tvPercentCommission.setText((int) percentageDoneCommission + "%");
        tvPercentageORD.setText((int) percentageDoneORD + "%");
        //Toast.makeText(this,"done",Toast.LENGTH_SHORT).show();

        //DownloadTask downloadTask = new DownloadTask();

       /* try {
            Toast.makeText(this,"started this is try",Toast.LENGTH_SHORT).show();
            //org.jsoup.nodes.Document doc = Jsoup.connect("https://www.google.com").get();
            //Element priceText = doc.getElementsByClass("priceText__1853e8a5").first();
            //String priceTxt = priceText.text();

            String dbsprice = downloadTask.doInBackground();
            Toast.makeText(this,dbsprice +" dbsprice",Toast.LENGTH_SHORT).show();
            dbsSharePrice.setText(dbsprice);
        }catch (Exception e){
            e.printStackTrace();
        }
*/
        //Try new AsyncTask
        new Thread(new Runnable() {
            @Override
            public void run() {
                String str;
                Element strr;
                try {
                    doc = Jsoup.connect("https://www.bloomberg.com/quote/DBS:SP").get();
                    Boolean a = true;
                    Log.i("checking for null", "check now");
                    strr = doc.getElementsByClass("priceText__1853e8a5").first();
                    if (strr != null) a = false;
                    str = strr.text();
                    Log.i("bloomberg price",str + " true or false: " + a);
                } catch (Exception e) {
                    str= ",";
                    e.printStackTrace();
                }
                final String finalStr = str;
                dbsSharePrice.post(new Runnable() {
                    @Override
                    public void run() {
                        dbsSharePrice.setText(finalStr);
                    }
                });
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                String str;
                Element strr;
                try {
                    doc = Jsoup.connect("https://www.bloomberg.com/quote/OCBC:SP").get();
                    strr = doc.getElementsByClass("priceText__1853e8a5").first();
                    str = strr.text();
                } catch (Exception e) {
                    str = ",";
                    e.printStackTrace();
                }
                final String finalStr = str;
                ocbcSharePrice.post(new Runnable() {
                    @Override
                    public void run() {
                        ocbcSharePrice.setText(finalStr);
                    }
                });
            }
        }).start();


/*
        DownloadTask downloadTask = new DownloadTask();

        try {
            String fullWebpage = downloadTask.execute("https://www.bloomberg.com/quote/DBS:SP").get();
            Log.i("Download Progress", "Downloaded Webpage DBS" + fullWebpage);
            String[] firstSplit = fullWebpage.split("<span class=\"priceText__1853e8a5\">");
            Log.i("Download Progress", "firstSplit done" + firstSplit[0] + "hi");
            String[] secondSplit = firstSplit[1].split("<");
            Log.i("Download Progress", "Second split done");
            dbsSharePrice.setText(secondSplit[0]);
            Log.i("Download Progress", "Text Loaded");

        } catch (Exception e) {
            e.printStackTrace();
            Log.i("Download Progress", "Dead");
        }*/
/*
        DownloadTask downloadTask2 = new DownloadTask();

        try{
            String fullWebpage2 = downloadTask2.execute("https://www.bloomberg.com/quote/OCBC:SP").get();
            Log.i("Download Progress","Downloaded Webpage OCBC");
            String[] firstSplit2 = fullWebpage2.split("<span class=\"priceText__1853e8a5\">");
            Log.i("Download Progress","First Split done");
            String[] secondSplit2 = firstSplit2[1].split("<");
            Log.i("Download Progress","Second split done");
            ocbcSharePrice.setText(secondSplit2[0]);
            Log.i("Download Progress","OCBC text loaded");
        } catch (Exception e){
            e.printStackTrace();
        }

    }*/

}
/*
    private static String extractPageData( org.jsoup.nodes.Document doc){
        Element strr;
        String str;
        strr = doc.getElementsByClass("priceText__1853e8a5").first();
        str = strr.text();

        return str;

    }
    */
        /*public class DownloadTask extends AsyncTask<String, Void, String> {

            Element priceText;
            org.jsoup.nodes.Document doc;
            String hi;
            @Override
            protected String doInBackground(String... urls) {

                URL url;
                HttpURLConnection urlConnection = null;

                try {
                   //Toast.makeText(getApplicationContext(),"started",Toast.LENGTH_SHORT).show();
                   doc = Jsoup.connect("https://respectivelyintrospective.wordpress.com").get();
                   hi =doc.text();


                } catch (Exception e) {
                    e.printStackTrace();
                }
               return hi;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //Toast.makeText(getApplicationContext(),priceText.text()+" hi",Toast.LENGTH_SHORT).show();



            }*/
        }

