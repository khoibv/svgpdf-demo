package vn.com.nev.svg;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

public class Utility {

  public static BufferedImage convertSVGToPNG(String url) throws TranscoderException, IOException {
    ByteArrayOutputStream resultByteStream = new ByteArrayOutputStream();

    TranscoderInput transcoderInput = new TranscoderInput(url);
    TranscoderOutput transcoderOutput = new TranscoderOutput(resultByteStream);

    PNGTranscoder pngTranscoder = new PNGTranscoder();
    pngTranscoder.addTranscodingHint(SVGAbstractTranscoder.KEY_HEIGHT, 256f);
    pngTranscoder.addTranscodingHint(SVGAbstractTranscoder.KEY_WIDTH, 256f);
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
    if(!"svg".equals(ext)) {
      throw new IllegalArgumentException("Image type not supported: " + name);
    }

    BufferedImage bim = convertSVGToPNG(name);
    return LosslessFactory.createFromImage(doc, bim);
  }

}
