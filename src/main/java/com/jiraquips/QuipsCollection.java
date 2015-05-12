package com.jiraquips;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.PropertySetManager;
import com.atlassian.jira.user.ApplicationUser;

public class QuipsCollection
{
	public Quip addQuip(ApplicationUser author, String quip)
	{
		Calendar now = Calendar.getInstance();
		String key = Long.toString(now.getTimeInMillis()) + "-" + author.getName();
		PropertySet ps = getQuipsPropertySet();
		ps.setText(key, quip);

		return new Quip(key, now.getTimeInMillis(), author.getName(), quip);
	}

	public void deleteQuip(String key)
	{
		PropertySet ps = getQuipsPropertySet();
		ps.remove(key);
	}

	public Map<String, Quip> getQuips()
	{
		Map<String, Quip> rtn = new HashMap<String, Quip>();
		PropertySet ps = getQuipsPropertySet();
		for (Iterator itr = ps.getKeys().iterator(); itr.hasNext(); )
		{
			String key = (String)itr.next();
			String[] parts = key.split("-");
			long millis = Long.parseLong(parts[0]);

			String author = parts[1];
			String text = ps.getText(key);

			rtn.put(key, new Quip(key, millis, author, text));
		}

		return rtn;
	}

	private PropertySet getQuipsPropertySet()
	{
		HashMap ofbizArgs = new HashMap();
        ofbizArgs.put("delegator.name", "default");
        ofbizArgs.put("entityName", "com.google.code.quips");
        ofbizArgs.put("entityId", new Long(1));

        return PropertySetManager.getInstance("ofbiz", ofbizArgs);
	}
}
