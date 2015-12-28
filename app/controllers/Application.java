package controllers;


import actions.Authenticator;
import helpers.ControllerHelper;
import models.User;
import play.libs.Json;
import play.mvc.*;
import play.data.Form;


public class Application extends Controller {

    public Result index() {
    	if(session("conectado") != null){
    		return ok("M k");
    	}else{
    		return redirect(
                    routes.Application.login()
    	            );
    	}
    }
    public Result login(){
    	Form<User> userForm = Form.form(User.class);
    	return ok(views.html.login.render(userForm));
    }
    
    @Security.Authenticated(Authenticator.class)
    public Result logout(){
    	User user = (User) Http.Context.current().args.get("userLogged");
    	user.logout();
    	user.update();
    	return ok("Desconectado");
    }
    
    public Result authenticate(){
    	Form<User> form = Form.form(User.class).bindFromRequest();
    	if (form.hasErrors()) {
    		return badRequest(ControllerHelper.errorJson(2, "Datos incorrectos", form.errorsAsJson()));
        }
            if(User.authenticate(form.get())){
            	User user = User.findByUsername(form.get().username);
            	user.tokenGenerate();
            	user.update();
            	return ok(Json.toJson(user));
            }
    	return ok("toy aqui");
    }

}
