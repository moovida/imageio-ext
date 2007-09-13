package it.geosolutions.imageio.plugins.jp2kakadu;

import it.geosolutions.imageio.stream.output.FileImageOutputStreamExtImpl;
import it.geosolutions.resources.TestData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageReadParam;
import javax.imageio.ImageWriter;
import javax.media.jai.JAI;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderedOp;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.xmlbeans.XmlException;

import com.sun.media.jai.operator.ImageWriteDescriptor;

public class JP2KProfilesTest extends AbstractJP2KTestCase {

	final static String testFileName = "sample.jp2";

	final static String profileFileNames[] = new String[] { "huge.xml",
			"large.xml", "lossless.xml", "lossy.xml" };

	public JP2KProfilesTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		// multithreading settings
		JP2GDALKakaduImageReaderSpi.setReadMultithreadingLevel(5);
		JP2GDALKakaduImageWriterSpi.setWriteMultithreadingLevel(5);
	}

	public void testWriteProfile() throws IOException, FileNotFoundException,
			XmlException {
		final int nProfiles = profileFileNames.length;
		for (int i = 0; i < nProfiles; i++) {
			// //
			// Preparing input/output files
			// //
			final File inputFile =
//				 TestData.file(this, testFileName);
			new File("E:/work/data/GTiff/spezia_wgs84.tif");
			assertTrue(inputFile.exists());

			final File profileFile = TestData.file(this, profileFileNames[i]);
			assertTrue(profileFile.exists());

			// //
			// Preparing to read
			// //
			final ParameterBlockJAI pbjImageRead = new ParameterBlockJAI(
					"ImageRead");
			pbjImageRead.setParameter("Input", inputFile);
			ImageReadParam irp = new ImageReadParam();
			irp.setSourceSubsampling(4, 4, 0, 0);
			pbjImageRead.setParameter("readParam", irp);
			RenderedOp image = JAI.create("ImageRead", pbjImageRead);

			// ////////////////////////////////////////////////////////////////////
			// preparing to write
			// ////////////////////////////////////////////////////////////////////
			final String profileName = profileFileNames[i].substring(0,
					profileFileNames[i].length() - 4);
			final File outputFile = TestData.temp(this, "writingprofile-"
					+ profileName + "-.jp2", false);
			// outputFile.deleteOnExit();

			// Setting output and writer
			final ParameterBlockJAI pbjImageWrite = new ParameterBlockJAI(
					"ImageWrite");
			pbjImageWrite.setParameter("Output",
					new FileImageOutputStreamExtImpl(outputFile));
			ImageWriter writer = new JP2GDALKakaduImageWriterSpi()
					.createWriterInstance();
			pbjImageWrite.setParameter("Writer", writer);

			// Specifying image source to write
			pbjImageWrite.addSource(image);

			JP2GDALKakaduImageWriteParam param = new JP2GDALKakaduImageWriteParam(
					profileFile);
			pbjImageWrite.setParameter("writeParam", param);

			// Writing
			final RenderedOp op = JAI.create("ImageWrite", pbjImageWrite);
			((ImageWriter) op
					.getProperty(ImageWriteDescriptor.PROPERTY_NAME_IMAGE_WRITER))
					.dispose();

			// ////////////////////////////////////////////////////////////////////
			// read it back
			// ////////////////////////////////////////////////////////////////////
			// pbjImageRead.setParameter("Input", outputFile);
			// image = JAI.create("ImageRead", pbjImageRead);
			// Viewer.visualize(image);
		}
	}

	public static Test suite() {
		final TestSuite suite = new TestSuite();
		suite.addTest(new JP2KProfilesTest("testWriteProfile"));
		return suite;
	}

	public static void main(java.lang.String[] args) {
		junit.textui.TestRunner.run(suite());
	}
}
