package pt.ieeta.dicoogledefacerplugin.core.pluginset.jetty;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.apache.http.entity.mime.*;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.util.Collection;
import java.util.Objects;

public class test extends HttpServlet
{
    private static final Logger logger = LoggerFactory.getLogger(test.class);

    public static final String MULTIPART_FORMDATA_TYPE = "multipart/form-data";

    public static boolean isMultipartRequest(ServletRequest request) {
        return request.getContentType() != null && request.getContentType().startsWith(MULTIPART_FORMDATA_TYPE);
    }

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    static byte[] getBytesFromInputStream(InputStream is) throws IOException {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream();)
        {
            byte[] buffer = new byte[0xFFFF];

            for (int len; (len = is.read(buffer)) != -1;)
                os.write(buffer, 0, len);

            os.flush();

            return os.toByteArray();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            proxy(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void proxy(HttpServletRequest req, HttpServletResponse resp ) throws Exception {
        // Cliente vai fazer um semi proxy, redireciona o pedido de post para o servidor Defacer.
        // A resposta do servidor ao Post vai ser usada para criar um ficheiro.
        // Este ficheiro é enviado para outro servidor por um pedido do Cliente)

        if (req.getContentType() == null) {
            JSONObject reply = new JSONObject();
            try {
                reply.put("error", "no content");
            } catch (JSONException e) {
                logger.warn("Interrupted", e);
            }
            resp.getWriter().print(reply.toString());
            resp.setStatus(400);
            return;

        } else if (isMultipartRequest(req)) {


            MultipartEntityBuilder builder = MultipartEntityBuilder.create();

            final Collection<Part> parts = req.getParts();

            System.out.println("Receiving data...");
            parts.stream().sequential()
                    .filter(Objects::nonNull)
                .forEach(part -> {
                    //System.out.println("" + part.getName() + " -> " + getFileName(part));
                    try {
                        byte[] data = getBytesFromInputStream(part.getInputStream());
                        builder.addBinaryBody("file", data, ContentType.APPLICATION_OCTET_STREAM, getFileName(part));
                    } catch (IOException ex) {
                        logger.warn("Failed to fetch file", ex);
                    }
                });

            System.out.println("Sending data...");
            HttpEntity entity = builder.build();



            CloseableHttpClient client = HttpClients.createDefault();

            HttpPost post = new HttpPost("http://127.0.0.1:5000/");
            post.setEntity(entity);

            CloseableHttpResponse response = client.execute(post);
            System.out.println(response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode() == 200) {
                final HttpEntity resEntity = response.getEntity();

                ByteArrayOutputStream os = new ByteArrayOutputStream();

                resEntity.writeTo(os);

                os.close();
                response.close();

                byte[] byteArray = os.toByteArray();
                InputStream in = new ByteArrayInputStream(byteArray);

                MultipartEntityBuilder builder2 = MultipartEntityBuilder.create();
                builder2.addBinaryBody("file", in);


                HttpEntity entity2 = builder2.build();

                HttpPost post2 = new HttpPost("http://127.0.0.1:8080/nifti/convert");
                System.out.println("Sending data...");
                post2.setEntity(entity2);
                CloseableHttpClient client2 = HttpClients.createDefault();
                CloseableHttpResponse response2 = client2.execute(post2);

                System.out.println(response2.getStatusLine().getStatusCode());

            }



        }
    }


    private String getFileName(final Part part) {
        final String partHeader = part.getHeader("content-disposition");
        logger.info("Part Header = {}", partHeader);
        for (String content : part.getHeader("content-disposition").split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(
                        content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }

    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response)
            throws IOException, ServletException
    {
        try {
            proxy(request,response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String [ ] args) throws Exception {

        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http", "DEBUG");

        ServletHolder fileUploadServletHolder = new ServletHolder(new test());
        fileUploadServletHolder.getRegistration().setMultipartConfig(new MultipartConfigElement("/tmp/upload"));

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.addServlet(fileUploadServletHolder, "/fileupload");

        Server server = new Server(9090);
        server.setHandler(context);

        server.start();
        server.join();
    }

}
