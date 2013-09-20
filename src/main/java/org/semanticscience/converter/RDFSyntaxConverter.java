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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.shared.JenaException;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.util.FileManager;

/**
 * @author Jose Cruz-Toledo
 * 
 */
public class RDFSyntaxConverter {
	static final Logger log = Logger.getLogger(RDFSyntaxConverter.class);
	/**
	 * The Directory that contains files to be converted
	 */
	private File inputDirectory;
	/**
	 * The output Directory file
	 */
	private File outputDirectory;
	private Model m;
	private Dataset d;

	public RDFSyntaxConverter() {
		inputDirectory = null;
		outputDirectory = null;
		m = null;
		d = null;
	}

	/**
	 * Default converter, reads inputFile into a model, guesses the syntax and
	 * outputs an RDF/XML RDF file in outputFile
	 * 
	 * @param inputDirectory
	 *            the rdf directory to be converted
	 * @param outputDirectory
	 *            the default directory where the RDF files will be stored
	 * @throws Exception
	 */
	public RDFSyntaxConverter(File anInputDir, File anOutputDir)
			throws Exception {
		this.setInputDirectory(anInputDir);
		this.setOutputDirectory(anOutputDir);
		try {
			// Try reading the input file/directory
			if (this.getInputDirectory().isDirectory()
					&& this.getOutputDirectory().isDirectory()) {
				File[] files = this.getInputDirectory().listFiles();
				String base = "/tmp/jena";
				FileUtils.deleteDirectory(new File(base));
				FileUtils.forceMkdir(new File(base));
				d = TDBFactory.createDataset("/tmp/jena/tdb");
				m = d.getDefaultModel();
				FileManager g = FileManager.get();
				for (int i = 0; i < files.length; i++) {
					File f = files[i];
					try {
						g.readModel(m, f.getAbsolutePath());
						// write it to file using rdf/xml syntax
						if (m != null) {
							System.out.println("Converting "
									+ f.getAbsolutePath() + "... ");
							String outname = FilenameUtils.getBaseName(f
									.getName());
							File outFile = new File(
									outputDirectory.getAbsolutePath() + "/"
											+ outname + ".rdf");
							m.write(new FileOutputStream(outFile));
						}else{
							throw new Exception("Skipping file: "+f.getAbsolutePath());
						}
					} catch (JenaException e) {
						System.out.println("Syntax error in file: "+f.getAbsolutePath());
						e.printStackTrace();
						continue;
					}finally{
						m.removeAll();
					}
				}
				System.out.println("...done!");
			} else {
				throw new Exception(
						"Input and output directories must be specified!");
			}
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

	public RDFSyntaxConverter(File anInputDir, File anOutputDir,
			String outputSyntax) throws Exception {
		this.setInputDirectory(anInputDir);
		this.setOutputDirectory(anOutputDir);
		try{
			if (this.getInputDirectory().isDirectory()
					&& this.getOutputDirectory().isDirectory()) {
				File[] files = this.getInputDirectory().listFiles();
				String base = "/tmp/jena";
				FileUtils.deleteDirectory(new File(base));
				FileUtils.forceMkdir(new File(base));
				d = TDBFactory.createDataset("/tmp/jena/tdb");
				m = d.getDefaultModel();
				FileManager g = FileManager.get();
				for (int i = 0; i < files.length; i++) {
					File f = files[i];
					try {
						g.readModel(m, f.getAbsolutePath());
						// write it to file using rdf/xml syntax
						if (m != null) {
							System.out.println("Converting "
									+ f.getAbsolutePath() + "... ");
							String outname = FilenameUtils.getBaseName(f
									.getName());
							String suffix = null;
							if (outputSyntax.equals("RDF/XML")
									|| outputSyntax.equals("RDF/XML-ABBREV")) {
								suffix = ".rdf";
							} else if (outputSyntax.equals("N-TRIPLE")) {
								suffix = ".nt";
							} else {
								suffix = "." + outputSyntax.toLowerCase();
							}
							File outFile = new File(outputDirectory.getAbsolutePath()
									+ "/" + outname + suffix);
							m.write(new FileOutputStream(outFile), outputSyntax);							
						}else{
							throw new Exception("Skipping file: "+f.getAbsolutePath());
						}
					}catch (JenaException e) {
						System.out.println("Syntax error in file: "+f.getAbsolutePath());
						e.printStackTrace();
						continue;
					}finally{
						m.removeAll();
					}
					
				}//for
			}//if
			
		}catch(FileNotFoundException e) {
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

	public Model getModel() {
		return this.m;
	}

	public File getOutputFile() {
		return this.outputDirectory;

	}

	private void setInputDirectory(File aDir) {
		this.inputDirectory = aDir;
	}

	private void setOutputDirectory(File aDir) {
		this.outputDirectory = aDir;
	}

	public File getInputDirectory() {
		return this.inputDirectory;
	}

	public File getOutputDirectory() {
		return this.outputDirectory;
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
