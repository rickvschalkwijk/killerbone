package controllers;

import java.util.List;

import models.Friendship;
import models.User;
import play.mvc.*;
import views.xml.friendshipList;
import views.xml.userList;

public class Development extends Controller
{
	public static Result getAllUsers()
	{
		List<User> allUsers = User.find.all();
		return ok(userList.render(allUsers));
	}
	
	public static Result getAllFriendships()
	{
		List<Friendship> allFriendships = Friendship.find.all();
		return ok(friendshipList.render(allFriendships));
	}	
}
