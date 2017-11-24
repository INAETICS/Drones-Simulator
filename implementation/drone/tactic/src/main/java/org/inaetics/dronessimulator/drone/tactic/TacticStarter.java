//package org.inaetics.dronessimulator.drone.tactic;
//
//import java.io.*;
//import org.osgi.framework.launch.*;
//import org.apache.felix.main.AutoProcessor;
//
///**
// * @see <a href={http://felix.apache.org/documentation/subprojects/apache-felix-framework/apache-felix-framework
// * -launching-and-embedding.html}>http://felix.apache.org/documentation/subprojects/apache-felix-framework/apache-felix-framework-launching-and-embedding.html</a>
// */
//public class TacticStarter
//{
//    private static Framework m_fwk = null;
//
//    public static void main(String[] argv) throws Exception
//    {
//        // Print welcome banner.
//        System.out.println("\nWelcome to My Launcher");
//        System.out.println("======================\n");
//
//        try
//        {
//            m_fwk = getFrameworkFactory().newFramework(null);
//            m_fwk.init();
//            AutoProcessor.process(null, m_fwk.getBundleContext());
//            m_fwk.start();
//            m_fwk.waitForStop(0);
//            System.exit(0);
//        }
//        catch (Exception ex)
//        {
//            System.err.println("Could not create framework: " + ex);
//            ex.printStackTrace();
//            System.exit(-1);
//        }
//    }
//
//    private static FrameworkFactory getFrameworkFactory() throws Exception
//    {
//        java.net.URL url = TacticStarter.class.getClassLoader().getResource(
//                "META-INF/services/org.osgi.framework.launch.FrameworkFactory");
//        if (url != null)
//        {
//            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
//            try
//            {
//                for (String s = br.readLine(); s != null; s = br.readLine())
//                {
//                    s = s.trim();
//                    // Try to load first non-empty, non-commented line.
//                    if ((s.length() > 0) && (s.charAt(0) != '#'))
//                    {
//                        return (FrameworkFactory) Class.forName(s).newInstance();
//                    }
//                }
//            }
//            finally
//            {
//                if (br != null) br.close();
//            }
//        }
//
//        throw new Exception("Could not find framework factory.");
//    }
//}