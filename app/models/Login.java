package models;

import java.util.List;

import org.bson.types.ObjectId;

import play.Logger;
import play.data.validation.Constraints.Required;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;

import controllers.MorphiaObject;

@Entity
public class Login {
	
	@Id
	public ObjectId id;
	
	@Required
	public String userId;
	
	@Required
	public String password;
	
	public static boolean isAuth(Login login) {
		Logger.debug("SEARCH :: " + MorphiaObject.datastore);
		
		if (MorphiaObject.datastore != null && login != null) {
			List<Login> objs = MorphiaObject.datastore.find(Login.class).filter("userId", login.userId).filter("password", login.password).asList();
			if(objs.size() > 0) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	public static void create(Login login) {
		Logger.debug("Login data > " + login.toString());
		MorphiaObject.datastore.save(login);
	}
	
	public String toJSON() {
		return "{\"id\":\"" + id + "\",\"userId\":\"" + userId + "\",\"password\":\"" + password + "\"}";
	}
	
	@Override
	public String toString() {
		return "id:" + id + ", userId:" + userId + ", password:" + password;
	}
}
