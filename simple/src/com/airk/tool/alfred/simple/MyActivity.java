package com.airk.tool.alfred.simple;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.airk.tool.tinyalfred.TinyAlfred;
import com.airk.tool.tinyalfred.annotation.FindView;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */


    @FindView(R.id.text)
    TextView textView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        TinyAlfred.process(this);

        textView.setText("123");
    }

    public class Holder {
        @FindView(R.id.image)
        public ImageView imageView;
    }
}
