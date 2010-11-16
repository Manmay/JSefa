/*
 * Copyright 2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jsefa.common.config;

import static org.jsefa.common.config.Configuration.Defaults.DEFAULT_SIMPLE_TYPE_CONVERTER_PROVIDER_PROVIDER;
import static org.jsefa.common.config.Configuration.Defaults.DEFAULT_VALIDATION_MODE;
import static org.jsefa.common.config.Configuration.Defaults.DEFAULT_VALIDATOR_PROVIDER_PROVIDER;
import static org.jsefa.common.config.InitialConfigurationParameters.OBJECT_ACCESSOR_PROVIDER_CLASS;
import static org.jsefa.common.config.InitialConfigurationParameters.SIMPLE_TYPE_CONVERTER_PROVIDER;
import static org.jsefa.common.config.InitialConfigurationParameters.VALIDATION_MODE;
import static org.jsefa.common.config.InitialConfigurationParameters.VALIDATOR_PROVIDER;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.jsefa.Deserializer;
import org.jsefa.IOFactory;
import org.jsefa.Serializer;
import org.jsefa.common.accessor.ObjectAccessorProvider;
import org.jsefa.common.accessor.ReflectionBasedObjectAccessorProvider;
import org.jsefa.common.converter.BigDecimalConverter;
import org.jsefa.common.converter.BooleanConverter;
import org.jsefa.common.converter.DateConverter;
import org.jsefa.common.converter.EnumConverter;
import org.jsefa.common.converter.IntegerConverter;
import org.jsefa.common.converter.LongConverter;
import org.jsefa.common.converter.SimpleListConverter;
import org.jsefa.common.converter.SimpleTypeConverter;
import org.jsefa.common.converter.StringConverter;
import org.jsefa.common.converter.provider.SimpleTypeConverterProvider;
import org.jsefa.common.mapping.EntryPoint;
import org.jsefa.common.mapping.TypeMappingRegistry;
import org.jsefa.common.util.OnDemandObjectProvider;
import org.jsefa.common.util.ReflectionUtil;
import org.jsefa.common.validator.BigDecimalValidator;
import org.jsefa.common.validator.CollectionValidator;
import org.jsefa.common.validator.IntegerValidator;
import org.jsefa.common.validator.LongValidator;
import org.jsefa.common.validator.MapValidator;
import org.jsefa.common.validator.StringValidator;
import org.jsefa.common.validator.provider.ValidatorProvider;

/**
 * The abstract superclass for configuration object classes. It uses lazy initialization.
 * <p>
 * A configuration object is used when creating a new {@link IOFactory}. One configuration object can be used for
 * the creation of multiple factories as each new factory holds its own copy of the configuration object. So the
 * configuration object can be changed after creating a factory with it without affecting the configuration of the
 * factory.
 * 
 * @param <R> the type of the TypeMappingRegistry
 * @param <E> the type of the EntryPoint
 * 
 * @author Norman Lahme-Huetig
 */
public abstract class Configuration<R extends TypeMappingRegistry<?>, E extends EntryPoint<?, ?>> {

    private ObjectAccessorProvider objectAccessorProvider;

    private SimpleTypeConverterProvider simpleTypeConverterProvider;
    
    private ValidatorProvider validatorProvider;

    private R typeMappingRegistry;

    private Collection<E> entryPoints;
    
    private ValidationMode validationMode;

    /**
     * Constructs a new <code>Configuration</code>.
     */
    protected Configuration() {
    }

    /**
     * Constructs a new <code>Configuration</code> as a copy of the given one.
     * 
     * @param other the other config
     */
    @SuppressWarnings("unchecked")
    protected Configuration(Configuration<R, E> other) {
        setObjectAccessorProvider(other.getObjectAccessorProvider());
        setSimpleTypeConverterProvider(other.getSimpleTypeConverterProvider().createCopy());
        setValidatorProvider(other.getValidatorProvider().createCopy());
        setTypeMappingRegistry((R) other.getTypeMappingRegistry().createCopy());
        setEntryPoints(new ArrayList<E>(other.getEntryPoints()));
        setValidationMode(other.getValidationMode());
    }

