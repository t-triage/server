package com.clarolab.runner.custom;

// Processor for Categories annotation

import org.junit.experimental.categories.Category;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import static com.clarolab.runner.custom.Categories.CombinationRule.AND;
import static com.clarolab.runner.custom.Categories.CombinationRule.OR;

public class CategoriesAnnotationProcessor {
    private String packageName;

    public CategoriesAnnotationProcessor(String packageName) {
        this.packageName = packageName;
    }

    /**
     * @return annotated classes by categories specified in {@code &064;Category} annotation
     */
    public Class<?>[] getCategorizedClasses(Categories categoriesAnnotaton) {
        Class<?>[] annotatedClasses = getClassesAnnotatedWithCategory();
        Class<?>[] desiredCategoryClasses = categoriesAnnotaton.categoryClasses();

        Set<Class<?>> resultSet = new HashSet<Class<?>>();

        for (Class<?> klass : annotatedClasses) {
            Category categoryAnnotation = getCategoryAnnotation(klass);
            if (categoryAnnotation == null)
                continue;

            Class<?>[] categoryClasses = categoryAnnotation.value();
            if (isCombinationRuleNotViolated(categoriesAnnotaton.rule(), categoryClasses, desiredCategoryClasses)) {
                resultSet.add(klass);
            }
        }

        Class<?>[] result = new Class[resultSet.size()];
        resultSet.toArray(result);

        return result;
    }

    /**
     * Retrieves classes annotated with {@code &064;Category} annotation from specified package
     *
     * @return test classes from specified package
     */
    private Class<?>[] getClassesAnnotatedWithCategory() {
        Reflections reflections = new Reflections(packageName);
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(Category.class);

        Class<?>[] result = new Class[annotatedClasses.size()];
        annotatedClasses.toArray(result);
        return result;
    }

    /**
     * Returns Category annotation from specified class
     *
     * @param klass class annotated with {@code &064;Category}
     * @return {@code &064;Category} annotation if class is annotated and null otherwise
     */
    private Category getCategoryAnnotation(Class<?> klass) {
        Annotation[] annotations = klass.getAnnotations();
        for (Annotation classEntryAnnotation : annotations) {
            if (classEntryAnnotation instanceof Category) {
                return (Category) classEntryAnnotation;
            }
        }
        return null;
    }

    /**
     * Checks if combination rule is not violated
     *
     * @param rule combination rule specified at annotation
     * @param container category classes retrieved from class which belongs to test suite
     * @param desiredValues category classes specified in annotation
     * @return true if rule is not violated and false otherwise
     */
    private boolean isCombinationRuleNotViolated(Categories.CombinationRule rule, Class<?>[] container, Class<?>[] desiredValues) {
        int numberOfValues = numberOfDesiredValuesInContainer(container, desiredValues);

        if (rule.equals(AND)) {
            return numberOfValues == desiredValues.length && numberOfValues == container.length;
        }

        if (rule.equals(OR)) {
            return numberOfValues >= 1;
        }

        return false;
    }

    /**
     * Counts number of desired values in specified container
     *
     * @param container array of classes
     * @param desiredValues array of desired classes
     * @return number of desired values
     */
    private int numberOfDesiredValuesInContainer(Class<?>[] container, Class<?>[] desiredValues) {
        int valuesCounter = 0;
        for (Class<?> classFromContainer : container) {
            for (Class<?> classToFind : desiredValues) {
                if (classFromContainer.equals(classToFind)) {
                    valuesCounter++;
                    break;
                }
            }
        }
        return valuesCounter;
    }
}