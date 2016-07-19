package ssa.test;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import ssa.paymentrequest.UnitaOrganizzativa;

public class TempTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ObjectMapper mapper = new ObjectMapper();
		String src = "{\"codice\":\"ICT\",\"descrizione\":\"ICT\",\"id\":3,\"version\":0}";
		try {
			UnitaOrganizzativa readValue = mapper.readValue(src, UnitaOrganizzativa.class);
			System.out.println(">> "+readValue.getDescrizione());
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
