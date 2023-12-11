package tools.sapcx.commerce.toolkit.testing.testdoubles.catalog;

import static java.util.stream.Collectors.toMap;
import static org.apache.commons.collections4.ListUtils.emptyIfNull;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.platformbackoffice.dao.ClassificationAttributeAssignmentDAO;

public class ClassificationAttributeAssignmentDAOStub implements ClassificationAttributeAssignmentDAO {
	private Map<String, ClassAttributeAssignmentModel> assignments;

	public static ClassificationAttributeAssignmentDAOStub with(List<ClassAttributeAssignmentModel> assignments) {
		Map<String, ClassAttributeAssignmentModel> keyBasesAssignments = emptyIfNull(assignments).stream()
				.collect(toMap(ClassificationServiceFake::key, Function.identity()));
		return new ClassificationAttributeAssignmentDAOStub(keyBasesAssignments);
	}

	private ClassificationAttributeAssignmentDAOStub(Map<String, ClassAttributeAssignmentModel> assignments) {
		this.assignments = assignments;
	}

	@Override
	public ClassAttributeAssignmentModel getClassificationAttributeAssignmnent(String systemId, String systemVersion, String classificationClass, String attributeCode) {
		return this.getClassificationAttributeAssignment(systemId, systemVersion, classificationClass, attributeCode);
	}

	@Override
	public ClassAttributeAssignmentModel getClassificationAttributeAssignment(String systemId, String systemVersion, String classificationClass, String attributeCode) {
		String key = ClassificationServiceFake.key(systemId, systemVersion, classificationClass, attributeCode);
		return assignments.get(key);
	}
}
