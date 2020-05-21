package vn.com.nev.svg;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;

public class PdfBoxBatikCreator {

  /**
   * Add an image to an existing PDF document.
   *
   * @param inputPdf  The input PDF to add the image to.
   * @param inputSvg  The filename of the image to put in the PDF.
   * @param outputPdf The file to write to the pdf to.
   * @throws IOException If there is an error writing the data.
   */
  public void createPDF(String inputPdf, String inputSvg, String outputPdf)
      throws IOException, TranscoderException {
    try (PDDocument doc = PDDocument.load(new File(inputPdf))) {
      //we will add the image to the first page.
      PDPage page = doc.getPage(0);

      // createFromFile is the easiest way with an image file
      // if you already have the image in a BufferedImage,
      // call LosslessFactory.createFromImage() instead
//      PDImageXObject pdImage = PDImageXObject.createFromFile(imagePath, doc);
      PDImageXObject pdImage = createImageObjectFromSvg(inputSvg, doc);

      try (PDPageContentStream contentStream = new PDPageContentStream(doc, page, AppendMode.APPEND,
          true, true)) {
//        contentStream.saveGraphicsState();
        PDExtendedGraphicsState pdExtGfxState = new PDExtendedGraphicsState();
        pdExtGfxState.getCOSObject().setItem(COSName.BM,
            COSName.MULTIPLY); // pdExtGfxState.setBlendMode(BlendMode.MULTIPLY) doesn't work yet, maybe in later version
        pdExtGfxState.setNonStrokingAlphaConstant(1f);
        contentStream.setGraphicsStateParameters(pdExtGfxState);

        // contentStream.drawImage(ximage, 20, 20 );
        // better method inspired by http://stackoverflow.com/a/22318681/535646
        // reduce this value if the image is too large

        float scale = 1.f;
        contentStream
            .drawImage(pdImage, 0, 0, pdImage.getWidth() * scale, pdImage.getHeight() * scale);

//        contentStream.restoreGraphicsState();
      }
      doc.save(outputPdf);
    }
  }

  public static BufferedImage convertSVGToPNG(String url) throws TranscoderException, IOException {
    ByteArrayOutputStream resultByteStream = new ByteArrayOutputStream();

    TranscoderInput transcoderInput = new TranscoderInput(url);
    TranscoderOutput transcoderOutput = new TranscoderOutput(resultByteStream);

    PNGTranscoder pngTranscoder = new PNGTranscoder();
    pngTranscoder.addTranscodingHint(SVGAbstractTranscoder.KEY_HEIGHT, 1657f);
    pngTranscoder.addTranscodingHint(SVGAbstractTranscoder.KEY_WIDTH, 2330f);
    pngTranscoder.transcode(transcoderInput, transcoderOutput);

    resultByteStream.flush();

    return ImageIO.read(new ByteArrayInputStream(resultByteStream.toByteArray()));
  }

  public static PDImageXObject createImageObjectFromSvg(String name, PDDocument doc)
      throws IOException, TranscoderException {
    int dot = name.lastIndexOf('.');
    if (dot == -1) {
      throw new IllegalArgumentException("Image type not supported: " + name);
    }
    String ext = name.substring(dot + 1).toLowerCase();
    if (!"svg".equals(ext)) {
      throw new IllegalArgumentException("Image type not supported: " + name);
    }

    BufferedImage bim = convertSVGToPNG(name);
    return LosslessFactory.createFromImage(doc, bim);
  }
}
