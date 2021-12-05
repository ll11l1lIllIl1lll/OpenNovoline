package net.minecraft.client.renderer;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.yS;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings$Options;
import net.optifine.Config;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.ARBMultitexture;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.EXTBlendFuncSeparate;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLContext;
import oshi.SystemInfo;
import oshi.hardware.Processor;

public class OpenGlHelper {
   public static boolean nvidia;
   public static boolean field_181063_b;
   public static int GL_FRAMEBUFFER;
   public static int GL_RENDERBUFFER;
   public static int GL_COLOR_ATTACHMENT0;
   public static int GL_DEPTH_ATTACHMENT;
   public static int GL_FRAMEBUFFER_COMPLETE;
   public static int GL_FB_INCOMPLETE_ATTACHMENT;
   public static int GL_FB_INCOMPLETE_MISS_ATTACH;
   public static int GL_FB_INCOMPLETE_DRAW_BUFFER;
   public static int GL_FB_INCOMPLETE_READ_BUFFER;
   private static int framebufferType;
   public static boolean framebufferSupported;
   private static boolean shadersAvailable;
   private static boolean arbShaders;
   public static int GL_LINK_STATUS;
   public static int GL_COMPILE_STATUS;
   public static int GL_VERTEX_SHADER;
   public static int GL_FRAGMENT_SHADER;
   private static boolean arbMultitexture;
   public static int defaultTexUnit;
   public static int lightmapTexUnit;
   public static int GL_TEXTURE2;
   private static boolean arbTextureEnvCombine;
   public static int GL_COMBINE;
   public static int g;
   public static int GL_PRIMARY_COLOR;
   public static int U;
   public static int GL_PREVIOUS;
   public static int GL_COMBINE_RGB;
   public static int GL_SOURCE0_RGB;
   public static int GL_SOURCE1_RGB;
   public static int J;
   public static int GL_OPERAND0_RGB;
   public static int GL_OPERAND1_RGB;
   public static int W;
   public static int GL_COMBINE_ALPHA;
   public static int GL_SOURCE0_ALPHA;
   public static int GL_SOURCE1_ALPHA;
   public static int GL_SOURCE2_ALPHA;
   public static int GL_OPERAND0_ALPHA;
   public static int GL_OPERAND1_ALPHA;
   public static int GL_OPERAND2_ALPHA;
   private static boolean openGL14;
   public static boolean extBlendFuncSeparate;
   public static boolean openGL21;
   public static boolean shadersSupported;
   private static String logText = "";
   private static String field_183030_aa;
   public static boolean vboSupported;
   public static boolean field_181062_Q;
   private static boolean arbVbo;
   public static int GL_ARRAY_BUFFER;
   public static int GL_STATIC_DRAW;
   private static final String S = "CL_00001179";
   public static float lastBrightnessX = 0.0F;
   public static float lastBrightnessY = 0.0F;

