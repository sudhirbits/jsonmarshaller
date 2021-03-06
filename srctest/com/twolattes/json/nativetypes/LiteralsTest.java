package com.twolattes.json.nativetypes;

import static com.twolattes.json.Json.FALSE;
import static com.twolattes.json.Json.NULL;
import static com.twolattes.json.Json.TRUE;
import static com.twolattes.json.Json.array;
import static com.twolattes.json.Json.number;
import static com.twolattes.json.Json.object;
import static com.twolattes.json.Json.string;
import static com.twolattes.json.TwoLattes.createEntityMarshaller;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.twolattes.json.Entity;
import com.twolattes.json.EntityMarshaller;
import com.twolattes.json.Json;
import com.twolattes.json.Value;

public class LiteralsTest {

  @Entity
  static class Int {
    @Value int literal;
    @Value Integer object;
    @Value int[] nativeArray;
    @Value Integer[] objectArray;
  }

  @Test
  public void testInt() throws Exception {
    EntityMarshaller<Int> marshaller = createEntityMarshaller(Int.class);

    Json.Object object = marshaller.marshall(new Int() {{
      this.literal = 1;
      this.object = 2;
      this.nativeArray = new int[] { 3, 4 };
      this.objectArray = new Integer[] { 5, 6 };
    }});

    assertJsonObjectWellFormed(object);

    assertEquals("1", object.get(string("literal")).toString());

    Int instance = marshaller.unmarshall(object);

    assertEquals(1, instance.literal);
    assertEquals(2, instance.object);
    assertEquals(2, instance.nativeArray.length);
    assertEquals(3, instance.nativeArray[0]);
    assertEquals(4, instance.nativeArray[1]);
    assertEquals(2, instance.objectArray.length);
    assertEquals(5, instance.objectArray[0]);
    assertEquals(6, instance.objectArray[1]);
  }

  @Entity
  static class Double {
    @Value double literal;
    @Value java.lang.Double object;
    @Value double[] nativeArray;
    @Value java.lang.Double[] objectArray;
  }

  @Test
  public void testDouble() throws Exception {
    EntityMarshaller<Double> marshaller = createEntityMarshaller(Double.class);

    Json.Object object = marshaller.marshall(new Double() {{
      this.literal = 1.0;
      this.object = 2.0;
      this.nativeArray = new double[] { 3, 4 };
      this.objectArray = new java.lang.Double[] { 5.0, 6.0 };
    }});

    assertJsonObjectWellFormed(object);

    Double instance = marshaller.unmarshall(object);

    assertEquals(1.0, instance.literal);
    assertEquals(2.0, instance.object);
    assertEquals(2, instance.nativeArray.length);
    assertEquals(3.0, instance.nativeArray[0]);
    assertEquals(4.0, instance.nativeArray[1]);
    assertEquals(2, instance.objectArray.length);
    assertEquals(5.0, instance.objectArray[0]);
    assertEquals(6.0, instance.objectArray[1]);
  }

  @Entity
  static class Short {
    @Value short literal;
    @Value java.lang.Short object;
    @Value short[] nativeArray;
    @Value java.lang.Short[] objectArray;
  }

  @Test
  public void testShort() throws Exception {
    EntityMarshaller<Short> marshaller = createEntityMarshaller(Short.class);

    Json.Object object = marshaller.marshall(new Short() {{
      this.literal = 1;
      this.object = 2;
      this.nativeArray = new short[] { 3, 4 };
      this.objectArray = new java.lang.Short[] { 5, 6 };
    }});

    assertJsonObjectWellFormed(object);

    assertEquals("1", object.get(string("literal")).toString());

    Short instance = marshaller.unmarshall(object);

    assertEquals(1, instance.literal);
    assertEquals(2.0, instance.object);
    assertEquals(2, instance.nativeArray.length);
    assertEquals(3, instance.nativeArray[0]);
    assertEquals(4, instance.nativeArray[1]);
    assertEquals(2, instance.objectArray.length);
    assertEquals(5, instance.objectArray[0]);
    assertEquals(6, instance.objectArray[1]);
  }

  @Entity
  static class Long {
    @Value long literal;
    @Value java.lang.Long object;
    @Value long[] nativeArray;
    @Value java.lang.Long[] objectArray;
  }

  @Test
  public void testLong() throws Exception {
    EntityMarshaller<Long> marshaller = createEntityMarshaller(Long.class);

    Json.Object object = marshaller.marshall(new Long() {{
      this.literal = 1L;
      this.object = 2L;
      this.nativeArray = new long[] { 3, 4 };
      this.objectArray = new java.lang.Long[] { 5L, 6L };
    }});

    assertJsonObjectWellFormed(object);

    assertEquals("1", object.get(string("literal")).toString());

    Long instance = marshaller.unmarshall(object);

    assertEquals(1, instance.literal);
    assertEquals(2.0, instance.object);
    assertEquals(2, instance.nativeArray.length);
    assertEquals(3, instance.nativeArray[0]);
    assertEquals(4, instance.nativeArray[1]);
    assertEquals(2, instance.objectArray.length);
    assertEquals(5, instance.objectArray[0]);
    assertEquals(6, instance.objectArray[1]);
  }

  @Entity
  static class Float {
    @Value(optional = true) float literal;
    @Value(optional = true) java.lang.Float object;
    @Value(optional = true) float[] nativeArray;
    @Value(optional = true) java.lang.Float[] objectArray;
  }

