package Controllers;

import android.util.Size;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.opencv.ColorBlobLocatorProcessor;
import org.firstinspires.ftc.vision.opencv.ColorRange;
import org.firstinspires.ftc.vision.opencv.ColorSpace;

import org.firstinspires.ftc.vision.opencv.ImageRegion;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Config.VisionConfig;

public class VisionController {

    private Size resolution;

    public ColorBlobLocatorProcessor colorLocatorYellow, colorLocatorAlliance;
    public VisionPortal portal;

    public void initialize(HardwareMap hardwareMap, boolean blueAlliance,boolean is_busket) {
        colorLocatorYellow = new ColorBlobLocatorProcessor.Builder()
//                .setTargetColorRange(new ColorRange(ColorSpace.YCrCb, new Scalar(100, 100, 0), new Scalar(255, 210, 70)))
                .setTargetColorRange(new ColorRange(ColorSpace.HSV, new Scalar(14, 65, 100), new Scalar(37, 255, 255)))
//                .setTargetColorRange(new ColorRange(ColorSpace.HSV, new Scalar(0, 65, 100), new Scalar(0, 255, 255)))
                .setErodeSize(15)
//                .setDilateSize(6)
//                .setBlurSize(4)
                .setContourMode(ColorBlobLocatorProcessor.ContourMode.EXTERNAL_ONLY)
                .setRoi(ImageRegion.entireFrame())
                .setDrawContours(true)
                .setBlurSize(5)
                .build();


        if (blueAlliance){
            //BLUE
            colorLocatorAlliance = new ColorBlobLocatorProcessor.Builder()
                    .setTargetColorRange(ColorRange.BLUE)
                    .setContourMode(ColorBlobLocatorProcessor.ContourMode.EXTERNAL_ONLY)
                    .setRoi(ImageRegion.entireFrame())
                    .setDrawContours(true)
                    .setBlurSize(2)
                    .build();
        }
        else{
            //RED
            colorLocatorAlliance = new ColorBlobLocatorProcessor.Builder()
                    .setTargetColorRange(VisionConfig.RED_COLOR_RANGE)
                    .setContourMode(ColorBlobLocatorProcessor.ContourMode.EXTERNAL_ONLY)
                    .setRoi(ImageRegion.entireFrame())
                    .setDrawContours(true)
                    .setBlurSize(2)
                    .build();
        }




        resolution = VisionConfig.resolution;

        portal = new VisionPortal.Builder()
                .addProcessor(colorLocatorYellow)
                .addProcessor(colorLocatorAlliance)
                .setCameraResolution(resolution)
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))

                .build();

        portal.setProcessorEnabled(colorLocatorYellow, is_busket);

        portal.setProcessorEnabled(colorLocatorAlliance, true);

        FtcDashboard dashboard = FtcDashboard.getInstance();
        dashboard.startCameraStream(portal, 20);

        portal.resumeStreaming();
        portal.resumeLiveView();
    }

    private double norm(Point point) {
        return Math.sqrt(point.dot(point));
    }

    public BlobInfo getBiggestBlobInfo(MultipleTelemetry multipleTelemetry, boolean debug, boolean alliance, boolean is_busket) {
        List<ColorBlobLocatorProcessor.Blob> blobs = new ArrayList<>();

        if (is_busket){
            blobs.addAll(colorLocatorAlliance.getBlobs());
            blobs.addAll(colorLocatorYellow.getBlobs());
        }
        else{
            blobs.addAll(colorLocatorAlliance.getBlobs());
        }

        ColorBlobLocatorProcessor.Util.filterByArea(1e3, 1e5, blobs);
        //ColorBlobLocatorProcessor.Util.filterByAspectRatio(0.5, 2.0, blobs);

        ColorBlobLocatorProcessor.Blob biggestBlob = null;
        for (ColorBlobLocatorProcessor.Blob blob : blobs)
            if (biggestBlob == null || blob.getContourArea() > biggestBlob.getContourArea())
                biggestBlob = blob;

        if (biggestBlob == null) return null;

        RotatedRect boxFit = biggestBlob.getBoxFit();

        Point[] pt = new Point[4];
        boxFit.points(pt);

        Point edge1 = new Point(pt[1].x - pt[0].x, pt[1].y - pt[0].y);
        Point edge2 = new Point(pt[2].x - pt[1].x, pt[2].y - pt[1].y);

        Point usedEdge = edge1;
        if (norm(edge1) < norm(edge2)) usedEdge = edge2;

        Point reference = new Point(0, 1);
        int angle = (int) Math.toDegrees(Math.acos(reference.dot(usedEdge) / norm(usedEdge)));

        int deltaX = (int) boxFit.center.x - resolution.getWidth() / 2;
        int deltaY = (int) boxFit.center.y - resolution.getHeight() / 2;

        if (debug) {
            multipleTelemetry.addLine(String.format(Locale.ENGLISH, "Angle %3d", angle));
            multipleTelemetry.addLine(String.format(Locale.ENGLISH, "Area %3d", biggestBlob.getContourArea()));
            multipleTelemetry.addLine(String.format(Locale.ENGLISH, "Distance (%3d %3d)", deltaX, deltaY));
        }

        return new BlobInfo(angle, deltaX, deltaY);
    }

    public static class BlobInfo {
        public final int angle, deltaX, deltaY;

        BlobInfo(int angle, int deltaX, int deltaY) {
            this.angle = angle;
            this.deltaX = deltaX;
            this.deltaY = deltaY;
        }
    }
}