   public static void initializeTextures() {
      Config.initDisplay();
      ContextCapabilities var0 = GLContext.getCapabilities();
      arbMultitexture = var0.GL_ARB_multitexture && !var0.OpenGL13;
      arbTextureEnvCombine = var0.GL_ARB_texture_env_combine && !var0.OpenGL13;
      if(arbMultitexture) {
         logText = logText + "Using ARB_multitexture.\n";
      } else {
         logText = logText + "Using GL 1.3 multitexturing.\n";
      }

      defaultTexUnit = '蓀';
      lightmapTexUnit = '蓁';
      GL_TEXTURE2 = '蓂';
      if(arbTextureEnvCombine) {
         logText = logText + "Using ARB_texture_env_combine.\n";
      } else {
         logText = logText + "Using GL 1.3 texture combiners.\n";
      }

      GL_COMBINE = '蕰';
      g = '蕵';
      GL_PRIMARY_COLOR = '蕷';
      U = '蕶';
      GL_PREVIOUS = '蕸';
      GL_COMBINE_RGB = '蕱';
      GL_SOURCE0_RGB = '薀';
      GL_SOURCE1_RGB = '薁';
      J = '薂';
      GL_OPERAND0_RGB = '薐';
      GL_OPERAND1_RGB = '薑';
      W = '薒';
      GL_COMBINE_ALPHA = '蕲';
      GL_SOURCE0_ALPHA = '薈';
      GL_SOURCE1_ALPHA = '薉';
      GL_SOURCE2_ALPHA = '薊';
      GL_OPERAND0_ALPHA = '薘';
      GL_OPERAND1_ALPHA = '薙';
      GL_OPERAND2_ALPHA = '薚';
      extBlendFuncSeparate = var0.GL_EXT_blend_func_separate && !var0.OpenGL14;
      openGL14 = var0.OpenGL14 || var0.GL_EXT_blend_func_separate;
      framebufferSupported = openGL14 && (var0.GL_ARB_framebuffer_object || var0.GL_EXT_framebuffer_object || var0.OpenGL30);
      if(framebufferSupported) {
         logText = logText + "Using framebuffer objects because ";
         if(var0.OpenGL30) {
            logText = logText + "OpenGL 3.0 is supported and separate blending is supported.\n";
            framebufferType = 0;
            GL_FRAMEBUFFER = '赀';
            GL_RENDERBUFFER = '赁';
            GL_COLOR_ATTACHMENT0 = '賠';
            GL_DEPTH_ATTACHMENT = '贀';
            GL_FRAMEBUFFER_COMPLETE = '賕';
            GL_FB_INCOMPLETE_ATTACHMENT = '賖';
            GL_FB_INCOMPLETE_MISS_ATTACH = '賗';
            GL_FB_INCOMPLETE_DRAW_BUFFER = '賛';
            GL_FB_INCOMPLETE_READ_BUFFER = '賜';
         } else if(var0.GL_ARB_framebuffer_object) {
            logText = logText + "ARB_framebuffer_object is supported and separate blending is supported.\n";
            framebufferType = 1;
            GL_FRAMEBUFFER = '赀';
            GL_RENDERBUFFER = '赁';
            GL_COLOR_ATTACHMENT0 = '賠';
            GL_DEPTH_ATTACHMENT = '贀';
            GL_FRAMEBUFFER_COMPLETE = '賕';
            GL_FB_INCOMPLETE_MISS_ATTACH = '賗';
            GL_FB_INCOMPLETE_ATTACHMENT = '賖';
            GL_FB_INCOMPLETE_DRAW_BUFFER = '賛';
            GL_FB_INCOMPLETE_READ_BUFFER = '賜';
         } else if(var0.GL_EXT_framebuffer_object) {
            logText = logText + "EXT_framebuffer_object is supported.\n";
            framebufferType = 2;
            GL_FRAMEBUFFER = '赀';
            GL_RENDERBUFFER = '赁';
            GL_COLOR_ATTACHMENT0 = '賠';
            GL_DEPTH_ATTACHMENT = '贀';
            GL_FRAMEBUFFER_COMPLETE = '賕';
            GL_FB_INCOMPLETE_MISS_ATTACH = '賗';
            GL_FB_INCOMPLETE_ATTACHMENT = '賖';
            GL_FB_INCOMPLETE_DRAW_BUFFER = '賛';
            GL_FB_INCOMPLETE_READ_BUFFER = '賜';
         }
      } else {
         logText = logText + "Not using framebuffer objects because ";
         logText = logText + "OpenGL 1.4 is " + (var0.OpenGL14?"":"not ") + "supported, ";
         logText = logText + "EXT_blend_func_separate is " + (var0.GL_EXT_blend_func_separate?"":"not ") + "supported, ";
         logText = logText + "OpenGL 3.0 is " + (var0.OpenGL30?"":"not ") + "supported, ";
         logText = logText + "ARB_framebuffer_object is " + (var0.GL_ARB_framebuffer_object?"":"not ") + "supported, and ";
         logText = logText + "EXT_framebuffer_object is " + (var0.GL_EXT_framebuffer_object?"":"not ") + "supported.\n";
      }

      openGL21 = var0.OpenGL21;
      shadersAvailable = openGL21 || var0.GL_ARB_vertex_shader && var0.GL_ARB_fragment_shader && var0.GL_ARB_shader_objects;
      logText = logText + "Shaders are " + (shadersAvailable?"":"not ") + "available because ";
      if(shadersAvailable) {
         if(var0.OpenGL21) {
            logText = logText + "OpenGL 2.1 is supported.\n";
            arbShaders = false;
         } else {
            logText = logText + "ARB_shader_objects, ARB_vertex_shader, and ARB_fragment_shader are supported.\n";
            arbShaders = true;
         }

         GL_LINK_STATUS = '讂';
         GL_COMPILE_STATUS = '讁';
         GL_VERTEX_SHADER = '謱';
         GL_FRAGMENT_SHADER = '謰';
      } else {
         logText = logText + "OpenGL 2.1 is " + (var0.OpenGL21?"":"not ") + "supported, ";
         logText = logText + "ARB_shader_objects is " + (var0.GL_ARB_shader_objects?"":"not ") + "supported, ";
         logText = logText + "ARB_vertex_shader is " + (var0.GL_ARB_vertex_shader?"":"not ") + "supported, and ";
         logText = logText + "ARB_fragment_shader is " + (var0.GL_ARB_fragment_shader?"":"not ") + "supported.\n";
      }

      shadersSupported = framebufferSupported && shadersAvailable;
      String var1 = GL11.glGetString(7936).toLowerCase();
      nvidia = var1.contains("nvidia");
      arbVbo = !var0.OpenGL15 && var0.GL_ARB_vertex_buffer_object;
      vboSupported = var0.OpenGL15 || arbVbo;
      logText = logText + "VBOs are " + (vboSupported?"":"not ") + "available because ";
      if(vboSupported) {
         if(arbVbo) {
            logText = logText + "ARB_vertex_buffer_object is supported.\n";
         } else {
            logText = logText + "OpenGL 1.5 is supported.\n";
         }

         GL_STATIC_DRAW = '裤';
         GL_ARRAY_BUFFER = '袒';
      }

      field_181063_b = var1.contains("ati");
      if(field_181063_b) {
         if(vboSupported) {
            field_181062_Q = true;
         } else {
            GameSettings$Options.RENDER_DISTANCE.setValueMax(16.0F);
         }
      }

      try {
         Processor[] var2 = (new SystemInfo()).getHardware().getProcessors();
         field_183030_aa = String.format("%dx %s", new Object[]{Integer.valueOf(var2.length), var2[0]}).replaceAll("\\s+", " ");
      } catch (Throwable var3) {
         ;
      }

   }

