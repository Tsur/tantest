package com.scripturesos.tantest.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;

public final class TestUtil {

    /**
     * Get the preferred implementor of some interface or abstract class.
     * This is currently done by looking up a plugins file by the name of
     * the given class, and assuming that the "default" key is an implementation
     * of said class. Warnings are given otherwise.
     * @param clazz The class or interface to find an implementation of.
     * @return The configured implementing class.
     * @throws MalformedURLException if the plugin file can not be found
     * @throws IOException if there is a problem reading the found file
     * @throws ClassNotFoundException if the read contents are not found
     * @throws ClassCastException if the read contents are not valid
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     * @see PluginUtil#getImplementors(Class)
     */
    private static Test getImplementor(Class<?> clazz, String prop) throws IOException, ClassNotFoundException, ClassCastException, InstantiationException, IllegalAccessException
    {
        Properties props = getPlugin(clazz);
        
        String name = props.getProperty(prop);
        
        Log.i("tantest","class to instantiate: "+ name);
        
        Test impl = (Test) forName(name);

        return impl;
    }
    
    public static Object forName(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException
    {
    	return Class.forName(className).newInstance();
    }

    /**
     * Get and instantiate the preferred implementor of some interface or abstract class.
     * @param clazz The class or interface to find an implementation of.
     * @return The configured implementing class.
     * @throws MalformedURLException if the plugin file can not be found
     * @throws IOException if there is a problem reading the found file
     * @throws ClassNotFoundException if the read contents are not found
     * @throws ClassCastException if the read contents are not valid
     * @throws InstantiationException if the new object can not be instantiated
     * @throws IllegalAccessException if the new object can not be instantiated
     * @see PluginUtil#getImplementors(Class)
     */
    public static Test getInstance(Class<?> clazz) throws MalformedURLException, ClassCastException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        return getImplementor(clazz,DEFAULT);
    }
    
    public static Test getInstance(Class<?> clazz, String prop) throws MalformedURLException, ClassCastException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException
    {
    	return getImplementor(clazz,prop);
    }

	/**
     * Get and load a plugin file by looking it up as a resource.
     * @param clazz The name of the desired resource
     * @return The found and loaded plugin file
     * @throws IOException if the resource can not be loaded
     * @throws MissingResourceException if the resource can not be found
     */
    private static Properties getPlugin(Class<?> clazz) throws IOException
    {
        String pluginName = clazz.getSimpleName() + EXTENSION_PLUGIN;
        
        try
        {	
        	InputStream in = clazz.getResourceAsStream(pluginName);

            Properties prop = new Properties();
            
            prop.load(in);
            
            return prop;
        }
        catch (MissingResourceException e)
        {
            return new Properties();
        }
    }
    
    public static JSONArray getSource(InputStream file)
    {
	
		try
		{
			//Vamos a leer el fichero linea por linea
			BufferedReader reader = new BufferedReader(new InputStreamReader(file, "UTF-8"),8);

			//Obtenemos todo el fichero JSON en un string
			StringBuilder string_builder = new StringBuilder();
			String line = null;
			String result;
			
			while ((line = reader.readLine()) != null)
			{
				string_builder.append(line);
				//Log.i("tantes","JSON linea: " + line);
			}
			
			result = string_builder.toString();
			//Log.i("tantes","JSON content: " + result);

			//Creamos objeto json a partir del string
			return new JSONArray(result);
			
		}
		catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
    }
    
    public static JSONArray getRemoteSource(String url) throws TestRemoteSourceException, MalformedURLException, IOException
	{    	
    	/*URL stream = new URL(url);
    	URLConnection connection = stream.openConnection();
        connection.setConnectTimeout(5 * 1000);//5s
        connection.setReadTimeout(40 * 1000);//40s
        return getSource(connection.getInputStream());*/
        
		return getSource((new URL(url)).openStream());
		
	}
    
    public static Set<Integer> random(int amount, int max)
	{
		Set<Integer> randoms 	= new HashSet<Integer>();
		Random randomGenerator  = new Random();
		int res;
		
		//Obtenemos num_questions numeros aleatorios que no se repitan entre 0 y test.length(), no incluido test.length()
		while(randoms.size() < amount)
		{
			res = randomGenerator.nextInt(max);
			randoms.add(res);
		}
		
		return randoms;
	}
    
    private static final String EXTENSION_PLUGIN = ".plugin";
    private static final String DEFAULT = "0";
}
