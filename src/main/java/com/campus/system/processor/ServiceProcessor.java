package com.campus.system.processor;

import com.campus.system.annotation.Service;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"com.campus.system.annotation.Service"})
public class ServiceProcessor extends AbstractProcessor {
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "ServiceProcessor");
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        dealService(annotations, roundEnv);
        return true;
    }

    private void dealService(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Service.class);
        for (Element element : elements) {
            // 获取 ModuleService 注解的值
            Service service = element.getAnnotation(Service.class);
            String serviceURI = service.name();

            TypeSpec.Builder serviceMapBuilder = TypeSpec.classBuilder(service.module() + "ServiceMap")
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

            MethodSpec.Builder registerBuilder = MethodSpec.methodBuilder("register")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(void.class);

            registerBuilder.addStatement("$T.getInstance().registeService($S, $T.class)"
                    , ClassName.get("com.campus.system", "ServiceContext")
                    , serviceURI
                    , ClassName.get((TypeElement) element));
            serviceMapBuilder.addMethod(registerBuilder.build());
            JavaFile javaFile = JavaFile.builder("com.campus.system", serviceMapBuilder.build())
                    .build();
            try {
                javaFile.writeTo(processingEnv.getFiler());
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("生产类异常：" + e.getMessage());
            }
        }
    }
}