   public static boolean areShadersSupported() {
      return shadersSupported;
   }

   public static String getLogText() {
      return logText;
   }

   public static int glGetProgrami(int var0, int var1) {
      return arbShaders?ARBShaderObjects.glGetObjectParameteriARB(var0, var1):GL20.glGetProgrami(var0, var1);
   }

   public static void glAttachShader(int var0, int var1) {
      if(arbShaders) {
         ARBShaderObjects.glAttachObjectARB(var0, var1);
      } else {
         GL20.glAttachShader(var0, var1);
      }

   }

   public static void glDeleteShader(int var0) {
      if(arbShaders) {
         ARBShaderObjects.glDeleteObjectARB(var0);
      } else {
         GL20.glDeleteShader(var0);
      }

   }

   public static int glCreateShader(int var0) {
      return arbShaders?ARBShaderObjects.glCreateShaderObjectARB(var0):GL20.glCreateShader(var0);
   }

   public static void glShaderSource(int var0, ByteBuffer var1) {
      if(arbShaders) {
         ARBShaderObjects.glShaderSourceARB(var0, var1);
      } else {
         GL20.glShaderSource(var0, var1);
      }

   }

   public static void glCompileShader(int var0) {
      if(arbShaders) {
         ARBShaderObjects.glCompileShaderARB(var0);
      } else {
         GL20.glCompileShader(var0);
      }

   }

