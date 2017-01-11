package com.example.wifidirecttest;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class LogMessagesAdapter extends ArrayAdapter<String> {
    public LogMessagesAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_1);
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = createNewItemView();
        }

        final TextView item = (TextView) convertView;
        item.setText(getItem(position));
        return item;
    }

    private TextView createNewItemView() {
        final TextView newItem = new TextView(getContext());
        newItem.setPadding(0,0,0,0);
        newItem.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        newItem.setTextColor(ContextCompat.getColor(getContext(), R.color.grey));
        newItem.setTypeface(Typeface.MONOSPACE);
        return newItem;
    }
}
