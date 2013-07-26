package pl.edu.mimuw.sources;

import java.io.IOException;

import org.junit.Test;

public class AdditiveSourceDataCreatorTest {

	@Test
	public void junitTest() throws IOException {
		AdditiveSourceDataCreator creator = new AdditiveSourceDataCreator();
		creator.createData("/home/ballo0/GTI/projects/testDataCreatorData/junit",
				"/home/ballo0/GTI/projects/testDataCreatorResults/junit/");
	}

	@Test
	public void jhotDrawTest() throws IOException {
		AdditiveSourceDataCreator creator = new AdditiveSourceDataCreator();
		creator.createData("/home/ballo0/GTI/projects/testDataCreatorData/jhotdraw",
				"/home/ballo0/GTI/projects/testDataCreatorResults/jhotdraw/");
	}

	@Test
	public void jloximTest() throws IOException {
		AdditiveSourceDataCreator creator = new AdditiveSourceDataCreator();
		creator.createData("/home/ballo0/GTI/projects/testDataCreatorData/jloxim",
				"/home/ballo0/GTI/projects/testDataCreatorResults/jloxim/");
	}

	@Test
	public void hibernateORMTest() throws IOException {
		AdditiveSourceDataCreator creator = new AdditiveSourceDataCreator();
		creator.createData("/home/ballo0/GTI/projects/testDataCreatorData/hibernate-orm",
				"/home/ballo0/GTI/projects/testDataCreatorResults/hibernateOrm/");
	}

	@Test
	public void gephiMaventTest() throws IOException {
		AdditiveSourceDataCreator creator = new AdditiveSourceDataCreator();
		creator.createData("/home/ballo0/GTI/projects/testDataCreatorData/gephi-maven",
				"/home/ballo0/GTI/projects/testDataCreatorResults/gephiMaven/");
	}

	@Test
	public void jmeterTest() throws IOException {
		AdditiveSourceDataCreator creator = new AdditiveSourceDataCreator();
		creator.createData("/home/ballo0/GTI/projects/testDataCreatorData/jmeter",
				"/home/ballo0/GTI/projects/testDataCreatorResults/jmeter/");
	}

	@Test
	public void tedTest() throws IOException {
		AdditiveSourceDataCreator creator = new AdditiveSourceDataCreator();
		creator.createData("/home/ballo0/GTI/projects/testDataCreatorData/ted",
				"/home/ballo0/GTI/projects/testDataCreatorResults/ted/");
	}

	@Test
	public void jackrabbitTest() throws IOException {
		AdditiveSourceDataCreator creator = new AdditiveSourceDataCreator();
		creator.createData("/home/ballo0/GTI/projects/testDataCreatorData/jackrabbit",
				"/home/ballo0/GTI/projects/testDataCreatorResults/jackrabbit/");
	}

}
