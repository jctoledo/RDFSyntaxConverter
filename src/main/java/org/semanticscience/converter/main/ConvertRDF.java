/**
 * Copyright (c) 2012  Jose Cruz-Toledo
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.semanticscience.converter.main;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.semanticscience.converter.RDFSyntaxConverter;

/**
 * @author  Jose Cruz-Toledo
 *
 */
public class ConvertRDF {
	public static void main(String[] args){
		Options options = createOptions();
		CommandLineParser parser = createCliParser();
		File iD = null;
		File oD = null;
		String syntax = null;
		try{
			CommandLine cmd = parser.parse(options, args);
			if(cmd.hasOption("help")){
				printUsage();
				System.exit(1);
			}
			
			if(cmd.hasOption("inputDir")){
				iD = new File(cmd.getOptionValue("inputDir"));
			}else{
				System.out.println("You must specify an input directory");
				System.exit(1);
			}
			
			if(cmd.hasOption("outputDir")){
				oD = new File(cmd.getOptionValue("outputDir"));
			}else{
				System.out.println("You must specify an output directory");
				System.exit(1);
			}
			
			if(cmd.hasOption("syntax")){
				syntax = cmd.getOptionValue("syntax");
			}
			
			if(syntax != null){
				try {
					RDFSyntaxConverter rsc = new RDFSyntaxConverter(iD, oD, syntax);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				try {
					RDFSyntaxConverter rsc = new RDFSyntaxConverter(iD, oD);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}catch (ParseException e){
			System.out.println("Unable to parse specified options.");
			printUsage();
			System.exit(1);
		}
	}
	
	@SuppressWarnings("static-access")
	public static Options createOptions(){
		Options o = new Options();
		//help option
		Option help = new Option("help", false, "Print this message");
		Option inputDirectory = OptionBuilder.withArgName("inputDir")
				.hasArg(true)
				.withDescription("Full path to directory of RDF files to be converted")
				.isRequired()
				.create("inputDir");
		Option outputDirectory = OptionBuilder.withArgName("outputDir")
				.hasArg(true)
				.withDescription("Full path to output directory for converted RDF files")
				.isRequired()
				.create("outputDir");
		Option syntax =OptionBuilder.withArgName("syntax")
		.hasArg(true)
		.withDescription("The output syntax (RDF/XML|RDF/XML-ABBREV|N-TRIPLE|TURTLE|TTL|N3")
		.create("syntax");
		
		o.addOption(help);
		o.addOption(inputDirectory);
		o.addOption(outputDirectory);
		o.addOption(syntax);
		return o;
	}
	
	private static CommandLineParser createCliParser(){
		return new GnuParser();
	}
	private static void printUsage(){
		HelpFormatter hf = new HelpFormatter();
		hf.printHelp("convertRDF [OPTIONS]",createOptions());
	}
	

}
