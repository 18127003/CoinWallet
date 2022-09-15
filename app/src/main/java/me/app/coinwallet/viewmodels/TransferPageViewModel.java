package me.app.coinwallet.viewmodels;

import android.app.Application;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import me.app.coinwallet.Configuration;
import me.app.coinwallet.WalletApplication;
import me.app.coinwallet.bitcoinj.LocalWallet;
import me.app.coinwallet.bluetooth.BluetoothPaymentRequest;
import me.app.coinwallet.bluetooth.DirectPaymentRequest;
import me.app.coinwallet.data.addressbook.AddressBookDao;
import me.app.coinwallet.data.addressbook.AddressBookDatabase;
import me.app.coinwallet.data.addressbook.AddressBookEntry;
import me.app.coinwallet.transfer.*;
import me.app.coinwallet.utils.Utils;
import org.bitcoin.protocols.payments.Protos;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.protocols.payments.PaymentProtocol;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TransferPageViewModel extends AndroidViewModel {
    private final AddressBookDao addressBookDao;
    public PaymentRequest paymentRequest;
    private final Configuration configuration;
    public final MutableLiveData<SendMethod> sendMethod = new MutableLiveData<>(SendMethod.getDefault());
    private final LocalWallet localWallet = LocalWallet.getInstance();

    public TransferPageViewModel(@NonNull Application application) {
        super(application);
        addressBookDao = AddressBookDatabase.getDatabase(application.getApplicationContext()).addressBookDao();
        configuration = ((WalletApplication) application).getConfiguration();
    }

    public void saveToAddressBook(String label, String address){
        addressBookDao.insertOrUpdate(new AddressBookEntry(address, label));
    }

    public void send(Recipient recipient, String password) throws AddressFormatException, IllegalArgumentException{
        Address sendTo = Address.fromString(localWallet.parameters(), recipient.address);
        paymentRequest = PaymentRequest.from(sendTo, recipient.amount, false);
        send(password);
    }

    public void send(List<Recipient> recipients, String password) throws AddressFormatException, IllegalArgumentException{
        if(recipients.isEmpty()){
            throw new IllegalArgumentException("No available recipients");
        }
        List<PaymentRequest> paymentRequests = recipients.stream().map(recipient -> {
            Address sendTo = Address.fromString(localWallet.parameters(), recipient.address);
            return PaymentRequest.from(sendTo, recipient.amount, false);
        }).collect(Collectors.toList());
        paymentRequest = PaymentRequest.merge(paymentRequests);
        send(password);
    }

    public void send(String password){
        SendMethod method = sendMethod.getValue();
        SendTask sendTask;
        switch (method.method){
            case DEFAULT:
                if(Utils.hasInternetAccess(getApplication())){
                    sendTask = new SimpleSendTask(configuration) {
                        @Override
                        protected void onSuccess(Transaction transaction) {
                            configuration.toastUtil.toast("Send money success", Toast.LENGTH_SHORT);
                        }
                    };
                } else {
                    sendTask = new OfflineSendTask(configuration) {
                        @Override
                        protected void onSuccess(Transaction transaction) {
                            configuration.toastUtil.toast("Send money success, wait for Internet to broadcast", Toast.LENGTH_SHORT);
                        }
                    };
                }
                break;
            case BLUETOOTH:
                sendTask = new OfflineSendTask(configuration) {
                    @Override
                    protected void onSuccess(Transaction transaction) {
                        Protos.Payment payment = PaymentProtocol.createPaymentMessage(Collections.singletonList(transaction),
                                null,null, paymentRequest.memo, null);
                        sendBluetooth(payment);
                    }
                };
                break;
            default:
                sendTask = new SimpleSendTask(configuration) {
                    @Override
                    protected void onSuccess(Transaction transaction) {
                        configuration.toastUtil.toast("Send money success", Toast.LENGTH_SHORT);
                    }
                };
        }
        sendTask.send(paymentRequest.toSendRequest(), password);
    }

    private void sendBluetooth(Protos.Payment payment){
        DirectPaymentRequest.ResultCallback callback = new DirectPaymentRequest.ResultCallback() {
            @Override
            public void onResult(boolean ack) {
                if (ack){
                    configuration.toastUtil.postToast("Transaction sent", Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onFail(int messageResId, Object... messageArgs) {
                configuration.toastUtil.postToast("Transaction sent failed", Toast.LENGTH_SHORT);
            }
        };
        new BluetoothPaymentRequest(configuration.executorService, callback, sendMethod.getValue().bluetoothDevice)
                .send(payment);
    }

    public LiveData<List<AddressBookEntry>> getAddressBook() {
        return addressBookDao.getAll();
    }

    public static class Recipient{
        public String address;
        public String amount;
        public Recipient(String address, String amount){
            this.address = address;
            this.amount = amount;
        }
    }
}
