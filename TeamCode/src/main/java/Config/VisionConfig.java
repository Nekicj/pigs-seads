    package Config;

    import android.util.Size;

    import com.acmerobotics.dashboard.config.Config;

    import org.firstinspires.ftc.vision.opencv.ColorRange;
    import org.firstinspires.ftc.vision.opencv.ColorSpace;
    import org.opencv.core.Scalar;

    @Config
    public class VisionConfig {

        public static Size resolution = new Size(320,240);

        public static Scalar BLUE_LOWER = new Scalar(100, 150, 50);
        public static Scalar BLUE_UPPER = new Scalar(140, 255, 255);

        public static Scalar RED_LOWER = new Scalar(0, 150, 50);
        public static Scalar RED_UPPER = new Scalar(30, 255, 255);

        public static Scalar YELLOW_LOWER = new Scalar(20, 100, 100);
        public static Scalar YELLOW_UPPER = new Scalar(40, 255, 255);
        public static ColorRange RED_YELLOW_COLOR_RANGE = new ColorRange(
                ColorSpace.YCrCb,
                new Scalar(0, 150, 50),
                new Scalar(255, 255, 150));
        public static ColorRange RED_COLOR_RANGE = new ColorRange(
                ColorSpace.YCrCb,
                new Scalar(0, 160, 80),
                new Scalar(255, 255, 120)
        );


    }