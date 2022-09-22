package com.lhauspie.example.hateoas;

import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class PojoGenerator {

  private static final EasyRandom easyRandom =
      new EasyRandom(
          new EasyRandomParameters()
              .seed(123L)
              .objectPoolSize(100)
              .randomizationDepth(5)
              .charset(StandardCharsets.UTF_8)
              .stringLengthRange(5, 10)
              .collectionSizeRange(1, 3)
              .scanClasspathForConcreteTypes(true)
              .overrideDefaultInitialization(false)
              .ignoreRandomizationErrors(true));

  public static <T> T generate(Class<T> clazz) {
    return easyRandom.nextObject(clazz);
  }

  public static <T> List<T> generate(int nbItems, Class<T> clazz) {
    return easyRandom.objects(clazz, nbItems).collect(Collectors.toList());
  }
}
