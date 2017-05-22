# Pick_textfromxml

Class reads single or multiple xml files and extracts texts from files.
Available command line parameters are: inputfile (-i) outputfile(-o) and
inputDir(-a). If outputfile is not defined inputfile name is used, but ".xml"
is renamed to "_raw.txt" so that it generates text file. If outputfile is "stdout"
class prints results to console and does not generate text files. Class uses apache
commons cli library to parse command line options so that library is needed to compile
class.

Example usages:

Extracts single xml file from given input path. Writes extracted texts to text file:
java Pick_TextFromXml -i 
"Z:\\nlf_ocrdump_v0-21_newspapers_1771-1870fin\\1771-1870\\fin\\1775\\1457-4683_1775-09-01_0_001.xml"

Extracts single xml file from given inputPath. Writes extracted texts to console:
java Pick_TextFromXml -o "stdout" -i 
"Z:\\nlf_ocrdump_v0-21_newspapers_1771-1870fin\\1771-1870\\fin\\1775\\1457-4683_1775-09-01_0_001.xml"

All xmls from directory: java Pick_TextFromXml -i
"Z:\\nlf_ocrdump_v0-21_newspapers_1771-1870fin\\1771-1870\\fin\\1775\\" -a
"true"
