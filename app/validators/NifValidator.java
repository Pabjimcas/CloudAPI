package validators;

import models.Client;
import play.data.validation.Constraints;
import play.libs.F;
import play.libs.F.Tuple;

public class NifValidator extends Constraints.Validator<String>{
	
	private static final String LETRAS_NIF = "TRWAGMYFPDXBNJZSQVHLCKE";
	public static Boolean existNif = false;

	@Override
	public Tuple<String, Object[]> getErrorMessageKey() {
		if(existNif){
			return new F.Tuple<String, Object[]>("El Nif ya existe", new Object[]{""});
		}
		return new F.Tuple<String, Object[]>("El Nif es erróneo", new Object[]{""});
		
	}

	@Override
	public boolean isValid(String value) {
		Boolean res = false;
         try {
         String nif=value.toUpperCase();
         if (nif.matches("[0-9]{8}[" + LETRAS_NIF + "]")) {
             int dni = Integer.parseInt(nif.substring(0, 8));
             char letraCalculada = LETRAS_NIF.charAt(dni % 23);
             if (letraCalculada == nif.charAt(8)) {
            	 if(Client.findByNif(value) == null){
         			res = true;
            	 }else{
            		 existNif = true;
            	 }
             }else{
            	 existNif = false;
             }
         }
         } catch (Exception e) {
             res = false;
         }

		return res;
	}

}
