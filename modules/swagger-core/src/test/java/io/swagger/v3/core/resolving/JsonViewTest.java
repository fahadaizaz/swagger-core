package io.swagger.v3.core.resolving;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverterContextImpl;
import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.core.resolving.resources.JsonViewObject;
import io.swagger.v3.oas.models.media.Schema;
import org.testng.annotations.Test;

import java.lang.annotation.Annotation;
import java.util.Map;

import static io.swagger.v3.core.resolving.SwaggerTestBase.mapper;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class JsonViewTest {

  @Test
  @JsonView(JsonViewObject.View.Protected.class)
  public void includePropertiesToWhichJsonviewIsNotAnnotated() throws NoSuchMethodException {
    ObjectMapper mapper = mapper();
    final ModelResolver modelResolver = new ModelResolver(mapper);
    final ModelConverterContextImpl context = new ModelConverterContextImpl(modelResolver);

    Schema model = context
        .resolve(new AnnotatedType(JsonViewObject.Person.class)
            .jsonViewAnnotation(new JsonView() {
              public Class<? extends Annotation> annotationType() {
                return JsonView.class;
              }
              public Class<?>[] value() {
                return new Class[] {JsonViewObject.View.Protected.class};
              }
            })
            .ctxAnnotations(
                this.getClass()
                    .getMethod("includePropertiesToWhichJsonviewIsNotAnnotated")
                    .getAnnotations()));

    Map<String, Schema> properties = model.getProperties();
    assertEquals(properties.size(), 4);
    assertNotNull(properties.get("id"));
    assertNotNull(properties.get("firstName"));
    assertNotNull(properties.get("lastName"));
    assertNotNull(properties.get("email"));
  }

  @Test
  @JsonView(JsonViewObject.View.Protected.class)
  public void notIncludePropertiesToWhichJsonviewIsNotAnnotated() throws NoSuchMethodException {
    ObjectMapper mapper = mapper();
    mapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);

    final ModelResolver modelResolver = new ModelResolver(mapper);
    final ModelConverterContextImpl context = new ModelConverterContextImpl(modelResolver);

    Schema model = context
        .resolve(new AnnotatedType(JsonViewObject.Person.class)
            .jsonViewAnnotation(new JsonView() {
              public Class<? extends Annotation> annotationType() {
                return JsonView.class;
              }
              public Class<?>[] value() {
                return new Class[] {JsonViewObject.View.Protected.class};
              }
            })
            .includePropertiesWithoutJSONView(false)
            .ctxAnnotations(
                this.getClass()
                    .getMethod("includePropertiesToWhichJsonviewIsNotAnnotated")
                    .getAnnotations()));

    Map<String, Schema> properties = model.getProperties();
    assertEquals(properties.size(), 3);
    assertNotNull(properties.get("id"));
    assertNotNull(properties.get("firstName"));
    assertNotNull(properties.get("lastName"));
  }
}
