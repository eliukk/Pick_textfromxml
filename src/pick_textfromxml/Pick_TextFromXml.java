/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pick_textfromxml;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.apache.commons.cli.*;

/**
 * Class reads single or multiple xml files and extracts texts from files.
 * Available command line parameters are: inputfile (-i) outputfile(-o) and
 * inputDir(-a). If outputfile is not defined inputfile name is used, but ".xml"
 * is renamed to "_raw.txt" so that it generates text file. If outputfile is "stdout"
 * class prints results to console and does not generate text files. Class uses apache
 * commons cli library to parse command line options.
 * <p>
 * Example usages:
 * <p>
 * Extracts single xml file from given input path. Writes extracted texts to text file.
 * java Pick_TextFromXml -i 
 * "Z:\\nlf_ocrdump_v0-21_newspapers_1771-1870fin\\1771-1870\\fin\\1775\\1457-4683_1775-09-01_0_001.xml"
 * <p>
 * Extracts single xml file from given inputPath. Writes extracted texts to console.
 * java Pick_TextFromXml -o "stdout" -i 
 * "Z:\\nlf_ocrdump_v0-21_newspapers_1771-1870fin\\1771-1870\\fin\\1775\\1457-4683_1775-09-01_0_001.xml"
 * <p>
 * All xmls from directory: java Pick_TextFromXml -i
 * "Z:\\nlf_ocrdump_v0-21_newspapers_1771-1870fin\\1771-1870\\fin\\1775\\" -a
 * "true"
 *
 * @author esliukko
 */
public class Pick_TextFromXml {

    /**
     * Main method that reads inputfile (-i) outputfile(-o) and inputDir(-a)
     * commandline parameters. Only inputfile(-i) is required commandline
     * parameter. If outputfile is not defined inputfile name is used, but
     * ".xml" is renamed to "_raw.txt" so that it generates text file. if
     * inputDir is specified extractAll method is called with inputFilePath as a
     * parameter. if inputDir is not specified xml file defined in inputFilePath
     * is extracted using parseXmlFile method.
     *
     * @param args commandline args.
     */
    public static void main(String[] args) {

        Options options = new Options();

        Option input = new Option("i", "input", true, "input file path");
        input.setRequired(true);
        options.addOption(input);

        Option output = new Option("o", "output", true, "output file or stdout");
        output.setRequired(false);
        options.addOption(output);

        Option directory = new Option("a", "inputDir", true, "input is directory");
        directory.setRequired(false);
        options.addOption(directory);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);

            System.exit(1);
            return;
        }

        String inputFilePath = cmd.getOptionValue("input");
        String outputFilePath = cmd.getOptionValue("output");
        String inputIsDir = cmd.getOptionValue("inputDir");

        if (inputIsDir != null) {

            extractAll(inputFilePath, outputFilePath);
        } else {
            parseXmlFile(inputFilePath, outputFilePath);
        }
    }

    /**
     * Parses xml file from inputFilePath that is given as a parameter. If outputFilePath
     * is "stdout" application prints parsing result texts to console. Else application outputs
     * txt file that contains texts from xml file to outputFilePath.
     *
     * @param inputFilePath File path that contains the path to xml file.
     * @param outputFilePath File path that contains the path to txt file where
     * text contents are extracted.
     */
    public static void parseXmlFile(String inputFilePath, String outputFilePath) {

        String contents = "";

        try {
            //Z:\\nlf_ocrdump_v0-21_newspapers_1771-1870fin\\1771-1870\\fin\\1775\\1457-4683_1775-09-01_0_001.xml"
            File inputFile = new File(inputFilePath);
            DocumentBuilderFactory dbFactory
                    = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("String");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    String currentContent = eElement.getAttribute("CONTENT");
                    //System.out.println(currentContent);

                    //if (outputFilePath == null || outputFilePath.length() == 0) {
                    contents += currentContent + "\r\n";
                    //}
                }
            }

            if (outputFilePath == null) {
                outputFilePath = inputFilePath.replace(".xml", "_raw.txt");
            }

            if (outputFilePath.equals("stdout")) {
                System.out.println(contents);
            } else {

                PrintWriter out = new PrintWriter(outputFilePath);
                out.println(contents);
                out.close();

                System.out.println("File " + outputFilePath + " written.");
            }
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Pick_TextFromXml.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(Pick_TextFromXml.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Pick_TextFromXml.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Gets array of File objects from the directoryPath String that is given by
     * parameter. Calls extractTexts method with files parameter.
     *
     * @param directoryPath Path to directory where to start extract.
     * @param outputPath outputPath String
     */
    private static void extractAll(String directoryPath, String outputPath) {

        File[] files = new File(directoryPath).listFiles();
        extractTexts(files, outputPath);
    }

    /**
     * Goes through array of File objects and calls parseXmlFile method if file
     * name contains ".xml". if File is a directory extractTexts method is
     * called with parameter that contains File objects of that directory.
     *
     * @param files Array of file objects
     * @param outputPath outputPath String
     */
    private static void extractTexts(File[] files, String outputPath) {
        for (File file : files) {
            if (file.isDirectory()) {
                extractTexts(file.listFiles(), outputPath); // Calls same method again.
            } else {

                String fileName = file.getName();

                if (fileName.contains(".xml")) {
                    parseXmlFile(file.getAbsolutePath(), outputPath);
                }
            }
        }
    }
}
