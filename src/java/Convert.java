
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;
public class Convert extends HttpServlet {


    private double CC(String from, String to, double from_input) {
        double data = 0;
        try {
            String url_str = "https://api.exchangerate-api.com/v4/latest/USD";

            // Making Request
            URL url = new URL(url_str);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.connect();

            // Convert to JSON
            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
            JsonObject jsonobj = root.getAsJsonObject();
            JsonObject rates = jsonobj.get("rates").getAsJsonObject();

            // Accessing object
            Double from_rate = rates.get(from).getAsDouble();
            Double to_rate = rates.get(to).getAsDouble();

            // divided 
            data = ((1.0 / from_rate) * to_rate)*from_input;

        } catch (JsonIOException | JsonSyntaxException | IOException ex) {
            Logger.getLogger(Convert.class.getName()).log(Level.SEVERE, null, ex);
        }
        return data;
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        PrintWriter out = response.getWriter();

        String FROM = request.getParameter("FROM");
        String TO = request.getParameter("TO");
        double FROM_INPUT = Double.parseDouble(request.getParameter("FROM_INPUT"));
        double output = CC(FROM, TO, FROM_INPUT);
        System.out.println(FROM_INPUT + "\n" + FROM + "\n" + TO+"\n"+output);
        out.print(output);
        try
        {
            Class.forName("org.apache.derby.jdbc.ClientDriver");
            Connection con=DriverManager.getConnection("jdbc:derby://localhost:1572/Currency","sa","123456");
            PreparedStatement ps=con.prepareStatement("insert into CurrencyConverter values(?,?,?,?)");
            ps.setString(1,FROM);
            ps.setString(2, TO);
            ps.setDouble(3,FROM_INPUT);
            ps.setDouble(4,output);
            ps.executeUpdate();
        } 
        catch(Exception e)
        {
            
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
