package viaversion.viaversion.api.remapper;

import org.jetbrains.annotations.Nullable;
import viaversion.viaversion.api.PacketWrapper;
import viaversion.viaversion.api.type.Type;
import viaversion.viaversion.exception.InformativeException;

public abstract class ValueTransformer<T1, T2> implements ValueWriter<T1> {
    private final Type<T1> inputType;
    private final Type<T2> outputType;

    public ValueTransformer(@Nullable Type<T1> inputType, Type<T2> outputType) {
        this.inputType = inputType;
        this.outputType = outputType;
    }

    public ValueTransformer(Type<T2> outputType) {
        this(null, outputType);
    }

    /**
     * Transform a value from one type to another
     *
     * @param wrapper    The current packet
     * @param inputValue The input value
     * @return The value to write to the wrapper
     * @throws Exception Throws exception if it fails to transform a value
     */
    public abstract T2 transform(PacketWrapper wrapper, T1 inputValue) throws Exception;

    @Override
    public void write(PacketWrapper writer, T1 inputValue) throws Exception {
        try {
            writer.write(outputType, transform(writer, inputValue));
        } catch (InformativeException e) {
            e.addSource(this.getClass());
            throw e;
        }
    }

    @Nullable
    public Type<T1> getInputType() {
        return inputType;
    }

    public Type<T2> getOutputType() {
        return outputType;
    }
}
