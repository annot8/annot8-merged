/* Annot8 (annot8.io) - Licensed under Apache-2.0. */
package io.annot8.common.components.capabilities;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import io.annot8.common.data.bounds.NoBounds;
import io.annot8.core.capabilities.AnnotationCapability;

public class SimpleAnnotationCapabilityTest {
  @Test
  public void testGetters() {
    AnnotationCapability ac = new SimpleAnnotationCapability("test", NoBounds.class);

    assertEquals("test", ac.getType());
    assertEquals(NoBounds.class, ac.getBounds());
  }

  @Test
  public void testEqualAndHashCode() {
    AnnotationCapability ac1 = new SimpleAnnotationCapability("test", NoBounds.class);
    AnnotationCapability ac2 = new SimpleAnnotationCapability("test", NoBounds.class);

      assertEquals(ac1, ac2);
      assertNotEquals("Hello world", ac1);
      assertNotEquals(123, ac1);

    assertEquals(ac1.hashCode(), ac2.hashCode());
  }
}
