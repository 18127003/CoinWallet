package me.app.coinwallet.transfer;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import me.app.coinwallet.Constants;
import me.app.coinwallet.utils.WalletUtil;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.protocols.payments.PaymentProtocol;
import org.bitcoinj.protocols.payments.PaymentProtocolException;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.script.ScriptException;
import org.bitcoinj.uri.BitcoinURI;
import org.bitcoinj.uri.BitcoinURIParseException;
import org.bitcoinj.wallet.SendRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static androidx.core.util.Preconditions.checkArgument;

public class PaymentRequest implements Parcelable {
    public enum Standard {
        NONE, BIP21
    }

    public final Standard standard;

    @NonNull
    public final List<Output> outputs;

    @NonNull
    public final Boolean useBluetooth;

    @Nullable
    public final String memo;

    public PaymentRequest(final Standard standard, @NonNull final List<Output> outputs,
                         @Nullable final String memo, final boolean useBluetooth) {
        this.standard = standard;
        this.outputs = outputs;
        this.useBluetooth = useBluetooth;
        this.memo = memo;
    }

    private PaymentRequest(final Address address, @Nullable final String addressLabel) {
        this(Standard.NONE, buildSimplePayTo(Coin.ZERO, address), addressLabel, false);
    }

    public static PaymentRequest from(final Address address, @Nullable final String addressLabel) {
        return new PaymentRequest(address, addressLabel);
    }

    public static PaymentRequest from(final Address address, @Nullable final String addressLabel, @Nullable final Coin amount,
                                      boolean useBluetooth) {
        return new PaymentRequest(Standard.NONE, buildSimplePayTo(amount, address),addressLabel, useBluetooth);
    }

    public static PaymentRequest from(final String address, final String amount, boolean useBluetooth)
            throws IllegalArgumentException {
        Address sendAddress = Address.fromString(Constants.NETWORK_PARAMETERS, address);
        Coin sendAmount = Coin.parseCoin(amount);
        return PaymentRequest.from(sendAddress, "", sendAmount, useBluetooth);
    }

    public static PaymentRequest from(final String string) throws BitcoinURIParseException {
        if(string.startsWith("bitcoin")){
            return fromBitcoinUri(new BitcoinURI(string));
        }
        return from(Address.fromString(Constants.NETWORK_PARAMETERS, string),null);
    }

    public static PaymentRequest fromBitcoinUri(final BitcoinURI bitcoinUri) {
        final Address address = bitcoinUri.getAddress();
        final List<Output> outputs = address != null ? buildSimplePayTo(bitcoinUri.getAmount(), address) : null;
        final boolean useBluetooth = Boolean.parseBoolean((String) bitcoinUri.getParameterByName(Constants.BT_ENABLED_PARAM));
        return new PaymentRequest(Standard.BIP21, outputs, bitcoinUri.getLabel(), useBluetooth);
    }

    public PaymentRequest mergeWithEditedValues(final String editedAmount) throws IllegalArgumentException{
        Coin amount = Coin.parseCoin(editedAmount);
        return mergeWithEditedValues(amount);
    }

    public PaymentRequest mergeWithEditedValues(final Coin editedAmount) {
        final Output[] outputs;

        if (hasOutputs()) {
            outputs = new Output[] { new Output(editedAmount, this.outputs.get(0).script) };
        } else {
            throw new IllegalStateException();
        }

        return new PaymentRequest(standard, Arrays.asList(outputs), memo, useBluetooth);
    }

    public SendRequest toSendRequest() {
        final Transaction transaction = new Transaction(Constants.NETWORK_PARAMETERS);
        outputs.forEach(output -> transaction.addOutput(output.amount, output.script));
        return SendRequest.forTx(transaction);
    }

    private static List<Output> buildSimplePayTo(final Coin amount, final Address address) {
        return Collections.singletonList(new Output(amount, ScriptBuilder.createOutputScript(address)));
    }

