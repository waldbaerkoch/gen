package waldbaer.gentest;

import java.io.File;
import java.util.Arrays;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.junit.Assert;
import org.junit.Test;

public class GenTest {

	@Test
	public void test() {
		StringBuilder sb = new StringBuilder();
		final Model model = ModelFactory.createDefaultModel();
		for (final File file : Arrays.asList(new File("./").listFiles())) {
			if (!file.getAbsolutePath().endsWith(".ttl")) {
				continue;
			}
			Model model2 = ModelFactory.createDefaultModel();
			try {
				model2.read("file:///" + file.getAbsolutePath(), "TURTLE");
				final ResIterator subjects = model2.listSubjects();
				while (subjects.hasNext()) {
					final Resource subject = subjects.next();
					if (!subject.isAnon()) {
						if (model.contains(subject, null, (RDFNode) null)) {
							sb.append(file.getAbsolutePath() + " has triple with subject " + subject.getURI() + "\n");
						}
					}
				}
				model.add(model2);
			} catch (Exception e) {
				sb.append(file.getAbsolutePath() + " " + e.getMessage() + "\n");
			}
		}
//		int subjectCounter = 0;
//		final ResIterator subjects = model.listSubjects();
//		while (subjects.hasNext()) {
//			final Resource subject = subjects.next();
//			if (!subject.isAnon()) {
//				subjectCounter++;
//			}
//		}
//		System.out.println(subjectCounter);
		if (sb.length() > 0) {
			System.err.println(sb.toString());
			Assert.fail(sb.toString());
		}
	}

}

