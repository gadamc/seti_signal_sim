# seti_siginal_sim

## Compile

```
source setup.sh   #adds dependencies to CLASSPATH
javac apps/simulate/DataSimulator.java
```

## Simulate

```
source setup.sh #if not done already
java apps.simulate.DataSimulator <parameters>
```

If you just call `java apps.simulate.DataSimulator`, the output should describe all of the
command-line input parameters.

```
  11 arguments expected

  sigmaNoise deltaPhi SNR  drift sigmaSquiggle outputLength ampModType ampModPeriod ampModDuty signalClass filename

  where

    sigmaNoise   (double 0 - 127) noise mean power, 13 is good choice
    deltaPhi  (double -180 - 180) average phase angle (degrees) between samples
    SNR (double) Signal amplitude in terms of sigma_noise
    drift (double) Average drift rate of signal
    sigmaSquiggle (double) amplitude of squiggle noise
    outputLength  (int > 2) number of complex-valued samples to write to output
    ampModType  (string = 'none','square','sine') specifies how the amplitude is modulated
    ampModPeriod  (int > 2) periodicity of amplitude modulation, in same units of outputLength
    ampModDuty  (double betweeen 0 and 1) duty cycle of square wave amplitude modulation.
    signalClass (string) a name to classify the signal.
    filename  (string) output filename for data
```

### Example

```
java apps.simulate.DataSimulator 13 100 0.3 -0.0002 0.0001 792576 square 61440 .5 squiggle_pulsed test.data
```

To get 129 raster lines with 6144 frequency bins, which is the size of an archive-compamp file with the
over-sampled frequencies removed (aka, a waterfall plot), the output length of data is a product of these two numbers
129 * 6144 = 792576.

Also, in this example, I've added a square wave amplitude modulation with a periodicity of 61440
samples (equivalent to 10 raster lines) with a duty cycle of 0.5.  One can also add a sine wave
amplitude modulation (in the case of a `sine` modulation, the duty cycle value is ignored.)

## Convert to Spectrogram and output to PGM file

The output file contains a header, all contained within the first line. It is in JSON format. 
From the command-line, one can skip the header and stream the remainder of the data with 
the `tail` command. Then pipe the data into the standard SETI command-line tools.

```
len=6144  
tail -n +2 test.data | sqsample -l $len | sqwindow -l $len | sqfft -l $len | sqabs -l $len | sqreal -l $len | sqpnm -c $len -r 129 -p > wf1.pgm
```

SETI command line tools are here: https://github.com/setiQuest/Algorithms


## Convert to Spectrogram in Python

TODO



## View PGM file

XView will display the PGM file by simply

```
xv wf.pgm
```

In python, I do

```
from __future__ import print_function
from PIL import Image, ImageFilter
 
im = Image.open(‘wf1.pgm’)
im.show()
```
