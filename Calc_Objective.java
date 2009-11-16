import ij.*;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;
import ij.gui.*;
import java.awt.*;

/*Sebastian Rhode
Plugin calculates some parameters of an objective lens
which might be useful for microscopy.

Version 1.2	2009-03-23
*/

public class Calc_Objective implements PlugIn {

    public void run(String arg) {
            if (IJ.versionLessThan("1.39t"))
                    return;

            double ft	= 180;		// focal length of tube lens
            double wl	= 491;		// wavelength
            double n1	= 1.518;	// refractive index of immersion medium
            double n2	= 1.330;	// refractive index of medium
            double NA	= 1.45;   	// numerical aperture of objective lens
            double M	= 60;		// magnification of objective lens
            double pix	= 6.45;		// CCD pixel size
            double fill = 0.8;      // desired fill factor of back aperture
									// 0.7 --> best compromise between resolution and intensity loss

            GenericDialog gd = new GenericDialog("Specify Setup Parameters", IJ.getInstance());
            gd.addNumericField("Numerical Aperture of objective", NA, 2);
            gd.addNumericField("Objective Magnification", M, 0);
            gd.addNumericField("Focal Length Tube Lens [mm]", ft, 0);
            gd.addMessage("Olympus: 180mm - ZEISS: 164.5mm - NIKON & LEICA: 200mm");
            gd.addMessage("");
            //gd.addMessage("-----------------------------------------------------");
            gd.addNumericField("Wavelength [nm]", wl, 0);
            //gd.addMessage("-----------------------------------------------------");
            gd.addNumericField("refractive index (n1) of immersion medium", n1, 3);
            gd.addNumericField("refractive index (n2) of medium", n2, 3);
            //gd.addMessage("-----------------------------------------------------");
            gd.addNumericField("Desired Fill Factor of Back Aperture", fill, 2);
            //gd.addMessage("-----------------------------------------------------");
            gd.addNumericField("CCD Pixel Size [microns]", pix, 2);
            gd.showDialog();
            if (gd.wasCanceled())
                    return;

            NA      = gd.getNextNumber();
            M       = gd.getNextNumber();
            ft      = gd.getNextNumber();
            wl      = gd.getNextNumber();
            n1      = gd.getNextNumber();
            n2      = gd.getNextNumber();
            fill    = gd.getNextNumber();
            pix     = gd.getNextNumber();
            
            // CALCULATIONS
            // maximum working angle of objective
            double maxAngle = Math.ceil(Math.asin(NA/n1)* 180/Math.PI * 10)/10;
            double minAngle = Math.ceil(Math.asin(n2/n1)* 180/Math.PI * 10)/10;
            // focal length of objective [mm]
            double fo = ft / M;		
            // effective pupil diameter
            double peff = fo * NA * 2;
            // collection efficiency
            double fraction = 0;
            if (NA>=1.330) {	// NA > 1.33 will not improve collection efficiency
            	fraction = 0.5;}
            else if (NA<1.330) {
            	fraction = 0.5-0.5*(Math.sqrt(1-( (NA*NA)/(n2*n2) ))); }// imaginary part can be "ignored"

			//fraction = 0.5-0.5*(Math.sqrt(1-( (NA*NA)/(n2*n2) )));            

			// it does not contribute to the number of collected photons        
            // Rayleigh Criterium
            double d = 0.61 * wl / NA;
            // spot size (approximation)    --> 1/e of lateral focal spot size
            double spot = 1.22 * (1-1/ Math.exp(1) ) * wl / NA;
            // required beam diamter 1/e2 for a fill factor of 1 and fill
            double beamf10 = peff;
            double beam_df = fill * peff;
            // pixel size in object plane
            double psob = pix / M * 1000;
            // spot size in pixel
            double spot_pix = spot / psob;
            
            IJ.write("---------- INPUT PARAMETERS -----------");
            IJ.write("Objective  NA : " + NA);
            IJ.write("Objective  Magnification : " + M);
            IJ.write("Focal Length Tube Lens [mm] : " + ft);
            IJ.write("Wavelength [nm] : " + wl);
            IJ.write("Refractive Index n1 : " + n1);
            IJ.write("Refractive Index n2 : " + n2);
            IJ.write("Desired Fill Factor : " + fill);
            IJ.write("CCD Pixel Size [microns] : " + pix);
            IJ.write(" ");
            IJ.write("--------------- RESULTS ---------------");
            IJ.write("Focal Length Objective [mm] : " + fo);
            IJ.write("TIR Angle [grad] : " + minAngle);
            IJ.write("Maximum Angle [grad] : " + maxAngle);
            IJ.write("Collection Efficiency : " + fraction);
            IJ.write("Rayleigh Criterion [nm] : " + Math.rint(d));
            IJ.write("Spot Diameter (1/e) [nm] : " + Math.rint(spot));
            IJ.write("Spot Diameter (1/e) [pixel] : " + Math.rint(spot_pix*100)/100);
            IJ.write("eff. pupil diameter [mm] : " + Math.rint(peff*100)/100);
            IJ.write("Beam (1/e2) Fill=1.0 [mm] : " + Math.rint(beamf10*100)/100);
            IJ.write("Beam (1/e2) Fill=" + fill + " [mm] : " + Math.rint(beam_df*10)/10);
            IJ.write("Pixel Size Object Plane [nm] : " + Math.rint(psob*100)/100);
    }
}