   public static int glGetShaderi(int var0, int var1) {
      return arbShaders?ARBShaderObjects.glGetObjectParameteriARB(var0, var1):GL20.glGetShaderi(var0, var1);
   }

   public static String glGetShaderInfoLog(int var0, int var1) {
      return arbShaders?ARBShaderObjects.glGetInfoLogARB(var0, var1):GL20.glGetShaderInfoLog(var0, var1);
   }

   public static String glGetProgramInfoLog(int var0, int var1) {
      return arbShaders?ARBShaderObjects.glGetInfoLogARB(var0, var1):GL20.glGetProgramInfoLog(var0, var1);
   }

   public static void glUseProgram(int var0) {
      if(arbShaders) {
         ARBShaderObjects.glUseProgramObjectARB(var0);
      } else {
         GL20.glUseProgram(var0);
      }

   }

   public static int glCreateProgram() {
      return arbShaders?ARBShaderObjects.glCreateProgramObjectARB():GL20.glCreateProgram();
   }

   public static void glDeleteProgram(int var0) {
      if(arbShaders) {
         ARBShaderObjects.glDeleteObjectARB(var0);
      } else {
         GL20.glDeleteProgram(var0);
      }

   }

   public static void glLinkProgram(int var0) {
      if(arbShaders) {
         ARBShaderObjects.glLinkProgramARB(var0);
      } else {
         GL20.glLinkProgram(var0);
      }

   }

   public static int glGetUniformLocation(int var0, CharSequence var1) {
      return arbShaders?ARBShaderObjects.glGetUniformLocationARB(var0, var1):GL20.glGetUniformLocation(var0, var1);
   }

   public static void glUniform1(int var0, IntBuffer var1) {
      if(arbShaders) {
         ARBShaderObjects.glUniform1ARB(var0, var1);
      } else {
         GL20.glUniform1(var0, var1);
      }

   }

   public static void glUniform1i(int var0, int var1) {
      if(arbShaders) {
         ARBShaderObjects.glUniform1iARB(var0, var1);
      } else {
         GL20.glUniform1i(var0, var1);
      }

   }

   public static void glUniform1(int var0, FloatBuffer var1) {
      if(arbShaders) {
         ARBShaderObjects.glUniform1ARB(var0, var1);
      } else {
         GL20.glUniform1(var0, var1);
      }

   }

   public static void glUniform2(int var0, IntBuffer var1) {
      if(arbShaders) {
         ARBShaderObjects.glUniform2ARB(var0, var1);
      } else {
         GL20.glUniform2(var0, var1);
      }

   }

   public static void glUniform2(int var0, FloatBuffer var1) {
      if(arbShaders) {
         ARBShaderObjects.glUniform2ARB(var0, var1);
      } else {
         GL20.glUniform2(var0, var1);
      }

   }

   public static void glUniform3(int var0, IntBuffer var1) {
      if(arbShaders) {
         ARBShaderObjects.glUniform3ARB(var0, var1);
      } else {
         GL20.glUniform3(var0, var1);
      }

   }

   public static void glUniform3(int var0, FloatBuffer var1) {
      if(arbShaders) {
         ARBShaderObjects.glUniform3ARB(var0, var1);
      } else {
         GL20.glUniform3(var0, var1);
      }

   }

   public static void glUniform4(int var0, IntBuffer var1) {
      if(arbShaders) {
         ARBShaderObjects.glUniform4ARB(var0, var1);
      } else {
         GL20.glUniform4(var0, var1);
      }

   }

   public static void glUniform4(int var0, FloatBuffer var1) {
      if(arbShaders) {
         ARBShaderObjects.glUniform4ARB(var0, var1);
      } else {
         GL20.glUniform4(var0, var1);
      }

   }

   public static void glUniformMatrix2(int var0, boolean var1, FloatBuffer var2) {
      if(arbShaders) {
         ARBShaderObjects.glUniformMatrix2ARB(var0, var1, var2);
      } else {
         GL20.glUniformMatrix2(var0, var1, var2);
      }

   }

