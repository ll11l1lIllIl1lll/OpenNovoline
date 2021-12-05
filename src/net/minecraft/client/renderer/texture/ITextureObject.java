package net.minecraft.client.renderer.texture;

import java.io.IOException;
import net.minecraft.client.resources.IResourceManager;
import net.shadersmod.client.MultiTexID;

public interface ITextureObject {
   void setBlurMipmap(boolean var1, boolean var2);

   void restoreLastBlurMipmap();

   void loadTexture(IResourceManager var1) throws IOException;

   int getGlTextureId();

   MultiTexID getMultiTexID();
}
