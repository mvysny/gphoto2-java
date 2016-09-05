# GPhoto2 Java Bindings

Java bindings for Linux gphoto/gphoto2 library (see http://www.gphoto.org/ for details).

## Example

See the https://github.com/mvysny/gphoto2-java/blob/master/src/main/java/org/gphoto2/Camera.java file for example on usage. Simple usage:

```java
System.out.println("GPhoto version: " + getLibraryVersion());
final CameraList cl = new CameraList();
System.out.println("Cameras: " + cl);
CameraUtils.closeQuietly(cl);
final Camera c = new Camera();
c.initialize();
final CameraFile cf2 = c.captureImage();
cf2.save(new File("captured.jpg").getAbsolutePath());
CameraUtils.closeQuietly(cf2);
CameraUtils.closeQuietly(c);
```

Note that the https://github.com/twall/jna library is required to be present on the classpath (1.4 and prior versions tested with JNA 3.0.9, 1.5 tested with JNA 4.2.2).

## Downloads

Please find all downloadable artefacts here: http://www.baka.sk/maven2/org/gphoto/gphoto2-java/

## Usage with Maven 2/3

Add the baka.sk maven 2 repo to your maven installation - edit `~/.m2/settings.xml` so that it will look like the following:

```xml
<settings>
 <profiles>
  <profile>
   <id>default</id>
   <activation><activeByDefault>true</activeByDefault></activation>
   <repositories>
    <repository>
     <id>baka</id>
     <name>baka.sk</name>
     <url>http://www.baka.sk/maven2</url>
    </repository>
   </repositories>
  </profile>
 </profiles>
</settings>
```

Then add the following to your dependencies:

```xml
<dependency>
 <groupId>org.gphoto</groupId>
 <artifactId>gphoto2-java</artifactId>
 <version>1.5</version>
</dependency>
```

## Reporting Bugs

If your camera is not working as expected, please try it out with gphoto2 (`gphoto2 --capture-image`) before reporting bugs for this project. gphoto2-related bugs will be closed as invalid.

