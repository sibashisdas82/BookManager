package controllers;

import java.util.List;

import play.*;
import play.mvc.*;
import play.libs.Json;
import play.Logger;

import org.bson.types.ObjectId;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import models.Book;

public class Books extends Controller {

	public static Result books() {
		Logger.debug("Raw data : " + Book.all().toString());
		return ok(Book.all().toString());
	}
	
	public static Result userBooks(String userId) {
		Logger.debug("Raw data : " + Book.filterList(userId).toString());
		return ok(Book.filterList(userId).toString());
	}
	
	public static Result filterBooks(String title) {
		Logger.debug("Raw search data : " + Book.search(title).toString());
		return ok(Book.search(title).toString());
	}

	public static Result newBook() {
		JsonNode json = request().body().asJson();
		ObjectNode result = Json.newObject();
		String id = json.findPath("id").getTextValue();
		String userId = json.findPath("userId").getTextValue();
  		String author = json.findPath("author").getTextValue();
  		String title = json.findPath("title").getTextValue();
  		String content = json.findPath("content").getTextValue();
		
		if(userId == null || author == null || title == null || content == null) {
			result.put("status", "KO");
    		result.put("message", "Missing parameter(s)");
			return badRequest(result);
		} else {
			Book book = Json.fromJson(json, Book.class);
			if(id != null) {
				result.put("status", "OK");
				result.put("message", "Book updated successfully");
			}
			else {
				ObjectId newId = new ObjectId();
				book.setId(newId);
				Logger.debug("New JSON :: " + book.toString());
				
				result.put("status", "OK");
				result.put("message", "New book '" + title + "' saved successfully");
				result.put("bookId", newId.toString());
			}
			Book.create(book);
			return ok(result);			
		}
	}
	
	public static Result deleteBook(String id) {
		ObjectNode result = Json.newObject();
		Logger.debug("Book ID :: " + id);
		if(id == null) {
			result.put("status", "KO");
    		result.put("message", "Missing parameter(s)");
			return badRequest(result);
		} else {
			Book.delete(id);
			result.put("status", "OK");
			result.put("message", "Book deleted successfully");
			return ok(result);
		}
	}

}
