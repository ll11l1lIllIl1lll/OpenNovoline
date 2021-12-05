package viaversion.viaversion.api.minecraft.item;

import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Item {
    @SerializedName(value = "identifier", alternate = "id")
    private int identifier;
    private byte amount;
    private short data;
    private CompoundTag tag;

    public Item() {
    }

    public Item(int identifier, byte amount, short data, @Nullable CompoundTag tag) {
        this.identifier = identifier;
        this.amount = amount;
        this.data = data;
        this.tag = tag;
    }

    public Item(Item toCopy) {
        this(toCopy.getIdentifier(), toCopy.getAmount(), toCopy.getData(), toCopy.getTag());
    }

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public byte getAmount() {
        return amount;
    }

    public void setAmount(byte amount) {
        this.amount = amount;
    }

    public short getData() {
        return data;
    }

    public void setData(short data) {
        this.data = data;
    }

    @Nullable
    public CompoundTag getTag() {
        return tag;
    }

    public void setTag(@Nullable CompoundTag tag) {
        this.tag = tag;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        if (identifier != item.identifier) return false;
        if (amount != item.amount) return false;
        if (data != item.data) return false;
        return Objects.equals(tag, item.tag);
    }

    @Override
    public int hashCode() {
        int result = identifier;
        result = 31 * result + (int) amount;
        result = 31 * result + (int) data;
        result = 31 * result + (tag != null ? tag.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Item{" +
                "identifier=" + identifier +
                ", amount=" + amount +
                ", data=" + data +
                ", tag=" + tag +
                '}';
    }
}
