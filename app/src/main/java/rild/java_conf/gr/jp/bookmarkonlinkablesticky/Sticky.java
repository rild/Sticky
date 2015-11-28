package rild.java_conf.gr.jp.bookmarkonlinkablesticky;

import android.content.Context;
import android.widget.TextView;

class Sticky {
    public TextView[][] tv_sticky = new TextView[4][2];
    private Context mContext;

    public Sticky(Context mContext) {
        this.mContext = mContext;
    }
}