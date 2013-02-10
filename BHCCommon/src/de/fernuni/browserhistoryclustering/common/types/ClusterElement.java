package de.fernuni.browserhistoryclustering.common.types;

public class ClusterElement {
	
	private Long m_Id;
	private String m_Url;
	private String m_Title;
	
	public ClusterElement() {
		// TODO Auto-generated constructor stub
	}

	public Long getV_Id() {
		return m_Id;
	}

	public void setV_Id(Long p_Id) {
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

}