   public static void glUniformMatrix3(int var0, boolean var1, FloatBuffer var2) {
      if(arbShaders) {
         ARBShaderObjects.glUniformMatrix3ARB(var0, var1, var2);
      } else {
         GL20.glUniformMatrix3(var0, var1, var2);
      }

   }

   public static void glUniformMatrix4(int var0, boolean var1, FloatBuffer var2) {
      if(arbShaders) {
         ARBShaderObjects.glUniformMatrix4ARB(var0, var1, var2);
      } else {
         GL20.glUniformMatrix4(var0, var1, var2);
      }

   }

   public static int glGetAttribLocation(int var0, CharSequence var1) {
      return arbShaders?ARBVertexShader.glGetAttribLocationARB(var0, var1):GL20.glGetAttribLocation(var0, var1);
   }

   public static int glGenBuffers() {
      return arbVbo?ARBVertexBufferObject.glGenBuffersARB():GL15.glGenBuffers();
   }

   public static void glBindBuffer(int var0, int var1) {
      if(arbVbo) {
         ARBVertexBufferObject.glBindBufferARB(var0, var1);
      } else {
         GL15.glBindBuffer(var0, var1);
      }

   }

   public static void glBufferData(int var0, ByteBuffer var1, int var2) {
      if(arbVbo) {
         ARBVertexBufferObject.glBufferDataARB(var0, var1, var2);
      } else {
         GL15.glBufferData(var0, var1, var2);
      }

   }

   public static void glDeleteBuffers(int var0) {
      if(arbVbo) {
         ARBVertexBufferObject.glDeleteBuffersARB(var0);
      } else {
         GL15.glDeleteBuffers(var0);
      }

   }

   public static boolean useVbo() {
      return !Config.isMultiTexture() && vboSupported && Minecraft.getInstance().gameSettings.useVbo;
   }

   public static void glBindFramebuffer(int var0, int var1) {
      if(framebufferSupported) {
         switch(framebufferType) {
         case 0:
            GL30.glBindFramebuffer(var0, var1);
            break;
         case 1:
            ARBFramebufferObject.glBindFramebuffer(var0, var1);
            break;
         case 2:
            EXTFramebufferObject.glBindFramebufferEXT(var0, var1);
         }
      }

   }

   public static void glBindRenderbuffer(int var0, int var1) {
      if(framebufferSupported) {
         switch(framebufferType) {
         case 0:
            GL30.glBindRenderbuffer(var0, var1);
            break;
         case 1:
            ARBFramebufferObject.glBindRenderbuffer(var0, var1);
            break;
         case 2:
            EXTFramebufferObject.glBindRenderbufferEXT(var0, var1);
         }
      }

   }

   public static void glDeleteRenderbuffers(int var0) {
      if(framebufferSupported) {
         switch(framebufferType) {
         case 0:
            GL30.glDeleteRenderbuffers(var0);
            break;
         case 1:
            ARBFramebufferObject.glDeleteRenderbuffers(var0);
            break;
         case 2:
            EXTFramebufferObject.glDeleteRenderbuffersEXT(var0);
         }
      }

   }

   public static void glDeleteFramebuffers(int var0) {
      if(framebufferSupported) {
         switch(framebufferType) {
         case 0:
            GL30.glDeleteFramebuffers(var0);
            break;
         case 1:
            ARBFramebufferObject.glDeleteFramebuffers(var0);
            break;
         case 2:
            EXTFramebufferObject.glDeleteFramebuffersEXT(var0);
         }
      }

   }

   public static int glGenFramebuffers() {
      if(!framebufferSupported) {
         return -1;
      } else {
         switch(framebufferType) {
         case 0:
            return GL30.glGenFramebuffers();
         case 1:
            return ARBFramebufferObject.glGenFramebuffers();
         case 2:
            return EXTFramebufferObject.glGenFramebuffersEXT();
         default:
            return -1;
         }
      }
   }

