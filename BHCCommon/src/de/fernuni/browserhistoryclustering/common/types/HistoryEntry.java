 		package de.fernuni.browserhistoryclustering.common.types;

/**
 * @author ah
 *
 * Wraps browser history entry
 */
public class HistoryEntry {
	
	private Long m_Id;
	private String m_Url;
	private String m_Title;
	
	/**
	 * @return History entry ID
	 */
	public Long getId() {
		return m_Id;
	}

	/**
	 * @param p_Id
	 */
	public void setId(Long p_Id) {
		this.m_Id = p_Id;
	}

	/**
	 * @return History entry URL
	 */
	public String getUrl() {
		return m_Url;
	}

	/**
	 * @param p_url
	 */
	public void setUrl(String p_url) {
		m_Url = p_url;
	}

	/**
	 * @return History entry title
	 */
	public String getTitle() {
		return m_Title;
	}

	/**
	 * @param title
	 */
	public void setTitle(String title) {
		m_Title = title;
	}
	
	/**
	 * @return google search query
	 */
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
