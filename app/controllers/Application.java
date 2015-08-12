package controllers;

import play.*;
import play.mvc.*;
import play.Logger;

import views.html.*;

public class Application extends Controller {

	public static Result main(String any) {
		return ok(main.render());
	}
  
	public static Result loginPage() {
    	return ok(loginPage.render());
  	}
  	
  	public static Result bookListPage() {
    	return ok(bookListPage.render());
  	}
  
}
