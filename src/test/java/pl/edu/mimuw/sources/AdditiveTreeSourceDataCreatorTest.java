package pl.edu.mimuw.sources;

import java.io.IOException;

import org.junit.Test;

public class AdditiveTreeSourceDataCreatorTest {

	private final static String IN_DIR = "/home/ballo0/GTI/projects/testDataCreatorData/";
	private final static String OUT_DIR = "/home/ballo0/GTI/projects/testDataCreatorTreeResults/";

	@Test
	public void junitTest() throws IOException {
		AbstractSourceDataCreator creator = new AdditiveTreeSourceDataCreator();
		creator.createData(IN_DIR + "junit", OUT_DIR + "junit/");
	}

	@Test
	public void jhotDrawTest() throws IOException {
		AbstractSourceDataCreator creator = new AdditiveTreeSourceDataCreator();
		creator.createData(IN_DIR + "jhotdraw", OUT_DIR + "jhotdraw/");
	}

	@Test
	public void jloximTest() throws IOException {
		AbstractSourceDataCreator creator = new AdditiveTreeSourceDataCreator();
		creator.createData(IN_DIR + "jloxim", OUT_DIR + "jloxim/");

	}

	@Test
	public void hibernateORMTest() throws IOException {
		AbstractSourceDataCreator creator = new AdditiveTreeSourceDataCreator();
		creator.createData(IN_DIR + "hibernate-orm", OUT_DIR + "hibernateOrm/");

	}

	@Test
	public void gephiMaventTest() throws IOException {
		AbstractSourceDataCreator creator = new AdditiveTreeSourceDataCreator();
		creator.createData(IN_DIR + "gephi-maven", OUT_DIR + "gephiMaven/");

	}

	@Test
	public void jmeterTest() throws IOException {
		AbstractSourceDataCreator creator = new AdditiveTreeSourceDataCreator();
		creator.createData(IN_DIR + "jmeter", OUT_DIR + "jmeter/");

	}

	@Test
	public void tedTest() throws IOException {
		AbstractSourceDataCreator creator = new AdditiveTreeSourceDataCreator();
		creator.createData(IN_DIR + "ted", OUT_DIR + "ted/");

	}

	@Test
	public void jackrabbitTest() throws IOException {
		AbstractSourceDataCreator creator = new AdditiveTreeSourceDataCreator();
		creator.createData(IN_DIR + "jackrabbit", OUT_DIR + "jackrabbit/");

	}

}
