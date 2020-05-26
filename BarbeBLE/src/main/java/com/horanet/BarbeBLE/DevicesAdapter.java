package com.horanet.BarbeBLE;

import android.bluetooth.le.ScanResult;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.ViewHolder>  {


    public interface DevicesAdapterListener {
        void onDeviceItemClick(String deviceName, String deviceAddress);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView mDeviceNameView;

        ViewHolder(View view) {

            super(view);
            mDeviceNameView = (TextView) view.findViewById(R.id.device_name);
        }

    }

    private ArrayList<ScanResult> mArrayList;
    private DevicesAdapterListener mListener;


    public DevicesAdapter(DevicesAdapterListener listener) {
        mArrayList = new ArrayList<>();
        mListener = listener;
    }

    public DevicesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_device_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ScanResult scanResult = mArrayList.get(position);
        final String deviceName = scanResult.getDevice().getName();
        final String deviceAddress = scanResult.getDevice().getAddress();


        if (TextUtils.isEmpty(deviceName)) {
            holder.mDeviceNameView.setText("");
        } else {
            holder.mDeviceNameView.setText(deviceName);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(deviceName) && !TextUtils.isEmpty(deviceAddress)) {
                    if (mListener != null) {
                        mListener.onDeviceItemClick(deviceName, deviceAddress);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    public void add(ScanResult scanResult) {
        add(scanResult, true);
    }


    public void add(ScanResult scanResult, boolean notify) {

        if (scanResult == null) {
            return;
        }

        int existingPosition = getPosition(scanResult.getDevice().getAddress());

        if (existingPosition >= 0) {
            mArrayList.set(existingPosition, scanResult);
        } else {
            mArrayList.add(scanResult);
        }

        if (notify) {
            notifyDataSetChanged();
        }

    }

    public void add(List<ScanResult> scanResults) {
        if (scanResults != null) {
            for (ScanResult scanResult : scanResults) {
                add(scanResult, false);
            }
            notifyDataSetChanged();
        }
    }

    private int getPosition(String address) {
        int position = -1;
        for (int i = 0; i < mArrayList.size(); i++) {
            if (mArrayList.get(i).getDevice().getAddress().equals(address)) {
                position = i;
                break;
            }
        }
        return position;
    }


}
