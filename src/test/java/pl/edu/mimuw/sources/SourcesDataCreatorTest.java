package pl.edu.mimuw.sources;

import org.junit.Test;

public class SourcesDataCreatorTest {

	@Test
	public void junitTest() {
		SourcesDataCreator creator = new SourcesDataCreator();
		creator.createData("/home/ballo0/GTI/projects/testDataCreatorData/");
	}
}
