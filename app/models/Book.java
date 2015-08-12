package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.text.SimpleDateFormat;

import org.bson.types.ObjectId;

import play.Logger;
import play.data.validation.Constraints.Required;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;

import controllers.MorphiaObject;

@Entity
public class Book {

	@Id
	public ObjectId id;
	
	@Required
	public String userId;
	
	@Required
	public String title;
	
	@play.data.format.Formats.DateTime(pattern = "yyyy-MM-dd")
	public Date date;
	
	@Required
	public String content;
	
	@Required
	public String author; 

	public void setId(ObjectId id) {
		this.id = id;
	}
	
	public static List<Book> all() {
		if (MorphiaObject.datastore != null) {
			return MorphiaObject.datastore.find(Book.class).asList();
		} else {
			return new ArrayList<Book>();
		}
	}
	
	public static List<Book> search(String strSearch) {
		Logger.debug("SEARCH :: " + MorphiaObject.datastore);
		Pattern regexp = Pattern.compile(strSearch);
		
		if (MorphiaObject.datastore != null) {
			return MorphiaObject.datastore.find(Book.class).filter("title", regexp).asList();
		} else {
			return new ArrayList<Book>();
		}
	}
	
	public static List<Book> filterList(String userId) {
		Logger.debug("SEARCH :: " + MorphiaObject.datastore);
		
		if (MorphiaObject.datastore != null) {
			return MorphiaObject.datastore.find(Book.class).filter("userId", userId).asList();
		} else {
			return new ArrayList<Book>();
		}
	}

	public static void create(Book book) {
		Logger.debug("Book data > " + book.toString());
		MorphiaObject.datastore.save(book);
		//Logger.debug("Saved book id :: " + id);
	}
	
	public static void delete(String idToDelete) {
		Book toDelete = MorphiaObject.datastore.find(Book.class).field("_id").equal(new ObjectId(idToDelete)).get();
		if (toDelete != null) {
			Logger.info("toDelete: " + toDelete);
			MorphiaObject.datastore.delete(toDelete);
		} else {
			Logger.debug("ID No Found: " + idToDelete);
		}
	}
	
	public String convertDateToString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String strDate = sdf.format(date);
		return strDate;
	}
	
	@Override
	public String toString() {
		return "{\"id\":\"" + id + "\",\"userId\":\"" + userId + "\",\"title\":\"" + title + "\",\"date\":\"" + convertDateToString(date) + "\",\"content\":\"" + content + "\",\"author\":\"" + author + "\"}";
	}
}
