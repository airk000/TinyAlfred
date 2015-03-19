package com.airk.tool.alfred.simple;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.airk.tool.tinyalfred.TinyAlfred;
import com.airk.tool.tinyalfred.annotation.FindView;
import com.airk.tool.tinyalfred.annotation.OnClick;

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

    @OnClick(R.id.text)
    void doClick(View view) {

        TextView tv = (TextView) view;
        Toast.makeText(this, tv.getText().toString(), Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.image)
    void doImageClick(View v) {
        Toast.makeText(this, "ImageClick", Toast.LENGTH_SHORT).show();
    }

    public class Holder {
        @FindView(R.id.image)
        public ImageView imageView;
    }
}
