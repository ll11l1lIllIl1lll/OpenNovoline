package net.minecraft.client.audio;

import com.google.common.collect.*;
import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import paulscode.sound.*;
import paulscode.sound.codecs.CodecJOrbis;
import paulscode.sound.libraries.LibraryLWJGLOpenAL;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SoundManager {

    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * The marker used for logging
     */
    private static final Marker LOG_MARKER = MarkerManager.getMarker("SOUNDS");

    /**
     * A reference to the sound handler.
     */
    private final SoundHandler sndHandler;

    /**
     * Reference to the GameSettings object.
     */
    private final GameSettings options;

    /**
     * A reference to the sound system.
     */
    private SoundManager.SoundSystemStarterThread sndSystem;

    /**
     * Set to true when the SoundManager has been initialised.
     */
    private boolean loaded;

    /**
     * A counter for how long the sound manager has been running
     */
    private int playTime = 0;
    private final Map<String, ISound> playingSounds = HashBiMap.create();
    private final Map<ISound, String> invPlayingSounds;
    private Map<ISound, SoundPoolEntry> playingSoundPoolEntries;
    private final Multimap<SoundCategory, String> categorySounds;
    private final List<ITickableSound> tickableSounds;
    private final Map<ISound, Integer> delayedSounds;
    private final Map<String, Integer> playingSoundsStopTime;

    public SoundManager(SoundHandler p_i45119_1_, GameSettings p_i45119_2_) {
        this.invPlayingSounds = ((BiMap) this.playingSounds).inverse();
        this.playingSoundPoolEntries = Maps.newHashMap();
        this.categorySounds = HashMultimap.create();
        this.tickableSounds = Lists.newArrayList();
        this.delayedSounds = Maps.newHashMap();
        this.playingSoundsStopTime = Maps.newHashMap();
        this.sndHandler = p_i45119_1_;
        this.options = p_i45119_2_;

        try {
            SoundSystemConfig.addLibrary(LibraryLWJGLOpenAL.class);
            SoundSystemConfig.setCodec("ogg", CodecJOrbis.class);
        } catch (SoundSystemException soundsystemexception) {
            LOGGER.error(LOG_MARKER, "Error linking with the LibraryJavaSound plug-in", soundsystemexception);
        }
    }

    public void reloadSoundSystem() {
        this.unloadSoundSystem();
        this.loadSoundSystem();
    }

    /**
     * Tries to add the paulscode library and the relevant codecs. If it fails, the master volume  will be set to zero.
     */

    private synchronized void loadSoundSystem() {
        if (!this.loaded) {
            try {
                new Thread(() -> {
                    SoundSystemConfig.setLogger(new SoundSystemLogger() {

                        public void message(String p_message_1_, int p_message_2_) {
                            if (!p_message_1_.isEmpty()) {
                                SoundManager.LOGGER.info(p_message_1_);
                            }
                        }

                        public void importantMessage(String p_importantMessage_1_, int p_importantMessage_2_) {
                            if (!p_importantMessage_1_.isEmpty()) {
                                SoundManager.LOGGER.warn(p_importantMessage_1_);
                            }
                        }

                        public void errorMessage(String p_errorMessage_1_, String p_errorMessage_2_, int p_errorMessage_3_) {
                            if (!p_errorMessage_2_.isEmpty()) {
                                SoundManager.LOGGER.error("Error in class '" + p_errorMessage_1_ + "'");
                                SoundManager.LOGGER.error(p_errorMessage_2_);
                            }
                        }
                    });
                    SoundManager.this.sndSystem = SoundManager.this.new SoundSystemStarterThread();
                    SoundManager.this.loaded = true;
                    SoundManager.this.sndSystem.setMasterVolume(SoundManager.this.options.getSoundLevel(SoundCategory.MASTER));
                    SoundManager.LOGGER.info(SoundManager.LOG_MARKER, "Sound engine started");
                }, "Sound Library Loader").start();
            } catch (RuntimeException runtimeexception) {
                LOGGER.error(LOG_MARKER, "Error starting SoundSystem. Turning off sounds & music", runtimeexception);
                this.options.setSoundLevel(SoundCategory.MASTER, 0.0F);
                this.options.saveOptions();
            }
        }
    }

    /**
     * Returns the sound level (between 0.0 and 1.0) for a category, but 1.0 for the master sound category
     */
    private float getSoundCategoryVolume(SoundCategory category) {
        return category != null && category != SoundCategory.MASTER ? this.options.getSoundLevel(category) : 1.0F;
    }

    /**
     * Adjusts volume for currently playing sounds in this category
     */
    public void setSoundCategoryVolume(SoundCategory category, float volume) {
        if (this.loaded) {
            if (category == SoundCategory.MASTER) {
                this.sndSystem.setMasterVolume(volume);
            } else {
                for (String s : this.categorySounds.get(category)) {
                    ISound isound = this.playingSounds.get(s);
                    float f = this.getNormalizedVolume(isound, this.playingSoundPoolEntries.get(isound), category);

                    if (f <= 0.0F) {
                        this.stopSound(isound);
                    } else {
                        this.sndSystem.setVolume(s, f);
                    }
                }
            }
        }
    }

    /**
     * Cleans up the Sound System
     */
    public void unloadSoundSystem() {
        if (this.loaded) {
            this.stopAllSounds();
            this.sndSystem.cleanup();
            this.loaded = false;
        }
    }

    /**
     * Stops all currently playing sounds
     */
    public void stopAllSounds() {
        if (this.loaded) {
            for (String s : this.playingSounds.keySet()) {
                this.sndSystem.stop(s);
            }

            this.playingSounds.clear();
            this.delayedSounds.clear();
            this.tickableSounds.clear();
            this.categorySounds.clear();
            this.playingSoundPoolEntries.clear();
            this.playingSoundsStopTime.clear();
        }
    }

    public void updateAllSounds() {
        ++this.playTime;

        for (ITickableSound itickablesound : this.tickableSounds) {
            itickablesound.update();

            if (itickablesound.isDonePlaying()) {
                this.stopSound(itickablesound);
            } else {
                String s = this.invPlayingSounds.get(itickablesound);
                this.sndSystem.setVolume(s, this.getNormalizedVolume(itickablesound, this.playingSoundPoolEntries.get(itickablesound), this.sndHandler.getSound(itickablesound.getSoundLocation()).getSoundCategory()));
                this.sndSystem.setPitch(s, this.getNormalizedPitch(itickablesound, this.playingSoundPoolEntries.get(itickablesound)));
                this.sndSystem.setPosition(s, itickablesound.getXPosF(), itickablesound.getYPosF(), itickablesound.getZPosF());
            }
        }

        Iterator<Entry<String, ISound>> iterator = this.playingSounds.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<String, ISound> entry = iterator.next();
            String s1 = entry.getKey();
            ISound isound = entry.getValue();

            if (!this.sndSystem.playing(s1)) {
                int i = this.playingSoundsStopTime.get(s1);

                if (i <= this.playTime) {
                    int j = isound.getRepeatDelay();

                    if (isound.canRepeat() && j > 0) {
                        this.delayedSounds.put(isound, this.playTime + j);
                    }

                    iterator.remove();
                    LOGGER.debug(LOG_MARKER, "Removed channel {} because it's not playing anymore", s1);
                    this.sndSystem.removeSource(s1);
                    this.playingSoundsStopTime.remove(s1);
                    this.playingSoundPoolEntries.remove(isound);

                    try {
                        this.categorySounds.remove(this.sndHandler.getSound(isound.getSoundLocation()).getSoundCategory(), s1);
                    } catch (RuntimeException var8) {
                    }

                    if (isound instanceof ITickableSound) {
                        this.tickableSounds.remove(isound);
                    }
                }
            }
        }

        Iterator<Entry<ISound, Integer>> iterator1 = this.delayedSounds.entrySet().iterator();

        while (iterator1.hasNext()) {
            Entry<ISound, Integer> entry = iterator1.next();

            if (this.playTime >= entry.getValue()) {
                ISound sound = entry.getKey();

                if (sound instanceof ITickableSound) {
                    ((ITickableSound) sound).update();
                }

                this.playSound(sound);
                iterator1.remove();
            }
        }
    }

    /**
     * Returns true if the sound is playing or still within time
     */
    public boolean isSoundPlaying(ISound sound) {
        if (!this.loaded) {
            return false;
        } else {
            String s = this.invPlayingSounds.get(sound);
            return s != null && (this.sndSystem.playing(s) || this.playingSoundsStopTime.containsKey(s) && this.playingSoundsStopTime.get(s) <= this.playTime);
        }
    }

    public void stopSound(ISound sound) {
        if (this.loaded) {
            String s = this.invPlayingSounds.get(sound);

            if (s != null) {
                this.sndSystem.stop(s);
            }
        }
    }

    public void playSound(ISound sound) {
        if (this.loaded) {
            if (this.sndSystem.getMasterVolume() <= 0.0F) {
                LOGGER.debug(LOG_MARKER, "Skipped playing soundEvent: {}, master volume was zero", sound.getSoundLocation());
            } else {
                SoundEventAccessorComposite accessorComposite = this.sndHandler.getSound(sound.getSoundLocation());

                if (accessorComposite == null) {
                    LOGGER.warn(LOG_MARKER, "Unable to play unknown soundEvent: {}", sound.getSoundLocation());
                } else {
                    SoundPoolEntry soundpoolentry = accessorComposite.cloneEntry();

                    if (soundpoolentry == SoundHandler.missing_sound) {
                        LOGGER.warn(LOG_MARKER, "Unable to play empty soundEvent: {}", accessorComposite.getSoundEventLocation());
                    } else {
                        float f = sound.getVolume();
                        float f1 = 16.0F;

                        if (f > 1.0F) {
                            f1 *= f;
                        }

                        SoundCategory soundcategory = accessorComposite.getSoundCategory();
                        float f2 = this.getNormalizedVolume(sound, soundpoolentry, soundcategory);
                        double d0 = this.getNormalizedPitch(sound, soundpoolentry);
                        ResourceLocation resourcelocation = soundpoolentry.getSoundPoolEntryLocation();

                        if (f2 == 0.0F) {
                            LOGGER.debug(LOG_MARKER, "Skipped playing sound {}, volume was zero.", resourcelocation);
                        } else {
                            boolean flag = sound.canRepeat() && sound.getRepeatDelay() == 0;
                            String s = MathHelper.getRandomUuid(ThreadLocalRandom.current()).toString();

                            if (soundpoolentry.isStreamingSound()) {
                                this.sndSystem.newStreamingSource(false, s, getURLForSoundResource(resourcelocation), resourcelocation.toString(), flag, sound.getXPosF(), sound.getYPosF(), sound.getZPosF(), sound.getAttenuationType().getTypeInt(), f1);
                            } else {
                                this.sndSystem.newSource(false, s, getURLForSoundResource(resourcelocation), resourcelocation.toString(), flag, sound.getXPosF(), sound.getYPosF(), sound.getZPosF(), sound.getAttenuationType().getTypeInt(), f1);
                            }

                            LOGGER.debug(LOG_MARKER, "Playing sound {} for event {} as channel {}", soundpoolentry.getSoundPoolEntryLocation(), accessorComposite.getSoundEventLocation(), s);
                            this.sndSystem.setPitch(s, (float) d0);
                            this.sndSystem.setVolume(s, f2);
                            this.sndSystem.play(s);
                            this.playingSoundsStopTime.put(s, this.playTime + 20);
                            this.playingSounds.put(s, sound);
                            this.playingSoundPoolEntries.put(sound, soundpoolentry);

                            if (soundcategory != SoundCategory.MASTER) {
                                this.categorySounds.put(soundcategory, s);
                            }

                            if (sound instanceof ITickableSound) {
                                this.tickableSounds.add((ITickableSound) sound);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Normalizes pitch from parameters and clamps to [0.5, 2.0]
     */
    private float getNormalizedPitch(ISound sound, SoundPoolEntry entry) {
        return (float) MathHelper.clamp_double((double) sound.getPitch() * entry.getPitch(), 0.5D, 2.0D);
    }

    /**
     * Normalizes volume level from parameters.  Range [0.0, 1.0]
     */
    private float getNormalizedVolume(ISound sound, SoundPoolEntry entry, SoundCategory category) {
        return (float) MathHelper.clamp_double((double) sound.getVolume() * entry.getVolume(), 0.0D, 1.0D) * this.getSoundCategoryVolume(category);
    }

    /**
     * Pauses all currently playing sounds
     */
    public void pauseAllSounds() {
        for (String s : this.playingSounds.keySet()) {
            LOGGER.debug(LOG_MARKER, "Pausing channel {}", s);
            this.sndSystem.pause(s);
        }
    }

    /**
     * Resumes playing all currently playing sounds (after pauseAllSounds)
     */
    public void resumeAllSounds() {
        for (String s : this.playingSounds.keySet()) {
            LOGGER.debug(LOG_MARKER, "Resuming channel {}", s);
            this.sndSystem.play(s);
        }
    }

    /**
     * Adds a sound to play in n tick
     */
    public void playDelayedSound(ISound sound, int delay) {
        this.delayedSounds.put(sound, this.playTime + delay);
    }

    private static URL getURLForSoundResource(final ResourceLocation p_148612_0_) {
        String s = String.format("%s:%s:%s", "mcsounddomain", p_148612_0_.getResourceDomain(), p_148612_0_.getResourcePath());
        URLStreamHandler urlstreamhandler = new URLStreamHandler() {

            protected URLConnection openConnection(final URL p_openConnection_1_) {
                return new URLConnection(p_openConnection_1_) {

                    public void connect() throws IOException {
                    }

                    public InputStream getInputStream() throws IOException {
                        return Minecraft.getInstance().getResourceManager().getResource(p_148612_0_).getInputStream();
                    }
                };
            }
        };

        try {
            return new URL(null, s, urlstreamhandler);
        } catch (MalformedURLException var4) {
            throw new Error("TODO: Sanely handle url exception! :D");
        }
    }

    /**
     * Sets the listener of sounds
     */
    public void setListener(EntityPlayer player, float p_148615_2_) {
        if (this.loaded && player != null) {
            float f = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * p_148615_2_;
            float f1 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * p_148615_2_;
            double d0 = player.prevPosX + (player.posX - player.prevPosX) * (double) p_148615_2_;
            double d1 = player.prevPosY + (player.posY - player.prevPosY) * (double) p_148615_2_ + (double) player.getEyeHeight();
            double d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * (double) p_148615_2_;
            float f2 = MathHelper.cos((f1 + 90.0F) * 0.017453292F);
            float f3 = MathHelper.sin((f1 + 90.0F) * 0.017453292F);
            float f4 = MathHelper.cos(-f * 0.017453292F);
            float f5 = MathHelper.sin(-f * 0.017453292F);
            float f6 = MathHelper.cos((-f + 90.0F) * 0.017453292F);
            float f7 = MathHelper.sin((-f + 90.0F) * 0.017453292F);
            float f8 = f2 * f4;
            float f9 = f3 * f4;
            float f10 = f2 * f6;
            float f11 = f3 * f6;
            this.sndSystem.setListenerPosition((float) d0, (float) d1, (float) d2);
            this.sndSystem.setListenerOrientation(f8, f5, f9, f10, f7, f11);
        }
    }

    class SoundSystemStarterThread extends SoundSystem {

        private SoundSystemStarterThread() {
        }

        public boolean playing(String p_playing_1_) {
            synchronized (SoundSystemConfig.THREAD_SYNC) {
                if (this.soundLibrary == null) {
                    return false;
                } else {
                    Source source = this.soundLibrary.getSources().get(p_playing_1_);
                    return source != null && (source.playing() || source.paused() || source.preLoad);
                }
            }
        }

    }

}
