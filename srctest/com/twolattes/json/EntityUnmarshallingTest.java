package com.twolattes.json;

import static com.google.common.collect.Lists.newArrayList;
import static com.twolattes.json.Json.object;
import static com.twolattes.json.Json.string;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.twolattes.json.OuterClass.InnerClass;

public class EntityUnmarshallingTest {
  @Test
  public void testBaseTypeEntity() throws Exception {
    BaseTypeEntity base = unmarshall(BaseTypeEntity.class,
        "{\"_0\":5,\"_1\":\"h\",\"_2\":89,\"_3\":3.2,\"_4\":16," +
        "\"_5\":\"ya\",\"_6\":true,\"_7\":6218.687231}");

    assertEquals(5, base.get_0());
    assertEquals('h', base.get_1());
    assertEquals(89L, base.get_2());
    assertEquals(3.2f, base.get_3());
    assertEquals((short) 16, base.get_4());
    assertEquals("ya", base.get_5());
    assertTrue(base.is_6());
    assertEquals(6218.687231, base.get_7());
  }

  @Test
  public void testDoubleFloatBaseTypes1() throws Exception {
    DoubleFloatPojo p = unmarshall(DoubleFloatPojo.class,
        "{\"dval\":34,\"fval\":54}");

    assertEquals(54.0f, p.getFloatValue());
    assertEquals(34.0, p.getDoubleValue());
  }

  @Test
  public void testDoubleFloatBaseTypes2() throws Exception {
    DoubleFloatPojo p = unmarshall(DoubleFloatPojo.class,
        "{\"dval\":34.0,\"fval\":54.0}");

    assertEquals(54.0f, p.getFloatValue());
    assertEquals(34.0, p.getDoubleValue());
  }

  @Test
  public void testDoubleFloatBaseTypes3() throws Exception {
    DoubleFloatPojo p = unmarshall(DoubleFloatPojo.class,
        "{\"dval\":null,\"fval\":null}");

    assertNull(p.getFloatValue());
    assertNull(p.getDoubleValue());
  }

  @Test
  public void testCollectionEntity() throws Exception {
    CollectionEntity base = unmarshall(CollectionEntity.class,
        "{\"friends\":[\"Simon\",   \"David\", \"Michael\"]}");

    Set<String> friends = new HashSet<String>(3);
    friends.add("Simon");
    friends.add("David");
    friends.add("Michael");

    assertEquals(3, base.getBuddies().size());
    assertTrue(friends.containsAll(base.getBuddies()));
  }

  @Test
  public void testCollectionEntityWithNull() throws Exception {
    CollectionEntity base = unmarshall(CollectionEntity.class,
        "{\"friends\":null}");

    assertEquals(null, base.getBuddies());
  }

  @Test
  public void testBaseTypeWithNull() throws Exception {
    Email email = unmarshall(Email.class, "{\"email\": null}");

    assertEquals(null, email.email);
  }

  @Test
  public void testCollectionInCollection() throws Exception {
    CollectionInCollection base = unmarshall(CollectionInCollection.class,
        "{\"data\": [[\"C\"], [\"A\", \"B\"], []]}");

    assertEquals(3, base.getData().size());
    assertTrue(base.getData().get(0).containsAll(Sets.immutableSet("C")));
    assertTrue(base.getData().get(1).containsAll(Sets.immutableSet("A", "B")));
    assertTrue(base.getData().get(2).isEmpty());
  }

  @Test
  public void testInlinedEntity() throws Exception {
    Marshaller<User> marshaller = TwoLattes.createMarshaller(User.class);
    User user = marshaller.unmarshall(
        Json.fromString("{\"email\": \"jack@bauer.net\"}"));

    assertEquals("jack@bauer.net", user.email.email);
  }

  @Test
  public void testInlinedEntityWithNull() throws Exception {
    User user = unmarshall(User.class, "{\"email\": null}");

    assertNull(user.email);
  }

  @Test
  public void testInlinedEntityUsingInlineAnnotation6() throws Exception {
    List<EmailInline> emails =
        unmarshallList(EmailInline.class, "[\"jack.bauer@ctu.gov\"]");

    assertNotNull(emails);
    assertEquals(1, emails.size());
    assertNotNull(emails.get(0));
    assertEquals("jack.bauer@ctu.gov", emails.get(0).email);
  }

  @Test
  public void testInliningPolymorphicEntityThatHasOnlyDiscriminator() {
    Marshaller<InlinePolymorphic> marshaller = TwoLattes.createMarshaller(InlinePolymorphic.class);

    InlinePolymorphic notInlined = marshaller.unmarshall(
        object(string("doNotInlineMe"), object(string("foo"), string("bar"))));

    assertNull(notInlined.inlineMe);
    assertNotNull(notInlined.doNotInlineMe);

    InlinePolymorphic inlined = marshaller.unmarshall(
        object(string("inlineMe"), string("bar")));

    assertNotNull(inlined.inlineMe);
    assertNull(inlined.doNotInlineMe);
  }

