package com.fisko.selectivetask;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ListViewAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private JSONArray data;
    private Locale locale;

    public ListViewAdapter(Context context, JSONArray data) {
        this.data = data;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.locale = Locale.getDefault();
    }

    private static class ViewHolder {
        TextView eventDate;
        TextView eventTime;
        TextView eventName;
        TextView eventCountryCity;
    }

    private static class ItemDataHolder {
        String eventDate = "";
        String eventTime = "";
        String eventName = "";
        String eventCountryCity = "";
    }

    private void parseDate(String s, ItemDataHolder h) {
        s = s.replace(" at", "");
        s = s.replaceAll(",", "");
        DateFormat inputFormat = new SimpleDateFormat("EEE MMM dd yyyy hh:mma", Locale.ENGLISH);
        DateFormat outputDateFormat = new SimpleDateFormat("dd MMMMM yyyy", locale);
        DateFormat outputTimeFormat = new SimpleDateFormat("HH:mm", locale);
        Date parsedDate;

        try {
            parsedDate = inputFormat.parse(s);
            h.eventDate =  outputDateFormat.format(parsedDate);
            h.eventTime =  outputTimeFormat.format(parsedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCount() {
        return data.length();
    }

    @Override
    public JSONObject getItem(int position) {
        try {
            return (JSONObject) data.get(position);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        final ViewHolder holder;

        if (view == null) {
            view = inflater.inflate(R.layout.list_item, null);
            holder = new ViewHolder();
            holder.eventDate = (TextView) view.findViewById(R.id.eventDate);
            holder.eventTime = (TextView) view.findViewById(R.id.eventTime);
            holder.eventName = (TextView) view.findViewById(R.id.eventName);
            holder.eventCountryCity = (TextView) view.findViewById(R.id.eventCountryCity);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        ItemDataHolder h = new ItemDataHolder();
        try {
            JSONObject item = getItem(position);
            parseDate(item.getString("formatted_datetime"), h);
            JSONObject venue = item.getJSONObject("venue");
            h.eventName = venue.getString("name");
            h.eventCountryCity = venue.getString("country") + ", " + venue.getString("city");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.eventDate.setText(h.eventDate);
        holder.eventTime.setText(h.eventTime);
        holder.eventName.setText(h.eventName);
        holder.eventCountryCity.setText(h.eventCountryCity);

        return view;
    }
}
