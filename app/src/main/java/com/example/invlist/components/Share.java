package com.example.invlist.components;

import com.example.invlist.ui.fragments.StockFragment;
import com.example.invlist.utils.DateUtils;
import com.example.invlist.utils.HTTPClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.concurrent.Callable;

public class Share implements Callable<String> {
    public String name;
    public String price;
    public String date;
    public String message;
    public JSONObject pricesData;

    public Share(String name) {
        this.name = name;
        this.message = null;
    }

    private String formatMessage(String curDate, String status) {
        if (curDate.split("\\s+").length > 1) {
            curDate = DateUtils.convertTimestamp(curDate, "yyyy-MM-dd HH:mm:ss");
        } else {
            curDate = DateUtils.convertFormat(curDate, "yyyy-MM-dd");
        }
        return String.format("%s :  %s, %s EDT\n%s", name, price, curDate, status);
    }

    private String formatDate(String curDate) {
        if (curDate.split("\\s+").length > 1) {
            curDate = DateUtils.convertTimestamp(curDate, "yyyy-MM-dd HH:mm:ss");
        } else {
            curDate = DateUtils.convertFormat(curDate, "yyyy-MM-dd");
        }
        return String.format("%s EDT", curDate);
    }

    private String parseResponse(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            pricesData = jsonResponse.getJSONObject("Time Series (Daily)");

            JSONObject metadata = jsonResponse.getJSONObject("Meta Data");
            String date = metadata.getString("3. Last Refreshed");
            name = metadata.getString("2. Symbol");

            JSONObject prices = jsonResponse.getJSONObject("Time Series (Daily)");
            Iterator<String> datesKey = prices.keys();

            String curDate = datesKey.next();
            JSONObject curObject = prices.getJSONObject(curDate);
            String currPrice = curObject.getString("4. close");
            String prevPrice = prices.getJSONObject(datesKey.next()).getString("4. close");
            float diff = Float.parseFloat(currPrice) - Float.parseFloat(prevPrice);

            this.price = currPrice;
            this.date = formatDate(date);
            Float curPrice = Float.parseFloat(currPrice);

            datesKey = prices.keys();
            datesKey.next();
            if (diff > 0) {
                while(datesKey.hasNext()) {
                    String oldDate = datesKey.next();
                    JSONObject oldObj = prices.getJSONObject(oldDate);
                    String oldPrice = oldObj.getString("2. high");
                    if (curPrice < Float.parseFloat(oldPrice)) {
                        int days = Stock.getDaysCount(curDate, oldDate);
                        //return formatMessage(date, String.format("+%f: Highest in %d days", diff, days));
                        return String.format("+%f (%.3f%%): Highest in %d days", diff, (diff/curPrice)*100.0, days);
                    }
                }
                //return formatMessage(date, String.format("+%f: Highest in all days", diff));
                return String.format("+%f (%.3f%%): Highest in all days", diff, (diff/curPrice)*100.0);
            } else {
                while(datesKey.hasNext()) {
                    String oldDate = datesKey.next();
                    JSONObject oldObj = prices.getJSONObject(oldDate);
                    String oldPrice = oldObj.getString("3. low");
                    if (curPrice > Float.parseFloat(oldPrice)) {
                        int days = Stock.getDaysCount(curDate, oldDate);
                        //return formatMessage(date, String.format("%f: Lowest in %d days", diff, days));
                        return String.format("%f (%.3f%%): Lowest in %d days", diff, (diff/curPrice)*100.0, days);
                    }
                }
                //return formatMessage(date, String.format("%f: Lowest in %d days", diff));
                return String.format("%f (%.3f%%): Lowest in %d days", diff, (diff/curPrice)*100.0);
            }
        } catch (JSONException e) {
            return response;
        }
    }

    public String call() throws Exception {
        String response = HTTPClient.getResponse(String.format(Stock.STOCK_URL, name));
        if (response.contains("Error Message")) {
            message = "ERROR: Invalid API call for " + name;
        } else if (response.contains("Note")) {
            message = "WARNING: Overload of API calls. Try after a minute";
        } else {
            message = parseResponse(response);
        }
        //updateValue(value);
        StockFragment.updateListView(this);
        return message;
    }
}

