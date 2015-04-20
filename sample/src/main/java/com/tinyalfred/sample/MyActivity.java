package com.tinyalfred.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.github.airk.tinyalfred.TinyAlfred;
import com.github.airk.tinyalfred.annotation.*;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */

    @NullableView
    @FindView(R.id.text)
    TextView textView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        TinyAlfred.process(this);

        textView.setText("123");
    }

    @OnLongClick(R.id.text) boolean doTextLongClick() {
        Toast.makeText(this, "LONG", Toast.LENGTH_SHORT).show();
        return true;
    }

    @OnPreDraw(R.id.text) void doPreText() {
        Toast.makeText(this, textView.getMeasuredHeight() + "", Toast.LENGTH_SHORT).show();
    }

    @OnClick({R.id.text, R.id.image})
    void doClick(View view) {
        if (view instanceof TextView) {
            TextView tv = (TextView) view;
            Toast.makeText(this, tv.getText().toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public class Holder {
        @FindView(R.id.image)
        public ImageView imageView;
    }
}
