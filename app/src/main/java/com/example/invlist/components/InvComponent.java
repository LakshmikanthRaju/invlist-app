package com.example.invlist.components;

import android.os.Handler;
import android.widget.TextView;

public abstract class InvComponent {

    private TextView textView = null;
    private Handler handler;
    private String defaultMesage = null;

    private String value = null;
    private boolean loading = false;

    public InvComponent(String defaultMesage) {
        this.defaultMesage = defaultMesage;
    }

    public void setTextView(TextView textView, Handler handler) {
        this.textView = textView;
        this.handler = handler;
    }

    public String values() {
        if (value != null) {
            return value;
        }
        load();
        return defaultMesage;
    }

    protected synchronized void updateValue(String value) {
        if (this.value == null) {
            this.value = value;
        } else {
            this.value = this.value + "\n\n" + value;
        }
        //this.value = value;
        updateTextView();
    }

    protected void setValue(String value) {
        this.value = value;
        loading = false;
        updateTextView();
    }

    private void updateTextView() {
        handler.post(new Runnable() {
            public void run() {
                textView.setText(value);
            }
        });
    }

    private void load() {
        if (loading) return;
        loading = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                getPrices();
            }
        }).start();
    }

    public abstract void getPrices();
}
