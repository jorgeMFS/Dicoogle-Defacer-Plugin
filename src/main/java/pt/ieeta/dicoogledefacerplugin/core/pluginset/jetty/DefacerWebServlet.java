package pt.ieeta.dicoogledefacerplugin.core.pluginset.jetty;

import net.jcores.utils.internal.io.FileUtils;
import org.dcm4che2.io.DicomInputStream;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ieeta.dicoogledefacerplugin.core.util.RuntimeIOException;
import pt.ua.dicoogle.sdk.StorageInterface;
import pt.ieeta.dicoogledefacerplugin.core.util.RuntimeIOException;
import pt.ua.dicoogle.sdk.StorageInterface;
import pt.ua.dicoogle.sdk.core.DicooglePlatformInterface;
import pt.ua.dicoogle.sdk.core.PlatformCommunicatorInterface;

import org.eclipse.jetty.client.HttpClient;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import java.io.*;

import java.util.concurrent.ForkJoinPool;
import java.util.stream.Stream;

import org.eclipse.jetty.*;
/**
 * Main web service.
 *
 * @author Jorge Miguel Ferreira da Silva
 */
public class DefacerWebServlet extends HttpServlet implements PlatformCommunicatorInterface{

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(DefacerWebServlet.class);
    private static DicooglePlatformInterface platform;
    private final ForkJoinPool pool = new ForkJoinPool(1);

    @Override
    public void setPlatformProxy(DicooglePlatformInterface core) {
        this.platform = core;
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            proxy(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
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

         } else if (req.getContentType().startsWith("multipart/form-data")) {
             final Part filePart = req.getPart("file");
             final String path = req.getParameter("destination");
             final String fileName = getFileName(filePart);
             OutputStream out = null;
             InputStream filecontent = null;


             out = new FileOutputStream(new File(path + File.separator + fileName));
             filecontent = filePart.getInputStream();

             int read = 0;
             final byte[] bytes = new byte[1024];

             while ((read = filecontent.read(bytes)) != -1) {
                 out.write(bytes, 0, read);
             }

         // Instantiate and configure the SslContextFactory
         SslContextFactory sslContextFactory = new SslContextFactory();
         // Instantiate HttpClient with the SslContextFactory
         HttpClient httpClient = new HttpClient(sslContextFactory);
         // Configure HttpClient:
         httpClient.setFollowRedirects(false);
         // Start HttpClient
         httpClient.start();
         httpClient.stop();

     }
    }
}



















