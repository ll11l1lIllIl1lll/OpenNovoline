package viaversion.viaversion.exception;

import io.netty.handler.codec.DecoderException;
import viaversion.viaversion.api.Via;

/**
 * Thrown during packet decoding when an incoming packet should be cancelled.
 * Specifically extends {@link DecoderException} to prevent netty from wrapping the exception.
 */
public class CancelDecoderException extends DecoderException implements CancelCodecException {
    public static final CancelDecoderException CACHED = new CancelDecoderException("This packet is supposed to be cancelled; If you have debug enabled, you can ignore these") {
        @Override
        public Throwable fillInStackTrace() {
            return this;
        }
    };

    public CancelDecoderException() {
        super();
    }

    public CancelDecoderException(String message, Throwable cause) {
        super(message, cause);
    }

    public CancelDecoderException(String message) {
        super(message);
    }

    public CancelDecoderException(Throwable cause) {
        super(cause);
    }

    /**
     * Returns a cached CancelDecoderException or a new instance when {@link viaversion.viaversion.ViaManager#isDebug()} is true.
     *
     * @param cause cause for being used when a new instance is creeated
     * @return a CancelDecoderException instance
     */
    public static CancelDecoderException generate(Throwable cause) {
        return Via.getManager().isDebug() ? new CancelDecoderException(cause) : CACHED;
    }
}
