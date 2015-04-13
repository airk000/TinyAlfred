package com.airk.tool.alfred.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.airk.tool.tinyalfred.TinyAlfred;
import com.airk.tool.tinyalfred.annotation.FindView;
import com.airk.tool.tinyalfred.annotation.NullableView;
import com.airk.tool.tinyalfred.annotation.OnClick;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */


    @NullableView @FindView(R.id.text)
    TextView textView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        TinyAlfred.process(this);

        textView.setText("123");
        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
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