   public static int glGenRenderbuffers() {
      if(!framebufferSupported) {
         return -1;
      } else {
         switch(framebufferType) {
         case 0:
            return GL30.glGenRenderbuffers();
         case 1:
            return ARBFramebufferObject.glGenRenderbuffers();
         case 2:
            return EXTFramebufferObject.glGenRenderbuffersEXT();
         default:
            return -1;
         }
      }
   }

   public static void glRenderbufferStorage(int var0, int var1, int var2, int var3) {
      if(framebufferSupported) {
         switch(framebufferType) {
         case 0:
            GL30.glRenderbufferStorage(var0, var1, var2, var3);
            break;
         case 1:
            ARBFramebufferObject.glRenderbufferStorage(var0, var1, var2, var3);
            break;
         case 2:
            EXTFramebufferObject.glRenderbufferStorageEXT(var0, var1, var2, var3);
         }
      }

   }

   public static void glFramebufferRenderbuffer(int var0, int var1, int var2, int var3) {
      if(framebufferSupported) {
         switch(framebufferType) {
         case 0:
            GL30.glFramebufferRenderbuffer(var0, var1, var2, var3);
            break;
         case 1:
            ARBFramebufferObject.glFramebufferRenderbuffer(var0, var1, var2, var3);
            break;
         case 2:
            EXTFramebufferObject.glFramebufferRenderbufferEXT(var0, var1, var2, var3);
         }
      }

   }

   public static int glCheckFramebufferStatus(int var0) {
      if(!framebufferSupported) {
         return -1;
      } else {
         switch(framebufferType) {
         case 0:
            return GL30.glCheckFramebufferStatus(var0);
         case 1:
            return ARBFramebufferObject.glCheckFramebufferStatus(var0);
         case 2:
            return EXTFramebufferObject.glCheckFramebufferStatusEXT(var0);
         default:
            return -1;
         }
      }
   }

   public static void glFramebufferTexture2D(int var0, int var1, int var2, int var3, int var4) {
      if(framebufferSupported) {
         switch(framebufferType) {
         case 0:
            GL30.glFramebufferTexture2D(var0, var1, var2, var3, var4);
            break;
         case 1:
            ARBFramebufferObject.glFramebufferTexture2D(var0, var1, var2, var3, var4);
            break;
         case 2:
            EXTFramebufferObject.glFramebufferTexture2DEXT(var0, var1, var2, var3, var4);
         }
      }

   }

   public static void setActiveTexture(int var0) {
      if(!arbMultitexture && !yS.a()) {
         GL13.glActiveTexture(var0);
      } else {
         ARBMultitexture.glActiveTextureARB(var0);
      }

   }

   public static void setClientActiveTexture(int var0) {
      if(!arbMultitexture && !yS.a()) {
         GL13.glClientActiveTexture(var0);
      } else {
         ARBMultitexture.glClientActiveTextureARB(var0);
      }

   }

   public static void setLightmapTextureCoords(int var0, float var1, float var2) {
      if(arbMultitexture) {
         ARBMultitexture.glMultiTexCoord2fARB(var0, var1, var2);
      } else {
         GL13.glMultiTexCoord2f(var0, var1, var2);
      }

      if(var0 == lightmapTexUnit) {
         lastBrightnessX = var1;
         lastBrightnessY = var2;
      }

   }

   public static void glBlendFunc(int var0, int var1, int var2, int var3) {
      if(openGL14) {
         if(extBlendFuncSeparate) {
            EXTBlendFuncSeparate.glBlendFuncSeparateEXT(var0, var1, var2, var3);
         } else {
            GL14.glBlendFuncSeparate(var0, var1, var2, var3);
         }
      } else {
         GL11.glBlendFunc(var0, var1);
      }

   }

   public static boolean isFramebufferEnabled() {
      return !Config.isAntialiasing() && framebufferSupported && Minecraft.getInstance().gameSettings.fboEnable;
   }

   public static String func_183029_j() {
      return field_183030_aa == null?"32x Baikal-TX3 CPU @ 5.7GHz":field_183030_aa;
   }

   private static Throwable a(Throwable var0) {
      return var0;
   }
}
