package pl.poznan.put.cs.bioserver.external;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.Charset;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Matplotlib {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(Matplotlib.class);

    public enum Method {
        SINGLE, COMPLETE, AVERAGE, WEIGHTED
    }

    public static void hierarchicalClustering(File outfile, double[][] matrix,
            String[] labels, Method method)
            throws ParserConfigurationException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();

        Element root = document.createElement("hierarchical");
        document.appendChild(root);

        Element element = document.createElement("outfile");
        element.setTextContent(outfile.getAbsolutePath());
        root.appendChild(element);

        element = document.createElement("method");
        element.setTextContent(method.name().toLowerCase());
        root.appendChild(element);

        element = document.createElement("labels");
        for (String label : labels) {
            Element subElement = document.createElement("label");
            subElement.setTextContent(label);
            element.appendChild(subElement);
        }
        root.appendChild(element);

        element = document.createElement("data");
        for (int i = 0; i < matrix.length; i++) {
            assert matrix.length == matrix[i].length;
            for (int j = i + 1; j < matrix.length; j++) {
                Element subElement = document.createElement("value");
                subElement.setTextContent(Double.toString(matrix[i][j]));
                element.appendChild(subElement);
            }
        }
        root.appendChild(element);

        URL resource = Matplotlib.class.getResource("/pl/poznan/put/cs/"
                + "bioserver/external/MatplotlibHierarchical.xsl");
        File file = null;
        try (InputStream stream = resource.openStream()) {
            String script = XSLT.transform(new StreamSource(stream),
                    new DOMSource(document));
            Matplotlib.LOGGER.trace(script);

            file = File.createTempFile("hierarchical", ".py");
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(script);
            }

            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(new String[] { "python",
                    file.getAbsolutePath() });
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                // do nothing
            }

            StringWriter writer = new StringWriter();
            IOUtils.copy(process.getInputStream(), writer);
            Matplotlib.LOGGER.debug("Standard output:\n" + writer.toString());

            writer = new StringWriter();
            IOUtils.copy(process.getErrorStream(), writer);
            Matplotlib.LOGGER.debug("Standard error:\n" + writer.toString());
        } catch (IOException e) {
            Matplotlib.LOGGER.error("XSLT transformation and external tool "
                    + "running failed", e);
            throw e;
        } finally {
            if (file != null) {
                file.delete();
            }
        }
    }

    public static void partitionalClustering(File outfile, double[][] x,
            double[][] y, double[] mx, double[] my, String[] labels)
            throws ParserConfigurationException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();

        Element root = document.createElement("partitional");
        document.appendChild(root);

        Element element = document.createElement("outfile");
        element.setTextContent(outfile.getAbsolutePath());
        root.appendChild(element);

        element = document.createElement("x");
        for (double[] array : x) {
            Element elementArray = document.createElement("array");
            for (double value : array) {
                Element elementValue = document.createElement("value");
                elementValue.setTextContent(Double.toString(value));
                elementArray.appendChild(elementValue);
            }
            element.appendChild(elementArray);
        }
        root.appendChild(element);

        element = document.createElement("y");
        for (double[] array : y) {
            Element elementArray = document.createElement("array");
            for (double value : array) {
                Element elementValue = document.createElement("value");
                elementValue.setTextContent(Double.toString(value));
                elementArray.appendChild(elementValue);
            }
            element.appendChild(elementArray);
        }
        root.appendChild(element);

        element = document.createElement("mx");
        for (double value : mx) {
            Element elementValue = document.createElement("value");
            elementValue.setTextContent(Double.toString(value));
            element.appendChild(elementValue);
        }
        root.appendChild(element);

        element = document.createElement("my");
        for (double value : my) {
            Element elementValue = document.createElement("value");
            elementValue.setTextContent(Double.toString(value));
            element.appendChild(elementValue);
        }
        root.appendChild(element);

        element = document.createElement("labels");
        for (String label : labels) {
            Element subElement = document.createElement("label");
            subElement.setTextContent(label);
            element.appendChild(subElement);
        }
        root.appendChild(element);

        if (Matplotlib.LOGGER.isTraceEnabled()) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            try {
                XSLT.printDocument(document, stream);
                Matplotlib.LOGGER.trace(stream.toString("UTF-8"));
            } catch (TransformerException e) {
                // do nothing
            }
        }

        URL resource = Matplotlib.class.getResource("/pl/poznan/put/cs/"
                + "bioserver/external/MatplotlibPartitional.xsl");
        File file = null;
        try (InputStream stream = resource.openStream()) {
            String script = XSLT.transform(new StreamSource(stream),
                    new DOMSource(document));
            Matplotlib.LOGGER.trace(script);

            file = File.createTempFile("partitional", ".py");
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(script);
            }

            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(new String[] { "python",
                    file.getAbsolutePath() });
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                // do nothing
            }

            StringWriter writer = new StringWriter();
            IOUtils.copy(process.getInputStream(), writer);
            Matplotlib.LOGGER.info("Standard output:\n" + writer.toString());

            writer = new StringWriter();
            IOUtils.copy(process.getErrorStream(), writer);
            Matplotlib.LOGGER.info("Standard error:\n" + writer.toString());
        } catch (IOException e) {
            Matplotlib.LOGGER.error("XSLT transformation and external tool "
                    + "running failed", e);
            throw e;
        } finally {
            if (file != null) {
                file.delete();
            }
        }
    }

    public static void local(File outfile, String[] residues,
            double[][] differences, String[] labels)
            throws ParserConfigurationException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();

        Element root = document.createElement("local");
        document.appendChild(root);

        Element element = document.createElement("outfile");
        element.setTextContent(outfile.getAbsolutePath());
        root.appendChild(element);

        element = document.createElement("residues");
        for (String value : residues) {
            Element elementValue = document.createElement("value");
            elementValue.setTextContent(value);
            element.appendChild(elementValue);
        }
        root.appendChild(element);

        element = document.createElement("differences");
        for (double[] array : differences) {
            Element elementArray = document.createElement("array");
            for (double value : array) {
                Element elementValue = document.createElement("value");
                elementValue.setTextContent(Double.toString(value));
                elementArray.appendChild(elementValue);
            }
            element.appendChild(elementArray);
        }
        root.appendChild(element);

        element = document.createElement("labels");
        for (String label : labels) {
            Element subElement = document.createElement("label");
            subElement.setTextContent(label);
            element.appendChild(subElement);
        }
        root.appendChild(element);

        if (Matplotlib.LOGGER.isTraceEnabled()) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            try {
                XSLT.printDocument(document, stream);
                Matplotlib.LOGGER.trace(stream.toString("UTF-8"));
            } catch (TransformerException e) {
                // do nothing
            }
        }

        URL resource = Matplotlib.class.getResource("/pl/poznan/put/cs/"
                + "bioserver/external/MatplotlibLocal.xsl");
        File file = null;
        try (InputStreamReader reader = new InputStreamReader(
                resource.openStream(), Charset.forName("UTF-8"))) {
            String script = XSLT.transform(new StreamSource(reader),
                    new DOMSource(document));
            Matplotlib.LOGGER.trace(script);

            file = File.createTempFile("local", ".py");
            try (Writer writer = new FileWriterWithEncoding(file,
                    Charset.forName("UTF-8"))) {
                writer.write(script);
            }

            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(new String[] { "python",
                    file.getAbsolutePath() });
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                // do nothing
            }

            StringWriter writer = new StringWriter();
            IOUtils.copy(process.getInputStream(), writer);
            Matplotlib.LOGGER.info("Standard output:\n" + writer.toString());

            writer = new StringWriter();
            IOUtils.copy(process.getErrorStream(), writer);
            Matplotlib.LOGGER.info("Standard error:\n" + writer.toString());
        } catch (IOException e) {
            Matplotlib.LOGGER.error("XSLT transformation and external tool "
                    + "running failed", e);
            throw e;
        } finally {
            if (file != null) {
                file.delete();
            }
        }
    }

    private Matplotlib() {
    }
}
