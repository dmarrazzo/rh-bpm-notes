package ssa.test;

import org.jbpm.formModeler.api.model.Field;
import org.junit.Test;

import ssa.poc.RestDataProvider;

public class RestTest {

	@Test
	public void test() {
		RestDataProvider restDataProvider = new RestDataProvider();
		Field field = new Field();
		field.setFieldName("ok");
		restDataProvider.getSelectOptions(field, null, null, null);
		
	}

}
