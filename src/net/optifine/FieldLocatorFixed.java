package net.optifine;

import java.lang.reflect.Field;
import net.optifine.IFieldLocator;

public class FieldLocatorFixed implements IFieldLocator {
   private Field field;

   public FieldLocatorFixed(Field var1) {
      this.field = var1;
   }

   public Field getField() {
      return this.field;
   }
}
