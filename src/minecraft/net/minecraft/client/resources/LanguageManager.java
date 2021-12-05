package net.minecraft.client.resources;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.client.resources.data.LanguageMetadataSection;
import net.minecraft.util.StringTranslate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

public class LanguageManager implements IResourceManagerReloadListener {

    private static final Logger LOGGER = LogManager.getLogger();

    private final IMetadataSerializer theMetadataSerializer;
    private String currentLanguage;
    protected static final Locale currentLocale = new Locale();
    private final Map<String, Language> languageMap = Maps.newHashMap();

    public LanguageManager(IMetadataSerializer theMetadataSerializerIn, String currentLanguageIn) {
        this.theMetadataSerializer = theMetadataSerializerIn;
        this.currentLanguage = currentLanguageIn;
        I18n.setLocale(currentLocale);
    }

    public void parseLanguageMetadata(List<IResourcePack> p_135043_1_) {
        this.languageMap.clear();

        for (IResourcePack iresourcepack : p_135043_1_) {
            try {
                final LanguageMetadataSection languagemetadatasection = iresourcepack.getPackMetadata(this.theMetadataSerializer, "language");

                if (languagemetadatasection != null) {
                    for (Language language : languagemetadatasection.getLanguages()) {
                        if (!this.languageMap.containsKey(language.getLanguageCode())) {
                            this.languageMap.put(language.getLanguageCode(), language);
                        }
                    }
                }
            } catch (RuntimeException | IOException runtimeexception) {
                LOGGER.warn("Unable to parse metadata section of resourcepack: " + iresourcepack.getPackName(), runtimeexception);
            }
        }
    }

    public void onResourceManagerReload(IResourceManager resourceManager) {
        final List<String> list = Lists.newArrayList("en_US");

        if (!this.currentLanguage.equals("en_US")) {
            list.add(this.currentLanguage);
        }

        currentLocale.loadLocaleDataFiles(resourceManager, list);
        StringTranslate.replaceWith(currentLocale.properties);
    }

    public boolean isCurrentLocaleUnicode() {
        return currentLocale.isUnicode();
    }

    public boolean isCurrentLanguageBidirectional() {
        return this.getCurrentLanguage() != null && this.getCurrentLanguage().isBidirectional();
    }

    public Language getCurrentLanguage() {
        return this.languageMap.containsKey(this.currentLanguage) ? this.languageMap.get(this.currentLanguage) : this.languageMap.get("en_US");
    }

    public void setCurrentLanguage(Language currentLanguageIn) {
        this.currentLanguage = currentLanguageIn.getLanguageCode();
    }

    public SortedSet<Language> getLanguages() {
        return Sets.newTreeSet(this.languageMap.values());
    }

}
