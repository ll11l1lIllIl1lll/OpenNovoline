package shadersmod.client;

import java.io.InputStream;
import shadersmod.client.IShaderPack;
import shadersmod.client.Shaders;

public class ShaderPackDefault implements IShaderPack {
   public void close() {
   }

   public InputStream getResourceAsStream(String var1) {
      return ShaderPackDefault.class.getResourceAsStream(var1);
   }

   public String getName() {
      return Shaders.packNameDefault;
   }

   public boolean hasDirectory(String var1) {
      return false;
   }
}
