package br.eng.moretto;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CustomMapUtil
	{
	    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue( Map<K, V> map )
	    {
	        List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>( map.entrySet() );
	        
	        Collections.sort( list, new Comparator<Map.Entry<K, V>>()
	        {
	            public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
	            {
	                return (o2.getValue()).compareTo( o1.getValue() );
	            }
	        } );

	        Map<K, V> result = new LinkedHashMap<K, V>();
	        for (Map.Entry<K, V> entry : list)
	        {
	            result.put( entry.getKey(), entry.getValue() );
	        }
	        return result;
	    }
	    
	    /**
	     * Metodo que insere o value no map, porem, se ja tiver, ele soma esse Integer value
	     *  
	     * @param source HashMap a ser inserido
	     * @param key
	     * @param value
	     */
	    public static void addOrCount(Map<Integer,Integer> source, Integer key, Integer value){
	    		    	
	    	if(source.containsKey(key))
	    		source.put(key, source.get(key)+value);
	    	else
	    		source.put(key, value);	    	
	    	
	    }   
	    
	    
	    
	}