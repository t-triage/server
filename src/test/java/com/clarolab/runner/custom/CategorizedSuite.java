package com.clarolab.runner.custom;

import org.junit.internal.builders.AllDefaultPossibilitiesBuilder;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

import java.util.List;

/**
 * This JUnit suite implementation provides a capability to lookup categorized tests automatically without using Suite.SuiteClasses
 */

public class CategorizedSuite extends ParentRunner<Runner> {
    private final List<Runner> runners;

    /**
     * Computes classes to run basing on {@code &064;Category} annotation
     *
     * @param suiteClass class of test suite
     * @return array of computed classes
     * @throws InitializationError if suite class is not annotated
     */
    static Class<?>[] computeSuiteClasses(Class<?> suiteClass) throws InitializationError {
        Categories categoriesAnnotaton = suiteClass.getAnnotation(Categories.class);
        if (categoriesAnnotaton == null) {
            throw new InitializationError(String.format("class '%s' must have a Categories annotation",
                    suiteClass.getName()));
        }

        String packageName;
        BasePackage basePackageAnnotation = suiteClass.getAnnotation(BasePackage.class);
        if (basePackageAnnotation == null) {
            packageName = suiteClass.getPackage().getName();
        } else {
            packageName = basePackageAnnotation.name();
        }

        CategoriesAnnotationProcessor categoriesAnnotationProcessor = new CategoriesAnnotationProcessor(packageName);
        return categoriesAnnotationProcessor.getCategorizedClasses(categoriesAnnotaton);
    }

    /**
     * Called reflectively on classes annotated with {@code &064;RunWith(CategorizedSuite.class)}
     *
     * @param klass the root class
     * @param builder builds runners for classes in the suite
     * @throws InitializationError
     */
    public CategorizedSuite(Class<?> klass, RunnerBuilder builder) throws InitializationError {
        this(builder, klass, computeSuiteClasses(klass));
    }

    /**
     * Call this when there is no single root class (for example, multiple class names passed on the command line to
     * {@link org.junit.runner.JUnitCore}
     *
     * @param builder builds runners for classes in the suite
     * @param classes the classes in the suite
     * @throws InitializationError
     */
    public CategorizedSuite(RunnerBuilder builder, Class<?>[] classes) throws InitializationError {
        this(null, builder.runners(null, classes));
    }

    /**
     * Call this when the default builder is good enough. Left in for compatibility with JUnit 4.4.
     *
     * @param klass the root of the suite
     * @param suiteClasses the classes in the suite
     * @throws InitializationError
     */
    protected CategorizedSuite(Class<?> klass, Class<?>[] suiteClasses) throws InitializationError {
        this(new AllDefaultPossibilitiesBuilder(true), klass, suiteClasses);
    }

    /**
     * Called by this class and subclasses once the classes making up the suite have been determined
     *
     * @param builder builds runners for classes in the suite
     * @param klass the root of the suite
     * @param suiteClasses the classes in the suite
     * @throws InitializationError
     */
    protected CategorizedSuite(RunnerBuilder builder, Class<?> klass, Class<?>[] suiteClasses)
            throws InitializationError {
        this(klass, builder.runners(klass, suiteClasses));
    }

    /**
     * Called by this class and subclasses once the runners making up the suite have been determined
     *
     * @param klass root of the suite
     * @param runners for each class in the suite, a {@link Runner}
     * @throws InitializationError
     */
    protected CategorizedSuite(Class<?> klass, List<Runner> runners) throws InitializationError {
        super(klass);
        this.runners = runners;
    }

    @Override
    protected List<Runner> getChildren() {
        return runners;
    }

    @Override
    protected Description describeChild(Runner child) {
        return child.getDescription();
    }

    @Override
    protected void runChild(Runner runner, final RunNotifier notifier) {
        runner.run(notifier);
    }

    /**
     * Returns an empty suite.
     */
    public static Runner emptySuite() {
        try {
            return new CategorizedSuite((Class<?>) null, new Class<?>[0]);
        } catch (InitializationError e) {
            throw new RuntimeException("This shouldn't be possible");
        }
    }
}
