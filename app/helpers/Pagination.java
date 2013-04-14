package helpers;

import java.util.List;

import models.Event;
import models.Location;
import models.User;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.Query;

public class Pagination
{
	/**
	 * Creates a pagination page for the user table.
	 * @param int
	 * @param int
	 * @param String
	 * @param String
	 * @return Page<User>
	 */
	public static Page<User> getUserPage(int page, int pageSize, String orderBy, String filter)
	{
		page = Math.max(page, 1);
		orderBy = Common.ensureNotNull(orderBy);
		filter = Common.ensureNotNull(filter);

		// Compose query
		Query<User> query = Ebean.find(User.class)
								 .setFirstRow((page - 1) * pageSize)
								 .setMaxRows(pageSize)
								 .where()
								 .or(Expr.like("email", "%" + filter + "%"), Expr.like("name", "%" + filter + "%"))
								 .orderBy(orderBy + (orderBy.equals("lastActivityDate") ? " desc" : ""));
	
		// Execute query
		List<User> users = query.findList();
		int totalRowCount = query.findRowCount();
		
		return new Page<User>(users, totalRowCount, page, pageSize);		
	}
	
	/**
	 * Creates a pagination page for the event table.
	 * @param int
	 * @param int
	 * @param String
	 * @param String
	 * @return Page<Event>
	 */	
	public static Page<Event> getEventPage(int page, int pageSize, String orderBy, String filter)
	{
		page = Math.max(page, 1);
		orderBy = Common.ensureNotNull(orderBy);
		filter = Common.ensureNotNull(filter);
		
		// Compose query
		Query<Event> query = Ebean.find(Event.class)
								  .setFirstRow((page - 1) * pageSize)
								  .setMaxRows(pageSize)
								  .where()
								  .like("title", "%" + filter + "%")
								  .orderBy(orderBy);
		
		// Execute query
		List<Event> events = query.findList();
		int totalRowCount = query.findRowCount();
		
		return new Page<Event>(events, totalRowCount, page, pageSize);	
	}
	
	/**
	 * Creates a pagination page for the location table.
	 * @param int
	 * @param int
	 * @param String
	 * @param String
	 * @return Page<Event>
	 */	
	public static Page<Location> getLocationPage(int page, int pageSize, String orderBy, String filter)
	{
		page = Math.max(page, 1);
		orderBy = Common.ensureNotNull(orderBy);
		filter = Common.ensureNotNull(filter);
		
		// Compose query
		Query<Location> query = Ebean.find(Location.class)
								     .setFirstRow((page - 1) * pageSize)
								     .setMaxRows(pageSize)
								     .where()
								     .like("title", "%" + filter + "%")
								     .orderBy(orderBy);
		
		// Execute query
		List<Location> locations = query.findList();
		int totalRowCount = query.findRowCount();
		
		return new Page<Location>(locations, totalRowCount, page, pageSize);	
	}	
	
	//-----------------------------------------------------------------------//
	
	public static class Page<T>
	{
		private final int pageSize;
		private final long totalRowCount;
		private final int pageIndex;
		private final List<T> list;

		public Page(List<T> data, long total, int page, int pageSize)
		{
			this.list = data;
			this.totalRowCount = total;
			this.pageIndex = page;
			this.pageSize = pageSize;
		}

		public long getTotalRowCount()
		{
			return totalRowCount;
		}

		public int getPageIndex()
		{
			return pageIndex;
		}

		public List<T> getList()
		{
			return list;
		}

		public boolean hasPrev()
		{
			return pageIndex > 1;
		}

		public boolean hasNext()
		{
			return (totalRowCount / pageSize) >= pageIndex;
		}

		public String getDisplayXtoYofZ()
		{
			int start = ((pageIndex - 1) * pageSize + 1);
			int end = start + Math.min(pageSize, list.size()) - 1;
			start = Math.min(start, end);
			return start + " to " + end + " of " + totalRowCount;
		}
	}
}
