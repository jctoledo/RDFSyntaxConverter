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
package org.semanticscience.converter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.zip.Checksum;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.util.FileManager;

/**
 * @author Jose Cruz-Toledo
 * 
 */
public class RDFSyntaxConverter {
	static final Logger log = Logger.getLogger(RDFSyntaxConverter.class);
	private static String outputSyntax;
	private String directory = "/tmp/jena";
	/**
	 * The file that is to be converted
	 */
	private File inputFile;
	/**
	 * The converted file
	 */
	private File outputFile;
	private Model m;
	private Dataset d;

	public RDFSyntaxConverter() {
		inputFile = null;
		outputFile = null;
		m = null;
		d = null;
	}

	/**
	 * Default converter, reads inputFile into a model, guesses the syntax and
	 * outputs an RDF/XML RDF file in outputFile
	 * 
	 * @param inputFile
	 *            the rdf file to be converted
	 * @param outputFile
	 *            the default output RDF file RDF/XML syntax
	 */
	public RDFSyntaxConverter(File anInputFile, File anOutputFile) {
		inputFile = anInputFile;
		outputFile = anOutputFile;
		try {
			// Try reading the input file
			d = TDBFactory.createDataset("/tmp/jena/tdb");
			m = d.getDefaultModel();
			FileManager.get().readModel(m, inputFile.getAbsolutePath());
			// m = FileManager.get().loadModel(inputFile.getAbsolutePath());
			// write it to file using rdf/xml syntax

			m.write(new FileOutputStream(anOutputFile));
		} catch (FileNotFoundException e) {
			log.error("could not write to file", e);
		} finally {
			try {
				m.close();
				d.close();
			} catch (Exception e) {
				log.error(e);
			}
		}
	}

	public RDFSyntaxConverter(File anInputFile, File anOutputFile,
			String outputSyntax) {
		inputFile = anInputFile;
		outputFile = anOutputFile;
		// check for valid output syntax
		if (checkOutputSyntax(outputSyntax)) {
			m = FileManager.get().loadModel(inputFile.getAbsolutePath());

			try {
				m.write(new FileOutputStream(anOutputFile), outputSyntax);
			} catch (FileNotFoundException e) {
				log.error("could not write to file!", e);
			}
		} else {
			System.out.println("Invalid syntax given! => " + outputSyntax);
		}
	}

	public Model getModel() {
		return this.m;
	}

	public File getOutputFile() {
		return this.outputFile;

	}

	private boolean checkOutputSyntax(String aSyntax) {
		if (aSyntax.equals("RDF/XML")) {
			return true;
		} else if (aSyntax.equals("RDF/XML-ABBREV")) {
			return true;
		} else if (aSyntax.equals("N-TRIPLE")) {
			return true;
		} else if (aSyntax.equals("TURTLE")) {
			return true;
		} else if (aSyntax.equals("TTL")) {
			return true;
		} else if (aSyntax.equals("N3")) {
			return true;
		} else {
			return false;
		}
	}

}
