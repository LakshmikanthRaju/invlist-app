package com.example.invlist;

import android.content.res.Resources;
import android.os.Bundle;

import com.example.invlist.components.InvFactory;
import com.example.invlist.components.InvType;
import com.example.invlist.ui.fragments.DebtFragment;
import com.example.invlist.ui.fragments.ForexFragment;
import com.example.invlist.ui.fragments.EquityFragment;
import com.example.invlist.ui.fragments.StockFragment;
import com.example.invlist.ui.main.ViewPagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ViewPager viewPager = findViewById(R.id.view_pager);
        setupViewPager(viewPager);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        tabs.setTabGravity(TabLayout.GRAVITY_FILL);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InvFactory.reset(InvType.EQUITY);
                InvFactory.reset(InvType.STOCK);
                Snackbar.make(view, "Refreshing...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                recreate();
            }
        });
        //fab.hide();

        Toast.makeText(getApplicationContext(), "Welcome to InvList", Toast.LENGTH_SHORT).show();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        Resources res = this.getResources();

        adapter.addFragment(new EquityFragment(), res.getString(R.string.equity_text));
        adapter.addFragment(new StockFragment(), res.getString(R.string.stock_text));
        adapter.addFragment(new DebtFragment(), res.getString(R.string.debt_text));
        adapter.addFragment(new ForexFragment(), res.getString(R.string.forex_text));

        viewPager.setAdapter(adapter);
    }
}