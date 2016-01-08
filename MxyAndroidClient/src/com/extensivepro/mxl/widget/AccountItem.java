package com.extensivepro.mxl.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.extensivepro.mxl.R;

public class AccountItem extends LinearLayout
{

    public AccountItem(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item = inflater.inflate(R.layout.account_item, null);
        item.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        TextView itemTextView = (TextView) item.findViewById(R.id.item_text);
        TypedArray array = context.obtainStyledAttributes(attrs,
                R.styleable.account_item);
        String itemText = array.getString(R.styleable.account_item_text);
        array.recycle();
        itemTextView.setText(itemText);
        addView(item);
    }

    public AccountItem(Context context)
    {
        super(context);
    }

}
