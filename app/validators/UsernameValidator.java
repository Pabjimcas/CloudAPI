package validators;

import javax.validation.ConstraintValidator;

import models.User;
import play.data.validation.Constraints;
import play.libs.F.Tuple;

public class UsernameValidator extends Constraints.Validator<String> implements ConstraintValidator<Username, String> {

	@Override
	public Tuple<String, Object[]> getErrorMessageKey() {
		return null;
	}

	@Override
	public boolean isValid(String username) {
		return User.findByUsername(username) == null;
	}

	@Override
	public void initialize(Username arg0) {
	}

}