  @Test
  public void testCollectionOfEntities() throws Exception {
    List<User> users = unmarshallList(User.class,
        "[{\"email\": \"jack@bauer.net\"}, {\"email\": \"foo@bar.com\"}]");

    assertEquals(2, users.size());
    assertEquals("jack@bauer.net", users.get(0).email.email);
    assertEquals("foo@bar.com", users.get(1).email.email);
  }

  @Test
  public void testStreamOfEntities() throws Exception {
    final List<User> users = newArrayList();
    streamingUnmarshallList(User.class,
        "[{\"email\": \"jack@bauer.net\"}, {\"email\": \"foo@bar.com\"}]",
        new Marshaller.Generator<User>() {
          public void yield(User entity) {
            users.add(entity);
          }
        });

    assertEquals(2, users.size());
    assertEquals("jack@bauer.net", users.get(0).email.email);
    assertEquals("foo@bar.com", users.get(1).email.email);
  }

  @Test
  public void testTopLevelMap() throws Exception {
    Marshaller<User> marshaller = TwoLattes.createMarshaller(User.class);
    Map<String, User> map = marshaller.unmarshallMap(
        (Json.Object) Json.fromString("{\"1\":{\"email\":\"jack@bauer.net\"}}"));

    assertEquals(1, map.size());
    assertEquals("jack@bauer.net", map.get("1").email.email);
  }

  @Test
  public void testTopLevelEmptyMap() throws Exception {
    Marshaller<User> marshaller = TwoLattes.createMarshaller(User.class);
    Map<String, User> map = marshaller.unmarshallMap(Json.object());
    assertEquals(0, map.size());
  }

  @Test
  public void testTopLevelMapWithNullValue() throws Exception {
    Marshaller<User> marshaller = TwoLattes.createMarshaller(User.class);
    Map<String, User> map = marshaller.unmarshallMap(
        (Json.Object) Json.fromString("{\"1\":null}"));

    assertEquals(1, map.size());
    assertNull(map.get("1"));
  }

  @Test
  public void testEmbeddedMap() throws Exception {
    EntityMap base = unmarshall(EntityMap.class,
        "{\"emails\": {\"a\": {\"email\": \"plperez@stanford.edu\"}, \"b\": {\"email\": \"nonono@yesyesyes.com\"}}}");

    assertEquals(2, base.numberOfEmails());
    assertEquals("plperez@stanford.edu", base.get("a").email);
    assertEquals("nonono@yesyesyes.com", base.get("b").email);
  }

  @Test
  public void testEmbeddedNullMap() throws Exception {
    EntityMap base = unmarshall(EntityMap.class, "{\"emails\": null}");

    assertEquals(null, base.getEmails());
  }

  @Test
  public void testUserType() throws Exception {
    EntityWithURL e =
      unmarshall(EntityWithURL.class, "{\"url\":\"http://www.google.com\"}");

    assertEquals(new URL("http://www.google.com"), e.getUrl());
  }

  @Test
  public void testUserTypeWithNull() throws Exception {
    EntityWithURL e =
      unmarshall(EntityWithURL.class, "{\"url\":null}");

    assertEquals(null, e.getUrl());
  }

  @Test
  public void testArray() throws Exception {
    ArrayEntity e =
      unmarshall(ArrayEntity.class, "{\"values\":[\"hello\", \"world\"]}");

    assertNotNull(e.values);
    assertEquals(2, e.values.length);
    assertEquals("hello", e.values[0]);
    assertEquals("world", e.values[1]);
  }

  @Test
  public void testArrayWithNull() throws Exception {
    ArrayEntity e =
      unmarshall(ArrayEntity.class, "{\"values\":null}");

    assertNull(e.values);
  }

  @Test
  public void testArrayOfList() throws Exception {
    ArrayOfList e = unmarshall(ArrayOfList.class, "{\"weird\":[[5.0,6.0,0.9],[2.3],[12.45,78.0]]}");

    assertNotNull(e.weird);
    assertEquals(3, e.weird.length);
    assertEquals(Lists.immutableList(5.0, 6.0, 0.9), e.weird[0]);
    assertEquals(Lists.immutableList(2.3), e.weird[1]);
    assertEquals(Lists.immutableList(12.45,78.0), e.weird[2]);
  }

  @Test
  public void testInnerClass() throws Exception {
    InnerClass e = unmarshall(InnerClass.class, "{\"field\":\"hello world!\"}");

    assertEquals("hello world!", e.field);
  }

  @Test
  public void testGetterSetter1() throws Exception {
    GetterSetterEntity e = unmarshall(GetterSetterEntity.class, "{\"name\":\"foo bar\"}");

    assertEquals("foo bar", e.getName());
  }

