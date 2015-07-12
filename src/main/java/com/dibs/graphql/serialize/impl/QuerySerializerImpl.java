package com.dibs.graphql.serialize.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dibs.graphql.data.Query;
import com.dibs.graphql.data.deserialize.Punctuator;
import com.dibs.graphql.deserialize.SerializationException;
import com.dibs.graphql.serialize.QuerySerializer;

public class QuerySerializerImpl implements QuerySerializer {
	private static final Log LOG = LogFactory.getLog(QuerySerializerImpl.class);
	
	private static final byte[] LINE_SEPARATOR = System.lineSeparator().getBytes();
	private static final byte[] TAB = "\t".getBytes();
	private static final byte[] SPACE = " ".getBytes();
	
	public void serialize(OutputStream stream, Query query, boolean isPrettyPrint) throws SerializationException {
		if (LOG.isTraceEnabled()) {
			LOG.trace("Starting query serialization");
		}
		
		try {
			writeQuery(stream, query, 0, isPrettyPrint, true);
		} catch(Exception e) {
			throw new SerializationException(e);
		}
			
		if (LOG.isTraceEnabled()) {
			LOG.trace("Finished query serialization");
		}
	}
	
	private void writeAttributes(OutputStream stream, Query query, boolean isPrettyPrint) throws IOException {
		if (isPrettyPrint) {
			stream.write(SPACE);
		}
		
		ArrayList<Map.Entry<String, Object>> attributes = new ArrayList<>(query.getArguments().entrySet());
				
		stream.write(Punctuator.OPEN_PAREN.getValue());
		
		int attributeSize = attributes.size();

		for (int i = 0; i < attributeSize; i++) {
			boolean isLastAttribute = (i == (attributeSize - 1));
			
			Map.Entry<String, Object> attribute = attributes.get(i);
			
			stream.write(attribute.getKey().getBytes());
			stream.write(Punctuator.COLON.getValue());
			stream.write(attribute.getValue().toString().getBytes());
			
			if (!isLastAttribute) {
				stream.write(Punctuator.COMMA.getValue());
			}
			
		}
		
		stream.write(Punctuator.CLOSE_PAREN.getValue());
	}
	
	private void writeQuery(OutputStream stream, Query query, int depth, boolean isPrettyPrint, boolean isLastChild) throws IOException {	

		if (isPrettyPrint) {
			indent(stream, depth);
		}
		
		if (query.getName() != null) {
			stream.write(query.getName().getBytes());
		}
		
		if (query.getArguments() != null) {
			writeAttributes(stream, query, isPrettyPrint);
		}
		
		List<Query> subQueries = query.getSubQueries();
		
		if (subQueries != null && !subQueries.isEmpty()) {
			// Weird, but solves the case of padding the root node and not padding a comma
			if (query.getName() != null && isPrettyPrint) {
				stream.write(SPACE);
			}
			stream.write(Punctuator.OPEN_CURLY_BRACE.getValue());
			
			if (isPrettyPrint) {
				stream.write(LINE_SEPARATOR);
			}
			
			int subQueryCount = query.getSubQueries().size();
			
			for (int i = 0; i < subQueryCount; i++) {
				boolean isLastSubQuery = (i == (subQueryCount - 1));
				
				writeQuery(stream, subQueries.get(i), depth + 1, isPrettyPrint, isLastSubQuery);
			}
			
			if (isPrettyPrint) {
				indent(stream, depth);
			}
			
			stream.write(Punctuator.CLOSE_CURELY_BRACE.getValue());
		}

		if (!isLastChild) {
			stream.write(Punctuator.COMMA.getValue());
		}
		
		if (isPrettyPrint) {
			stream.write(LINE_SEPARATOR);
		}
	}
	
	private void indent(OutputStream outputStream, int depth) throws IOException {
		for (int i = 0; i < depth; i++) {
			outputStream.write(TAB);
		}
	}
}