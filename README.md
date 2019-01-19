# Password Usability Analysis
This project represents a usability study of two password authentication schemas and an implementation for a proposed alternative system. All work featured
herein was submitted in satisfaction of the requirements outlined in project 2 of the winter 2018 session of COMP3008.

## Files
```
    /LogProcessor
        /src
            /com
                /comp_3008
                    dataBySite.csv
                    dataByUser.csv
                    Generator.java
                    Imagept21.csv
                    Text21.csv
                    TimeDifference.java
    /SchemeComparison
        SchemeComparison.R
        SchemeComparison.Rproj
        SchemeComparisonPseudocode.txt
    /PasswordApp
        /testSet
            /Stimuli/Action
                002.jpg...200.jpg
            /all
                002.jpg...200.jpg
        Password_test.py
        testingframework.py
        Password_test.ipynb
        save.p
        PasswordApp.csv
    dataBySite.csv
    dataByUser.csv
    p2.pdf
    COMP3008 Project 2 Quantitative Usability Evaluation.pdf
    COMP3008 Project 2 Handout.pdf
    README.md
    README.txt
```

## Quantitative Usability Evaluation
The Quantitative usability evaluation document is provided in 
"COMP3008 Project 2 Quantitative Usability Evaluation.pdf".

## LogProcessor
This directory consists of all necessary source files for the operation of
the LogProcessor. Any Processed CSV files produced by the log processor will 
also be contained in this directory.

## SchemeComparison
This directory consists of all necessary source and project files for operation
of SchemeComparison. Any graph files and processed CSV files containing 
descriptive statistics generated will also be contained in this directory.

## PasswordApp
This directory contains all of the necessary project and source files required 
for the operation of the PasswordApp system. Source code for inspection outside 
of the Jupityr notebook are provided in Password_test.py and testingframework.py.
Any log files extracted from PasswordApp are also provided in this folder.

# Instructions
## LogProcessor
To compile the LogProcessor software, use the command "javac com/comp_com
[3008/*.java" from the LogProcessor/src directory. Note: if javac is not 
installed on your system, use whatever java compiler you prefer.
To execute the Log Processor software, use the command "java 
com/comp_3008/Generator" from the LogProcessor/src directory. This will delete 
any .cvs files in the com/comp_3008 directory other than the two input files to 
be parsed, parse the two input csv files, and generate two csv files to be used 
by the SchemeComparison software.
The first csv file generated is the dataByUser.csv, which organizes the data by 
user.
The second csv file generated is the dataBySite.csv, which organizes the data by 
website.

## SchemeComparison
To execute the SchemeComparison software simply open the SchemeComparison 
project 
located in /COMP3008-p2 in R Studio and execute the SchemeComparison.R sript. 
All graphs and 
csv files containing descriptive statistics will be generated within this 
directory. Note that 
the directory structure must be maintained for the script to function correctly. 

## PasswordApp
### Setting up program
Download appropriate files and folders from git
Install Anaconda 3.6 (with add to path checked!)
Open command prompt
Enter in: conda install pillow
Enter in: jupyter notebook 
Jupyter notebook will have been installed with Anaconda
Jupyter notebook will open in browser and you'll be presented with your 
computer's library
Navigate to where you save the appropriate files from the github upload or the 
zip folder 
Please open the file titled Password_test.ipynb in Jupyter

Please note, there are some logs visible within the program for your convenience 
Hit Shift+Enter on the logs as you proceed through the program

### Instructions for program
You may see messages indicating Jupyter Widgets are not running, the program is working
 Click the top block and hit Shift +Enter till you have gone through every block
Now go to the top of the program:
Type in your desired username along with account, do NOT hit enter
Click block with 4 pictures:ctrl+enter to get new images (or skip to next step if you are ok with images)
Click block with password:ctrl+enter to get new password (or skip to next step if you are ok with password)
Click next block with forms: Type in username and password for which to test password, do NOT hit enter
Click next block with images: Shift+Enter for associated images with your account for your username
Type in your password in Password and do NOT hit enter
Click next block:ctrl+enter to find of if you got the correct password

## Licensing 
This project is licensed under the MIT license - see LICENSE.md for more details.