  @Test
  public void testGetterSetter2() throws Exception {
    EntityInterface e = unmarshall(EntityInterface.class, "{\"whatever\":true}");

    assertTrue(e.isWhatever());
  }

  @Test
  public void testDoublyInlined1() throws Exception {
    DoublyInlined e = unmarshall(DoublyInlined.class, "{\"foo\":\"hello there\"}");

    assertNotNull(e.foo);
    assertNotNull(e.foo.bar);
    assertEquals("hello there", e.foo.bar.hello);
  }

  @Test
  public void testDoublyInlined2() throws Exception {
    DoublyInlined e = unmarshall(DoublyInlined.class, "{\"foo\":null}");

    assertNull(e.foo);
  }

  @Test
  public void testPrivateNoArgConstructor() throws Exception {
    PrivateNoArgConstructor e =
        unmarshall(PrivateNoArgConstructor.class, "{\"foo\":\"hi\"}");

    assertNotNull(e);
    assertEquals("hi", e.getFoo());
  }

  @Test
  @Ignore("not yet implemented")
  public void testCyclicStructure() throws Exception {
    Node n = unmarshall(Node.class, "{\"neighbors\":[{\"id\":0}],\"id\":0}", true);

    assertEquals(Sets.immutableSet(n), n.neighbors());
  }

  @Test
  public void nativeArray() throws Exception {
    EntityWithNativeArray e = unmarshall(EntityWithNativeArray.class, "{\"ids\":[5,1,3]}");

    assertNotNull(e.ids);
    assertEquals(3, e.ids.length);
    assertEquals(5, e.ids[0]);
    assertEquals(1, e.ids[1]);
    assertEquals(3, e.ids[2]);
  }

  @Test
  public void nullOptionalObject1() throws Exception {
    NullOptionalValue obj = unmarshall(NullOptionalValue.class, "{}");

    assertNull(obj.getOptional());
  }

  @Test
  public void nullOptionalObject2() throws Exception {
    NullOptionalValue obj = unmarshall(NullOptionalValue.class, "{\"optionalBoolean\":null}");

    assertNull(obj.getOptional());
  }

  @Test
  public void nonNullOptionalObject() throws Exception {
    NullOptionalValue obj = unmarshall(NullOptionalValue.class, "{\"optional\":\"yesItIs\"}");

    assertEquals("yesItIs", obj.getOptional());
  }

  @Test
  public void testMismatchedGetterSetter() {
    AWithNonstandardAccessorNames nsan = unmarshall(
        AWithNonstandardAccessorNames.class, "{\"foo\":\"foo\",\"bar\":\"bar\"}");
    assertEquals(nsan.getA(), "foo");
    assertEquals(nsan.getATag(), "bar");

    String baz = "baz";
    nsan.setAWithWeirdName(baz);
    assertEquals(nsan.getA(), baz);
  }

 /**
  * Tests a field whose {@code @Value} name is different from the getter/setter name (see {@link Foo}),
  * in effect duplicating the value under two different names in the serialized form. This is useful when
  * migrating from an old to a new protocol (i.e. renaming with no down time).
  */
  @Test
  public void testDifferentFieldGetterSetterName1() {
    for (int i = 0; i < 10000; i++) {
      assertEquals(unmarshall(Foo.class, "{\"bar\":42.0,\"foo\":42.0}").foo, 42);
    }
  }

  @Test
  @Ignore("this behavior is not implemented")
  public void testDifferentFieldGetterSetterName2() {
    for (int i = 0; i < 10000; i++) {
      // The following two tests are not strictly necessary because the field name and method name should ALWAYS be
      // identical. However, these tests are here to check that the field name takes precedence over the getter/setter
      // names.
      assertEquals(unmarshall(Foo.class, "{\"bar\":0.0,\"foo\":42.0}").foo, 0);
      assertEquals(unmarshall(Foo.class, "{\"bar\":42.0,\"foo\":0.0}").foo, 42);
    }
  }

  public static <T> T unmarshall(Class<T> clazz, String json) {
    return unmarshall(clazz, json, false, null);
  }

  public static <T> T unmarshall(Class<T> clazz, String json, boolean cyclic) {
    return unmarshall(clazz, json, cyclic, null);
  }

  public static <T> T unmarshall(Class<T> clazz, String json, boolean cyclic, String view) {
    Marshaller<T> marshaller = TwoLattes.createMarshaller(clazz);
    return marshaller.unmarshall(Json.fromString(json), view);
  }

  public static <T> List<T> unmarshallList(Class<T> clazz, String json) {
    Marshaller<T> marshaller = TwoLattes.createMarshaller(clazz);
    return marshaller.unmarshallList((Json.Array) Json.fromString(json));
  }

  public static <T> void streamingUnmarshallList(
      Class<T> clazz, String json, Marshaller.Generator<? super T> generator) {
    Marshaller<T> marshaller = TwoLattes.createMarshaller(clazz);
    try {
      marshaller.unmarshallStream(new StringReader(json), generator);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