  @Test
  public void testFloat() throws Exception {
    EntityMarshaller<Float> marshaller = createEntityMarshaller(Float.class);

    Json.Object object = marshaller.marshall(new Float() {{
      this.literal = 1f;
      this.object = 2f;
      this.nativeArray = new float[] { 3f, 4f };
      this.objectArray = new java.lang.Float[] { 5f, null };
    }});

    assertJsonObjectWellFormed(object, number(1), number(2), number(3),
        number(4), number(5), NULL);

    Float instance = marshaller.unmarshall(object);

    assertEquals(1f, instance.literal);
    assertEquals(2f, instance.object);
    assertEquals(2, instance.nativeArray.length);
    assertEquals(3f, instance.nativeArray[0]);
    assertEquals(4f, instance.nativeArray[1]);
    assertEquals(2, instance.objectArray.length);
    assertEquals(5f, instance.objectArray[0]);
    assertNull(instance.objectArray[1]);

    assertEquals(0f, marshaller.unmarshall(object()).literal);

    try {
      marshaller.unmarshall(object(string("nativeArray"), array(NULL)));
      fail("expected NPE");
    } catch (NullPointerException e) {
    }
  }

  @Entity
  static class Char {
    @Value char literal;
    @Value Character object;
    @Value char[] nativeArray;
    @Value Character[] objectArray;
  }

  @Test
  public void testChar() throws Exception {
    EntityMarshaller<Char> marshaller = createEntityMarshaller(Char.class);

    Json.Object object = marshaller.marshall(new Char() {{
      this.literal = 'a';
      this.object = 'b';
      this.nativeArray = new char[] { 'c', 'd' };
      this.objectArray = new Character[] { 'e', 'f' };
    }});

    assertJsonObjectWellFormed(object,
        string("a"), string("b"), string("c"),
        string("d"), string("e"), string("f"));

    Char instance = marshaller.unmarshall(object);

    assertEquals('a', instance.literal);
    assertEquals('b', instance.object);
    assertEquals(2, instance.nativeArray.length);
    assertEquals('c', instance.nativeArray[0]);
    assertEquals('d', instance.nativeArray[1]);
    assertEquals(2, instance.objectArray.length);
    assertEquals('e', instance.objectArray[0]);
    assertEquals('f', instance.objectArray[1]);
  }

  @Entity
  static class Bool {
    @Value boolean literal;
    @Value Boolean object;
    @Value boolean[] nativeArray;
    @Value Boolean[] objectArray;
  }

  @Test
  public void testBool() throws Exception {
    EntityMarshaller<Bool> marshaller = createEntityMarshaller(Bool.class);

    Json.Object object = marshaller.marshall(new Bool() {{
      this.literal = true;
      this.object = false;
      this.nativeArray = new boolean[] { true, false };
      this.objectArray = new Boolean[] { true, false };
    }});

    assertJsonObjectWellFormed(object,
        TRUE, FALSE, TRUE, FALSE, TRUE, FALSE);

    Bool instance = marshaller.unmarshall(object);

    assertEquals(true, instance.literal);
    assertEquals(false, instance.object);
    assertEquals(2, instance.nativeArray.length);
    assertEquals(true, instance.nativeArray[0]);
    assertEquals(false, instance.nativeArray[1]);
    assertEquals(2, instance.objectArray.length);
    assertEquals(true, instance.objectArray[0]);
    assertEquals(false, instance.objectArray[1]);
  }

  @Entity
  static class Bytes {
    @Value byte literal;
    @Value Byte object;
    @Value byte[] nativeArray;
    @Value Byte[] objectArray;
  }

  @Test
  public void testBytes() throws Exception {
    EntityMarshaller<Bytes> marshaller = createEntityMarshaller(Bytes.class);

    Json.Object object = marshaller.marshall(new Bytes() {{
      this.literal = 8;
      this.object = 5;
      this.nativeArray = new byte[] { 0, -128 };
      this.objectArray = new Byte[] { 127, null };
    }});

    assertJsonObjectWellFormed(object,
        number(8), number(5), number(0), number(-128), number(127), NULL);

    Bytes instance = marshaller.unmarshall(object);

    assertEquals((byte) 8, instance.literal);
    assertEquals(Byte.valueOf((byte) 5), instance.object);
    assertEquals(2, instance.nativeArray.length);
    assertEquals((byte) 0, instance.nativeArray[0]);
    assertEquals((byte) -128, instance.nativeArray[1]);
    assertEquals(2, instance.objectArray.length);
    assertEquals(Byte.valueOf((byte) 127), instance.objectArray[0]);
    assertNull(instance.objectArray[1]);
  }

  private void assertJsonObjectWellFormed(Json.Object object) {
    assertEquals(4, object.size());
    assertJsonObjectWellFormed(object, number(1), number(2), number(3),
        number(4), number(5), number(6));
  }

  private void assertJsonObjectWellFormed(Json.Object object, Json.Value one,
      Json.Value two, Json.Value three, Json.Value four, Json.Value five,
      Json.Value six) {
    assertEquals(one, object.get(string("literal")));
    assertEquals(two, object.get(string("object")));
    Json.Array nativeArray = (Json.Array) object.get(string("nativeArray"));
    assertEquals(2, nativeArray.size());
    assertEquals(three, nativeArray.get(0));
    assertEquals(four, nativeArray.get(1));
    Json.Array objectArray = (Json.Array) object.get(string("objectArray"));
    assertEquals(2, objectArray.size());
    assertEquals(five, objectArray.get(0));
    assertEquals(six, objectArray.get(1));
  }

}