    /**
     * Returns the type mapping registry.
     * 
     * @return the type mapping registry
     */
    public final R getTypeMappingRegistry() {
        if (this.typeMappingRegistry == null) {
            this.typeMappingRegistry = createDefaultTypeMappingRegistry();
        }
        return this.typeMappingRegistry;
    }

    /**
     * Sets the type mapping registry.
     * 
     * @param typeMappingRegistry a type mapping registry
     */
    public final void setTypeMappingRegistry(R typeMappingRegistry) {
        this.typeMappingRegistry = typeMappingRegistry;
    }

    /**
     * Returns the entry points. An entry point is required for every type of object which will be passed to
     * {@link Serializer#write} or which should be returned from {@link Deserializer#next} and only for these
     * objects (not for the objects related to these ones). If more than one entry point is defined for the same
     * data type name, then <br>
     * a) the last one is used for serialization<br>
     * b) all are used for deserialization whereas their respective designators are used as alternative designators
     * for the same data type.
     * 
     * @return the entry points.
     */
    public final Collection<E> getEntryPoints() {
        if (this.entryPoints == null) {
            this.entryPoints = new ArrayList<E>();
        }
        return this.entryPoints;
    }

    /**
     * Sets the entry points.
     * 
     * @param entryPoints the entry points
     */
    public final void setEntryPoints(Collection<E> entryPoints) {
        this.entryPoints = entryPoints;
    }

    /**
     * Returns the object accessor provider.
     * 
     * @return the object accessor provider
     */
    public final ObjectAccessorProvider getObjectAccessorProvider() {
        if (this.objectAccessorProvider == null) {
            Class<ObjectAccessorProvider> theClass = InitialConfiguration.get(OBJECT_ACCESSOR_PROVIDER_CLASS,
                    ReflectionBasedObjectAccessorProvider.class);
            this.objectAccessorProvider = ReflectionUtil.createInstance(theClass);
        }
        return this.objectAccessorProvider;
    }

    /**
     * Returns the simple type converter provider.
     * 
     * @return the simple type converter provider
     */
    public final SimpleTypeConverterProvider getSimpleTypeConverterProvider() {
        if (this.simpleTypeConverterProvider == null) {
            SimpleTypeConverterProvider initialProvider = InitialConfiguration.get(SIMPLE_TYPE_CONVERTER_PROVIDER,
                    DEFAULT_SIMPLE_TYPE_CONVERTER_PROVIDER_PROVIDER);
            this.simpleTypeConverterProvider = initialProvider.createCopy();
        }
        return this.simpleTypeConverterProvider;
    }
    
    /**
     * Returns the validator provider.
     * 
     * @return the validator provider
     */
    public final ValidatorProvider getValidatorProvider() {
        if (this.validatorProvider == null) {
            ValidatorProvider initialProvider = InitialConfiguration.get(VALIDATOR_PROVIDER,
                    DEFAULT_VALIDATOR_PROVIDER_PROVIDER);
            this.validatorProvider = initialProvider.createCopy();
        }
        return this.validatorProvider;
    }

    /**
     * Returns the validation mode.
     * @return the validation mode
     */
    public final ValidationMode getValidationMode() {
        if (this.validationMode == null) {
            this.validationMode = InitialConfiguration.get(VALIDATION_MODE, DEFAULT_VALIDATION_MODE);
        }
        return this.validationMode;
    }

    /**
     * Sets the <code>ObjectAccessorProvider</code>.
     * 
     * @param objectAccessorProvider the <code>ObjectAccessorProvider</code>
     */
    public final void setObjectAccessorProvider(ObjectAccessorProvider objectAccessorProvider) {
        this.objectAccessorProvider = objectAccessorProvider;
    }

    /**
     * Sets the <code>SimpleTypeConverterProvider</code>.
     * 
     * @param simpleTypeConverterProvider the <code>SimpleTypeConverterProvider</code>
     */
    public final void setSimpleTypeConverterProvider(SimpleTypeConverterProvider simpleTypeConverterProvider) {
        this.simpleTypeConverterProvider = simpleTypeConverterProvider;
    }
    
