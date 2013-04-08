package helpers;

import java.util.List;

import models.User;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.Query;

public class Pagination
{
	public static Page<User> getUserPage(int page, int pageSize, String orderBy, String filter)
	{
		if (page < 1)
		{
			page = 1;
		}
		if (orderBy == null)
		{
			orderBy = "";
		}
		if (filter == null)
		{
			filter = "";
		}

		// Compose query
		Query<User> query = Ebean.find(User.class)
								 .setFirstRow((page - 1) * pageSize)
								 .setMaxRows(pageSize)
								 .where()
								 .or(Expr.like("email", "%" + filter + "%"), Expr.like("name", "%" + filter + "%"))
								 .orderBy(orderBy);
		
		// Execute query
		List<User> users = query.findList();
		int totalRowCount = query.findRowCount();
		
		return new Page<User>(users, totalRowCount, page, pageSize);		
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
			return (totalRowCount / pageSize) > pageIndex;
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
