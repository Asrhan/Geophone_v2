package com.example.flaforgue.geophone.activities.superactivities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.flaforgue.geophone.R;
import com.example.flaforgue.geophone.managers.clicklisteners.MenuBtnClickListenerManager;
import com.example.flaforgue.geophone.managers.clicklisteners.OptionBtnClickListenerManager;

/**
 * Created by flaforgue on 21/11/2016.
 */

public abstract class ActivityWithMenu extends AppCompatActivity {

    // menu
    protected FloatingActionButton menuBtn;
    protected FloatingActionButton optionsBtn;
    protected FloatingActionButton mapBtn;
    protected RelativeLayout dimLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        this.menuBtn = (FloatingActionButton) this.findViewById(R.id.menuBtn);
        this.optionsBtn = (FloatingActionButton) this.findViewById(R.id.optionsBtn);
        this.mapBtn = (FloatingActionButton) this.findViewById(R.id.mapBtn);
        this.dimLayout = (RelativeLayout) findViewById(R.id.dim_layout);

        this.menuBtn.setOnClickListener(new MenuBtnClickListenerManager(this));
        this.optionsBtn.setOnClickListener(new OptionBtnClickListenerManager(this));
    }

    public void toggleMenu() {
        if (this.menuIsOpen()) {
            this.closeMenu();
        } else {
            this.openMenu();
        }
    }

    private boolean menuIsOpen() {
        return this.dimLayout.getVisibility() == View.VISIBLE;
    }

    private void openMenu() {
        this.dimLayout.setVisibility(View.VISIBLE);
        this.optionsBtn.setVisibility(View.VISIBLE);
        this.mapBtn.setVisibility(View.VISIBLE);

        this.menuBtn.setImageResource(R.drawable.cross);

    }

    private void closeMenu() {
        this.dimLayout.setVisibility(View.GONE);
        this.optionsBtn.setVisibility(View.GONE);
        this.mapBtn.setVisibility(View.GONE);

        this.menuBtn.setImageResource(R.drawable.menu);
    }
}