    /**
     * Sets the <code>ValidatorProvider</code>.
     * 
     * @param validatorProvider the <code>ValidatorProvider</code>
     */
    public final void setValidatorProvider(ValidatorProvider validatorProvider) {
        this.validatorProvider = validatorProvider;
    }

    /**
     * Sets the validation mode.
     * @param validationMode the validation mode
     */
    public final void setValidationMode(ValidationMode validationMode) {
        this.validationMode = validationMode;
    }

    /**
     * Creates a copy of this <code>Configuration</code>.
     * 
     * @return a copy of this <code>Configuration</code>
     */
    public abstract Configuration<R, E> createCopy();

    /**
     * Creates the default type mapping registry to be used if none is explicitly given.
     * 
     * @return the default type mapping registry
     */
    protected abstract R createDefaultTypeMappingRegistry();

    /**
     * Set of default configuration values.
     * 
     * @author Norman Lahme-Huetig
     */
    public interface Defaults {
        /**
         * The default simple type converter provider provider.
         */
        OnDemandObjectProvider DEFAULT_SIMPLE_TYPE_CONVERTER_PROVIDER_PROVIDER = new OnDemandObjectProvider() {
            @SuppressWarnings("unchecked")
            public SimpleTypeConverterProvider get() {
                SimpleTypeConverterProvider provider = new SimpleTypeConverterProvider();
                provider.registerConverterType(String.class, StringConverter.class);
                provider.registerConverterType(boolean.class, BooleanConverter.class);
                provider.registerConverterType(Boolean.class, BooleanConverter.class);
                provider.registerConverterType(long.class, LongConverter.class);
                provider.registerConverterType(Long.class, LongConverter.class);
                provider.registerConverterType(int.class, IntegerConverter.class);
                provider.registerConverterType(Integer.class, IntegerConverter.class);
                provider.registerConverterType(BigDecimal.class, BigDecimalConverter.class);
                provider.registerConverterType(Date.class, DateConverter.class);
                if (ReflectionUtil.hasClass("javax.xml.datatype.XMLGregorianCalendar")) {
                    Class<?> gregorianCalendar = ReflectionUtil.getClass("javax.xml.datatype.XMLGregorianCalendar");
                    Class<? extends SimpleTypeConverter> gregorianConverter = null;

                    if (ReflectionUtil.hasClass("org.jsefa.common.converter.XMLGregorianCalendarConverter")) {
                        gregorianConverter = (Class<? extends SimpleTypeConverter>) ReflectionUtil.
                        	getClass("org.jsefa.common.converter.XMLGregorianCalendarConverter");
                        provider.registerConverterType(gregorianCalendar, gregorianConverter);
                    }
                }                
                provider.registerConverterType(Enum.class, EnumConverter.class);
                provider.registerConverterType(Collection.class, SimpleListConverter.class);
                return provider;
            }
        };
        
        /**
         * The default validator provider provider.
         */
        OnDemandObjectProvider DEFAULT_VALIDATOR_PROVIDER_PROVIDER = new OnDemandObjectProvider() {
            @SuppressWarnings("unchecked")
            public ValidatorProvider get() {
                ValidatorProvider provider = new ValidatorProvider();
                provider.registerValidatorType(String.class, StringValidator.class);
                provider.registerValidatorType(Integer.class, IntegerValidator.class);
                provider.registerValidatorType(int.class, IntegerValidator.class);
                provider.registerValidatorType(Long.class, LongValidator.class);
                provider.registerValidatorType(long.class, LongValidator.class);
                provider.registerValidatorType(BigDecimal.class, BigDecimalValidator.class);
                provider.registerValidatorType(Collection.class, CollectionValidator.class);
                provider.registerValidatorType(Map.class, MapValidator.class);
                return provider;
            }
        };

        /**
         * The default validation mode.
         */
        ValidationMode DEFAULT_VALIDATION_MODE = ValidationMode.BOTH; 
    }

}
