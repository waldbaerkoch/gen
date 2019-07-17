package waldbaer.gentest;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.junit.Assert;
import org.junit.Test;

public class GenTest {

	private static final String NS_URI_GEN = "http://jjkoch.de/gen#";
	private static final String NS_URI_OWL = "http://www.w3.org/2002/07/owl#";
	private static final String[] VALID_PERSON_PROPERTIES = new String[] {
		NS_URI_GEN + "name",
		NS_URI_GEN + "birthDate",
		NS_URI_GEN + "deathDate",
		NS_URI_GEN + "father",
		NS_URI_GEN + "mother",
		NS_URI_GEN + "adoptedBy",
		NS_URI_OWL + "sameAs"
	};
	private static final List<String> L_VALID_PERSON_PROPERTIES = Arrays.asList(VALID_PERSON_PROPERTIES);
	private static final String[] VALID_MARRIAGE_PROPERTIES = new String[] {
		NS_URI_GEN + "spouse",
		NS_URI_GEN + "startDate",
		NS_URI_GEN + "divorceDate",
		NS_URI_GEN + "annulledDate",
		NS_URI_GEN + "separatedDate",
		NS_URI_GEN + "dissolvedDate"
	};
	private static final List<String> L_VALID_MARRIAGE_PROPERTIES = Arrays.asList(VALID_MARRIAGE_PROPERTIES);

	@Test
	public void test() {
		StringBuilder sb = new StringBuilder();
		final Model model = ModelFactory.createDefaultModel();
		for (final File file : Arrays.asList(new File("./").listFiles())) {
			if (!file.getAbsolutePath().endsWith(".ttl")) {
				continue;
			}
			final Model model2 = ModelFactory.createDefaultModel();
			try {
				model2.read("file:///" + file.getAbsolutePath(), "TURTLE");
				final ResIterator subjects = model2.listSubjects();
				while (subjects.hasNext()) {
					final Resource subject = subjects.next();
					if (!subject.isAnon()) {
						if (model.contains(subject, null, (RDFNode) null)) {
							sb.append(file.getAbsolutePath() + " has triple with subject " + subject.getURI() + "\n");
						}
						final StmtIterator statements = model2.listStatements(subject, null, (RDFNode) null);
						while (statements.hasNext()) {
							Statement s = statements.next();
							final Property p = s.getPredicate();
							if (!L_VALID_PERSON_PROPERTIES.contains(p.getURI())) {
								sb.append(file.getAbsolutePath() + ": subject " + subject.getURI() + " with invalid property " + p.getURI() + "\n");
							}
						}
					} else {
						final StmtIterator statements = model2.listStatements(subject, null, (RDFNode) null);
						while (statements.hasNext()) {
							Statement s = statements.next();
							final Property p = s.getPredicate();
							if (!L_VALID_MARRIAGE_PROPERTIES.contains(p.getURI())) {
								sb.append(file.getAbsolutePath() + ": anon subject with invalid property " + p.getURI() + "\n");
							}
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

