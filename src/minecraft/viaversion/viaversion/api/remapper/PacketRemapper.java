package viaversion.viaversion.api.remapper;

import viaversion.viaversion.api.PacketWrapper;
import viaversion.viaversion.api.Pair;
import viaversion.viaversion.api.type.Type;
import viaversion.viaversion.exception.CancelException;
import viaversion.viaversion.exception.InformativeException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class PacketRemapper {
    private final List<Pair<ValueReader, ValueWriter>> valueRemappers = new ArrayList<>();

    public PacketRemapper() {
        registerMap();
    }

    /**
     * Map a type to the same type.
     *
     * @param type Type to map
     */
    public void map(Type type) {
        TypeRemapper remapper = new TypeRemapper(type);
        map(remapper, remapper);
    }

    /**
     * Map a type from an old type to a new type
     *
     * @param oldType The old type
     * @param newType The new type
     */
    public void map(Type oldType, Type newType) {
        map(new TypeRemapper(oldType), new TypeRemapper(newType));
    }

    /**
     * Map a type from an old type to a transformed new type.
     *
     * @param oldType     The old type
     * @param <T1>        The old return type.
     * @param newType     The new type
     * @param <T2>        The new return type.
     * @param transformer The transformer to use to produce the new type.
     */
    public <T1, T2> void map(Type<T1> oldType, Type<T2> newType, Function<T1, T2> transformer) {
        map(new TypeRemapper<>(oldType), new ValueTransformer<T1, T2>(newType) {
            @Override
            public T2 transform(PacketWrapper wrapper, T1 inputValue) throws Exception {
                return transformer.apply(inputValue);
            }
        });
    }

    /**
     * Map a type from an old type to a transformed new type.
     *
     * @param <T1>        The old return type.
     * @param transformer The transformer to use to produce the new type.
     * @param <T2>        The new return type.
     */
    public <T1, T2> void map(ValueTransformer<T1, T2> transformer) {
        if (transformer.getInputType() == null) {
            throw new IllegalArgumentException("Use map(Type<T1>, ValueTransformer<T1, T2>) for value transformers without specified input type!");
        }
        map(transformer.getInputType(), transformer);
    }

    /**
     * Map a type from an old type to a transformed new type.
     *
     * @param oldType     The old type
     * @param <T1>        The old return type.
     * @param transformer The transformer to use to produce the new type.
     * @param <T2>        The new return type.
     */
    public <T1, T2> void map(Type<T1> oldType, ValueTransformer<T1, T2> transformer) {
        map(new TypeRemapper(oldType), transformer);
    }

    /**
     * Map a type using a basic ValueReader to a ValueWriter
     *
     * @param inputReader  The reader to read with.
     * @param outputWriter The writer to write with
     * @param <T>          The return type
     */
    public <T> void map(ValueReader<T> inputReader, ValueWriter<T> outputWriter) {
        valueRemappers.add(new Pair<>(inputReader, outputWriter));
    }

    /**
     * Create a value
     *
     * @param creator The creator to used to make the value(s).
     */
    public void create(ValueCreator creator) {
        map(new TypeRemapper(Type.NOTHING), creator);
    }

    /**
     * Create a handler
     *
     * @param handler The handler to use to handle the current packet.
     */
    public void handler(PacketHandler handler) {
        map(new TypeRemapper(Type.NOTHING), handler);
    }

    /**
     * Register the mappings for this packet
     */
    public abstract void registerMap();

    /**
     * Remap a packet wrapper
     *
     * @param packetWrapper The wrapper to remap
     * @throws InformativeException if it fails to write / read to the packet
     * @throws CancelException      if the packet should be cancelled
     */
    public void remap(PacketWrapper packetWrapper) throws Exception {
        try {
            // Read all the current values
            for (Pair<ValueReader, ValueWriter> valueRemapper : valueRemappers) {
                Object object = valueRemapper.getKey().read(packetWrapper);
                // Convert object to write type :O!!!
                valueRemapper.getValue().write(packetWrapper, object);
            }
            // If we had handlers we'd put them here
        } catch (InformativeException e) {
            e.addSource(this.getClass());
            throw e;
        } catch (CancelException e) {
            // Pass through CancelExceptions
            throw e;
        } catch (Exception e) {
            // Wrap other exceptions during packet handling
            InformativeException ex = new InformativeException(e);
            ex.addSource(this.getClass());
            throw ex;
        }
    }
}
