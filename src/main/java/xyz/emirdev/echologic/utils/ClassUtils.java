package xyz.emirdev.echologic.utils;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import xyz.emirdev.echologic.EchoLogic;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class ClassUtils {

    public static void findClasses(String pkg, Predicate<ClassInfo> condition, Consumer<ClassInfo> function) {
        try (ScanResult scanResult = new ClassGraph()
                .acceptPackages(pkg)
                .addClassLoader(EchoLogic.get().getClass().getClassLoader())
                .enableClassInfo()
                .scan()) {
            scanResult.getAllClasses().forEach(classinfo -> {
                if (!condition.test(classinfo))
                    return;

                function.accept(classinfo);
            });
        }

    }
}
