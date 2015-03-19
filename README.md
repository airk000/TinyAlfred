# TinyAlfred
Alfred for Android, just like ButterKnife, make your source code clean.

##Support annotation

- FindView

  ```
  @FindView(R.id.text)
  TextView textView;
  ```
- OnClick (accept one View type parameter)

  ```
  @OnClick(R.id.text)
  void doClick(View view) {
      TextView tv = (TextView) view;
      // do something with the TextView
  }
  ```

At last, don't forget this:

- For Activity:

  ```
  TinyAlfred.process(this/*Activity instance*/);
  ```
- For Fragment or your custom ViewHolder

  ```
  TinyAfred.process(this/*Fragment or ViewHolder instance*/, rootView);
  ```

##Usage

Please see MyActivity.java in sample.

##TODO

- OnUIThread
- OnXXXListener stuff

##Thanks
 It's highly inspired by ButterKnife, thanks to JakeWharton.
