
package actions;

import java.util.Date;

import models.User;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import play.mvc.Security;


public class Authenticator extends Security.Authenticator{
	
	@Override
    public String getUsername(Http.Context ctx) {
		String token = getTokenFromHeader(ctx);
        if (token != null) {
            User user = User.find.where().eq("authToken", token).findUnique();
            if (user != null) {
            	/*Date tokenDate = user.tokenDate;
                Date now = new Date();
                Long res = now.getTime() - tokenDate.getTime();
                if(res >= 86400000){
                	user.logout();
                	user.update();
                }else{ */
	            	ctx.args.put("userLogged", user);
	                return user.username;
                //}
            }
        }
        return null;
    }
	
	@Override
    public Result onUnauthorized(Http.Context context) {
        return Results.unauthorized("No estás autentificado");
    }
	
	private String getTokenFromHeader(Http.Context ctx) {
        String[] authTokenHeaderValues = ctx.request().headers().get("X-AUTH-TOKEN");
        if ((authTokenHeaderValues != null) && (authTokenHeaderValues.length == 1) && (authTokenHeaderValues[0] != null)) {
            return authTokenHeaderValues[0];
        }
        return null;
    }
}
