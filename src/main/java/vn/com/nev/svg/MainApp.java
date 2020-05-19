package vn.com.nev.svg;

import java.io.File;
import java.io.IOException;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;

public class MainApp {


  /**
   * This will load a PDF document and add a single image on it.
   *
   * @param args Command line arguments.
   */
  public static void main(String[] args) throws IOException, TranscoderException {
    MainApp app = new MainApp();

    final String inputPdf = "input/svg1.pdf";
    final String inputSvg = "input/sample.svg";
//    final String inputSvg = "input/draw3.svg";
    final String outputPdf = "output/svgpdf.pdf";

    app.createPDFFromImage(inputPdf, inputSvg, outputPdf);
  }

  /**
   * Add an image to an existing PDF document.
   *
   * @param inputFile  The input PDF to add the image to.
   * @param imagePath  The filename of the image to put in the PDF.
   * @param outputFile The file to write to the pdf to.
   * @throws IOException If there is an error writing the data.
   */
  public void createPDFFromImage(String inputFile, String imagePath, String outputFile)
      throws IOException, TranscoderException {
    try (PDDocument doc = PDDocument.load(new File(inputFile))) {
      //we will add the image to the first page.
      PDPage page = doc.getPage(0);

      // createFromFile is the easiest way with an image file
      // if you already have the image in a BufferedImage,
      // call LosslessFactory.createFromImage() instead
//      PDImageXObject pdImage = PDImageXObject.createFromFile(imagePath, doc);
      PDImageXObject pdImage = Utility.createImageObjectFromSvg(imagePath, doc);

      try (PDPageContentStream contentStream = new PDPageContentStream(doc, page, AppendMode.APPEND,
          true, true)) {
        contentStream.saveGraphicsState();
        PDExtendedGraphicsState pdExtGfxState = new PDExtendedGraphicsState();
        pdExtGfxState.getCOSObject().setItem(COSName.BM, COSName.MULTIPLY); // pdExtGfxState.setBlendMode(BlendMode.MULTIPLY) doesn't work yet, maybe in later version
        pdExtGfxState.setNonStrokingAlphaConstant(1f);
        contentStream.setGraphicsStateParameters(pdExtGfxState);

        // contentStream.drawImage(ximage, 20, 20 );
        // better method inspired by http://stackoverflow.com/a/22318681/535646
        // reduce this value if the image is too large


        float scale = 1f;
        contentStream
            .drawImage(pdImage, 20, 20, pdImage.getWidth() * scale, pdImage.getHeight() * scale);

        contentStream.restoreGraphicsState();
      }
      doc.save(outputFile);
    }
  }

}
