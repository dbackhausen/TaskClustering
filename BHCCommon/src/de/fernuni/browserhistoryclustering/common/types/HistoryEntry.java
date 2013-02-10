package de.fernuni.browserhistoryclustering.common.types;

public class HistoryEntry {
	
	private Long m_Id;
	private String m_Url;
	private String m_Title;
	
	public HistoryEntry() {
		// TODO Auto-generated constructor stub
	}

	public Long getId() {
		return m_Id;
	}

	public void setId(Long p_Id) {
		this.m_Id = p_Id;
	}

	public String getUrl() {
		return m_Url;
	}

	public void setUrl(String p_url) {
		m_Url = p_url;
	}

	public String getTitle() {
		return m_Title;
	}

	public void setTitle(String title) {
		m_Title = title;
	}
	
	public String getQuery()
	{
		if (getTitle().endsWith(" - Google-Suche")) {
			return getTitle().substring(0,
					getTitle().length() - " - Google-Suche".length());
		} 
		else if (getTitle().endsWith(" - Google Search")) {
			return getTitle().substring(0,
					getTitle().length() - " - Google Search".length());
		}
		else
			return getTitle();
	}

}
