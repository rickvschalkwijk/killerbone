package helpers;

import java.util.List;

import com.avaje.ebean.Ebean;

public class Pagination
{
	public static <T> Page<T> page(Class<T> entityClass, int page, int pageSize, String sortBy, String order, String filter)
	{
		if (page < 1)
		{
			page = 1;
		}
		
		int totalRowCount = Ebean.find(entityClass).findRowCount();
		List<T> entities = Ebean.find(entityClass).setFirstRow((page - 1) * pageSize).setMaxRows(pageSize).findList();
		
		return new Page<T>(entities, totalRowCount, page, pageSize);
	}

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
			return start + " to " + end + " of " + totalRowCount;
		}
	}
}
