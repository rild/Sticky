package rild.java_conf.gr.jp.bookmarkonlinkablesticky;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class MainActivity extends Activity {

    private int mState = 0;
    private int offsetX = 0;// ボタンクリックしたスクリーンのX座標とボタンのX座標の差分
    private int offsetY = 0;// ボタンクリックしたスクリーンのY座標とボタンのY座標の差分
    private final int STATE_NONE = 0;
    private final int STATE_DRAG = 1;
    private int aState = 0;//activity State
    private int bState = 0;
    private Button btn_edit;
    private Button btn_ac;
    private Button btn_lnk;
    private RelativeLayout mMainLayout;

    private final Context mContext = this;

    final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;


    private int[] sticky_number = {0, 0, 0, 0};//sticky_number[0](or[1][2][3])
    private ImageButton imgbtn[] = new ImageButton[4];
    private LinearLayout[] bg_sticky = new LinearLayout[4];
    private Rect rect = new Rect();

    int[][] location = new int[4][2];//{{0,50},{0,130},{0,210},{0,290}};//= new int[4][2];//(xn,yn)=(location[n][0],location[n][1])

    Sticky mEditItem = new Sticky(mContext);


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btn_edit = (Button) findViewById(R.id.edit);
        btn_ac = (Button) findViewById(R.id.edit4);
        btn_lnk = (Button) findViewById(R.id.edit3);
        imgbtn[0] = (ImageButton) findViewById(R.id.imageButton_pallet_pink);
        imgbtn[1] = (ImageButton) findViewById(R.id.imageButton_pallet_green);
        imgbtn[2] = (ImageButton) findViewById(R.id.imageButton_pallet_orange);
        imgbtn[3] = (ImageButton) findViewById(R.id.imageButton_pallet_blue);
        mMainLayout = (RelativeLayout) findViewById(R.id.main_Linearlayout);
        Log.v("showTheState", sticky_number[0] + "," + sticky_number[1] + "," + sticky_number[2] + "," + sticky_number[3]);
//下部の定義
        for (int n = 0; n < bg_sticky.length; n++) {
            createPallet(n);
            createSticky(n);
        }
        loadTheActivity();
        for (int n = 0; n < bg_sticky.length; n++) {
            if (sticky_number[n] == 1) {
                mMainLayout.addView(bg_sticky[n], new LinearLayout.LayoutParams(WC, WC));
            }
        }

        Log.v("showTheState", sticky_number[0] + "," + sticky_number[1] + "," + sticky_number[2] + "," + sticky_number[3]);
    }//onCreate


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //button1 Changing the State
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void edit(View v) {
        switch (aState) {
            case 0:
                //編集モードに遷移
                btn_edit.setBackgroundColor(Color.argb(180, 154, 205, 50));
                //YellowGreen#9ACD32
                btn_edit.setText("edit");

                aState = 1;
                Log.v("edit", "aState became 1");
                btn_lnk.setBackgroundColor(Color.argb(180, 181, 147, 100));
                //Coral#FF7F50
                btn_lnk.setText("link");
                bState = 0;
                Log.v("link", "bState became 0");
                break;
            case 1:
                //Deleteモードに遷移
                btn_edit.setBackgroundColor(Color.argb(180, 255, 127, 80));
                btn_ac.setBackgroundColor(Color.argb(180, 255, 127, 80));
                //Coral#FF7F50
                btn_edit.setText("delete");
                aState = 2;
                Log.v("edit", "aState became 2");
                btn_lnk.setBackgroundColor(Color.argb(180, 181, 147, 100));
                //Coral#FF7F50
                btn_lnk.setText("link");
                bState = 0;
                Log.v("link", "bState became 0");
                break;
            case 2:
                //Activityモードに遷移
                btn_edit.setBackgroundColor(Color.argb(180, 181, 147, 100));
                btn_ac.setBackgroundColor(Color.argb(180, 181, 147, 100));
                //.setBackgroundColor(Color.argb(int, int, int, int)
                //BurlyWood#DEB887 180,222,184,135->#b4b59364 180,181,147,100

                btn_edit.setText("activate");
                aState = 0;
                Log.v("edit", "aState became 0");
        }
    }

    //button2 Changing the State
    public void link(View v) {
        switch (bState) {
            case 0:
                btn_lnk.setBackgroundColor(Color.argb(180, 125, 180, 181));
                //YellowGreen#9ACD32
                btn_lnk.setText("link-on");

                bState = 1;
                Log.v("link", "bState became 1");

                aState = 0;
                Log.v("link", "aState became 0");
                btn_edit.setBackgroundColor(Color.argb(180, 181, 147, 100));
                btn_ac.setBackgroundColor(Color.argb(180, 181, 147, 100));
                btn_edit.setText("activate");
                break;
            case 1:
                btn_lnk.setBackgroundColor(Color.argb(180, 181, 147, 100));
                //Coral#FF7F50
                btn_lnk.setText("link");
                bState = 0;
                Log.v("link", "bState became 0");
                break;
        }
    }

    //Save button
    public void save(View v) {
        Toast.makeText(MainActivity.this, "セーブします", Toast.LENGTH_LONG).show();
        saveTheActivity();

    }

    //button Completely deleting
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void clear(View v) {
        if (aState == 2) {
            //トースト出力
            Toast.makeText(MainActivity.this,
                    "Deleted completely",
                    Toast.LENGTH_LONG).show();
            for (int n = 0; n < bg_sticky.length; n++) {
                Log.v("AllDelete", "SaveData is being removed");
                mMainLayout.removeView(bg_sticky[n]);
                sticky_number[n] = 0;
                mEditItem.tv_sticky[n][0].setText("");
                mEditItem.tv_sticky[n][1].setText("");
                location[n][0] = 0;
                location[n][1] = (160 * n);
                bg_sticky[n].setX(location[n][0]);
                bg_sticky[n].setY(location[n][1]);
                Log.v("Remove", "Button_Sticky" + (n + 1) + " was removed and number[" + n + "] became 0");
                SharedPreferences data = PreferenceManager.getDefaultSharedPreferences(mContext);
                SharedPreferences.Editor editor = data.edit();
                editor.remove("text" + KEY_INPUT_DATA + n).commit();
                editor.remove("url" + KEY_INPUT_DATA + n).commit();
                editor.remove("locationX" + KEY_SELECT_POS + n).commit();
                editor.remove("locationY" + KEY_SELECT_POS + n).commit();
                editor.remove("sticky_cnt" + KEY_INPUT_DATA + n).commit();
            }
            btn_edit.setBackgroundColor(Color.argb(180, 181, 147, 100));
            btn_ac.setBackgroundColor(Color.argb(180, 181, 147, 100));
            //.setBackgroundColor(Color.argb(int, int, int, int)
            //BurlyWood#DEB887 180,222,184,135->#b4b59364 180,181,147,100

            btn_edit.setText("activate");
            aState = 0;
            Log.v("edit", "aState became 0");
        }
    }

    //palletをつくるメソッド
    private void createPallet(final int n) {
        imgbtn[n].setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onClick(View v) {
                if (sticky_number[n] == 0) {
                    mMainLayout.addView(bg_sticky[n], new LinearLayout.LayoutParams(WC, WC));
                    bg_sticky[n].setX(location[n][0]);
                    bg_sticky[n].setY(location[n][1]);
                    sticky_number[n] = 1;
                    Log.v("Create", "Button_Sticky" + (n + 1) + " was created and number[" + n + "]became 1");
                }
            }//onClick
        });
        imgbtn[n].setOnLongClickListener(new View.OnLongClickListener() {
            //ボタンが長押しクリックされた時のハンドラ
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public boolean onLongClick(View v) {
                // TODO Auto-generated method stub
                // 長押しクリックされた時の処理を記述
                if (aState == 1) {
                    //トースト出力
                    Toast.makeText(MainActivity.this,
                            "Call the Sticky",
                            Toast.LENGTH_LONG).show();

                    mMainLayout.removeView(bg_sticky[n]);
                    sticky_number[n] = 0;
                    location[n][0] = 150;
                    location[n][1] = 300 + (160 * n);
                    bg_sticky[n].setX(location[n][0]);
                    bg_sticky[n].setY(location[n][1]);
                    Log.v("Remove", "Button_Sticky" + (n + 1) + " was removed and number[" + n + "]became 0");
                }
                Log.v("OnLongClick", "Button was clicked");
                return false;
            }//onLongClick
        });
    }

    //画像に透明化処理を施すメソッド
    private Bitmap imageProcessing(Bitmap input_bmp, int alpha) {
        Bitmap bitmap = input_bmp;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // BitmapDrawableにまかせる方法
        Bitmap bitmap2 = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        android.graphics.Canvas cvs = new android.graphics.Canvas(bitmap2);

        BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
        drawable.setAlpha(alpha);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(cvs);
        bitmap = bitmap2;

        return bitmap;
    }


    //インテント前にエラーがないか確認するメソッド
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkImplicitIntent(Context context, String url) {
        boolean ret = false;
        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        List<ResolveInfo> apps = pm.queryIntentActivities(intent, 0);
        if (apps.size() > 0) {
            ret = true;
        }
        return ret;
    }

    //付箋をつくるメソッド
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void createSticky(final int n) {
        bg_sticky[n] = new LinearLayout(mContext);
        bg_sticky[n].setBackground(new BitmapDrawable(getResources(),
                        imageProcessing(
                                BitmapFactory.decodeResource(
                                        getResources(), getResources().getIdentifier("res_sticky" + (n + 1), "drawable", getPackageName())),
                                178)
                )
        );
        bg_sticky[n].setOrientation(LinearLayout.VERTICAL);


        //ボタン作成
        mEditItem.tv_sticky[n][0] = new TextView(mContext);
        mEditItem.tv_sticky[n][1] = new TextView(mContext);
        bg_sticky[n].addView(mEditItem.tv_sticky[n][0], new LinearLayout.LayoutParams(WC, WC));
        bg_sticky[n].addView(mEditItem.tv_sticky[n][1], new LinearLayout.LayoutParams(WC, WC));

        // 各イベントリスナの登録
        bg_sticky[n].setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.v("OnClick", "Button_Sticky" + (n + 1) + "was clicked");
                if (aState == 0 && bState == 1) {
                    String str_url = mEditItem.tv_sticky[n][1].getText().toString();
                    if (checkImplicitIntent(mContext, str_url) == true) {
                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(str_url));
                        startActivity(i);
                    }
                    if (checkImplicitIntent(mContext, str_url) == false) {
                        Toast.makeText(MainActivity.this,
                                "アドレスを登録してください",
                                Toast.LENGTH_LONG).show();
                    }
                }

                if (aState == 1) {
                    createConfigDialog(n);
                }
                if (aState == 2) {
                    mMainLayout.removeView(bg_sticky[n]);
                    sticky_number[n] = 0;
                    mEditItem.tv_sticky[n][0].setText("");
                    mEditItem.tv_sticky[n][1].setText("");
                    location[n][0] = 0;
                    location[n][1] = 160 * n;
                    bg_sticky[n].setX(location[n][0]);
                    bg_sticky[n].setY(location[n][1]);
                    Log.v("Remove", "Button_Sticky" + (n + 1) + " was removed and number[" + n + "] became 0");
                }
            }
        });

        bg_sticky[n].setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.v("OnLongClick", "Button_Sticky_pink was clicked");
                return false;
            }
        });


        bg_sticky[n].setOnTouchListener(new View.OnTouchListener() {


            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bg_sticky[n].bringToFront();
                // ドラッグ
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        offsetX = (int) event.getX();
                        offsetY = (int) event.getY();
                        mState = STATE_DRAG;
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                        mState = STATE_NONE;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (mState == STATE_DRAG) {
                            location[n][0] = (int) event.getRawX() - offsetX;
                            Window window = getWindow();
                            window.getDecorView().getWindowVisibleDisplayFrame(rect);
                            int statusBarHeight = rect.top; // ステータスバーの高さ
                            location[n][1] = (int) event.getRawY() - (offsetY + statusBarHeight);
                            bg_sticky[n].setX(location[n][0]);
                            bg_sticky[n].setY(location[n][1]);
                        }
                        break;
                }
                return false;
            }
        });

    }

    //コンフィグ画面を作るメソッド
    private void createConfigDialog(final int n) {
        View rootView = this.getLayoutInflater().inflate(R.layout.sticky_config_dialog, null);
        final EditText input_editText_cfg_text = (EditText) rootView.findViewById(R.id.editText_text);
        final EditText input_editText_cfg_url = (EditText) rootView.findViewById(R.id.eText_url_cfg);
        input_editText_cfg_url.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
        String input_str_cfg_text = mEditItem.tv_sticky[n][0].getText().toString();
        String input_str_cfg_url = mEditItem.tv_sticky[n][1].getText().toString();
        if (!input_str_cfg_text.equals("")) {
            input_editText_cfg_text.setText(input_str_cfg_text);
        }
        if (input_str_cfg_text.equals("")) {
            input_editText_cfg_text.setText("テキストを入力してください");
        }
        Log.v("text", "input url = " + input_editText_cfg_url.getText().toString());
        if (!input_str_cfg_url.equals("")) {
            input_editText_cfg_url.setText(input_str_cfg_url);
        }
        if (input_str_cfg_url.equals("")) {
            input_editText_cfg_url.setText("http://life-is-tech.com/");
        }
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(R.string.config_title)
                .setIcon(android.R.drawable.ic_input_get)
                .setView(rootView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Log.v("OnClick", "input text = " + input_editText_cfg_text.getText().toString());
                        //入力した文字をトースト出力する
                        Toast.makeText(MainActivity.this,
                                input_editText_cfg_text.getText().toString() + "\n" + input_editText_cfg_url.getText().toString(),
                                Toast.LENGTH_LONG).show();
                        mEditItem.tv_sticky[n][0].setText(input_editText_cfg_text.getText().toString());
                        mEditItem.tv_sticky[n][1].setText(input_editText_cfg_url.getText().toString());
                    }
                })
                .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();
    }


    private static final String KEY_INPUT_DATA = "input.data";
    private static final String KEY_SELECT_POS = "select.pos";

    //Save & Load method
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void saveTheActivity() {
        Log.v("save", "saving");
        //トースト出力
        //Toast.makeText(MainActivity.this, "セーブします", Toast.LENGTH_LONG).show();
        SharedPreferences data = getSharedPreferences("DataSave", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = data.edit();
        for (int n = 0; n < bg_sticky.length; n++) {
            editor.putInt("sticky_cnt" + KEY_INPUT_DATA + n, sticky_number[n]);
            String str_text = mEditItem.tv_sticky[n][0].getText()
                    .toString();
            String str_url = mEditItem.tv_sticky[n][1].getText()
                    .toString();
            editor.putString("text" + KEY_INPUT_DATA + n, str_text);
            editor.putString("url" + KEY_INPUT_DATA + n, str_url);
            bg_sticky[n].getLocationInWindow(location[n]);
            Window window = getWindow();
            window.getDecorView().getWindowVisibleDisplayFrame(rect);
            int statusBarHeight = rect.top; // ステータスバーの高さ
            editor.putInt("locationX" + KEY_SELECT_POS + n,
                    location[n][0]);
            editor.putInt("locationY" + KEY_SELECT_POS + n,
                    location[n][1] - statusBarHeight);
            editor.apply();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void loadTheActivity() {
        Log.v("load", "loading");
        //トースト出力
        //Toast.makeText(MainActivity.this,"ロードします",Toast.LENGTH_LONG).show();
        SharedPreferences data = getSharedPreferences("DataSave", Context.MODE_PRIVATE);
        for (int n = 0; n < bg_sticky.length; n++) {
            sticky_number[n] = data.getInt("sticky_cnt" + KEY_INPUT_DATA + n, 0);
            String str_text = data.getString("text" + KEY_INPUT_DATA + n, "");
            String str_url = data.getString("url" + KEY_INPUT_DATA + n, "");
            mEditItem.tv_sticky[n][0].setText(str_text);
            mEditItem.tv_sticky[n][1].setText(str_url);
            location[n][0] = data.getInt("locationX" + KEY_SELECT_POS + n, 0);
            location[n][1] = data.getInt("locationY" + KEY_SELECT_POS + n, 160 * n);
            bg_sticky[n].setX(location[n][0]);
            bg_sticky[n].setY(location[n][1]);
        }
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    /* ここで状態を保存 */
        saveTheActivity();
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
  /* ここで保存した状態を読み出して設定 */
        loadTheActivity();
        for (int n = 0; n < bg_sticky.length; n++) {
            if (sticky_number[n] == 1) {
                mMainLayout.addView(bg_sticky[n], new LinearLayout.LayoutParams(WC, WC));
            }
        }
    }

}

