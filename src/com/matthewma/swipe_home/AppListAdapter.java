package com.matthewma.swipe_home;

import com.matthewma.swipe_home.SettingsActivity.PInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AppListAdapter extends ArrayAdapter<PInfo> {
	private Context context;
	private PInfo[] pinfos;

	public AppListAdapter(Context context, int textViewResourceId,
			PInfo[] pinfos) {
		super(context, textViewResourceId, pinfos);
		this.context = context;
		this.pinfos = pinfos;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.app_list_row, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.app_name);
		textView.setText(pinfos[position].appName);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.app_icon);
		imageView.setImageDrawable(pinfos[position].icon);
		return rowView;
	}
}