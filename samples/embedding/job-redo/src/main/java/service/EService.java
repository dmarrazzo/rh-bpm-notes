package service;

public class EService {

	public void exception(String param) {
		System.out.println("EService.exception() - "+param );
		if (param.contains("exception")) {
			throw new RuntimeException(param);
		}
	}
}
