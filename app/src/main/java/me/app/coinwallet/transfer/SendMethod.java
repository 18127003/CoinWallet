package me.app.coinwallet.transfer;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

public class SendMethod implements Parcelable {
    protected SendMethod(Parcel in) {
        method = (Method) in.readSerializable();
        bluetoothDevice = in.readParcelable(BluetoothDevice.class.getClassLoader());
    }

    public static final Creator<SendMethod> CREATOR = new Creator<SendMethod>() {
        @Override
        public SendMethod createFromParcel(Parcel in) {
            return new SendMethod(in);
        }

        @Override
        public SendMethod[] newArray(int size) {
            return new SendMethod[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(method);
        dest.writeParcelable(bluetoothDevice, flags);
    }

    public enum Method {
        DEFAULT,
        BLUETOOTH
    }
    public BluetoothDevice bluetoothDevice;
    public final Method method;

    public SendMethod(Method method){
        this.method = method;
    }

    public static SendMethod getBluetooth(BluetoothDevice device){
        SendMethod sendMethod = new SendMethod(Method.BLUETOOTH);
        sendMethod.bluetoothDevice = device;
        return sendMethod;
    }

    public static SendMethod getDefault(){
        return new SendMethod(Method.DEFAULT);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(method.toString());
        if(bluetoothDevice!=null){
            builder.append(" - ");
            builder.append(bluetoothDevice.getName());
        }
        return builder.toString();
    }
}
