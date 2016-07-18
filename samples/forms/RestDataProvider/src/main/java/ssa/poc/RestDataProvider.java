package ssa.poc;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jbpm.formModeler.api.client.FormRenderContext;
import org.jbpm.formModeler.api.model.Field;


public class RestDataProvider implements org.jbpm.formModeler.core.config.SelectValuesProvider{

	@Override
	public String getIdentifier() {
		return "ssa.poc.RestDataProvider";
	}

	@Override
	public Map<String, String> getSelectOptions(Field field, String value, FormRenderContext formRenderContext, Locale locale) {
		HashMap<String, String> options = new HashMap<String, String>();


		try {
			System.out.println("RestDataProvider.getSelectOptions() - "+field.getFieldName());
			
			ClientRequest request = new ClientRequest("http://localhost:8888/Rest/ok");
			ClientResponse<Simple[]> response = request.get(Simple[].class);
			Simple[] entity = response.getEntity();
			
			
			for (int i = 0; i < entity.length; i++) {
				options.put(entity[i].getKey(), entity[i].getValue());
//				System.out.println("> "+entity[i].getKey());
			}
						
			
			request.getExecutor().close();
			
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		return options;
	}

}
