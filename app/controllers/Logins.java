package controllers;

import java.util.List;

import play.*;
import play.mvc.*;
import play.libs.Json;
import play.Logger;

import org.bson.types.ObjectId;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import models.Login;

public class Logins extends Controller {
	
	public static Result newLogin() {
		JsonNode json = request().body().asJson();
		ObjectNode result = Json.newObject();
  		String userId = json.findPath("userId").getTextValue();
  		String password = json.findPath("password").getTextValue();
		
		if(userId == null || password == null) {
			result.put("status", "KO");
    		result.put("message", "Missing parameter(s)");
			return badRequest(result);
		} else {
			Login login = Json.fromJson(json, Login.class);
			Login.create(login);			
			
			result.put("status", "OK");
			result.put("message", "New login created successfully");
			return ok(result);
		}
	}
	
	public static Result userLogin() {
		JsonNode json = request().body().asJson();
		ObjectNode result = Json.newObject();
		String uid = json.findPath("userId").getTextValue();
  		String pass = json.findPath("password").getTextValue();
		
		if(uid == null || pass == null) {
			result.put("status", "KO");
    		result.put("message", "Missing parameter(s)");
			return badRequest(result);
		} else {
			Login login = new Login();
			login.userId = uid;
			login.password = pass;
			boolean isAuth = Login.isAuth(login);
			
			if(isAuth) {
				result.put("status", "OK");
				result.put("message", "Authetic user");
				result.put("userId", uid);
			} else {
				result.put("status", "KO");
				result.put("message", "Credential doesn't match");
			}			
			return ok(result);
		}	
	}
}
