package validators;

import javax.validation.ConstraintValidator;

import play.data.validation.Constraints;
import play.libs.F;
import play.libs.F.Tuple;

public class NoumValidator extends Constraints.Validator<String> implements ConstraintValidator<Noum, String> {

	@Override
	public Tuple<String, Object[]> getErrorMessageKey() {
		return new F.Tuple<String, Object[]>("No válido", new Object[]{""});
	}

	@Override
	public boolean isValid(String name) {
		Boolean res = true;
		if(name!= "" && name != null){
		for(int i=0; i< name.length();i++){
			if(Character.isDigit(name.charAt(i))){
				res = false;
				break;
			}
		}
		}
		return res;
	}

	@Override
	public void initialize(Noum arg0) {
	}
	

}
