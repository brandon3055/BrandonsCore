package com.brandon3055.brandonscore.lib.datamanager;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Function;

/**
 * Created by brandon3055 on 12/06/2017.
 */
public class ManagedTextComponent extends AbstractManagedData<ITextComponent> {

    private ITextComponent value;
    protected Function<ITextComponent, ITextComponent> validator = null;

    public ManagedTextComponent(String name, @Nullable ITextComponent defaultValue, DataFlags... flags) {
        super(name, flags);
        this.value = defaultValue;
    }

    /**
     * Default "" (Empty String)
     */
    public ManagedTextComponent(String name, DataFlags... flags) {
        this(name, null, flags);
    }

    public ITextComponent set(@Nullable ITextComponent value) {
        if (!Objects.equals(this.value, value)) {
            boolean set = true;
            ITextComponent prev = this.value;
            this.value = value;

            if (dataManager.isClientSide() && flags.allowClientControl) {
                dataManager.sendToServer(this);
                set = ccscsFlag;
            }

            if (set) {
                markDirty();
                notifyListeners(value);
            }
            else {
                this.value = prev;
            }
        }

        return this.value;
    }

    @Nullable
    public ITextComponent get() {
        return value;
    }

    /**
     * Use to validate new values. Use this to enforce any restrictions such as min/max then return the corrected value.
     *
     * @param validator a validator function that takes an input, applies restrictions if needed then returns the updated value.
     * @return
     */
    public ManagedTextComponent setValidator(Function<ITextComponent, ITextComponent> validator) {
        this.validator = validator;
        return this;
    }

    @Override
    public void validate() {
        if (validator != null) {
            value = validator.apply(value);
        }
    }

    @Override
    public void toBytes(MCDataOutput output) {
        output.writeBoolean(value != null);
        if (value != null) {
            output.writeTextComponent(value);
        }
    }

    @Override
    public void fromBytes(MCDataInput input) {
        if (input.readBoolean()){
            value = input.readTextComponent();
        } else {
            value = null;
        }
        notifyListeners(value);
    }

    @Override
    public void toNBT(CompoundNBT compound) {
        if (value != null) {
            compound.putString(name, ITextComponent.Serializer.toJson(value));
        }
    }

    @Override
    public void fromNBT(CompoundNBT compound) {
        if (compound.contains(name)) {
            value = ITextComponent.Serializer.fromJson(compound.getString(name));
        } else {
            value = null;
        }
        notifyListeners(value);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":[" + getName() + "="+ value + "]";
    }
}
