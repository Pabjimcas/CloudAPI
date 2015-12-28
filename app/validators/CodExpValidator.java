package validators;

import javax.validation.ConstraintValidator;

import models.Expedient;
import play.data.validation.Constraints;
import play.libs.F.Tuple;

public class CodExpValidator extends Constraints.Validator<String> implements ConstraintValidator<CodExp, String> {

	@Override
	public Tuple<String, Object[]> getErrorMessageKey() {
		return new Tuple<String, Object[]>("El código ya existe", new Object[]{""});
	}

	@Override
	public boolean isValid(String value) {
		return Expedient.findByCode(value) == null;
	}

	@Override
	public void initialize(CodExp arg0) {
	}

}