    public boolean hasOutputs() {
        return outputs.size() > 0;
    }

    public boolean hasAddress() {
        return getAddress() != null;
    }

    public Address getAddress() {
        if (outputs.size() != 1)
            throw new IllegalStateException();

        final Script script = outputs.get(0).script;
        final Address address = WalletUtil.getToAddress(script);
        if (address == null)
            throw new IllegalStateException();

        return address;
    }

    public boolean hasAmount() {
        if (hasOutputs())
            return outputs.stream().anyMatch(Output::hasAmount);
        return false;
    }

    public boolean useBluetooth() {
        return useBluetooth;
    }

    public boolean equalsAmount(final PaymentRequest other) {
        final boolean hasAmount = hasAmount();
        if (hasAmount != other.hasAmount())
            return false;
        return !hasAmount || getAmount().equals(other.getAmount());
    }

    public boolean equalsAddress(final PaymentRequest other) {
        final boolean hasAddress = hasAddress();
        if (hasAddress != other.hasAddress())
            return false;
        return !hasAddress || getAddress().equals(other.getAddress());
    }



    public Coin getAmount() {
        Coin amount = Coin.ZERO;

        if (hasOutputs()){
            amount = outputs.stream().map(output -> output.hasAmount()?output.amount:Coin.ZERO).reduce(Coin::add)
                    .orElse(Coin.ZERO);
        }

        if (amount.signum() != 0)
            return amount;
        else
            return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeSerializable(standard);
        dest.writeInt(outputs.size());
        dest.writeTypedArray(outputs.toArray(new Output[0]), 0);
        dest.writeString(memo);
        dest.writeInt(useBluetooth?1:0);
    }

    public static final Parcelable.Creator<PaymentRequest> CREATOR = new Parcelable.Creator<PaymentRequest>() {
        @Override
        public PaymentRequest createFromParcel(final Parcel in) {
            return new PaymentRequest(in);
        }

        @Override
        public PaymentRequest[] newArray(final int size) {
            return new PaymentRequest[size];
        }
    };

    private PaymentRequest(final Parcel in) {
        standard = (Standard) in.readSerializable();
        final int outputsLength = in.readInt();
        if (outputsLength > 0) {
            Output[] outputs1 = new Output[outputsLength];
            in.readTypedArray(outputs1, Output.CREATOR);
            outputs = Arrays.stream(outputs1).collect(Collectors.toList());
        } else {
            outputs = new ArrayList<>();
        }
        memo = in.readString();
        useBluetooth = in.readInt() == 1;
    }

    public final static class Output implements Parcelable {
        public final Coin amount;
        public final Script script;

        public Output(final Coin amount, final Script script) {
            this.amount = amount;
            this.script = script;
        }

        public static Output valueOf(final PaymentProtocol.Output output)
                throws PaymentProtocolException.InvalidOutputs {
            try {
                final Script script = new Script(output.scriptData);
                return new PaymentRequest.Output(output.amount, script);
            } catch (final ScriptException x) {
                throw new PaymentProtocolException.InvalidOutputs(
                        "unparseable script in output: " + Constants.HEX.encode(output.scriptData));
            }
        }

        public boolean hasAmount() {
            return amount != null && amount.signum() != 0;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(final Parcel dest, final int flags) {
            dest.writeSerializable(amount);
            final byte[] program = script.getProgram();
            dest.writeInt(program.length);
            dest.writeByteArray(program);
        }

        public static final Parcelable.Creator<Output> CREATOR = new Parcelable.Creator<Output>() {
            @Override
            public Output createFromParcel(final Parcel in) {
                return new Output(in);
            }

            @Override
            public Output[] newArray(final int size) {
                return new Output[size];
            }
        };

        private Output(final Parcel in) {
            amount = (Coin) in.readSerializable();
            final int programLength = in.readInt();
            final byte[] program = new byte[programLength];
            in.readByteArray(program);
            script = new Script(program);
        }
    }
}
