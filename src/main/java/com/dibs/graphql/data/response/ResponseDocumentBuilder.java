package com.dibs.graphql.data.response;

import java.util.HashMap;
import java.util.Map;

import com.dibs.graphql.data.QueryBeanParser;
import com.dibs.graphql.data.request.Query;

public class ResponseDocumentBuilder {
	
	private Map<String, Object> documentValues;
	private Query query;
	
	public ResponseDocumentBuilder(Query query) {
		this.query = query;
		documentValues = new HashMap<>();
	}
	
	public void addField(String fieldName, Object value) {
		documentValues.put(fieldName, value);
	}
	
	public void addAllFields(Map<String, Object> values) {
		documentValues.putAll(values);
	}
	
	public void addFromBean(Object bean) {
		Map<String, Object> beanValues = QueryBeanParser.parse(query, bean);
		documentValues.putAll(beanValues);
	}
	
	public Map<String, Object> getData() {
		return documentValues;
	}
}