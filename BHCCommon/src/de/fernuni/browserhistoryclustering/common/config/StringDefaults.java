package de.fernuni.browserhistoryclustering.common.config;

public final class StringDefaults {
	
	private StringDefaults()
	{
		throw new AssertionError();
	}
	
	public static final String BROWSERHISTORY_FILENAME = "history.sqlite";
	public static final String BASE_DIR = "../";
	//public static final String BOILERPIPE_OUTPUT_DIR = "BoilerpipeOut/";
	public static final String TIKA_OUTPUT_DIR = "TikaOut/";
	public static final String TOPICS_DIR = "TopicValues/";
	public static final String QUERY_CLUSTERS_DIR = "QueryGroups/";
	public static final String DATA_DIR = "Data/";
	public static final String HTML_DIR = "html/";
	public static final String TEXT_DIR = "text/";
	public static final String FILENAME_NORMTOPIC2TOPIC = "norm2top.dat";
	public static final String FILENAME_NORMTOPIC2VALUE = "norm2val.dat";
	public static final String FILENAME_MAUIMODEL = "keyphrextr";
	public static final String FILENAME_MAUISTOPWORDS = "stopwords_en.txt";
	public static final String FILENAME_FILENAME2QUERIES = "file2queries.dat";
	public static final String LANGPROFILES_DIR = "langprofiles";

}
