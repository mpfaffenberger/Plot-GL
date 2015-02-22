# Plot-GL
OpenGL utility for exploring records embedded in R^3. Supports zoom, rotation, and translation with mouse. 

Hold mouse1 to translate, mouse2 to rotate, and zoom with mousewheel. 

![Plot-GL](https://raw.githubusercontent.com/mpfaffenberger/Plot-GL/master/GL-Plot.JPG)

Input: Currently only a CSV with x,y,z as the first three columns. Colors records by numeric attribute of your choice.
Specify true/false for color attribute = numeric.
Will generalize input soon.

Use Apache Maven to build.

Usage: 
```
java -jar target/plot-gl-<version>.jar pfaff.plot.DoPlot /path/to/data.csv <color-column> true
```
OR
```
mvn scala:console
pfaff.plot.DoPlot.main(Array("/path/to/data.csv", "<color-column>", "true"))
```
