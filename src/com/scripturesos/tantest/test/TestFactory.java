package com.scripturesos.tantest.test;


//import java.io.IOException;
//import java.net.MalformedURLException;

/**
 * This class provides a way to:
 * 
 * (1) Create different sort of Tests without exposing the instantiation logic
 * (2) Use this new object created through a common interface 
 * 
 * @author Zuri Pavon <prucheta@gmail.com>
 * @version 1.0
 */
public class TestFactory
{
	
	/*
	 * The client uses the test as abstract products without being 
	 * aware about their concrete implementation.
	 * 
	 */
	
    private static TestFactory instance = null;
 
    /**
     * Private constructor: we don't want the class to be instantiated from
     * others
     * 
     */
    private TestFactory(){}    
 
    private static void createInstance()
    {
        if (instance == null)
        {
            // Sólo se accede a la zona sincronizada
            // cuando la instancia no está creada
            synchronized(TestFactory.class)
            {
                // En la zona sincronizada sería necesario volver
                // a comprobar que no se ha creado la instancia
                if (instance == null)
                { 
                	instance = new TestFactory();
                }
            }
        }
    }
    /**
     * Singleton Getter
     */
    public static TestFactory getInstance()
    {		
    	createInstance();
    	return instance;
    }
    
    /**
     * No permitimos clonar objetos de esta clase --> singleton
     * @see java.lang.Object#clone()
     */
    public Object clone() throws CloneNotSupportedException
    {
        throw new CloneNotSupportedException(); 
	}
    
    
    /**
     * 
     * Create Test
     */
    public static Test createTest(Class<?> clazz)
    {
    	try
    	{
			/**
			 *  Using a Plugin file:
			 *  
			 *  return (Test) TestUtil.getInstance(TestFactory.class, clazz.getName());
			 */
    		//

    		//return (Test) TestUtil.getInstance(clazz);
    		
    		return (Test) clazz.newInstance();
		}
    	/*
    	catch (MalformedURLException e)
    	{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
    	catch (ClassCastException e)
    	{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	/*
    	catch (IOException e)
    	{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	catch (ClassNotFoundException e)
    	{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/ 
    	catch (InstantiationException e)
    	{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    	catch (IllegalAccessException e)
    	{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
		return null;
    }
    
    public static Test createTest()
    {
    	try
    	{
    		return TestUtil.getInstance(TestFactory.class);
		}

    	catch (Exception e)
    	{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
		return null;
    }

    public static Test createTest(String prop)
    {
    	try
    	{
    		return TestUtil.getInstance(TestFactory.class, prop);
		}
    	catch (Exception e)
    	{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
		return null;
    }
    
    /*public static Test createTest(int category)
    {
    	try
    	{
    		String cat = null;
    		
    		switch(category)
    		{
    		case 0:
    			cat = "general";
    			break;
    		case 1:
    			cat = "history";
    			break;
    		case 2:
    			cat = "geography";
    			break;
    		case 3:
    			cat = "laws";
    			break;
    		case 4:
    			cat = "numbers";
    			break;
    		}
    		
    		return TestFactory.createTest(cat);
		}

    	catch (Exception e)
    	{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
		return null;
    }*/

}
