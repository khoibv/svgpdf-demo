package vn.com.nev.svg;

import com.itextpdf.text.DocumentException;
import java.io.IOException;
import org.apache.batik.transcoder.TranscoderException;

public class MainApp {


  private static final String INPUT_PDF = "input/svg1.pdf";
//  private static final String INPUT_SVG = "input/sample.svg";
    private static final String INPUT_SVG = "input/draw.svg";
  private static final String OUTPUT_PDF = "output/svgpdf.pdf";

  /**
   * This will load a PDF document and add a single image on it.
   *
   * @param args Command line arguments.
   */
  public static void main(String[] args) {
    MainApp app = new MainApp();

//    app.test01();
    app.test02();
  }

  private void test02() {
    // Using PdfBox + batik
    PdfBoxBatikCreator creator = new PdfBoxBatikCreator();
    try {
      creator.createPDF(INPUT_PDF, INPUT_SVG, "output/test02.pdf");
    } catch (IOException | TranscoderException e) {
      e.printStackTrace();
    }
  }

  private void test01() {
    // Using iText
    ITextPdfCreator creator = new ITextPdfCreator();
    try {
      creator.createPdf(INPUT_SVG, "output/test01.pdf");
    } catch (IOException | DocumentException e) {
      e.printStackTrace();
    }
  }


}
