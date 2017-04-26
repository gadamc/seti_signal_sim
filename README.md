# seti_siginal_sim

Yes, "siginal" is a typo. :)

## Compile

### Using SBT

Find links to install SBT on Mac, Linux and Windows here: http://www.scala-sbt.org/release/docs/Setup.html

This one command will download the dependecies, compile the code and package it into an
uber jar file. 

However, before you do, you should create the file `resources/simulation.properties`. There is a `.template` file
in the `resources` folder, which will tell you which fields you need to create.

```
sbt assembly
```

### OLD way, no SBT

Note, without using SBT, these instructions will not create an uber jar and it's unlikely 
this will run on a spark cluster (the dependecies must be found on all worker nodes). 

However, for local development this should work. 

Also, the old way requires that all dependency libraries to be downloaded and installed in the "dependcies"
folder. As of this writing, the java code is only dependent upon the Jackson tools for generating JSON. 


##### Compile

```
source setup.sh   #adds dependencies to CLASSPATH
javac apps/simulate/*.java
```

##### Build Jar (optional)

```
jar cfm setisimulator.jar MANIFEST.MF apps/simulate/*.class
```


## Run

### Using SBT 

If you've used `sbt` to package the code, the resulting jar file is 
`target/signalsimulation-assembly-8.0.jar`

This main class for this jar file, however, is now in [spark/SETISim.scala](spark/SETISim.scala)

```
java -jar <jar file> <parameters>
```

We now use the ParameterGenerator and related classes to define the parameters of the simulation. 
In the example below, the `narrowband` option tells the ParameterGenerator to choose a particular
class and the set of simulation parameters that his definied within its code.

The noise type is determined for the final option. In this case `gaussian` tells the program
to use the GaussianNoise.java class to generate noise.

##### Properties

You must create the file `resources/simulation.properties`. A template with all of the necessary
property values is in the repository. You should `cp resoureces/simulation.properties.template resoureces/simulation.properties` and then fill in the values. 

##### Example

```
java -jar  target/scala-2.11/signalsimulation-assembly-8.0.jar training serial 2 8 narrowband gaussian
```

or

```
java -jar  target/scala-2.11/signalsimulation-assembly-8.0.jar training spark 2 8 narrowband gaussian
```

```
java -jar  target/scala-2.11/signalsimulation-assembly-8.0.jar test spark 20 1000 narrowband sunnnoise
```


```
java -jar  target/scala-2.11/signalsimulation-assembly-8.0.jar private spark 20 1000 narrowband sunnnoise
```

#### Submitting to IBM Spark Cluster

```
./spark-submit.sh --vcap vcap.enterprise.json --deploy-mode cluster --conf spark.service.spark_version=2.0 --class org.seti.simulator.SETISim target/scala-2.11/signalsimulation-assembly-8.0.jar training spark 20 1000 narrowband gaussian
```


### OLD way, no SBT

If you did not package the compiled .class files into a jar file, you can call the 
main class directly. 

```
source setup.sh #if not done already. Only need to to this once.
java apps.simulate.DataSimulator <all individual parameters>

//example
java apps.simulate.DataSimulator 13 "" 100 0.4 -0.0001 -0.0002 0.0001 792576 square 61440 .5 squiggle_pulsed test.data
```


#### Unpackaged .class file

```
java apps.simulate.DataSimulator 13 "" 100 0.3 -0.0001 -0.0002 0.0001 792576 square 61440 .5 squiggle_pulsed test.data
```

#### Manual jar file

Alternatively 

```
java -jar  setisimulator.jar 13 "" 100 0.3 -0.0001 -0.0002 0.0001 792576 square 61440 .5 squiggle_pulsed test.data
```

### Description of above simulation

To get 129 raster lines with 6144 frequency bins, which is the size of an archive-compamp file with the
over-sampled frequencies removed (aka, a waterfall plot), the output length of data is a product of these two numbers
129 * 6144 = 792576.

Also, in this example, I've added a square wave amplitude modulation with a periodicity of 61440
samples (equivalent to 10 raster lines) with a duty cycle of 0.5.  One can also add a sine wave
amplitude modulation (in the case of a `sine` modulation, the duty cycle value is ignored.)


## Create Spectrogram 

The output file contains two headers, all contained within the first two lines. They are in JSON format. The
first header is called the "private" header, and the second header is the "public" header. When these data
are published, the information from the private header will be saved to a database and removed from the simulation
file and the public header will remain. 

### With Python
See `python/read_sim.py`.  It contains example functions to read the output data files and product spectrogram.

From iPython shell one can

```
%load python/read_sim.py
```

and then

```
read_and_show()
```


### With SETI Command Line tools



From the command-line, one can skip both headers and stream the remainder of the data with 
the `tail` command. Then pipe the data into the standard SETI command-line tools.

```
len=6144  
tail -n +3 test.data | sqsample -l $len | sqwindow -l $len | sqfft -l $len | sqabs -l $len | sqreal -l $len | sqpnm -c $len -r 129 -p > wf1.pgm
```

SETI command line tools are here: https://github.com/setiQuest/Algorithms


#### View PGM file

XView will display the PGM file by simply

```
xv wf.pgm
```

In python, I do

```
from __future__ import print_function
from PIL import Image, ImageFilter
 
im = Image.open('wf1.pgm')
im.show()
```