//    public static void main(String [] args) {
//        Vertx.vertx().createHttpServer().requestHandler(req -> {
//            if (req.method() == HttpMethod.POST) {
//                proxy2(req);
//            } else {
//                req.response()
//                        .putHeader("content-type", "text/plain")
//                        .end("Hello from Vert.x!");
//            }
//        }).listen(9090);
//    }
//
//    static void upload2(HttpServerRequest req) {
//        req.setExpectMultipart(true);
//        req.uploadHandler(up -> {
//            logger.info("UPLOADING {}", up.filename());
//            req.response().setChunked(true);
//
//            up.exceptionHandler(error -> {
//                logger.error("{}", error.getMessage());
//                error.printStackTrace();
//            });
//
//            up.endHandler(onEnd -> {
//                // logger.info("SAVED {}", filePath)
//                logger.info("UPLOAD-END");
//                req.response().end("Success");
//
//            });
//
//            //protect against filesystem attacks
//            /*
//            if (!PathValidator.isValid(upload.filename)) {
//                logger.error("Filename not accepted: {}", upload.filename);
//                req.response.statusCode = 403;
//                req.response.end = "Filename not accepted!";
//                return;
//            }
//
//            val filePath = user.theFolder + '/' + upload.filename;
//            req.response.chunked = true;
//
//            upload.streamToFileSystem(filePath);*/
//
//        });
//    }
//
//    static void proxy2(HttpServerRequest req) {
//        final HttpClient client = Vertx.vertx().createHttpClient(new HttpClientOptions());
//
//        System.out.println("Proxying request: " + req.uri());
//        final HttpClientRequest c_req = client.request(req.method(), 5000, "localhost", req.uri(), c_res -> {
//            System.out.println("Proxying response: " + c_res.statusCode());
//            /*req.response().setChunked(true);
//            req.response().setStatusCode(c_res.statusCode());
//            req.response().headers().setAll(c_res.headers());
//            */
//
//            //process python server response...
//            c_res.handler(data -> {
//                System.out.println("Proxying response body: " + data.toString("ISO-8859-1"));
//                req.response().write(data);
//                upload2(req);
//
//
//                //TODO: process file download?
//            });
//            c_res.endHandler((v) -> req.response().end());
//
//        });
//
//        //proxy python server request...
//        c_req.setChunked(true);
//        c_req.headers().setAll(req.headers());
//        req.handler(data -> {
//            System.out.println("Proxying request body " + data.toString("ISO-8859-1"));
//            c_req.write(data);
//        });
//        req.endHandler((v) -> c_req.end());
//    }
//
//}
//



    /*
    @SuppressWarnings("deprecation")
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response)
            throws ServletException, IOException {
        response.setStatus(418, "I'm a teapot");

    }*/

    /*@Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, RuntimeIOException {

        Stream<? extends InputStream> dicomObjects;
        Stream<? extends InputStream> niftiObject;

        Vertx vertx = Vertx.vertx();


        //get volume
        // req_deface= request_service
        // req = send(req_deface)
        // vol_defaced = req.data()
        // transform = tf(vol_defaced)
        // Dicoogle_store

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
        }

        else if (req.getContentType().startsWith("multipart/form-data")) {
            // for now we just retrieve the first valid part
            Collection<Part> parts = req.getParts();
            if (parts == null || parts.isEmpty()) {
                JSONObject reply = new JSONObject();
                try {
                    reply.put("error", "no valid content in multipart entity");
                } catch (JSONException e) {
                    logger.warn("Interrupted", e);
                }
                resp.getWriter().print(reply.toString());
                resp.setStatus(400);
                return;
            }
            niftiObject = parts.stream().sequential()
                    .filter(part -> part.getContentType() != null)
                    .map(part -> {
                        try {
                            return new BufferedInputStream(part.getInputStream());
                        } catch (IOException ex) {
                            logger.warn("Failed to fetch file", ex);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull);


            resp.setContentType("multipart/form-data");
            resp.getWriter().write(niftiObject.toString());

            // req = ???

            String ADDRESS = "127.0.0.1";
            URL url = new URL(ADDRESS);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "multipart/form-data");

            int status = con.getResponseCode();
        }


        *//*resp.setContentType("application/json");

            dicomObjects = parts.stream().sequential()
                    .filter(part -> part.getContentType() != null)
                    .map(part -> {
                        try {
                            return new BufferedInputStream(part.getInputStream());
                        } catch (IOException ex) {
                            logger.warn("Failed to fetch file", ex);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull);

        }
        else {
            dicomObjects = Stream.of(new BufferedInputStream(req.getInputStream()));
        }


        // fetch storage interface
        StorageInterface storage = platform.getStorageForSchema("defacerScheme");
        if (storage == null) throw new IllegalStateException();

        List<String> uris;
        try {
            uris = pool.submit(() -> dicomObjects
                    .filter(Objects::nonNull)
                    .map(dcm -> {

                        try {
                            return storage.store(new DicomInputStream(dcm));
                        } catch (IOException e) {
                            throw new RuntimeIOException(e);
                        }
                    })

                    .filter(Objects::nonNull)
                    .map(URI::toString)
                    .collect(Collectors.toList())).get();
            JSONObject reply = new JSONObject();
            reply.put("status", "COMPLETED");
            reply.put("dcmFiles", uris);
            resp.getWriter().print(reply.toString());
            resp.setStatus(200);

        } catch (RuntimeIOException | InterruptedException | ExecutionException | JSONException e) {
            logger.warn("Interrupted", e);
            JSONObject reply = new JSONObject();
            try {
                reply.put("message", e.getMessage());
                reply.put("status", "interrupted");
            } catch (JSONException e1) {
                logger.warn("Interrupted", e);
            }
            resp.getWriter().print(reply.toString());
            resp.setStatus(500);
        }

    }*/

