import java.awt.*;

/**
 * Manages the day/night cycle.
 *
 * Night mode activates automatically at score 700 and alternates every 700
 * points — exactly like the real Chrome Dino game.
 * Provides colours for background, ground tint, and text so the rest of the
 * game just asks NightMode for the right colour instead of hard-coding white.
 */
public class NightMode {

    // Day colours
    public static final Color DAY_BG         = Color.WHITE;
    public static final Color DAY_GROUND     = new Color(83,  83,  83);
    public static final Color DAY_TEXT       = new Color(83,  83,  83);
    public static final Color DAY_TEXT_DIM   = new Color(150, 150, 150);
    public static final Color DAY_HINT       = new Color(180, 180, 180);

    // Night colours
    public static final Color NIGHT_BG       = new Color(34,  34,  34);
    public static final Color NIGHT_GROUND   = new Color(200, 200, 200);
    public static final Color NIGHT_TEXT     = new Color(220, 220, 220);
    public static final Color NIGHT_TEXT_DIM = new Color(160, 160, 160);
    public static final Color NIGHT_HINT     = new Color(100, 100, 100);

    private static final int CYCLE_INTERVAL = 700;   // points per day/night phase

    private boolean isNight        = false;
    private float   transitionAlpha = 1.0f;           // 1 = fully settled, 0 = mid-transition
    private boolean transitioning  = false;
    private int     lastCycleScore = 0;

    /** Call every frame with the current score. */
    public void update(int score) {
        int phase = score / CYCLE_INTERVAL;
        boolean shouldBeNight = (phase % 2 == 1);

        if (shouldBeNight != isNight && !transitioning) {
            transitioning  = true;
            transitionAlpha = 0f;
        }

        if (transitioning) {
            transitionAlpha = Math.min(1f, transitionAlpha + 0.02f);
            if (transitionAlpha >= 1f) {
                isNight       = shouldBeNight;
                transitioning = false;
            }
        }
    }

    public boolean isNight()        { return isNight; }
    public float   getAlpha()       { return transitionAlpha; }

    /** Interpolated background colour (smooth fade between day and night). */
    public Color getBackground() {
        return isNight ? blend(DAY_BG, NIGHT_BG, transitionAlpha)
                       : blend(NIGHT_BG, DAY_BG, transitionAlpha);
    }

    public Color getGroundColor()  { return isNight ? NIGHT_GROUND   : DAY_GROUND;   }
    public Color getTextColor()    { return isNight ? NIGHT_TEXT      : DAY_TEXT;     }
    public Color getTextDimColor() { return isNight ? NIGHT_TEXT_DIM  : DAY_TEXT_DIM; }
    public Color getHintColor()    { return isNight ? NIGHT_HINT      : DAY_HINT;     }

    /** Reset to day on game reset. */
    public void reset() {
        isNight         = false;
        transitioning   = false;
        transitionAlpha = 1.0f;
        lastCycleScore  = 0;
    }

    // ── helpers ───────────────────────────────────────────────────────────

    private static Color blend(Color from, Color to, float t) {
        int r = (int)(from.getRed()   + (to.getRed()   - from.getRed())   * t);
        int g = (int)(from.getGreen() + (to.getGreen() - from.getGreen()) * t);
        int b = (int)(from.getBlue()  + (to.getBlue()  - from.getBlue())  * t);
        return new Color(
            Math.max(0, Math.min(255, r)),
            Math.max(0, Math.min(255, g)),
            Math.max(0, Math.min(255, b))
        );
    }
}
