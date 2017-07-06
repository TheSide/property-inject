/*******************************************************************************
 * Copyright 2017 xlate.io, http://www.xlate.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/
package io.xlate.inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;

import javax.enterprise.inject.InjectionException;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.json.Json;

import org.junit.Before;
import org.junit.Test;

public class PropertyProducerBeanTest {

    private PropertyProducerBean bean;

    @Before
    public void setup() {
        bean = new PropertyProducerBean();
    }

    private Property mockProperty(String name,
                                  String resourceName,
                                  PropertyResourceFormat resourceFormat,
                                  String systemProperty,
                                  String defaultValue) {
        return mockProperty(name, resourceName, resourceFormat, systemProperty, defaultValue, false);
    }

    private Property mockProperty(String name,
                                  String resourceName,
                                  PropertyResourceFormat resourceFormat,
                                  String systemProperty,
                                  String defaultValue,
                                  boolean resolveEnvironment) {
        Property property = mock(Property.class);
        when(property.name()).thenReturn(name);
        when(property.resourceName()).thenReturn(resourceName);
        when(property.resourceFormat()).thenReturn(resourceFormat);
        when(property.systemProperty()).thenReturn(systemProperty);
        when(property.defaultValue()).thenReturn(defaultValue);
        when(property.resolveEnvironment()).thenReturn(resolveEnvironment);
        return property;
    }

    private InjectionPoint mockInjectionPoint(Property property,
                                              Class<? extends Member> memberType,
                                              String memberName,
                                              int memberPosition) {
        return mockInjectionPoint(property, String.class, memberType, memberName, memberPosition);
    }

    @SuppressWarnings("unchecked")
    private InjectionPoint mockInjectionPoint(Property property,
                                              Type type,
                                              Class<? extends Member> memberType,
                                              String memberName,
                                              int memberPosition) {
        InjectionPoint injectionPoint = mock(InjectionPoint.class);

        Member member = mock(memberType);
        when(injectionPoint.getType()).thenReturn(type);
        when(injectionPoint.getMember()).thenReturn(member);
        when(member.getName()).thenReturn(memberName);

        @SuppressWarnings("rawtypes")
        Bean mockBean = mock(Bean.class);
        when(injectionPoint.getBean()).thenReturn(mockBean);
        when(mockBean.getBeanClass()).thenReturn(getClass());

        AnnotatedParameter<?> annotated = mock(AnnotatedParameter.class);
        when(annotated.getPosition()).thenReturn(memberPosition);
        when(injectionPoint.getAnnotated()).thenReturn(annotated);

        when(annotated.getAnnotation(Property.class)).thenReturn(property);

        return injectionPoint;
    }

    @Test
    public void testGetPropertyForFieldMember() throws Exception {
        Property property = this.mockProperty("testGetPropertyForFieldMember",
                                              "io/xlate/inject/PropertyProducerBeanTest.properties",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "testGetPropertyForFieldMember", -1);
        String result = bean.getProperty(point);
        assertEquals("testGetPropertyForFieldMemberValue", result);
    }

    @Test
    public void testGetSystemPropertyForFieldMember() throws Exception {
        Property property = this.mockProperty("",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this
                .mockInjectionPoint(property, Member.class, "testGetSystemPropertyForFieldMember", -1);
        System.setProperty("io.xlate.inject.PropertyProducerBeanTest.testGetSystemPropertyForFieldMember", "val123");
        String result = bean.getProperty(point);
        assertEquals("val123", result);
    }

    @Test
    public void testGetPropertyForFieldMemberWithDefaultResourceName() throws Exception {
        Property property = this.mockProperty("",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this
                .mockInjectionPoint(property, Member.class, "testGetPropertyForFieldMemberWithDefaultResourceName", -1);
        String result = bean.getProperty(point);
        assertEquals("testGetPropertyForFieldMemberWithDefaultResourceNameValue", result);
    }

    @Test
    public void testGetPropertyForFieldMemberWithNullResourceName() throws Exception {
        Property property = this.mockProperty("",
                                              null,
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this
                .mockInjectionPoint(property, Member.class, "testGetPropertyForFieldMemberWithNullResourceName", -1);
        String result = bean.getProperty(point);
        assertEquals("testGetPropertyForFieldMemberWithNullResourceNameValue", result);
    }

    @Test
    public void testGetPropertyForFieldMemberWithEnvReplacement() throws Exception {
        Property property = this.mockProperty("",
                                              null,
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL,
                                              true);
        InjectionPoint point = this
                .mockInjectionPoint(property, Member.class, "testGetPropertyForFieldMemberWithEnvReplacement", -1);
        String result = bean.getProperty(point);
        assertEquals("testGetPropertyForFieldMemberWithEnvReplacementValue" + System.getenv("INJECTED_VARIABLE"),
                     result);
    }

    /*-****************** produce String *************************/
    @Test
    public void testProducePropertyString() {
        Property property = this.mockProperty("testProducePropertyString",
                                              "io/xlate/inject/PropertyProducerBeanTest.properties",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "testProducePropertyString", -1);
        String result = bean.produceProperty(point);
        assertEquals("testProducePropertyStringValue", result);
    }

    @Test(expected = InjectionException.class)
    public void testProducePropertyStringInvalid() {
        Property property = this.mockProperty("testProducePropertyStringInvalid",
                                              "io/xlate/inject/Invalid.properties",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "testProducePropertyStringInvalid", -1);
        bean.produceProperty(point);
    }

    /*-****************** produce Integer *************************/
    @Test
    public void testProducePropertyInteger() {
        Property property = this.mockProperty("",
                                              "io/xlate/inject/PropertyProducerBeanTest.properties",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "testProducePropertyInteger", -1);
        assertEquals(Integer.valueOf(42), bean.produceIntegerProperty(point));
    }

    @Test
    public void testProducePropertyIntegerNull() {
        Property property = this.mockProperty("",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Integer.class, Member.class, "integerNull", -1);
        assertNull(bean.produceIntegerProperty(point));
    }

    @Test(expected = InjectionException.class)
    public void testProducePropertyIntegerInvalid() {
        Property property = this.mockProperty("testProducePropertyIntegerInvalid",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "testProducePropertyIntegerInvalid", -1);
        bean.produceIntegerProperty(point);
    }

    /*-****************** produce native Integer (int) *************************/
    @Test
    public void testProducePropertyNativeInteger() {
        Property property = this.mockProperty("",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, int.class, Member.class, "testProducePropertyInteger", -1);
        assertEquals(42, (int) bean.produceIntegerProperty(point));
    }

    @Test
    public void testProducePropertyNativeIntegerZero() {
        Property property = this.mockProperty("",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, int.class, Member.class, "integerNull", -1);
        assertEquals(0, (int) bean.produceIntegerProperty(point));
    }

    /*-****************** produce Long *************************/
    @Test
    public void testProducePropertyLong() {
        Property property = this.mockProperty("",
                                              "io/xlate/inject/PropertyProducerBeanTest.properties",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Long.class, Member.class, "testProducePropertyLong", -1);
        assertEquals(Long.valueOf(42), bean.produceLongProperty(point));
    }

    @Test
    public void testProducePropertyLongNull() {
        Property property = this.mockProperty("",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Long.class, Member.class, "longNull", -1);
        assertNull(bean.produceLongProperty(point));
    }

    @Test(expected = InjectionException.class)
    public void testProducePropertyLongInvalid() {
        Property property = this.mockProperty("testProducePropertyLongInvalid",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Long.class, Member.class, "testProducePropertyLongInvalid", -1);
        bean.produceLongProperty(point);
    }

    /*-****************** produce native Long (long) *************************/
    @Test
    public void testProducePropertyNativeLong() {
        Property property = this.mockProperty("",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, long.class, Member.class, "testProducePropertyLong", -1);
        assertEquals(42L, (long) bean.produceLongProperty(point));
    }

    @Test
    public void testProducePropertyNativeLongZero() {
        Property property = this.mockProperty("",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, long.class, Member.class, "longNull", -1);
        assertEquals(0L, (long) bean.produceLongProperty(point));
    }

    /*-****************** produce Float *************************/
    @Test
    public void testProducePropertyFloat() {
        Property property = this.mockProperty("",
                                              "io/xlate/inject/PropertyProducerBeanTest.properties",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "testProducePropertyFloat", -1);
        assertEquals(Float.valueOf(42.0f), bean.produceFloatProperty(point));
    }

    @Test
    public void testProducePropertyFloatNull() {
        Property property = this.mockProperty("",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "floatNull", -1);
        assertNull(bean.produceFloatProperty(point));
    }

    @Test(expected = InjectionException.class)
    public void testProducePropertyFloatInvalid() {
        Property property = this.mockProperty("testProducePropertyFloatInvalid",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "testProducePropertyFloatInvalid", -1);
        bean.produceFloatProperty(point);
    }

    /*-****************** produce native Float (float) *************************/
    @Test
    public void testProducePropertyNativeFloat() {
        Property property = this.mockProperty("",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "testProducePropertyFloat", -1);
        assertEquals(42.0f, bean.produceNativeFloatProperty(point), 0.0f);
    }

    @Test
    public void testProducePropertyNativeFloatZero() {
        Property property = this.mockProperty("",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "floatNull", -1);
        assertEquals(0.0f, bean.produceNativeFloatProperty(point), 0.0f);
    }

    /*-****************** produce Double *************************/
    @Test
    public void testProducePropertyDouble() {
        Property property = this.mockProperty("",
                                              "io/xlate/inject/PropertyProducerBeanTest.properties",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "testProducePropertyDouble", -1);
        assertEquals(Double.valueOf(42.0d), bean.produceDoubleProperty(point));
    }

    @Test
    public void testProducePropertyDoubleNull() {
        Property property = this.mockProperty("",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "DoubleNull", -1);
        assertNull(bean.produceDoubleProperty(point));
    }

    @Test(expected = InjectionException.class)
    public void testProducePropertyDoubleInvalid() {
        Property property = this.mockProperty("testProducePropertyDoubleInvalid",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "testProducePropertyDoubleInvalid", -1);
        bean.produceDoubleProperty(point);
    }

    /*-****************** produce native Double (double) *************************/
    @Test
    public void testProducePropertyNativeDouble() {
        Property property = this.mockProperty("",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "testProducePropertyDouble", -1);
        assertEquals(42.0d, bean.produceNativeDoubleProperty(point), 0.0d);
    }

    @Test
    public void testProducePropertyNativeDoubleZero() {
        Property property = this.mockProperty("",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "DoubleNull", -1);
        assertEquals(0.0d, bean.produceNativeDoubleProperty(point), 0.0d);
    }

    /*-****************** produce BigDecimal *************************/
    @Test
    public void testProducePropertyBigDecimal() {
        Property property = this.mockProperty("",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "testProducePropertyBigDecimal", -1);
        assertEquals(new BigDecimal("42.042"), bean.produceBigDecimalProperty(point));
    }

    @Test
    public void testProducePropertyBigDecimalNull() {
        Property property = this.mockProperty("",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "BigDecimalNull", -1);
        assertNull(bean.produceBigDecimalProperty(point));
    }

    @Test(expected = InjectionException.class)
    public void testProducePropertyBigDecimalInvalid() {
        Property property = this.mockProperty("testProducePropertyBigDecimalInvalid",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this
                .mockInjectionPoint(property, Member.class, "testProducePropertyBigDecimalInvalid", -1);
        bean.produceBigDecimalProperty(point);
    }

    /*-****************** produce BigInteger *************************/
    @Test
    public void testProducePropertyBigInteger() {
        Property property = this.mockProperty("",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "testProducePropertyBigInteger", -1);
        assertEquals(new BigInteger("-42"), bean.produceBigIntegerProperty(point));
    }

    @Test
    public void testProducePropertyBigIntegerNull() {
        Property property = this.mockProperty("",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "BigIntegerNull", -1);
        assertNull(bean.produceBigIntegerProperty(point));
    }

    @Test(expected = InjectionException.class)
    public void testProducePropertyBigIntegerInvalid() {
        Property property = this.mockProperty("testProducePropertyBigIntegerInvalid",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this
                .mockInjectionPoint(property, Member.class, "testProducePropertyBigIntegerInvalid", -1);
        bean.produceBigIntegerProperty(point);
    }

    /*-****************** produce JsonArray *************************/
    @Test
    public void testProducePropertyJsonArray() {
        Property property = this.mockProperty("",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "testProducePropertyJsonArray", -1);
        assertEquals(Json.createArrayBuilder().add("elem1").add("elem2").build(), bean.produceJsonArrayProperty(point));
    }

    @Test
    public void testProducePropertyJsonArrayNull() {
        Property property = this.mockProperty("",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "JsonArrayNull", -1);
        assertNull(bean.produceJsonArrayProperty(point));
    }

    @Test(expected = InjectionException.class)
    public void testProducePropertyJsonArrayInvalid() {
        Property property = this.mockProperty("testProducePropertyJsonArrayInvalid",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this
                .mockInjectionPoint(property, Member.class, "testProducePropertyJsonArrayInvalid", -1);
        bean.produceJsonArrayProperty(point);
    }

    /*-****************** produce JsonObject *************************/
    @Test
    public void testProducePropertyJsonObject() {
        Property property = this.mockProperty("",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "testProducePropertyJsonObject", -1);
        assertEquals(Json.createObjectBuilder()
                .add("key1", "value1")
                .add("key2", 42f)
                .add("key3",
                     Json.createArrayBuilder()
                             .add("elem1")
                             .add("elem2"))
                .build(),
                     bean.produceJsonObjectProperty(point));
    }

    @Test
    public void testProducePropertyJsonObjectNull() {
        Property property = this.mockProperty("",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "JsonObjectNull", -1);
        assertNull(bean.produceJsonObjectProperty(point));
    }

    @Test(expected = InjectionException.class)
    public void testProducePropertyJsonObjectInvalid() {
        Property property = this.mockProperty("testProducePropertyJsonObjectInvalid",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this
                .mockInjectionPoint(property, Member.class, "testProducePropertyJsonObjectInvalid", -1);
        bean.produceJsonObjectProperty(point);
    }

}
