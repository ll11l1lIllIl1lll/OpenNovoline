package net.skidunion;

import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.jvm.internal.Intrinsics;
import net.acE;
import net.skidunion.ai;
import net.skidunion.an;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 16},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004¨\u0006\u0005"},
   d2 = {"Lnet/skidunion/af;", "Lnet/skidunion/an;", "name", "", "(Ljava/lang/String;)V", "client"}
)
public final class af extends an {
   public af(@NotNull String var1) {
      ai.c();
      Intrinsics.checkParameterIsNotNull(var1, "name");
      super(7, (Pair)TuplesKt.to("name", var1));
      if(acE.b() == null) {
         ai.b(new int[1]);
      }

   }
}
