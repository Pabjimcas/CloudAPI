package validators;

import models.Inspector;
import play.data.validation.Constraints;
import play.libs.F;
import play.libs.F.Tuple;

public class InspectorCodeValidator extends Constraints.Validator<String>{

	public static Integer errorType = 0;
	
	@Override
	public Tuple<String, Object[]> getErrorMessageKey() {
		if(errorType == 1){
			return new F.Tuple<String, Object[]>("El código ya existe", new Object[]{""});
		}else if(errorType == 2){
			return new F.Tuple<String, Object[]>("El código es erróneo", new Object[]{""});
		}
		return new F.Tuple<String, Object[]>("El código es requerido", new Object[]{""});
		}

	
	@Override
	public boolean isValid(String value) {
		String regex = "[1-7]{2}[A-Z]{5}";
		Boolean res = false;
		
		if(value != null){
		String code =value.toUpperCase();
		
		if(code.matches(regex)){
			if(Inspector.findByCode(code) == null){
				res = true;
			}else{
				errorType = 1;
			}
		}else{
			errorType = 2;
		}
		}
		return res;
	}

}
